package xyz.reselim.worldgenbot.steps;

import xyz.reselim.worldgenbot.Context;
import xyz.reselim.worldgenbot.Next;

public interface Step {
	public void perform(Context context, Next next);
}
