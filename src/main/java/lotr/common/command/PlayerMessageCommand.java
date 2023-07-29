package lotr.common.command;

import java.util.*;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import lotr.common.command.arguments.PlayerMessageTypeArgument;
import lotr.common.data.PlayerMessageType;
import net.minecraft.command.*;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class PlayerMessageCommand extends LOTRBaseCommand {
	public static void register(CommandDispatcher dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("lotr_message").requires(context -> context.hasPermission(2))).then(Commands.argument("targets", EntityArgument.players()).then(Commands.literal("preset").then(Commands.argument("type", PlayerMessageTypeArgument.messageType()).executes(context -> sendPresetMessage((CommandSource) context.getSource(), EntityArgument.getPlayers(context, "targets"), PlayerMessageTypeArgument.getMessageType(context, "type"))))).then(Commands.literal("custom").then(Commands.argument("text", StringArgumentType.string()).executes(context -> sendCustomMessage(context.getSource(), EntityArgument.getPlayers(context, "targets"), StringArgumentType.getString(context, "text")))))));
	}

	private static int sendCustomMessage(CommandSource source, Collection players, String customText) {
		int sent = 0;

		for (Iterator var4 = players.iterator(); var4.hasNext(); ++sent) {
			ServerPlayerEntity player = (ServerPlayerEntity) var4.next();
			PlayerMessageType.displayCustomMessageTo(player, true, customText);
		}

		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.message.custom.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), customText), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.message.custom.multiple", players.size(), customText), true);
		}

		return sent;
	}

	private static int sendPresetMessage(CommandSource source, Collection players, PlayerMessageType messageType) {
		int sent = 0;

		for (Iterator var4 = players.iterator(); var4.hasNext(); ++sent) {
			ServerPlayerEntity player = (ServerPlayerEntity) var4.next();
			messageType.displayTo(player, true);
		}

		if (players.size() == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.message.preset.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName(), messageType.getSaveName()), true);
		} else {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.message.preset.multiple", players.size(), messageType.getSaveName()), true);
		}

		return sent;
	}
}
