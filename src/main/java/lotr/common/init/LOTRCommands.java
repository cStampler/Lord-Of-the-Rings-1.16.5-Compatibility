package lotr.common.init;

import com.mojang.brigadier.CommandDispatcher;

import lotr.common.command.*;

public class LOTRCommands {
	public static void registerCommands(CommandDispatcher dispatcher) {
		LOTRTimeCommand.register(dispatcher);
		WeatherCommandFixedForDimensions.register(dispatcher);
		WaypointRegionsCommand.register(dispatcher);
		WaypointCooldownCommand.register(dispatcher);
		FastTravelClockCommand.register(dispatcher);
		DateCommand.register(dispatcher);
		AlignmentCommand.register(dispatcher);
		PlayerMessageCommand.register(dispatcher);
		PledgeCommand.register(dispatcher);
	}
}
