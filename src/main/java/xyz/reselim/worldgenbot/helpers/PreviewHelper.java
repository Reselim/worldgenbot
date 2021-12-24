package xyz.reselim.worldgenbot.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public final class PreviewHelper {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	public static void step(int frame) {
		ClientPlayerEntity player = CLIENT.player;

		float progress = ((float) frame) / ((float) ConfigHelper.CONFIG.video.getFrameCount());
		player.setYaw(progress * 360);
		player.setPitch(0);
	}
}
