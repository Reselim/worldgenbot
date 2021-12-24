package xyz.reselim.worldgenbot.steps;

import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Next;
import net.minecraft.client.MinecraftClient;

public class StopStep implements Step {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public void perform(Context context, Next next) {
		CLIENT.stop();
		next.next();
	}
}
