package xyz.reselim.worldgenbot.helpers;

import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public final class ConfigHelper {
	public static Config CONFIG;

	public static void load() {
		try {
			CONFIG = new Gson().fromJson(
				new JsonReader(new FileReader(PathHelper.config())),
				Config.class
			);
		} catch(Exception error) {
			throw new RuntimeException(error);
		}
	}

	public static class Config {
		public ConfigTwitter twitter;
		public ConfigVideo video;
		public ConfigOverlay overlay;

		public static class ConfigTwitter {
			public String apiKey;
			public String apiKeySecret;
			public String accessToken;
			public String accessTokenSecret;
		}

		public static class ConfigVideo {
			public int duration;
			public int width;
			public int height;
			public int frameRate;
			public int scale;

			public int getFrameCount() {
				return frameRate * duration;
			}

			public int getScaledWidth() {
				return width * scale;
			}

			public int getScaledHeight() {
				return height * scale;
			}
		}

		public static class ConfigOverlay {
			public int scale;
			public String watermark;
		}
	}
}
