package lotr.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;

public class WeatherCommandFixedForDimensions {
	private static IServerWorldInfo locateOverworldInfo(CommandSource source) {
		ServerWorld world = source.getLevel().getServer().getLevel(World.OVERWORLD);
		return (IServerWorldInfo) world.getLevelData();
	}

	public static void register(CommandDispatcher dispatcher) {
		dispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) Commands.literal("weather").requires(context -> context.hasPermission(2))).then(((LiteralArgumentBuilder) Commands.literal("clear").executes(context -> setClear(context.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes(context -> setClear(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20))))).then(((LiteralArgumentBuilder) Commands.literal("rain").executes(context -> setRain(context.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes(context -> setRain(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20))))).then(((LiteralArgumentBuilder) Commands.literal("thunder").executes(context -> setThunder(context.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes(context -> setThunder(context.getSource(), IntegerArgumentType.getInteger(context, "duration") * 20)))));
	}

	private static int setClear(CommandSource source, int time) {
		IServerWorldInfo winfo = locateOverworldInfo(source);
		winfo.setClearWeatherTime(time);
		winfo.setRainTime(0);
		winfo.setThunderTime(0);
		winfo.setRaining(false);
		winfo.setThundering(false);
		source.sendSuccess(new TranslationTextComponent("commands.weather.set.clear"), true);
		return time;
	}

	private static int setRain(CommandSource source, int time) {
		IServerWorldInfo winfo = locateOverworldInfo(source);
		winfo.setClearWeatherTime(0);
		winfo.setRainTime(time);
		winfo.setThunderTime(time);
		winfo.setRaining(true);
		winfo.setThundering(false);
		source.sendSuccess(new TranslationTextComponent("commands.weather.set.rain"), true);
		return time;
	}

	private static int setThunder(CommandSource source, int time) {
		IServerWorldInfo winfo = locateOverworldInfo(source);
		winfo.setClearWeatherTime(0);
		winfo.setRainTime(time);
		winfo.setThunderTime(time);
		winfo.setRaining(true);
		winfo.setThundering(true);
		source.sendSuccess(new TranslationTextComponent("commands.weather.set.thunder"), true);
		return time;
	}
}
