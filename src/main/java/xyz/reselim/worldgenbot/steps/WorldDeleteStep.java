package xyz.reselim.worldgenbot.steps;

import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Next;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.world.level.storage.LevelStorage;

public class WorldDeleteStep implements Step {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public void perform(Context context, Next next) {
		LevelStorage levelStorage = CLIENT.getLevelStorage();

		try {
			LevelStorage.Session session = levelStorage.createSession(context.world);
			session.deleteSessionLock();
		} catch (Exception error) {
			CrashReport crash = CrashReport.create(error, "Failed to delete world");
			crash.addElement("World").add("Name", context.world);
			throw new CrashException(crash);
		}

		next.next();
	}
}
