package lotr.common.dim;

import java.util.OptionalLong;

import com.google.common.math.IntMath;

import lotr.common.time.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public abstract class LOTRDimensionType extends DimensionType {
	protected LOTRDimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm, boolean natural, boolean bedWorks, int logicalHeight, ResourceLocation infiniburn, ResourceLocation key, float ambientLight) {
		super(fixedTime, hasSkyLight, hasCeiling, ultrawarm, natural, 1.0D, false, bedWorks, false, false, logicalHeight, infiniburn, key, ambientLight);
	}

	public long getWorldTime(World world) {
		return LOTRTime.getWorldTime(world);
	}

	public boolean isLunarEclipse(World world) {
		long worldTimeME = getWorldTime(world);
		int day = MiddleEarthCalendar.currentDay;
		return moonPhase(worldTimeME) == 0 && IntMath.mod(day / MOON_BRIGHTNESS_PER_PHASE.length, 4) == 3;
	}

	@Override
	public int moonPhase(long worldTime) {
		int day = MiddleEarthCalendar.currentDay;
		return IntMath.mod(day, MOON_BRIGHTNESS_PER_PHASE.length);
	}

	@Override
	public float timeOfDay(long worldTime) {
		long worldTimeME = LOTRTime.getWorldTime(EffectiveSide.get().isClient());
		double d0 = MathHelper.frac(worldTimeME / 48000.0D - 0.25D);
		double d1 = 0.5D - Math.cos(d0 * 3.141592653589793D) / 2.0D;
		return (float) (d0 * 2.0D + d1) / 3.0F;
	}
}
