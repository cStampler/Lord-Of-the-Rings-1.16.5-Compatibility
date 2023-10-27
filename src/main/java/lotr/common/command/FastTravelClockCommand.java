package lotr.common.command;

import java.util.Collection;
import java.util.Iterator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import lotr.common.data.FastTravelDataModule;
import lotr.common.util.LOTRUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class FastTravelClockCommand extends LOTRBaseCommand {
	public static void register(CommandDispatcher dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("ftclock").requires(context -> context.hasPermission(2))).then(((RequiredArgumentBuilder) Commands.argument("targets", EntityArgument.players()).then(Commands.argument("seconds", IntegerArgumentType.integer(0, 1000000)).executes(context -> setFTClock(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "seconds"))))).then(Commands.literal("max").executes(context -> setFTClock(context.getSource(), EntityArgument.getPlayers(context, "targets"), 1000000)))));
	}

	private static int setFTClock(CommandSource source, Collection players, int seconds) {
		ITextComponent hmsTime = LOTRUtil.getHMSTime_Seconds(seconds);
		int ticks = seconds * 20;
		int clocked = 0;

		for (Iterator var6 = players.iterator(); var6.hasNext(); ++clocked) {
			ServerPlayerEntity player = (ServerPlayerEntity) var6.next();
			FastTravelDataModule ftData = getPlayerData(player).getFastTravelData();
			ftData.setTimeSinceFTWithUpdate(ticks);
		}

		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.ftclock.set.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), seconds, hmsTime), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.ftclock.set.multiple", players.size(), seconds, hmsTime), true);
		}

		return clocked;
	}
}
