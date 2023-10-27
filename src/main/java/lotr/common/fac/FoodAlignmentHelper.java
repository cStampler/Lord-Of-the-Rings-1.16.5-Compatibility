package lotr.common.fac;

import java.util.Collections;
import java.util.List;

import lotr.common.data.LOTRLevelData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FoodAlignmentHelper {
	public static final FactionType[] EVIL_CREATURE_FACTION_TYPES;

	static {
		EVIL_CREATURE_FACTION_TYPES = new FactionType[] { FactionType.ORC, FactionType.TROLL };
	}

	private static List getFactionsOfTypes(World world, FactionType... alignedTypes) {
		FactionSettings facSettings = FactionSettingsManager.sidedInstance(world).getCurrentLoadedFactions();
		return facSettings != null ? facSettings.getFactionsOfTypes(alignedTypes) : Collections.emptyList();
	}

	public static float getHighestAlignmentProportion(LivingEntity entity, float fullAlignment, FactionType... alignedTypes) {
		List factions = getFactionsOfTypes(entity.level, alignedTypes);
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			float highestAlign = LOTRLevelData.getSidedData(player).getAlignmentData().getHighestAlignmentAmong(factions);
			float prop = highestAlign / fullAlignment;
			return MathHelper.clamp(prop, 0.0F, 1.0F);
		}
		return isNonPlayerEntityAlignedToAny(entity, factions) ? 1.0F : 0.0F;
	}

	public static boolean hasAnyPositiveAlignment(LivingEntity entity, FactionType... alignedTypes) {
		List factions = getFactionsOfTypes(entity.level, alignedTypes);
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			return LOTRLevelData.getSidedData(player).getAlignmentData().hasAlignmentWithAny(factions, AlignmentPredicates.POSITIVE);
		}
		return isNonPlayerEntityAlignedToAny(entity, factions);
	}

	private static boolean isNonPlayerEntityAlignedToAny(LivingEntity entity, List factions) {
		return factions.contains(EntityFactionHelper.getFaction(entity));
	}

	public static boolean isPledgedOrEntityAlignedToAny(LivingEntity entity, FactionType[] alignedTypes) {
		List factions = getFactionsOfTypes(entity.level, alignedTypes);
		if (!(entity instanceof PlayerEntity)) {
			return isNonPlayerEntityAlignedToAny(entity, factions);
		}
		PlayerEntity player = (PlayerEntity) entity;
		Faction pledged = LOTRLevelData.getSidedData(player).getAlignmentData().getPledgeFaction();
		return pledged != null && factions.contains(pledged);
	}

	public static ItemStack onFoodEatenWithoutRestore(ItemStack stack, World world, LivingEntity entity) {
		world.playSound((PlayerEntity) null, entity.getX(), entity.getY(), entity.getZ(), entity.getEatingSound(stack), SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
		if (!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).abilities.instabuild) {
			stack.shrink(1);
		}

		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
			world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
			if (player instanceof ServerPlayerEntity) {
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) player, stack);
			}
		}

		return stack;
	}
}
