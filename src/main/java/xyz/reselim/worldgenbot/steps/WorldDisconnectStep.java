package xyz.reselim.worldgenbot.steps;

import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Next;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;

public class WorldDisconnectStep implements Step {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public void perform(Context context, Next next) {
		if (CLIENT.world != null) {
			CLIENT.world.disconnect();
		}
		CLIENT.disconnect(new TitleScreen());

		next.next();
	}
}
