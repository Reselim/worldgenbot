package xyz.reselim.worldgenbot.steps;

import java.util.ArrayList;
import java.util.Date;

import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Next;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.text.LiteralText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;

public class WorldCreateStep implements Step {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	private boolean finished = false;

	public void perform(Context context, Next next) {
		Date now = new Date();
		String name = ((Long) now.getTime()).toString();

		context.setWorld(name);
		createWorld(name);

		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if (!finished && CLIENT.player != null) {
				finished = true;
				next.next();
			}
		});
	}

	private static DataPackSettings createDataPackSettings() {
		return new DataPackSettings(
			new ArrayList<String>(0),
			new ArrayList<String>(0)
		);
	}

	private static GameRules createGameRules() {
		return new GameRules();
	}

	private static GeneratorOptions createGeneratorOptions() {
		DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
		return GeneratorOptions.getDefaultOptions(impl);
	}

	private static LevelInfo createLevelInfo(String name) {
		GameRules gameRules = createGameRules();
		DataPackSettings dataPackSettings = createDataPackSettings();

		return new LevelInfo(name, GameMode.CREATIVE, false, Difficulty.PEACEFUL, true, gameRules, dataPackSettings);
	}

	private static String createSaveName(String name) {
		try {
			return FileNameUtil.getNextUniqueName(CLIENT.getLevelStorage().getSavesDirectory(), name == null ? "World" : name, "");
		} catch (Exception error) {
			CrashReport crash = CrashReport.create(error, "Failed to create save name");
			throw new CrashException(crash);
		}
	}

	private static void createWorld(String name) {
		LevelInfo info = createLevelInfo(name);
		GeneratorOptions options = createGeneratorOptions();
		
		DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();
		CLIENT.createWorld(createSaveName(name), info, impl, options);

		CLIENT.setScreenAndRender(new SaveLevelScreen(new LiteralText("swag ....")));
	}
}
