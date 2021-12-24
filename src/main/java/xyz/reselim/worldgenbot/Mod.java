package xyz.reselim.worldgenbot;

import net.fabricmc.api.ModInitializer;
import xyz.reselim.worldgenbot.helpers.ConfigHelper;
import xyz.reselim.worldgenbot.steps.PostStep;
import xyz.reselim.worldgenbot.steps.RecordStep;
import xyz.reselim.worldgenbot.steps.Step;
import xyz.reselim.worldgenbot.steps.StopStep;
import xyz.reselim.worldgenbot.steps.TranscodeStep;
import xyz.reselim.worldgenbot.steps.WorldCreateStep;
import xyz.reselim.worldgenbot.steps.WorldDeleteStep;
import xyz.reselim.worldgenbot.steps.WorldDisconnectStep;
import xyz.reselim.worldgenbot.steps.WorldFetchStep;
import xyz.reselim.worldgenbot.steps.WorldRenderStep;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("worldgenbot");

	private int currentStep = -1;

	private static final List<Step> steps = List.of(
		new WorldCreateStep(),
		new WorldFetchStep(),
		new WorldRenderStep(),
		new RecordStep(),
		new TranscodeStep(),
		new PostStep(),
		new WorldDisconnectStep(),
		new WorldDeleteStep(),
		new StopStep()
	);

	private static final List<String> stepNames = List.of(
		"World Create",
		"World Render",
		"Record",
		"Transcode",
		"Post",
		"World Disconnect",
		"World Delete",
		"Stop"
	);
	
	@Override
	public void onInitialize() {
		ConfigHelper.load();
		
		ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
			next(new Context());
		});
	}

	public void next(Context context) {
		currentStep++;

		if (currentStep < steps.size()) {
			LOGGER.info(String.format("Running step %d/%d (%s)", currentStep + 1, steps.size(), stepNames.get(currentStep)));
			steps.get(currentStep).perform(context, () -> next(context));
		}
	}
}
