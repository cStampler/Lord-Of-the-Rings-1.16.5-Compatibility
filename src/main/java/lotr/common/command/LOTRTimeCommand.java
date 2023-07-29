package lotr.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import lotr.common.command.arguments.LOTRTimeArgument;
import lotr.common.time.LOTRTime;
import net.minecraft.command.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class LOTRTimeCommand {
	public static int addTime(CommandSource source, int amount) {
		LOTRTime.addWorldTime(source.getLevel(), amount);
		int i = getModuloDayTime(source.getLevel());
		source.sendSuccess(new TranslationTextComponent("commands.time.set", i), true);
		return i;
	}

	private static int getModuloDayTime(ServerWorld world) {
		return (int) (getTotalDayTime(world) % 48000L);
	}

	private static long getTotalDayTime(ServerWorld world) {
		return LOTRTime.getWorldTime(world);
	}

	public static void register(CommandDispatcher dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("lotr_time").requires(context -> context.hasPermission(2))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("set").then(Commands.literal("day").executes(context -> setTime(context.getSource(), 1680)))).then(Commands.literal("noon").executes(context -> setTime(context.getSource(), 12000)))).then(Commands.literal("night").executes(context -> setTime(context.getSource(), 25920)))).then(Commands.literal("midnight").executes(context -> setTime(context.getSource(), 36000)))).then(Commands.argument("time", LOTRTimeArgument.create()).executes(context -> setTime(context.getSource(), IntegerArgumentType.getInteger(context, "time")))))).then(Commands.literal("add").then(Commands.argument("time", LOTRTimeArgument.create()).executes(context -> addTime(context.getSource(), IntegerArgumentType.getInteger(context, "time")))))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("query").then(Commands.literal("daytime").executes(context -> sendQueryResults(context.getSource(), getModuloDayTime(context.getSource().getLevel()))))).then(Commands.literal("gametime").executes(context -> sendQueryResults(context.getSource(), (int) (context.getSource().getLevel().getGameTime() % 2147483647L))))).then(Commands.literal("day").executes(context -> sendQueryResults(context.getSource(), (int) (getTotalDayTime(context.getSource().getLevel()) / 48000L % 2147483647L))))));
	}

	private static int sendQueryResults(CommandSource source, int time) {
		source.sendSuccess(new TranslationTextComponent("commands.time.query", time), false);
		return time;
	}

	public static int setTime(CommandSource source, int time) {
		LOTRTime.setWorldTime(source.getLevel(), time);
		source.sendSuccess(new TranslationTextComponent("commands.time.set", time), true);
		return getModuloDayTime(source.getLevel());
	}
}
