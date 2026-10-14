package com.wildermods.multimyth.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.RuntimeSkin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.worldwalkergames.legacy.context.ClientDataContext.Skins;
import com.worldwalkergames.legacy.context.LegacyViewDependencies;
import com.worldwalkergames.legacy.integations.SteamManager;
import com.worldwalkergames.legacy.ui.Screen;
import com.worldwalkergames.ui.NiceLabel;
import com.worldwalkergames.ui.NiceButton;
import com.worldwalkergames.ui.layout.CanvasGroup;
import com.wildermods.multimyth.I18N;
import com.wildermods.multimyth.Launcher;
import com.wildermods.multimyth.internal.Install;
import com.wildermods.multimyth.internal.BadInstall;
import com.wildermods.multimyth.internal.IInstall;
import com.wildermods.multimyth.ui.element.AutoFocusingScrollPane;
import com.wildermods.multimyth.ui.element.Grid;
import com.wildermods.wilderforge.api.mixins.v1.Cast;
import com.wildermods.wilderforge.mixins.integrations.SteamManagerAccessor;
import com.wildermods.wilderforge.launch.ui.element.UIColors;
import com.wildermods.multimyth.mixin.SkinAccessor;

public class LauncherSelectionScreen extends Screen {

	private static final Logger LOGGER = LogManager.getLogger(LauncherSelectionScreen.class);
	private final RuntimeSkin skin;
	private Grid instanceGrid;
	
	protected LauncherSelectionScreen(LegacyViewDependencies dependencies) {
		super(dependencies);
		this.skin = dependencies.skin;
		dependencies.dataContext.calculateConnections(dependencies);
	}
	
	@Override
	public void before() {
		super.before();
		skin.skinChanged.add(this, this::build);
		build();
	}
	
	@Override
	public void after() {
		skin.skinChanged.remove(this);
		super.after();
	}
	
	private void build() {
		this.getGroup().clearChildren();
		CanvasGroup canvasGroup = new CanvasGroup();
		canvasGroup.setFillParent(true);
		
		Table mainTable = new Table(this.skin);
		mainTable.setFillParent(true);
		
		
		Table topTable = new Table();
		topTable.defaults().pad(4).padBottom(10);
		
		Table contentTable = new Table();
		Table sidebarTable = new Table();
		Table bottomTable = new Table();
		
		RuntimeSkin scaleUISkin = skin.getSisterSkin(Skins.SCALE_UI);
		topTable.setBackground(scaleUISkin.getDrawable("actionBar_top"));
		bottomTable.setBackground(scaleUISkin.getDrawable("actionBar_bottom"));

		mainTable.add(topTable).expandX().fillX();
		mainTable.row();
		mainTable.add(contentTable).expand().fill();
		
		Actor content = setupContent(contentTable);
		contentTable.add(content).align(Align.topLeft).expand().fill();
		contentTable.add(sidebarTable).expandY().fillY(); //TODO: set width
		mainTable.row();
		mainTable.add(bottomTable).expandX().fillX();
		
		//mainTable.debugAll();
		
		NiceButton newInstanceButton = new NiceButton(I18N.translate("multimyth.topPanel.newInstance"), skin); //TODO: fancy icon?
		topTable.add(newInstanceButton);
		newInstanceButton.clicked.add(this, () -> {
			NewInstancePopup newInstance = new NewInstancePopup(dependencies, this::refreshInstances);
			dependencies.popUpManager.pushFront(newInstance, true);
		});
		//topTable.add(new NiceButton(I18N.translate("multimyth.topPanel.folders"), skin));
		//topTable.add(new NiceButton(I18N.translate("multimyth.topPanel.settings"), skin));
		//topTable.add(new NiceButton(I18N.translate("multimyth.topPanel.help"), skin));
		NiceLabel steamLabel = new NiceLabel(I18N.translate("multimyth.topPanel.steam.notLoggedIn"), skin, "currentTurnLabel");
		steamLabel.setAlignment(Align.right);
		topTable.add(steamLabel).expandX().fillX().align(Align.right);
		if(SteamManager.launcherDetected && SteamManager.steamAppsInitialized()) { //TODO: periodically check if steam has been launched and user is logged in
			SteamManagerAccessor manager = Cast.from(new SteamManager());
			steamLabel.setText(I18N.translate("multimyth.topPanel.steam.loggedIn", manager.getSteamFriends().getPersonaName()));
		}

	
		sidebarTable.defaults().fillX();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.sidePanel.launch"), skin));
		sidebarTable.row();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.sidePanel.edit"), skin));
		sidebarTable.row();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.sidePanel.viewMods"), skin));
		sidebarTable.row();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.sidePanel.screenshots"), skin));
		sidebarTable.row();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.sidePanel.installFolder"), skin));
		sidebarTable.row();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.sidePanel.configFolder"), skin));
		sidebarTable.row();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.sidePanel.modFolder"), skin));
		sidebarTable.row();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.sidePanel.shortcut"), skin));
		sidebarTable.row();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.delete"), skin));
		sidebarTable.row();
		sidebarTable.add(new NiceButton(I18N.translate("multimyth.sidePanel.clone"), skin));
		
		String suffix;
		NiceLabel infoLabel;
		if(Launcher.BUILT_BY.equals("DEVELOPMENT VERSION")) {
			suffix = I18N.translate("multimyth.bottomPanel.buildinfo.develop");
		}
		else {
			suffix = I18N.translate("multimyth.bottomPanel.buildinfo.builtby", Launcher.BUILT_BY, Launcher.BUILD_DATE);
		}
		
		infoLabel = new NiceLabel(I18N.translate("multimyth.bottomPanel.version", suffix), skin, "currentTurnLabel");
		infoLabel.setAlignment(Align.bottomLeft);
		bottomTable.add(infoLabel).align(Align.bottomLeft).padTop(10).expand().fill();
		
		canvasGroup.add(mainTable);
		
		Texture c = dependencies.dataContext.loadTexture("assets/ui/creditsBG_1920x1080.jpg", false);
		TextureRegionDrawable drawable = new TextureRegionDrawable(c);
		mainTable.setBackground(drawable);

		canvasGroup.invalidate();
		this.addActor(canvasGroup);
	}
	
	private Actor setupContent(Table table) {
		SkinAccessor scaledSkin = Cast.from(this.skin.getSisterSkin(Skins.SCALE_UI));
		SkinAccessor skin = Cast.from(this.skin);
		
		Grid grid = new Grid((int)(128 * scaledSkin.getScale()));
		instanceGrid = grid;
		refreshInstances();
		
		AutoFocusingScrollPane pane = new AutoFocusingScrollPane(grid, Cast.from(skin));
		pane.setScrollingDisabled(true, false);
		pane.setFlickScroll(false);
		return pane;
	}

	private void refreshInstances() {
		instanceGrid.clearChildren();
		if(Files.notExists(Launcher.SAVE_DIR)) return;

		List<InstanceEntry> instances = new ArrayList<>();
		try(var directories = Files.list(Launcher.SAVE_DIR)) {
			for(Path directory : directories.peek(path -> LOGGER.info(
					"Instance candidate: {}; directory={}",
					path, Files.isDirectory(path)))
					.filter(Files::isDirectory)
					.toList()) {
				Path metadata = directory.resolve("instance.mm");
				try {
					Install<?> instance = Install.fromFile(metadata);
					if(instance.visible()) {
						instances.add(new InstanceEntry(instance, instance.lastLaunched()));
					}
				}
				catch(RuntimeException e) {
					LOGGER.warn("Could not load instance {}", directory, e);
					long created = 0;
					try {
						created = Files.readAttributes(directory, BasicFileAttributes.class).creationTime().toMillis();
					}
					catch(IOException | SecurityException timestampProblem) {
						LOGGER.warn("Could not read creation time for {}", directory, timestampProblem);
					}
					instances.add(new InstanceEntry(new BadInstall(directory, e), created));
				}
			}
		}
		catch(IOException | java.io.UncheckedIOException e) {
			LOGGER.error("Could not list instances in {}", Launcher.SAVE_DIR, e);
		}
		instances.sort(Comparator.comparingLong(InstanceEntry::lastLaunched)
				.reversed().thenComparing(entry -> entry.install().name()));
		for(InstanceEntry entry : instances) {
			IInstall instance = entry.install();
			NiceButton button = new NiceButton(instance.name(), this.skin);
			instanceGrid.addActor(button);
			if(instance instanceof BadInstall) {
				button.setColor(UIColors.invalidColor);
			}
		}
	}

	private record InstanceEntry(IInstall install, long lastLaunched) {}
	
	private static final record ButtonEntry<T>(String textKey, Supplier<T> action) {}
	
	private static final ButtonEntry<Void> NO_OP = new ButtonEntry<>("NO-OP", () -> {return null;});
	
}
