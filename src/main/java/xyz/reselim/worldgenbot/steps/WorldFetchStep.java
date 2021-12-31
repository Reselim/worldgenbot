package xyz.reselim.worldgenbot.steps;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.client.MinecraftClient;
import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Next;

public class WorldFetchStep implements Step {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	private boolean finished = false;

	public void perform(Context context, Next next) {
		ClientChunkEvents.CHUNK_LOAD.register((clientWorld, chunk) -> {
			if (!finished) {
				// For some reason, this equation only works with view distance 12
				int viewDiameter = CLIENT.options.viewDistance * 2 + 1;
				int chunkCount = clientWorld.getChunkManager().getLoadedChunkCount();
				int requiredChunkCount = viewDiameter * (viewDiameter - 1);

				if (chunkCount >= requiredChunkCount) {
					finished = true;
					next.next();
				}
			}
		});
	}
}
