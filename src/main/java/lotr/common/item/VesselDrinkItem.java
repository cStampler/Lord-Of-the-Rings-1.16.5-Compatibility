package lotr.common.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import lotr.common.LOTRMod;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.MiscDataModule;
import lotr.common.init.LOTRItemGroups;
import lotr.common.util.CalendarUtil;
import lotr.common.util.LOTRUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VesselDrinkItem extends Item {
	public final float drinkAlcoholicity;
	private final int drinkFoodRestore;
	private final float drinkSaturation;
	public final boolean hasPotencies;
	private final float drinkDamage;
	private final List drinkEffects;

	protected VesselDrinkItem(float alc, int food, float sat, boolean hasPots, float dmg, List effs) {
		super(new Properties().stacksTo(1).tab(LOTRItemGroups.FOOD));
		drinkAlcoholicity = alc;
		drinkFoodRestore = food;
		drinkSaturation = sat;
		hasPotencies = hasPots;
		drinkDamage = dmg;
		drinkEffects = effs;
	}

	@OnlyIn(Dist.CLIENT)
	protected void addPreEffectsTooltip(ItemStack stack, World world, List tooltip, ITooltipFlag flag) {
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List tooltip, ITooltipFlag flag) {
		LivingEntity relevantEntity = LOTRMod.PROXY.getClientPlayer();
		IFormattableTextComponent displayDmg;
		if (hasPotencies) {
			VesselDrinkItem.Potency potency = getPotency(stack);
			displayDmg = potency.getDisplayName().withStyle(TextFormatting.GRAY);
			tooltip.add(displayDmg);
			if (drinkAlcoholicity > 0.0F) {
				float alc = getAlcoholicityForStrength(stack) * 10.0F;
				TextFormatting color = TextFormatting.GREEN;
				if (alc < 2.0F) {
					color = TextFormatting.GREEN;
				} else if (alc < 5.0F) {
					color = TextFormatting.YELLOW;
				} else if (alc < 10.0F) {
					color = TextFormatting.GOLD;
				} else if (alc < 20.0F) {
					color = TextFormatting.RED;
				} else {
					color = TextFormatting.DARK_RED;
				}

				ITextComponent displayAlc = new TranslationTextComponent("item.lotr.drink.alcoholicity", alc).withStyle(color);
				tooltip.add(displayAlc);
			}
		}

		if (drinkDamage > 0.0F) {
			float dmg = getDrinkDamageForStrength(stack);
			displayDmg = new TranslationTextComponent("item.lotr.drink.damage", dmg).withStyle(TextFormatting.RED);
			tooltip.add(displayDmg);
		}

		addPreEffectsTooltip(stack, world, tooltip, flag);
		addPotionEffectsToTooltip(stack, tooltip, flag, convertPotionEffectsForStrengthAndEntity(stack, relevantEntity));
	}

	public boolean canBeginDrinking(PlayerEntity player, ItemStack stack) {
		boolean alwaysDrinkable = drinkAlcoholicity > 0.0F || !drinkEffects.isEmpty();
		return player.canEat(alwaysDrinkable);
	}

	private List convertPotionEffectsForStrengthAndEntity(ItemStack stack, LivingEntity entity) {
		float strength = 1.0F;
		if (hasPotencies) {
			strength = getPotency(stack).effectsMultiplier;
		}

		float benefitEffectiveness = entity != null ? getBenefitEffectivenessFor(entity) : 1.0F;
		List effects = new ArrayList();
		Iterator var6 = drinkEffects.iterator();

		while (var6.hasNext()) {
			EffectInstance base = (EffectInstance) var6.next();
			float effectDurationFactor = strength;
			if (base.getEffect().isBeneficial()) {
				effectDurationFactor = strength * benefitEffectiveness;
			}

			int duration = (int) (base.getDuration() * effectDurationFactor);
			EffectInstance modified = new EffectInstance(base.getEffect(), duration, base.getAmplifier(), base.isAmbient(), base.isVisible());
			effects.add(modified);
		}

		return effects;
	}

	@Override
	public void fillItemCategory(ItemGroup group, NonNullList items) {
		if (allowdedIn(group)) {
			VesselType[] displayVessels = { VesselType.WOODEN_MUG };
			if (group == null || group.hasSearchBar()) {
				displayVessels = VesselType.values();
			}

			VesselType[] var4 = displayVessels;
			int var5 = displayVessels.length;

			for (int var6 = 0; var6 < var5; ++var6) {
				VesselType ves = var4[var6];
				if (hasPotencies) {
					VesselDrinkItem.Potency[] var13 = VesselDrinkItem.Potency.values();
					int var9 = var13.length;

					for (int var10 = 0; var10 < var9; ++var10) {
						VesselDrinkItem.Potency pot = var13[var10];
						ItemStack stack = new ItemStack(this);
						setPotency(stack, pot);
						setVessel(stack, ves);
						items.add(stack);
					}
				} else {
					ItemStack stack = new ItemStack(this);
					setVessel(stack, ves);
					items.add(stack);
				}
			}
		}

	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		PlayerEntity asPlayer = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
		if (asPlayer instanceof ServerPlayerEntity) {
			CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) asPlayer, stack);
		}

		VesselType vessel = getVessel(stack);
		ItemStack emptyVessel = vessel.createEmpty();
		float benefitEffectiveness = getBenefitEffectivenessFor(entity);
		float healF = drinkFoodRestore * benefitEffectiveness;
		float sat = drinkSaturation * benefitEffectiveness;
		if (hasPotencies) {
			VesselDrinkItem.Potency potency = getPotency(stack);
			healF *= potency.foodMultiplier;
			sat *= potency.foodMultiplier;
		}

		int heal = Math.round(healF);
		if (asPlayer != null) {
			asPlayer.getFoodData().eat(heal, sat);
			asPlayer.awardStat(Stats.ITEM_USED.get(this));
		} else {
			entity.heal(heal);
		}

		if (!world.isClientSide && drinkAlcoholicity > 0.0F) {
			float alcStrength = getAlcoholicityForStrength(stack);
			Optional playerMiscData = Optional.ofNullable(asPlayer).map(p -> LOTRLevelData.sidedInstance(world).getData(p).getMiscData());
			int tolerance = (Integer) playerMiscData.map(hummel -> ((MiscDataModule) hummel).getAlcoholTolerance()).orElse(0);
			if (tolerance > 0) {
				float f = (float) Math.pow(0.99D, tolerance);
				alcStrength *= f;
			}

			if (world.random.nextFloat() < alcStrength) {
				int duration = (int) (60.0F * (1.0F + world.random.nextFloat() * 0.5F) * alcStrength);
				if (duration >= 1) {
					int durationTicks = duration * 20;
					entity.addEffect(new EffectInstance(Effects.CONFUSION, durationTicks));
					int toleranceAdd = Math.round(duration / 20.0F);
					int newTolerance = tolerance + toleranceAdd;
					playerMiscData.ifPresent(miscData -> {
						((MiscDataModule) miscData).setAlcoholTolerance(newTolerance);
					});
				}
			}
		}

		if (!world.isClientSide && !drinkEffects.isEmpty() && shouldApplyPotionEffects(stack, entity)) {
			List effects = convertPotionEffectsForStrengthAndEntity(stack, entity);
			Iterator var20 = effects.iterator();

			while (var20.hasNext()) {
				EffectInstance effect = (EffectInstance) var20.next();
				if (effect.getEffect().isInstantenous()) {
					effect.getEffect().applyInstantenousEffect(asPlayer, asPlayer, entity, effect.getAmplifier(), 1.0D);
				} else {
					entity.addEffect(new EffectInstance(effect));
				}
			}
		}

		if (drinkDamage > 0.0F) {
			entity.hurt(DamageSource.MAGIC, getDrinkDamageForStrength(stack));
		}

		if (asPlayer == null || !asPlayer.abilities.instabuild) {
			stack.shrink(1);
			if (stack.isEmpty()) {
				return emptyVessel;
			}

			if (asPlayer != null) {
				asPlayer.inventory.add(emptyVessel);
			}
		}

		return stack;
	}

	private float getAlcoholicityForStrength(ItemStack stack) {
		return drinkAlcoholicity * getPotency(stack).alcMultiplier;
	}

	protected float getBenefitEffectivenessFor(LivingEntity entity) {
		return 1.0F;
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack) {
		return getVessel(stack).createEmpty();
	}

	private float getDrinkDamageForStrength(ItemStack stack) {
		float dmg = drinkDamage;
		if (hasPotencies) {
			dmg *= getPotency(stack).damageMultiplier;
		}

		return dmg;
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		return CalendarUtil.isAprilFools() ? new StringTextComponent("Hooch") : super.getName(stack);
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.DRINK;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return true;
	}

	protected boolean shouldApplyPotionEffects(ItemStack stack, LivingEntity entity) {
		return getBenefitEffectivenessFor(entity) > 0.0F;
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (canBeginDrinking(player, heldItem)) {
			player.startUsingItem(hand);
			return ActionResult.consume(heldItem);
		}
		return ActionResult.fail(heldItem);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		ItemStack drinkItem = context.getItemInHand();
		VesselType vessel = getVessel(drinkItem);
		Item emptyVesselItem = vessel.createEmpty().getItem();
		if (emptyVesselItem instanceof IEmptyVesselItem) {
			ActionResultType vesselPlaceResult = ((IEmptyVesselItem) emptyVesselItem).tryToPlaceVesselBlock(context);
			if (vesselPlaceResult.consumesAction()) {
				return vesselPlaceResult;
			}
		}

		return super.useOn(context);
	}

	public static void addPotionEffectsToTooltip(ItemStack stack, List tooltip, ITooltipFlag flag, List itemEffects) {
		if (!itemEffects.isEmpty()) {
			ItemStack potionEquivalent = new ItemStack(Items.POTION);
			PotionUtils.setCustomEffects(potionEquivalent, itemEffects);
			List effectTooltips = new ArrayList();
			PotionUtils.addPotionTooltip(potionEquivalent, effectTooltips, 1.0F);
			tooltip.addAll(effectTooltips);
		}

	}

	public static VesselDrinkItem.Potency getPotency(ItemStack stack) {
		CompoundNBT nbt = stack.getTagElement("vessel");
		return nbt != null && nbt.contains("potency", 8) ? VesselDrinkItem.Potency.forName(nbt.getString("potency")) : VesselDrinkItem.Potency.MODERATE;
	}

	public static VesselType getVessel(ItemStack stack) {
		CompoundNBT nbt = stack.getTagElement("vessel");
		return nbt != null && nbt.contains("type", 8) ? VesselType.forName(nbt.getString("type")) : VesselType.WOODEN_MUG;
	}

	public static VesselDrinkItem newAlcohol(float alc, int food, float sat) {
		return new VesselDrinkItem(alc, food, sat, true, 0.0F, ImmutableList.of());
	}

	public static VesselDrinkItem newBasic(int food, float sat) {
		return new VesselDrinkItem(0.0F, food, sat, false, 0.0F, ImmutableList.of());
	}

	public static VesselDrinkItem newEffects(int food, float sat, EffectInstance... effs) {
		return new VesselDrinkItem(0.0F, food, sat, true, 0.0F, Arrays.asList(effs));
	}

	public static VesselDrinkItem newEffectsAlcohol(float alc, int food, float sat, EffectInstance... effs) {
		return new VesselDrinkItem(alc, food, sat, true, 0.0F, Arrays.asList(effs));
	}

	public static VesselDrinkItem newEffectsDamage(int food, float sat, float dmg, EffectInstance... effs) {
		return new VesselDrinkItem(0.0F, food, sat, true, dmg, Arrays.asList(effs));
	}

	public static void setPotency(ItemStack stack, VesselDrinkItem.Potency pot) {
		stack.getOrCreateTagElement("vessel").putString("potency", pot.getCodeName());
	}

	public static void setVessel(ItemStack stack, VesselType ves) {
		stack.getOrCreateTagElement("vessel").putString("type", ves.getCodeName());
	}

	public enum Potency {
		WEAK(0, "weak", 0.25F, 0.5F), LIGHT(1, "light", 0.5F, 0.75F), MODERATE(2, "moderate", 1.0F, 1.0F), STRONG(3, "strong", 2.0F, 1.25F), POTENT(4, "potent", 3.0F, 1.5F);

		private static final Map LEVEL_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), potency -> ((Potency) potency).level);
		private static final Map NAME_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), hummel -> ((VesselDrinkItem.Potency) hummel).getCodeName());
		private static int minLevel;
		private static int maxLevel;
		public final int level;
		public final String name;
		public final float alcMultiplier;
		public final float effectsMultiplier;
		public final float damageMultiplier;
		public final float foodMultiplier;

		Potency(int i, String s, float alc, float food) {
			level = i;
			name = s;
			alcMultiplier = alc;
			effectsMultiplier = alc;
			damageMultiplier = alc;
			foodMultiplier = food;
			recache();
		}

		public String getCodeName() {
			return name;
		}

		public IFormattableTextComponent getDisplayName() {
			return new TranslationTextComponent("item.lotr.drink." + name);
		}

		public VesselDrinkItem.Potency getNext() {
			return isMax() ? this : forLevel(level + 1);
		}

		public VesselDrinkItem.Potency getPrev() {
			return isMin() ? this : forLevel(level - 1);
		}

		public boolean isMax() {
			return level == maxLevel;
		}

		public boolean isMin() {
			return level == minLevel;
		}

		private void recache() {
			minLevel = Math.min(minLevel, level);
			maxLevel = Math.max(maxLevel, level);
		}

		public static VesselDrinkItem.Potency forLevel(int level) {
			return (VesselDrinkItem.Potency) LEVEL_LOOKUP.getOrDefault(level, MODERATE);
		}

		public static VesselDrinkItem.Potency forName(String name) {
			return (VesselDrinkItem.Potency) NAME_LOOKUP.getOrDefault(name, MODERATE);
		}

		public static VesselDrinkItem.Potency getMax() {
			return forLevel(maxLevel);
		}

		public static VesselDrinkItem.Potency getMin() {
			return forLevel(minLevel);
		}

		public static VesselDrinkItem.Potency randomForNPC(Random rand) {
			int i = rand.nextInt(3);
			if (i == 0) {
				return LIGHT;
			}
			return i == 1 ? MODERATE : STRONG;
		}
	}
}
