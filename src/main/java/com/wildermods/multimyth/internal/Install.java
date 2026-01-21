package com.wildermods.multimyth.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.wildermods.multimyth.Launcher;
import com.wildermods.thrixlvault.steam.IDownloadable;
import com.wildermods.thrixlvault.steam.IGame;
import com.wildermods.thrixlvault.wildermyth.WildermythManifest;

public record Install<T extends IDownloadable & IGame> (int schema, T installableGame, Path installPath, JVMInstance java, boolean isCoremodded, boolean visible) implements IInstall {

	public static final int CURRENT_SCHEMA = 1;
	
	public Install(T game, Path installPath, JVMInstance java, boolean isCoremodded, boolean visible) {
		this(CURRENT_SCHEMA, game, installPath, java, isCoremodded, visible);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends IDownloadable & IGame> Install<T> fromFile(Path p) throws JsonParseException {
		try {
			Gson gson = Launcher.GSON;
			JsonElement e = JsonParser.parseReader(gson.newJsonReader(Files.newBufferedReader(p)));
			if(e instanceof JsonObject) {
				JsonObject o = (JsonObject) e;
				int schema = o.get("schema").getAsInt();
				if(schema != CURRENT_SCHEMA) {
					throw new JsonParseException("Cannot parse schema version " + schema);
				}
				else { //schema == 1
					String gameName = o.get("game").getAsString();
					Path installPath = p.getParent();
					if(gameName.equals(WildermythManifest.GAME_NAME)) {
						GameOnDisk game = new GameOnDisk(installPath);
						JVMInstance jvm = gson.fromJson(o.get("jvm"), JVMInstance.class);
						boolean isCoremodded = o.get("isCoremodded").getAsBoolean();
						boolean visible = o.get("visible").getAsBoolean();
						return new Install<T>(CURRENT_SCHEMA, (T)game, installPath, jvm, isCoremodded, visible);
					}
					else {
						throw new UnsupportedOperationException("Don't know how to handle game: " + gameName);
					}
				}
	
			}
			else {
				throw new JsonParseException("Not a json object");
			}
		}
		catch(JsonParseException e) {
			throw e;
		}
		catch(Throwable t) {
			throw new JsonParseException(t);
		}
	}
	
	public String asJson() {
		Gson gson = Launcher.GSON;
		JsonObject o = new JsonObject();
		o.addProperty("schema", CURRENT_SCHEMA);
		o.addProperty("game", installableGame.gameName());
		o.add("jvm", gson.toJsonTree(java));
		o.addProperty("isCoremodded", isCoremodded);
		o.addProperty("visible", visible);
		return o.toString();
	}
	
	public Path getMetadataPath() {
		return installPath.resolve("instance.mm");
	}
	
	public void serialize(OpenOption... options) throws IOException {
		serialize(getMetadataPath(), options);
	}
	
	public void serialize(Path path, OpenOption... options) throws IOException {
		Files.write(path, asJson().getBytes(), options);
	}

	@Override
	public String name() {
		return installPath.getFileName().toString();
	}

	@Override
	public Path artifactPath() {
		throw new UnsupportedOperationException("Refusing to vault");
	}

	@Override
	public long game() {
		return installableGame.game();
	}
	
	public boolean exists() {
		return Files.exists(installPath) && Files.exists(getMetadataPath());
	}
	
}
