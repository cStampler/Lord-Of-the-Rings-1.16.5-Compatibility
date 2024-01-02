package lotr.common.init;

import com.mojang.brigadier.CommandDispatcher;

import lotr.common.command.AlignmentCommand;
import lotr.common.command.DateCommand;
import lotr.common.command.FastTravelClockCommand;
import lotr.common.command.LOTRTimeCommand;
import lotr.common.command.PlayerMessageCommand;
import lotr.common.command.PledgeCommand;
import lotr.common.command.WaypointCooldownCommand;
import lotr.common.command.WaypointRegionsCommand;
import lotr.common.command.WeatherCommandFixedForDimensions;
import net.minecraft.command.CommandSource;

public class LOTRCommands {
	public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
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
