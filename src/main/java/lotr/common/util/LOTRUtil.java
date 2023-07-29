package lotr.common.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import com.google.common.math.LongMath;

import lotr.common.LOTRLog;
import net.minecraft.block.*;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap.Type;

public class LOTRUtil {
	public static int calculatePlayersUsingSingleContainer(World world, int x, int y, int z, Class containerClass, Predicate tester) {
		int count = 0;
		AxisAlignedBB checkBox = new AxisAlignedBB(x - 5.0F, y - 5.0F, z - 5.0F, x + 1 + 5.0F, y + 1 + 5.0F, z + 1 + 5.0F);
		for (PlayerEntity player : world.getEntitiesOfClass(PlayerEntity.class, checkBox)) {
			Container container = player.containerMenu;
			if (container != null && containerClass.isInstance(container) && tester.test(container)) {
				++count;
			}
		}

		return count;
	}

	public static int[] combineArrays(int[] array1, int[] array2) {
		int[] result = new int[array1.length + array2.length];

		int i;
		for (i = 0; i < array1.length; ++i) {
			result[i] = array1[i];
		}

		for (i = 0; i < array2.length; ++i) {
			result[i + array1.length] = array2[i];
		}

		return result;
	}

	public static int[] combineArrays(int[] array1, int[] array2, int[] array3) {
		return combineArrays(combineArrays(array1, array2), array3);
	}

	public static Object[] combineVarargs(Object[] array1, Object... array2) {
		List combined = new ArrayList(Arrays.asList(array1));
		combined.addAll(Arrays.asList(array2));
		return combined.toArray();
	}

	public static void consumeOneInventoryItem(PlayerEntity player, ItemStack stack) {
		if (!player.abilities.instabuild) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				player.inventory.removeItem(stack);
			}
		}

	}

	public static Map createKeyedEnumMap(Enum[] values, Function keyGetter) {
		return (Map) Arrays.stream(values).collect(Collectors.toMap(keyGetter, type -> type));
	}

	public static ItemStack findHeldOrInventoryItem(PlayerEntity player, Predicate test) {
		ItemStack offhandItem = player.getItemInHand(Hand.OFF_HAND);
		if (test.test(offhandItem)) {
			return offhandItem;
		}
		ItemStack mainhandItem = player.getItemInHand(Hand.MAIN_HAND);
		if (test.test(mainhandItem)) {
			return mainhandItem;
		}
		IInventory playerInv = player.inventory;

		for (int i = 0; i < playerInv.getContainerSize(); ++i) {
			ItemStack invItem = playerInv.getItem(i);
			if (test.test(invItem)) {
				return invItem;
			}
		}

		return ItemStack.EMPTY;
	}

	public static int forceLoadChunkAndGetTopBlock(World world, int x, int z) {
		Chunk chunk = world.getChunkAt(new BlockPos(x, 0, z));
		return chunk.getHeight(Type.MOTION_BLOCKING_NO_LEAVES, x, z) + 1;
	}

	public static int getCoordinateRandomModulo(int x, int y, int z, int mod) {
		long rand = MathHelper.getSeed(x, y, z);
		return LongMath.mod(rand, mod);
	}

	public static ITextComponent getHMSTime_Seconds(int s) {
		return getHMSTime_Ticks(secondsToTicks(s));
	}

	public static ITextComponent getHMSTime_Ticks(int ticks) {
		int hours = ticks / 72000;
		int minutes = ticks % 72000 / 1200;
		int seconds = ticks % 72000 % 1200 / 20;
		ITextComponent sHours = new TranslationTextComponent("gui.lotr.time.hours", hours);
		ITextComponent sMinutes = new TranslationTextComponent("gui.lotr.time.minutes", minutes);
		ITextComponent sSeconds = new TranslationTextComponent("gui.lotr.time.seconds", seconds);
		if (hours > 0) {
			return new TranslationTextComponent("gui.lotr.time.format.hms", sHours, sMinutes, sSeconds);
		}
		return minutes > 0 ? new TranslationTextComponent("gui.lotr.time.format.ms", sMinutes, sSeconds) : new TranslationTextComponent("gui.lotr.time.format.s", sSeconds);
	}

	public static Direction getRandomPerpendicular(Direction dir, Random rand) {
		Direction[] perpendiculars = Arrays.stream(Direction.values()).filter(d -> (d.getAxis() != dir.getAxis())).toArray(a -> new Direction[a]);
		return (Direction) randInArray(perpendiculars, rand);
	}

	public static boolean hasSolidSide(IBlockReader world, BlockPos pos, Direction side) {
		BlockState state = world.getBlockState(pos);
		return Block.isFaceFull(state.getBlockSupportShape(world, pos), side);
	}

	public static int minutesToTicks(int m) {
		return secondsToTicks(m * 60);
	}

	public static float normalisedCos(float t) {
		return (MathHelper.cos(t) + 1.0F) / 2.0F;
	}

	public static float normalisedSin(float t) {
		return (MathHelper.sin(t) + 1.0F) / 2.0F;
	}

	public static float normalisedTriangleWave(float t, float period, float min, float max) {
		float relativeT = Math.abs(t / period % 1.0F);
		return min + (max - min) * -(Math.abs(relativeT - 0.5F) - 0.5F) * 2.0F;
	}

	public static Object randInArray(Object[] array, Random rand) {
		return array[rand.nextInt(array.length)];
	}

	public static int secondsToTicks(int s) {
		return s * 20;
	}

	public static void sendMessage(PlayerEntity player, ITextComponent message) {
		player.displayClientMessage(message, false);
	}

	public static void spawnXPOrbs(PlayerEntity player, int count, float xp) {
		if (xp == 0.0F) {
			count = 0;
		} else if (xp < 1.0F) {
			float totalXp = count * xp;
			int floorTotal = MathHelper.floor(totalXp);
			if (floorTotal < MathHelper.ceil(totalXp) && Math.random() < totalXp - floorTotal) {
				++floorTotal;
			}

			count = floorTotal;
		}

		while (count > 0) {
			int orbXp = ExperienceOrbEntity.getExperienceValue(count);
			count -= orbXp;
			player.level.addFreshEntity(new ExperienceOrbEntity(player.level, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, orbXp));
		}

	}

	public static String toPaddedHexString(int rgb) {
		return String.format("%1$06X", rgb);
	}

	public static float trapezoidalIntensitySinglePulse(float t, float fullDuration, float fadeInAndOutFraction, float min, float max) {
		float fadeOutStart = 1.0F - fadeInAndOutFraction;
		float frac = t / fullDuration;
		frac = MathHelper.clamp(frac, 0.0F, 1.0F);
		float intensity = 1.0F;
		if (frac < fadeInAndOutFraction) {
			intensity = frac / fadeInAndOutFraction;
		} else if (frac > fadeOutStart) {
			intensity = (1.0F - frac) / fadeInAndOutFraction;
		}

		return min + (max - min) * intensity;
	}

	public static void unlockConstructor(Constructor constr) {
		try {
			Field modifiersField = Constructor.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(constr, (constr.getModifiers() & -3 & -5 | 1) & -17);
		} catch (SecurityException | IllegalAccessException | NoSuchFieldException var2) {
			LOTRLog.error("Error unlocking final field " + constr.toString());
			var2.printStackTrace();
		}

	}

	public static void unlockFinalField(Field f) {
		try {
			f.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(f, (f.getModifiers() & -3 & -5 | 1) & -17);
		} catch (SecurityException | IllegalAccessException | NoSuchFieldException var2) {
			LOTRLog.error("Error unlocking final field " + f.toString());
			var2.printStackTrace();
		}

	}

	public static void unlockMethod(Method m) {
		try {
			m.setAccessible(true);
			Field modifiersField = Method.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(m, (m.getModifiers() & -3 & -5 | 1) & -17);
		} catch (SecurityException | IllegalAccessException | NoSuchFieldException var2) {
			LOTRLog.error("Error unlocking final method " + m.toString());
			var2.printStackTrace();
		}

	}
}
