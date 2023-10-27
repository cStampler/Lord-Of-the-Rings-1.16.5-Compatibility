package lotr.common.init;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags.Items;

public enum LOTRMaterial {
	BRONZE("bronze", 2, 230, 5.0F, 1.5F, 10, 0.5F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(LOTRTags.Items.INGOTS_BRONZE), new LOTRMaterial.Specials[0]), MITHRIL("mithril", 4, 2400, 9.0F, 5.0F, 10, 0.8F, SoundEvents.ARMOR_EQUIP_DIAMOND, 3.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.MITHRIL_INGOT.get() }), () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.MITHRIL_MAIL.get() }), new LOTRMaterial.Specials[0]), FUR("fur", 0, 180, 0.0F, 0.0F, 8, 0.4F, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.FUR.get() }), new LOTRMaterial.Specials[0]), BONE("bone", 0, 150, 0.0F, 0.0F, 10, 0.3F, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, () -> Ingredient.of(Items.BONES), new LOTRMaterial.Specials[0]), GONDOR("gondor", 2, 450, 6.0F, 2.5F, 10, 0.6F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), new LOTRMaterial.Specials[0]), DOL_AMROTH("dol_amroth", 2, 500, 6.0F, 3.0F, 10, 0.6F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), new LOTRMaterial.Specials[0]), ROHAN("rohan", 2, 300, 6.0F, 2.5F, 10, 0.5F, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), new LOTRMaterial.Specials[0]), ROHAN_MARSHAL("rohan_marshal", 2, 400, 6.0F, 3.0F, 10, 0.6F, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), new LOTRMaterial.Specials[0]), DUNLENDING("dunlending", 2, 250, 6.0F, 2.0F, 8, 0.5F, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), new LOTRMaterial.Specials[0]), DALE("dale", 2, 300, 6.0F, 2.5F, 10, 0.6F, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), new LOTRMaterial.Specials[0]), RANGER_NORTH("ranger_north", 2, 350, 6.0F, 2.5F, 12, 0.48F, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), () -> Ingredient.of(Items.LEATHER), new LOTRMaterial.Specials[0]), RANGER_ITHILIEN("ranger_ithilien", 2, 350, 6.0F, 2.5F, 12, 0.48F, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), () -> Ingredient.of(Items.LEATHER), new LOTRMaterial.Specials[0]), ARNOR("arnor", 2, 500, 6.0F, 3.0F, 10, 0.6F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), new LOTRMaterial.Specials[0]), DORWINION("dorwinion", 2, 400, 6.0F, 2.5F, 10, 0.5F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), new LOTRMaterial.Specials[0]), HARAD("harad", 2, 300, 6.0F, 2.5F, 10, 0.5F, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> Ingredient.of(LOTRTags.Items.INGOTS_BRONZE), new LOTRMaterial.Specials[0]), UMBAR("umbar", 2, 450, 6.0F, 2.5F, 10, 0.6F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(Items.INGOTS_IRON), new LOTRMaterial.Specials[0]), HARNENNOR("harnennor", 2, 250, 6.0F, 2.0F, 8, 0.5F, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> Ingredient.of(LOTRTags.Items.INGOTS_BRONZE), new LOTRMaterial.Specials[0]), LINDON("lindon", 2, 700, 8.0F, 3.0F, 15, 0.6F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.ELVEN_STEEL_INGOT.get() }), new LOTRMaterial.Specials[0]), RIVENDELL("rivendell", 2, 700, 8.0F, 3.0F, 15, 0.6F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.ELVEN_STEEL_INGOT.get() }), new LOTRMaterial.Specials[0]), GALADHRIM("galadhrim", 2, 600, 7.0F, 3.0F, 15, 0.6F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.ELVEN_STEEL_INGOT.get() }), new LOTRMaterial.Specials[0]), MALLORN("mallorn", 1, 200, 4.0F, 1.5F, 15, 0.0F, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.MALLORN_PLANKS.get() }), new LOTRMaterial.Specials[0]), WOOD_ELVEN("wood_elven", 2, 500, 9.0F, 3.0F, 15, 0.6F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.ELVEN_STEEL_INGOT.get() }), new LOTRMaterial.Specials[0]), DORWINION_ELVEN("dorwinion_elven", 2, 500, 7.0F, 3.0F, 15, 0.6F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.ELVEN_STEEL_INGOT.get() }), new LOTRMaterial.Specials[0]), DWARVEN("dwarven", 3, 700, 7.0F, 3.0F, 10, 0.7F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.DWARVEN_STEEL_INGOT.get() }), new LOTRMaterial.Specials[0]), BLUE_DWARVEN("blue_dwarven", 3, 650, 7.0F, 3.0F, 12, 0.7F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.DWARVEN_STEEL_INGOT.get() }), new LOTRMaterial.Specials[0]), MORDOR("mordor", 2, 400, 6.0F, 2.5F, 7, 0.6F, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.ORC_STEEL_INGOT.get() }), new LOTRMaterial.Specials[] { LOTRMaterial.Specials.MAN_FLESH }), URUK("uruk", 2, 550, 6.0F, 3.0F, 5, 0.7F, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> Ingredient.of(new IItemProvider[] { (IItemProvider) LOTRItems.URUK_STEEL_INGOT.get() }), new LOTRMaterial.Specials[] { LOTRMaterial.Specials.MAN_FLESH }), COSMETIC("cosmetic", 0, 0, 0.0F, 0.0F, 0, 0.0F, SoundEvents.ARMOR_EQUIP_GENERIC, 0.0F, () -> Ingredient.EMPTY, new LOTRMaterial.Specials[] { LOTRMaterial.Specials.UNDAMAGEABLE });

	private static final int[] ARMOR_DURABILITY_ARRAY = { 13, 15, 16, 11 };
	private String materialName;
	private LOTRMaterial.AsTool asTool;
	private final int harvestLevel;
	private final int maxUses;
	private final float efficiency;
	private final float attackDamage;
	private final int enchantability;
	private final LazyValue toolRepairMaterial;
	private LOTRMaterial.AsArmor asArmor;
	private final int armorDurabilityFactor;
	private final int[] armorProtectionArray;
	private final SoundEvent armorSoundEvent;
	private final float toughness;
	private final LazyValue armorRepairMaterial;
	private final Set specialProperties;

	LOTRMaterial(String name, int lvl, int uses, float eff, float atk, int ench, float pr, SoundEvent sound, float tough, Supplier repair, LOTRMaterial.Specials... specs) {
		this(name, lvl, uses, eff, atk, ench, pr, sound, tough, repair, repair, specs);
	}

	LOTRMaterial(String name, int lvl, int uses, float eff, float atk, int ench, float pr, SoundEvent sound, float tough, Supplier repair, Supplier armorRepair, LOTRMaterial.Specials... specs) {
		specialProperties = new HashSet();
		materialName = "lotr:" + name;
		harvestLevel = lvl;
		maxUses = uses;
		efficiency = eff;
		attackDamage = atk;
		enchantability = ench;
		toolRepairMaterial = new LazyValue(repair);
		armorDurabilityFactor = Math.round(maxUses * 0.06F);
		armorProtectionArray = LOTRMaterial.ArmorHelper.getArmorProtectionArray(pr);
		armorSoundEvent = sound;
		toughness = tough;
		armorRepairMaterial = new LazyValue(armorRepair);
		LOTRMaterial.Specials[] var15 = specs;
		int var16 = specs.length;

		for (int var17 = 0; var17 < var16; ++var17) {
			LOTRMaterial.Specials s = var15[var17];
			specialProperties.add(s);
		}

	}

	public LOTRMaterial.AsArmor asArmor() {
		if (asArmor == null) {
			asArmor = new LOTRMaterial.AsArmor(this);
		}

		return asArmor;
	}

	public LOTRMaterial.AsTool asTool() {
		if (asTool == null) {
			asTool = new LOTRMaterial.AsTool(this);
		}

		return asTool;
	}

	public static Optional ifLOTRArmorMaterial(IArmorMaterial material) {
		return material instanceof LOTRMaterial.AsArmor ? Optional.of((LOTRMaterial.AsArmor) material) : Optional.empty();
	}

	public static Optional ifLOTRToolMaterial(IItemTier material) {
		return material instanceof LOTRMaterial.AsTool ? Optional.of((LOTRMaterial.AsTool) material) : Optional.empty();
	}

	private static class ArmorHelper {
		private static final float[] ARMOR_PART_WEIGHTING = { 0.14F, 0.32F, 0.4F, 0.14F };

		public static int[] getArmorProtectionArray(float protection) {
			int[] armorArray = new int[ARMOR_PART_WEIGHTING.length];

			for (int i = 0; i < armorArray.length; ++i) {
				armorArray[i] = Math.round(ARMOR_PART_WEIGHTING[i] * protection * 25.0F);
			}

			return armorArray;
		}
	}

	public static class AsArmor implements IArmorMaterial {
		private final LOTRMaterial materialReference;

		public AsArmor(LOTRMaterial m) {
			materialReference = m;
		}

		@Override
		public int getDefenseForSlot(EquipmentSlotType slot) {
			return materialReference.armorProtectionArray[slot.getIndex()];
		}

		@Override
		public int getDurabilityForSlot(EquipmentSlotType slot) {
			return LOTRMaterial.ARMOR_DURABILITY_ARRAY[slot.getIndex()] * materialReference.armorDurabilityFactor;
		}

		@Override
		public int getEnchantmentValue() {
			return materialReference.enchantability;
		}

		@Override
		public SoundEvent getEquipSound() {
			return materialReference.armorSoundEvent;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.0F;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public String getName() {
			return materialReference.materialName;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return (Ingredient) materialReference.armorRepairMaterial.get();
		}

		@Override
		public float getToughness() {
			return materialReference.toughness;
		}

		public boolean isUndamageable() {
			return materialReference.specialProperties.contains(LOTRMaterial.Specials.UNDAMAGEABLE);
		}
	}

	public static class AsTool implements IItemTier {
		private final LOTRMaterial materialReference;

		public AsTool(LOTRMaterial m) {
			materialReference = m;
		}

		public boolean canHarvestManFlesh() {
			return materialReference.specialProperties.contains(LOTRMaterial.Specials.MAN_FLESH);
		}

		@Override
		public float getAttackDamageBonus() {
			return materialReference.attackDamage;
		}

		@Override
		public int getEnchantmentValue() {
			return materialReference.enchantability;
		}

		@Override
		public int getLevel() {
			return materialReference.harvestLevel;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return (Ingredient) materialReference.toolRepairMaterial.get();
		}

		@Override
		public float getSpeed() {
			return materialReference.efficiency;
		}

		@Override
		public int getUses() {
			return materialReference.maxUses;
		}
	}

	public static enum Specials {
		MAN_FLESH, UNDAMAGEABLE;
	}
}
