package lotr.common.command;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import lotr.common.time.LOTRDate;
import lotr.common.time.MiddleEarthCalendar;
import lotr.common.time.ShireReckoning;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DateCommand extends LOTRBaseCommand {
	public static final int MAX_DATE = 1000000;
	public static final int MIN_DATE = -1000000;

	private static int addToCurrentDate(CommandSource source, int addDate) {
		int currentDate = MiddleEarthCalendar.currentDay;
		int newDate = currentDate + addDate;
		if (newDate >= -1000000 && newDate <= 1000000) {
			LOTRDate.setDate(source.getLevel(), newDate);
			ITextComponent dateName = ShireReckoning.INSTANCE.getCurrentDateAndYearShortform();
			source.sendSuccess(new TranslationTextComponent("commands.lotr.lotr_date.add", addDate, newDate, dateName), true);
			return newDate;
		}
		throw new CommandException(new TranslationTextComponent("commands.lotr.lotr_date.add.failure.outOfBounds", newDate));
	}

	private static int displayCurrentDate(CommandSource source, Collection players) {
		players.forEach(hummel -> LOTRDate.sendDisplayPacket((ServerPlayerEntity) hummel));
		int numPlayers = players.size();
		if (numPlayers == 1) {
			source.sendSuccess(new TranslationTextComponent("commands.lotr.lotr_date.display.single", ((ServerPlayerEntity) players.iterator().next()).getDisplayName()), true);
			return 1;
		}
		source.sendSuccess(new TranslationTextComponent("commands.lotr.lotr_date.display.multiple", numPlayers), true);
		return numPlayers;
	}

	private static int queryCurrentDate(CommandSource source) {
		int date = MiddleEarthCalendar.currentDay;
		ITextComponent dateName = ShireReckoning.INSTANCE.getCurrentDateAndYearShortform();
		source.sendSuccess(new TranslationTextComponent("commands.lotr.lotr_date.get", date, dateName), false);
		return date;
	}

	public static void register(CommandDispatcher dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("lotr_date").requires(context -> context.hasPermission(2))).then(Commands.literal("get").executes(context -> queryCurrentDate(context.getSource())))).then(Commands.literal("set").then(Commands.argument("date", IntegerArgumentType.integer(-1000000, 1000000)).executes(context -> setCurrentDate(context.getSource(), IntegerArgumentType.getInteger(context, "date")))))).then(Commands.literal("add").then(Commands.argument("date", IntegerArgumentType.integer()).executes(context -> addToCurrentDate(context.getSource(), IntegerArgumentType.getInteger(context, "date")))))).then(Commands.literal("display").then(Commands.argument("targets", EntityArgument.players()).executes(context -> displayCurrentDate(context.getSource(), EntityArgument.getPlayers(context, "targets"))))));
	}

	private static int setCurrentDate(CommandSource source, int date) {
		LOTRDate.setDate(source.getLevel(), date);
		ITextComponent dateName = ShireReckoning.INSTANCE.getCurrentDateAndYearShortform();
		source.sendSuccess(new TranslationTextComponent("commands.lotr.lotr_date.set", date, dateName), true);
		return date;
	}
}
