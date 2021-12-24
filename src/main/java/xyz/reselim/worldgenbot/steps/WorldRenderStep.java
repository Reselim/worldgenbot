package xyz.reselim.worldgenbot.steps;

import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Next;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class WorldRenderStep implements Step {
	private boolean finished = false;
	
	public void perform(Context context, Next next) {
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			if (!finished && client.worldRenderer.getCompletedChunkCount() > 10 && client.worldRenderer.isTerrainRenderComplete()) {
				finished = true;
				next.next();
			}
		});
	}
}
