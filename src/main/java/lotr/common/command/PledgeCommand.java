package lotr.common.command;

import java.util.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import lotr.common.command.arguments.FactionArgument;
import lotr.common.data.AlignmentDataModule;
import lotr.common.fac.Faction;
import lotr.common.util.LOTRUtil;
import net.minecraft.command.*;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;

public class PledgeCommand extends LOTRBaseCommand {
	private static int breakPledge(CommandSource source, Collection players) throws CommandSyntaxException {
		int playersUnpledged = 0;
		Map brokenPledgesTo = new HashMap();
		Iterator var4 = players.iterator();

		while (var4.hasNext()) {
			ServerPlayerEntity player = (ServerPlayerEntity) var4.next();
			AlignmentDataModule alignData = getAlignData(player);
			Faction wasPledgedTo = alignData.getPledgeFaction();
			if (wasPledgedTo == null) {
				if (players.size() == 1) {
					throw new CommandException(new TranslationTextComponent("commands.lotr.pledge.break.failure.none", player.getDisplayName()));
				}
			} else {
				brokenPledgesTo.put(player, wasPledgedTo);
				alignData.revokePledgeFaction(player, true);
				++playersUnpledged;
			}
		}

		if (players.size() == 1) {
			ServerPlayerEntity onePlayer = (ServerPlayerEntity) players.iterator().next();
			source.sendSuccess(new TranslationTextComponent("commands.lotr.pledge.break.success.single", onePlayer.getDisplayName(), ((Faction) brokenPledgesTo.get(onePlayer)).getColoredDisplayName()), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.pledge.break.success.multiple", players.size()), true);
		}

		return playersUnpledged;
	}

	private static int queryPledge(CommandSource source, ServerPlayerEntity player) {
		Faction pledgeFaction = getAlignData(player).getPledgeFaction();
		if (pledgeFaction != null) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.pledge.query.pledged", player.getDisplayName(), pledgeFaction.getColoredDisplayName()), false);
			return 1;
		}
		source.sendSuccess(new TranslationTextComponent("commands.lotr.pledge.query.none", player.getDisplayName()), false);
		return 0;
	}

	public static void register(CommandDispatcher dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("pledge").requires(context -> context.hasPermission(2))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder) Commands.argument("faction", FactionArgument.faction()).executes(context -> setPledge((CommandSource) context.getSource(), EntityArgument.getPlayers(context, "targets"), FactionArgument.getFaction(context, "faction"), false))).then(Commands.literal("force").executes(context -> setPledge(context.getSource(), EntityArgument.getPlayers(context, "targets"), FactionArgument.getFaction(context, "faction"), true))))))).then(Commands.literal("break").then(Commands.argument("targets", EntityArgument.players()).executes(context -> breakPledge(context.getSource(), EntityArgument.getPlayers(context, "targets")))))).then(Commands.literal("cooldown").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("seconds", IntegerArgumentType.integer(0, 1000000)).executes(context -> setPledgeCooldown(context.getSource(), EntityArgument.getPlayers(context, "targets"), IntegerArgumentType.getInteger(context, "seconds"))))))).then(Commands.literal("query").then(Commands.argument("target", EntityArgument.player()).executes(context -> queryPledge(context.getSource(), EntityArgument.getPlayer(context, "target"))))));
	}

	private static int setPledge(CommandSource source, Collection players, Faction faction, boolean force) throws CommandSyntaxException {
		int playersPledged = 0;
		Iterator var5 = players.iterator();

		ServerPlayerEntity player;
		label39: do {
			while (var5.hasNext()) {
				player = (ServerPlayerEntity) var5.next();
				AlignmentDataModule alignData = getAlignData(player);
				if (!alignData.isValidPledgeFaction(faction)) {
					throw new CommandException(new TranslationTextComponent("commands.lotr.pledge.set.failure.invalid", faction.getDisplayName()));
				}

				if (!force && !alignData.canPledgeToNow(faction)) {
					continue label39;
				}

				if (alignData.getPledgeFaction() != null) {
					alignData.revokePledgeFaction(player, true);
				}

				if (force) {
					alignData.setAlignment(faction, faction.getPledgeAlignment());
				}

				alignData.setPledgeFaction(faction);
				++playersPledged;
			}

			if (players.size() == 1) {
				source.sendSuccess(new TranslationTextComponent("commands.lotr.pledge.set.success.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), faction.getColoredDisplayName()), true);
			} else {
				source.sendSuccess(new TranslationTextComponent("commands.lotr.pledge.set.success.multiple", players.size(), faction.getColoredDisplayName()), true);
			}

			return playersPledged;
		} while (players.size() != 1);

		throw new CommandException(new TranslationTextComponent("commands.lotr.pledge.set.failure.requirements", player.getDisplayName(), faction.getDisplayName()));
	}

	private static int setPledgeCooldown(CommandSource source, Collection players, int seconds) {
		ITextComponent hmsTime = LOTRUtil.getHMSTime_Seconds(seconds);
		int ticks = seconds * 20;
		int cooldownsSet = 0;

		for (Iterator var6 = players.iterator(); var6.hasNext(); ++cooldownsSet) {
			ServerPlayerEntity player = (ServerPlayerEntity) var6.next();
			getAlignData(player).setPledgeBreakCooldown(ticks);
		}

		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.pledge.cooldown.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), seconds, hmsTime), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.pledge.cooldown.multiple", players.size(), seconds, hmsTime), true);
		}

		return cooldownsSet;
	}
}
