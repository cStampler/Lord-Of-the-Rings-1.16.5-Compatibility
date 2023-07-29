package lotr.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import lotr.common.util.LOTRUtil;
import net.minecraft.command.*;
import net.minecraft.util.text.TranslationTextComponent;

public class WaypointCooldownCommand extends LOTRBaseCommand {
	public static void register(CommandDispatcher dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("wpcooldown").requires(context -> context.hasPermission(2))).then(Commands.literal("max").then(Commands.argument("seconds", IntegerArgumentType.integer(0, 86400)).executes(context -> setMaxCooldown(context.getSource(), IntegerArgumentType.getInteger(context, "seconds")))))).then(Commands.literal("min").then(Commands.argument("seconds", IntegerArgumentType.integer(0, 86400)).executes(context -> setMinCooldown(context.getSource(), IntegerArgumentType.getInteger(context, "seconds"))))));
	}

	private static int setMaxCooldown(CommandSource source, int max) {
		int min = getLevelData().getWaypointCooldownMin();
		boolean updatedMin = false;
		if (max < min) {
			min = max;
			updatedMin = true;
		}

		getLevelData().setWaypointCooldown(source.getLevel(), max, min);
		source.sendSuccess(new TranslationTextComponent("commands.lotr.wpcooldown.max.set", max, LOTRUtil.getHMSTime_Seconds(max)), true);
		if (updatedMin) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpcooldown.max.updatedMin", min), true);
		}

		return 1;
	}

	private static int setMinCooldown(CommandSource source, int min) {
		int max = getLevelData().getWaypointCooldownMax();
		boolean updatedMax = false;
		if (min > max) {
			max = min;
			updatedMax = true;
		}

		getLevelData().setWaypointCooldown(source.getLevel(), max, min);
		source.sendSuccess(new TranslationTextComponent("commands.lotr.wpcooldown.min.set", max, LOTRUtil.getHMSTime_Seconds(min)), true);
		if (updatedMax) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpcooldown.min.updatedMax", min), true);
		}

		return 1;
	}
}
