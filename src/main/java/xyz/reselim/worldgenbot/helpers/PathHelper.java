package xyz.reselim.worldgenbot.helpers;

import java.io.File;
import java.nio.file.Path;

import net.minecraft.client.MinecraftClient;

public final class PathHelper {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static File videoDirectOutput() {
		return Path.of(CLIENT.runDirectory.toString(), "video.mp4").toFile();
	}

	public static File videoTranscodedOutput() {
		return Path.of(CLIENT.runDirectory.toString(), "transcodedVideo.mp4").toFile();
	}

	public static File config() {
		return Path.of(CLIENT.runDirectory.toString(), "config/worldgenbot.json").toFile();
	}
}
