package lotr.common.speech;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.fac.RankGender;
import lotr.common.init.LOTRBiomes;
import lotr.common.world.biome.ExtendedWeatherType;
import lotr.common.world.biome.LOTRBiomeBase;
import lotr.common.world.biome.LOTRBiomeWrapper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IDayTimeReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class SpeechEnums {
	public enum DayOrNight {
		DAY, NIGHT;
	}

	public enum Daytime {
		DAWN, MORNING, AFTERNOON, EVENING, DUSK, NIGHT;

		public static SpeechEnums.Daytime getDaytime(World world) {
			float phase = getDaytimePhase(world);
			if (phase > 0.225F && phase < 0.265F) {
				return DAWN;
			}
			if (phase >= 0.265F && phase < 0.5F) {
				return MORNING;
			}
			if (phase >= 0.5F && phase < 0.68F) {
				return AFTERNOON;
			}
			if (phase >= 0.68F && phase < 0.735F) {
				return EVENING;
			}
			return phase >= 0.735F && phase < 0.775F ? DUSK : NIGHT;
		}

		public static float getDaytimePhase(World world) {
			float sunCycle = world.getTimeOfDay(1.0F);
			return (sunCycle + 0.5F) % 1.0F;
		}

		public static float getHour(World world) {
			return getDaytimePhase(world) * 24.0F;
		}
	}

	public enum Health {
		DANGER, LOW, MEDIUM, HIGH, FULL;

		public static SpeechEnums.Health getHealth(float frac) {
			frac = MathHelper.clamp(frac, 0.0F, 1.0F);
			if (frac == 1.0F) {
				return FULL;
			}
			if (frac > 0.75F) {
				return HIGH;
			}
			if (frac > 0.5F) {
				return MEDIUM;
			}
			return frac > 0.25F ? LOW : DANGER;
		}

		public static SpeechEnums.Health getHealth(LivingEntity e) {
			float healthF = e.getHealth() / e.getMaxHealth();
			return getHealth(healthF);
		}
	}

	public enum Hired {
		OWN, OTHER, NONE;
	}

	public enum InConversation {
		NONE, SAME, OTHER;

		public static SpeechEnums.InConversation getInConversationType(NPCEntity npc, PlayerEntity player) {
			LivingEntity currentTalkingTo = npc.getTalkingToEntity();
			if (currentTalkingTo == null) {
				return NONE;
			}
			return currentTalkingTo == player ? SAME : OTHER;
		}
	}

	public enum MoonPhase {
		FULL, WANING_GIBBOUS, THRID_QUARTER, WANING_CRESCENT, NEW, WAXING_CRESCENT, FIRST_QUARTER, WAXING_GIBBOUS;

		public static SpeechEnums.MoonPhase getPhase(IDayTimeReader world) {
			int phase = world.dimensionType().moonPhase(world.dayTime());
			return values()[MathHelper.clamp(phase, 0, values().length - 1)];
		}
	}

	public enum PledgeRelation {
		THIS, GOOD, NEUTRAL, BAD, NONE;
	}

	public enum PreferredGender {
		M, F;

		public static SpeechEnums.PreferredGender fromNPCGender(NPCEntity npc) {
			return npc.getPersonalInfo().isMale() ? M : F;
		}

		public static SpeechEnums.PreferredGender fromRankGender(RankGender rankGender) {
			return rankGender == RankGender.FEMININE ? F : M;
		}
	}

	public enum Relation {
		FRIENDLY, HOSTILE;
	}

	public enum Weather {
		CLEAR, RAIN, SNOW, ASH, SANDSTORM;

		public static SpeechEnums.Weather getWeather(World world, Biome biome, BlockPos pos) {
			if (!world.isRaining()) {
				return CLEAR;
			}
			LOTRBiomeWrapper biomeWrapper = LOTRBiomes.getWrapperFor(biome, world);
			ExtendedWeatherType extendedWeather = biomeWrapper.getExtendedWeatherVisually();
			if (extendedWeather == ExtendedWeatherType.ASHFALL) {
				return ASH;
			}
			if (extendedWeather == ExtendedWeatherType.SANDSTORM) {
				return SANDSTORM;
			}
			return LOTRBiomeBase.isSnowingVisually(biomeWrapper, world, pos) ? SNOW : RAIN;
		}

		public static boolean isThundering(World world) {
			return world.isThundering();
		}
	}
}
