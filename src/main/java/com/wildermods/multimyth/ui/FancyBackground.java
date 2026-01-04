package com.wildermods.multimyth.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.worldwalkergames.legacy.context.ClientDataContext;

public class FancyBackground {
	private static final Path assets = Path.of(".").resolve("assets");
	
	final List<Path> backgrounds = new ArrayList<>();
	final ClientDataContext context;
	public FancyBackground(ClientDataContext context) {
		
		Path comics = assets.resolve("ui").resolve("comics");
		try {
			Files.walk(comics).filter((path) -> {
				return path.getFileName().toString().startsWith("comicBG_");
			}).forEach((path) -> {
				backgrounds.add(path);
			});
		} catch (IOException e) {
			throw new Error(e);
		}
		this.context = context;
	}
	
	public Texture get() {
		Random random = new Random();
		String picked = backgrounds.get(random.ints(0, backgrounds.size()).findAny().getAsInt()).toString();
		return context.loadLocalizedTextureNoCache(picked.substring(2, picked.length() - 4), ".jpg");
	}
	
}
