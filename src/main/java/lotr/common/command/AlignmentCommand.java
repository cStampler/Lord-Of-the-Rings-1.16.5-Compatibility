package lotr.common.command;

import java.util.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import lotr.common.command.arguments.FactionArgument;
import lotr.common.fac.Faction;
import net.minecraft.command.*;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;

public class AlignmentCommand extends LOTRBaseCommand {
	private static int addAlignment(CommandSource source, Collection players, Faction faction, float addAmount) throws CommandSyntaxException {
		int playersChanged = 0;

		for (Iterator var5 = players.iterator(); var5.hasNext(); ++playersChanged) {
			ServerPlayerEntity player = (ServerPlayerEntity) var5.next();
			checkResultingAlignmentWithinBounds(player, faction, addAmount);
			getAlignData(player).addAlignment(faction, addAmount);
		}

		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.alignment.add.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), faction.getColoredDisplayName(), getAmountComponent(addAmount)), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.alignment.add.multiple", players.size(), faction.getColoredDisplayName(), getAmountComponent(addAmount)), true);
		}

		return playersChanged;
	}

	private static int addAllAlignments(CommandSource source, Collection players, float addAmount) throws CommandSyntaxException {
		int playersChanged = 0;

		for (Iterator var4 = players.iterator(); var4.hasNext(); ++playersChanged) {
			ServerPlayerEntity player = (ServerPlayerEntity) var4.next();
			Iterator var6 = allFactions().iterator();

			while (var6.hasNext()) {
				Faction faction = (Faction) var6.next();
				checkResultingAlignmentWithinBounds(player, faction, addAmount);
				getAlignData(player).addAlignment(faction, addAmount);
			}
		}

		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.alignment.add.all.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), getAmountComponent(addAmount)), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.alignment.add.all.multiple", players.size(), getAmountComponent(addAmount)), true);
		}

		return playersChanged;
	}

	private static FloatArgumentType alignmentSetValueArgument() {
		return FloatArgumentType.floatArg(-10000.0F, 10000.0F);
	}

	private static void checkResultingAlignmentWithinBounds(ServerPlayerEntity player, Faction faction, float addAmount) throws CommandSyntaxException {
		float currentAmount = getAlignData(player).getAlignment(faction);
		if (currentAmount + addAmount > 10000.0F) {
			throw new CommandException(new TranslationTextComponent("commands.lotr.alignment.add.tooHigh", player.getDisplayName(), faction.getDisplayName(), 10000.0F));
		}
		if (currentAmount + addAmount < -10000.0F) {
			throw new CommandException(new TranslationTextComponent("commands.lotr.alignment.add.tooLow", player.getDisplayName(), faction.getDisplayName(), -10000.0F));
		}
	}

	private static ITextComponent getAmountComponent(float amount) {
		IFormattableTextComponent text = new StringTextComponent(String.valueOf(amount));
		if (amount > 0.0F) {
			text.withStyle(TextFormatting.GREEN);
		} else if (amount < 0.0F) {
			text.withStyle(TextFormatting.RED);
		}

		return text;
	}

	private static int queryAlignment(CommandSource source, ServerPlayerEntity player, Faction faction) {
		float currentAmount = getAlignData(player).getAlignment(faction);
		source.sendSuccess(new TranslationTextComponent("commands.lotr.alignment.query", player.getDisplayName(), faction.getColoredDisplayName(), getAmountComponent(currentAmount)), false);
		return Math.round(currentAmount);
	}

	public static void register(CommandDispatcher dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("alignment").requires(context -> context.hasPermission(2))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("faction", FactionArgument.faction()).then(Commands.argument("amount", alignmentSetValueArgument()).executes(context -> setAlignment(context.getSource(), EntityArgument.getPlayers(context, "targets"), FactionArgument.getFaction(context, "faction"), FloatArgumentType.getFloat(context, "amount"))))).then(Commands.literal("all").then(Commands.argument("amount", alignmentSetValueArgument()).executes(context -> setAllAlignments(context.getSource(), EntityArgument.getPlayers(context, "targets"), FloatArgumentType.getFloat(context, "amount")))))))).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("faction", FactionArgument.faction()).then(Commands.argument("amount", FloatArgumentType.floatArg()).executes(context -> addAlignment(context.getSource(), EntityArgument.getPlayers(context, "targets"), FactionArgument.getFaction(context, "faction"), FloatArgumentType.getFloat(context, "amount"))))).then(Commands.literal("all").then(Commands.argument("amount", FloatArgumentType.floatArg()).executes(context -> addAllAlignments(context.getSource(), EntityArgument.getPlayers(context, "targets"), FloatArgumentType.getFloat(context, "amount")))))))).then(Commands.literal("query").then(Commands.argument("target", EntityArgument.player()).then(Commands.argument("faction", FactionArgument.faction()).executes(context -> queryAlignment((CommandSource) context.getSource(), EntityArgument.getPlayer(context, "target"), FactionArgument.getFaction(context, "faction")))))));
	}

	private static int setAlignment(CommandSource source, Collection players, Faction faction, float amount) throws CommandSyntaxException {
		int playersChanged = 0;

		for (Iterator var5 = players.iterator(); var5.hasNext(); ++playersChanged) {
			ServerPlayerEntity player = (ServerPlayerEntity) var5.next();
			getAlignData(player).setAlignment(faction, amount);
		}

		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.alignment.set.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), faction.getColoredDisplayName(), getAmountComponent(amount)), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.alignment.set.multiple", players.size(), faction.getColoredDisplayName(), getAmountComponent(amount)), true);
		}

		return playersChanged;
	}

	private static int setAllAlignments(CommandSource source, Collection players, float amount) throws CommandSyntaxException {
		int playersChanged = 0;

		for (Iterator var4 = players.iterator(); var4.hasNext(); ++playersChanged) {
			ServerPlayerEntity player = (ServerPlayerEntity) var4.next();
			Iterator var6 = allFactions().iterator();

			while (var6.hasNext()) {
				Faction faction = (Faction) var6.next();
				getAlignData(player).setAlignment(faction, amount);
			}
		}

		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.alignment.set.all.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), getAmountComponent(amount)), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.alignment.set.all.multiple", players.size(), getAmountComponent(amount)), true);
		}

		return playersChanged;
	}
}
