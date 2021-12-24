package xyz.reselim.worldgenbot;

import org.jetbrains.annotations.Nullable;

public class Context {
	@Nullable
	public String world;

	public void setWorld(String newWorld) {
		world = newWorld;
	}
}
