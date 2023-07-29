package lotr.common.command;

import java.util.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import lotr.common.command.arguments.WaypointRegionArgument;
import lotr.common.data.FastTravelDataModule;
import lotr.common.world.map.WaypointRegion;
import net.minecraft.command.*;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class WaypointRegionsCommand extends LOTRBaseCommand {
	private static int lockAllRegions(CommandSource source, Collection players) throws CommandSyntaxException {
		int locked = 0;
		Iterator var3 = players.iterator();

		while (var3.hasNext()) {
			ServerPlayerEntity player = (ServerPlayerEntity) var3.next();
			FastTravelDataModule ftData = getLevelData().getData(player).getFastTravelData();
			boolean lockedAny = false;
			Iterator var7 = currentLoadedMap().getWaypointRegions().iterator();

			while (var7.hasNext()) {
				WaypointRegion region = (WaypointRegion) var7.next();
				if (ftData.isWaypointRegionUnlocked(region)) {
					ftData.lockWaypointRegion(region);
					lockedAny = true;
				}
			}

			if (lockedAny) {
				++locked;
			}
		}

		if (locked == 0) {
			if (players.size() == 1) {
				throw new CommandException(new TranslationTextComponent("commands.lotr.wpregion.lock.all.failure.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName()));
			}
			throw new CommandException(new TranslationTextComponent("commands.lotr.wpregion.lock.all.failure.multiple", players.size()));
		}
		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpregion.lock.all.success.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName()), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpregion.lock.all.success.multiple", players.size()), true);
		}

		return locked;
	}

	private static int lockRegion(CommandSource source, Collection players, WaypointRegion region) throws CommandSyntaxException {
		int locked = 0;
		Iterator var4 = players.iterator();

		while (var4.hasNext()) {
			ServerPlayerEntity player = (ServerPlayerEntity) var4.next();
			FastTravelDataModule ftData = getLevelData().getData(player).getFastTravelData();
			if (ftData.isWaypointRegionUnlocked(region)) {
				ftData.lockWaypointRegion(region);
				++locked;
			}
		}

		if (locked == 0) {
			if (players.size() == 1) {
				throw new CommandException(new TranslationTextComponent("commands.lotr.wpregion.lock.failure.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), region.getName()));
			}
			throw new CommandException(new TranslationTextComponent("commands.lotr.wpregion.lock.failure.multiple", players.size(), region.getName()));
		}
		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpregion.lock.success.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), region.getName()), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpregion.lock.success.multiple", players.size(), region.getName()), true);
		}

		return locked;
	}

	public static void register(CommandDispatcher dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("wpregion").requires(context -> context.hasPermission(2))).then(Commands.literal("unlock").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("region", WaypointRegionArgument.waypointRegion()).executes(context -> unlockRegion((CommandSource) context.getSource(), EntityArgument.getPlayers(context, "targets"), WaypointRegionArgument.getRegion(context, "region")))).then(Commands.literal("all").executes(context -> unlockAllRegions(context.getSource(), EntityArgument.getPlayers(context, "targets"))))))).then(Commands.literal("lock").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("region", WaypointRegionArgument.waypointRegion()).executes(context -> lockRegion((CommandSource) context.getSource(), EntityArgument.getPlayers(context, "targets"), WaypointRegionArgument.getRegion(context, "region")))).then(Commands.literal("all").executes(context -> lockAllRegions(context.getSource(), EntityArgument.getPlayers(context, "targets")))))));
	}

	private static int unlockAllRegions(CommandSource source, Collection players) throws CommandSyntaxException {
		int unlocked = 0;
		Iterator var3 = players.iterator();

		while (var3.hasNext()) {
			ServerPlayerEntity player = (ServerPlayerEntity) var3.next();
			FastTravelDataModule ftData = getLevelData().getData(player).getFastTravelData();
			boolean unlockedAny = false;
			Iterator var7 = currentLoadedMap().getWaypointRegions().iterator();

			while (var7.hasNext()) {
				WaypointRegion region = (WaypointRegion) var7.next();
				if (!ftData.isWaypointRegionUnlocked(region)) {
					ftData.unlockWaypointRegion(region);
					unlockedAny = true;
				}
			}

			if (unlockedAny) {
				++unlocked;
			}
		}

		if (unlocked == 0) {
			if (players.size() == 1) {
				throw new CommandException(new TranslationTextComponent("commands.lotr.wpregion.unlock.all.failure.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName()));
			}
			throw new CommandException(new TranslationTextComponent("commands.lotr.wpregion.unlock.all.failure.multiple", players.size()));
		}
		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpregion.unlock.all.success.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName()), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpregion.unlock.all.success.multiple", players.size()), true);
		}

		return unlocked;
	}

	private static int unlockRegion(CommandSource source, Collection players, WaypointRegion region) throws CommandSyntaxException {
		int unlocked = 0;
		Iterator var4 = players.iterator();

		while (var4.hasNext()) {
			ServerPlayerEntity player = (ServerPlayerEntity) var4.next();
			FastTravelDataModule ftData = getLevelData().getData(player).getFastTravelData();
			if (!ftData.isWaypointRegionUnlocked(region)) {
				ftData.unlockWaypointRegion(region);
				++unlocked;
			}
		}

		if (unlocked == 0) {
			if (players.size() == 1) {
				throw new CommandException(new TranslationTextComponent("commands.lotr.wpregion.unlock.failure.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), region.getName()));
			}
			throw new CommandException(new TranslationTextComponent("commands.lotr.wpregion.unlock.failure.multiple", players.size(), region.getName()));
		}
		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpregion.unlock.success.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), region.getName()), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.wpregion.unlock.success.multiple", players.size(), region.getName()), true);
		}

		return unlocked;
	}
}
