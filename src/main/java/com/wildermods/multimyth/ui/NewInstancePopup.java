package com.wildermods.multimyth.ui;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.wildermods.multimyth.I18N;
import com.wildermods.multimyth.Launcher;
import com.wildermods.multimyth.internal.InstallException;
import com.wildermods.multimyth.internal.Installer;
import com.wildermods.multimyth.ui.element.Divider;
import com.wildermods.multimyth.ui.element.Divider.Orientation;
import com.wildermods.multimyth.ui.element.AutoFocusingScrollPane;
import com.wildermods.thrixlvault.ChrysalisizedVault;
import com.wildermods.thrixlvault.Vault;
import com.wildermods.thrixlvault.exception.VersionParsingException;
import com.wildermods.thrixlvault.utils.OS;
import com.wildermods.thrixlvault.utils.version.Version;
import com.wildermods.thrixlvault.wildermyth.WildermythManifest;
import com.worldwalkergames.legacy.context.LegacyViewDependencies;
import com.worldwalkergames.legacy.ui.DialogFrame;
import com.worldwalkergames.legacy.ui.MessageBoxDialog;
import com.worldwalkergames.legacy.ui.MessageBoxDialog.IMessageBoxHandler;
import com.worldwalkergames.legacy.ui.PopUp;
import com.worldwalkergames.legacy.ui.menu.OptionsDialog;
import com.worldwalkergames.ui.NiceButton;
import com.worldwalkergames.ui.NiceLabel;

public class NewInstancePopup extends PopUp {
	
	private static final Logger LOGGER = LogManager.getLogger(NewInstancePopup.class);
	boolean nameEdited = false;
	WildermythManifest selectedVersion = null;
	TextField nameField = null;
	NiceButton confirmButton = null;
	volatile Throwable downloadProblem = null;
	Skin skin;
	DialogFrame frame;
	Table masterTable;
	OptionsDialog.Style style;
	
	public NewInstancePopup(LegacyViewDependencies dependencies) {
		super(true, dependencies);

		masterTable = new Table();
	}
	
	public void build() {
		this.skin = dependencies.skin;
		style = skin.get(OptionsDialog.Style.class);
		
		if(frame != null) {
			group.removeActor(frame);
			masterTable.clear();
		}
		
		frame = new DialogFrame(dependencies);
		frame.preferredWidth = style.f("dialogWidth");
		
		

		
		//debugAll();
		
		setupTitleBar();
		
		Table table = masterTable;
		group.debugAll();
		table.debugAll();
		
		setupTopPanel(table);
		table.row();
		table.add(new Divider(Orientation.HORIZONTAL)).expand().fill().pad(4);
		table.row();
		setupMainPanel(table);
		table.row();
		setupBottomPanel(table);
		
		updateButtons();
		//this.background(UIHelper.createSolidColorDrawable(Color.CORAL));
		
		group.add(frame).setVerticalCenter(0).setHorizontalCenter(0);
		frame.addInner(masterTable).expand().fill();
	}
	
	private void setupTitleBar() {
		String title = I18N.translate("multimyth.topPanel.newInstance");
		Label titleLabel = new Label(title, skin, "dialogTitle");
		float padLeft = style.f("padLeft");
		frame.addInner(titleLabel).expandX().left().padLeft(padLeft).padBottom(style.f("titlePadBottom"));
		NiceButton closeButton = new NiceButton("x", dependencies.skin);
		closeButton.getLabel().setFontScale(1.2f);
		closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dependencies.popUpManager.removePopUp(NewInstancePopup.this);
            }
		});
		
		//getTitleTable().add(closeButton).prefWidth(4).prefHeight(4);
	}
	
	private void setupTopPanel(Table table) {
		Table topPanel = new Table();
		table.add(topPanel).fillX().expandX();
		topPanel.add(new NiceLabel(I18N.translate("multimyth.instance.create.label.window"), skin)).pad(2);
		nameField = new TextField("", skin);
		nameField.setTextFieldListener(new TextField.TextFieldListener() {

			@Override
			public void keyTyped(TextField textField, char c) {
				nameEdited = true;
				updateButtons();
			}
			
		});
		topPanel.add(nameField).pad(2).expandX().fillX();
		topPanel.add().width(10);
		NiceButton resetNameButton = new NiceButton(I18N.translate("multimyth.reset"), skin);
		resetNameButton.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				nameEdited = false;
				select(WildermythManifest.getLatest());
			}
		});
		select(WildermythManifest.getLatest());
		topPanel.add(resetNameButton);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupMainPanel(Table table) {
		
		Table mainPanel = new Table(skin);
		table.add(mainPanel).expand().fill();
		Table left = new Table();
		Table middle = new Table(skin);
		AutoFocusingScrollPane versionPane = new AutoFocusingScrollPane(middle, skin);
		//versionPane.debugAll();
		Table header = new Table(skin);
		header.add(new Label(I18N.translate("multimyth.instance.create.header.version"), skin)).left().expandX().fillX();
		header.add(new Label(I18N.translate("multimyth.instance.create.header.type"), skin)).right();
		middle.add(header).expand().fill();
		
		List<WildermythManifest> manifests = 
			WildermythManifest.manifestStream(OS.getOS()).
			filter((manifest) -> {
				return 
					(manifest.isPublic() && manifest.isVersionKnown()) 
					||
					manifest.isLatest("unstable");
			})
			.collect(Collectors.toList());
		
		manifests.sort((Comparator)Comparator.naturalOrder().reversed()); //cast needed to make the compiler shut up
		
		Version release;
		try {
			release = Version.parse("1.0.0");
		} catch (VersionParsingException e) {
			throw new AssertionError(e);
		}
		
		manifests.forEach((manifest) -> {
			middle.row();
			VersionSelectButton versionButton = new VersionSelectButton(manifest);
			versionButton.getCell(versionButton.getLabel()).left();
			versionButton.getLabel().setText(manifest.version().replace('+', '.'));
			versionButton.getLabel().setAlignment(Align.left);
			List<String> types = new ArrayList<>();
			try {
				if(manifest.isLatest()) {
					types.add(I18N.translate("multimyth.release.latest"));
				}
				else {
					types.add(I18N.translate("multimyth.release.old"));
				}
				if(manifest.isBranch("unstable")){
					types.add(I18N.translate("multimyth.release.unstable"));
				}
				if(manifest.isPublic()) {
					
					if(release.compareTo(Version.parse(manifest.version())) <= 0) {
						types.add(I18N.translate("multimyth.release.release"));
					}
					else {
						types.add(I18N.translate("multimyth.release.beta"));
					}
				}
				StringBuilder b = new StringBuilder();
				Iterator<String> ti = types.iterator();
				while(ti.hasNext()) {
					b.append(ti.next());
					if(ti.hasNext()) {
						b.append(' ');
					}
				}
				String typeString = b.toString();
				if(typeString.isEmpty()) {
					typeString = I18N.translate("multimyth.release.unknown");
				}
				versionButton.add(new NiceLabel(typeString, versionButton.getLabel().getStyle())).right();
			}
			catch(VersionParsingException e) {
				e.printStackTrace();
			}
			middle.add(versionButton).expandX().fillX();
		});
		
		Table right = new Table();
		
		mainPanel.add(left);
		mainPanel.add(versionPane).expand().fill();
		mainPanel.add(right);
	}
	
	
	private void setupBottomPanel(Table table) {
		Table bottomPanel = new Table();
		NiceButton cancelButton = new NiceButton(I18N.translate("multimyth.cancel"), skin);
		confirmButton = new NiceButton(I18N.translate("multimyth.ok"), skin);
		
		cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	dependencies.popUpManager.removePopUp(NewInstancePopup.this);
            }
		});
		
		confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	if(!confirmButton.isDisabled()) {
            		create(new Installer<>(selectedVersion, Launcher.SAVE_DIR.resolve(nameField.getText()), true), skin);
            	}
            }
		});
		
		bottomPanel.add().expandX().fillX();
		bottomPanel.add(cancelButton.pad(4f)).right();
		bottomPanel.add().width(5f);
		bottomPanel.add(confirmButton.pad(4f)).right();
		table.add(bottomPanel).padTop(10f).expandX().fillX();
	}
	
	private void select(WildermythManifest manifest) {
		selectedVersion = manifest;
		nameField.setText(selectedVersion.version());
		updateButtons();
	}
	
	private void updateButtons() {
		if(confirmButton != null) {
			if(Files.exists(Launcher.SAVE_DIR.resolve(nameField.getText()))) {
				confirmButton.setDisabled(true);
			}
			else {
				confirmButton.setDisabled(false);
			}
		}
	}
	
	public void create(Installer installer, Skin skin) {
		if(selectedVersion == null) {
			LOGGER.warn("No version selected?!");
			return;
		}
		ChrysalisizedVault chrysalis;
		
		try {
			if(!Vault.DEFAULT.hasChrysalis(selectedVersion)) {
				AtomicReference<Thread> downloadThread = new AtomicReference<>(null);
				MessageBoxDialog downloadingWindow = new MessageBoxDialog(true, dependencies);
				downloadingWindow.setText(I18N.translate("multimyth.popup.downloading.title"), I18N.translate("multimyth.popup.downloading.text"));
				downloadingWindow.setButtons(null, I18N.translate("multimyth.cancel"));
				
				downloadingWindow.setHandler(new IMessageBoxHandler() {

					@Override
					public boolean onAffirmative() {
						throw new AssertionError();
					}

					@Override
					public boolean onNegative() {
						return true; //MessageBoxDialog closes itself
					}
					
				});
				
				//might have to set popups to not go away if something else is clicked
				
				dependencies.popUpManager.pushFront(downloadingWindow, true);
				
				downloadThread.set(
					new Thread(() -> {
						try {
							installer.install();
						}
						catch(Throwable t) {
							downloadProblem = t;
						}
						finally {
							dependencies.popUpManager.removePopUp(downloadingWindow);
							downloadFinish(skin);
						}
					}
				));

				downloadThread.get().start();
			}
			
			else {
				installer.install();
			}
		}
		catch(InstallException e) {
			e.printStackTrace(); //TODO: popup
		}
	}
	
	private void downloadFinish(Skin skin) {
		dependencies.popUpManager.pushFront(this, true);
		if(downloadProblem != null) {
			MessageBoxDialog dialog = new MessageBoxDialog(true, dependencies);
			
			downloadProblem.printStackTrace();
			dialog.setText(I18N.translate("multimyth.popup.download.title.failed"), I18N.translate("multimyth.popup.download.text.failed", downloadProblem.getMessage()));
			
			dialog.setButtons(I18N.translate("multimyth.ok"), null);
			
			dialog.setHandler(new IMessageBoxHandler() {

				@Override
				public boolean onAffirmative() {
					return true; //MessageBoxDialog closes itself
				}

				@Override
				public boolean onNegative() {
					throw new AssertionError();
				}
				
			});
			
			dependencies.popUpManager.pushFront(dialog, true);

		}
	}
	
	private class VersionSelectButton extends NiceButton {
		
		private final WildermythManifest manifest;
		
		public VersionSelectButton(WildermythManifest manifest) {
			super("", skin);
			this.manifest = manifest;
			this.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					NewInstancePopup.this.select(manifest);
				}
			});
			
		}
		
		public WildermythManifest getManifest() {
			return manifest;
		}
	}
	
}

