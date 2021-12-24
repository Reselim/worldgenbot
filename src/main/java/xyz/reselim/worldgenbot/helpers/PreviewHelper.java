package xyz.reselim.worldgenbot.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class PreviewHelper {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	private static Vec3d initPos;

	public static void start() {
		initPos = CLIENT.player.getPos();
		CLIENT.player.setPos(initPos.x, initPos.y + 10, initPos.z);
	}

	public static void step(int frame) {
		ClientPlayerEntity player = CLIENT.player;

		float progress = ((float) frame) / ((float) ConfigHelper.CONFIG.video.getFrameCount());
		player.setYaw(progress * 360);
		player.setPitch(15f);
	}

	public static void end() {
		CLIENT.player.setPos(initPos.x, initPos.y, initPos.z);
	}
}
