package lotr.common.item;

import javax.annotation.Nullable;

import lotr.client.render.entity.model.armor.LOTRArmorModels;
import lotr.common.init.LOTRItemGroups;
import lotr.common.init.LOTRMaterial;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LOTRArmorItem extends ArmorItem {
	private final String specialTextureName;
	private final boolean isUndamageable;

	public LOTRArmorItem(IArmorMaterial material, EquipmentSlotType slot) {
		this(material, slot, defaultArmorProperties(), (String) null);
	}

	public LOTRArmorItem(IArmorMaterial material, EquipmentSlotType slot, Properties properties) {
		this(material, slot, properties, (String) null);
	}

	public LOTRArmorItem(IArmorMaterial material, EquipmentSlotType slot, Properties properties, String specialTex) {
		super(material, slot, properties);
		specialTextureName = specialTex;
		isUndamageable = (Boolean) LOTRMaterial.ifLOTRArmorMaterial(material).map(hummel -> ((LOTRMaterial.AsArmor) hummel).isUndamageable()).orElse(false);
	}

	public LOTRArmorItem(IArmorMaterial material, EquipmentSlotType slot, String specialTex) {
		this(material, slot, defaultArmorProperties(), specialTex);
	}

	public LOTRArmorItem(LOTRMaterial material, EquipmentSlotType slot) {
		this(material.asArmor(), slot);
	}

	public LOTRArmorItem(LOTRMaterial material, EquipmentSlotType slot, Properties properties) {
		this(material.asArmor(), slot, properties, (String) null);
	}

	public LOTRArmorItem(LOTRMaterial material, EquipmentSlotType slot, String specialTex) {
		this(material.asArmor(), slot, specialTex);
	}

	@Override
	public boolean canBeDepleted() {
		return super.canBeDepleted() && !isUndamageable;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	@Nullable
	public BipedModel getArmorModel(LivingEntity entity, ItemStack itemstack, EquipmentSlotType slot, BipedModel _default) {
		return LOTRArmorModels.getArmorModel(entity, itemstack, slot, _default);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		if (specialTextureName == null) {
			return super.getArmorTexture(stack, entity, slot, type);
		}
		ArmorItem item = (ArmorItem) stack.getItem();
		String materialName = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = materialName.indexOf(58);
		if (idx != -1) {
			domain = materialName.substring(0, idx);
			materialName = materialName.substring(idx + 1);
		}

		return String.format("%s:textures/models/armor/%s_%s%s.png", domain, materialName, specialTextureName, type == null ? "" : String.format("_%s", type));
	}

	private static Properties defaultArmorProperties() {
		return new Properties().tab(LOTRItemGroups.COMBAT);
	}
}
