package lotr.client.render.entity.model.armor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import lotr.common.init.LOTRItems;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LOTRArmorModels {
	private static boolean hasSetup = false;
	private static final Map specialArmorModels = new HashMap();

	private static void addSpecialArmorModel(Supplier itemSup, SpecialArmorModelHolder.SpecialArmorModelFactory factory) {
		specialArmorModels.put(itemSup.get(), new SpecialArmorModelHolder(factory));
	}

	@Nullable
	public static BipedModel getArmorModel(LivingEntity entity, ItemStack itemstack, EquipmentSlotType slot, BipedModel _default) {
		if (!hasSetup) {
			setup();
		}

		Item item = itemstack.getItem();
		if (!specialArmorModels.containsKey(item)) {
			return null;
		}
		BipedModel model = ((SpecialArmorModelHolder) specialArmorModels.get(item)).getSpecialModelFromBipedReference(_default);
		if (model instanceof ItemStackDependentModel) {
			((ItemStackDependentModel) model).setModelItem(itemstack);
		}

		if (model instanceof WearerDependentArmorModel) {
			((WearerDependentArmorModel) model).acceptWearingEntity(entity);
		}

		return model;
	}

	private static void setup() {
		hasSetup = true;
		addSpecialArmorModel(LOTRItems.GONDOR_HELMET, GondorHelmetModel::new);
		addSpecialArmorModel(LOTRItems.LINDON_HELMET, HighElvenHelmetModel::new);
		addSpecialArmorModel(LOTRItems.RIVENDELL_HELMET, HighElvenHelmetModel::new);
		addSpecialArmorModel(LOTRItems.GALADHRIM_HELMET, GaladhrimHelmetModel::new);
		addSpecialArmorModel(LOTRItems.HARAD_WARLORD_HELMET, HaradWarlordHelmetModel::new);
		addSpecialArmorModel(LOTRItems.UMBAR_HELMET, UmbarHelmetModel::new);
		addSpecialArmorModel(LOTRItems.URUK_HELMET, UrukHelmetModel::new);
		addSpecialArmorModel(LOTRItems.URUK_BERSERKER_HELMET, UrukHelmetModel::new);
		addSpecialArmorModel(LOTRItems.DORWINION_ELVEN_HELMET, DorwinionElvenHelmetModel::new);
		addSpecialArmorModel(LOTRItems.ROHAN_MARSHAL_HELMET, RohanMarshalHelmetModel::new);
		addSpecialArmorModel(LOTRItems.WINGED_GONDOR_HELMET, WingedGondorHelmetModel::new);
		addSpecialArmorModel(LOTRItems.HARNENNOR_HELMET, HarnennorHelmetModel::new);
		addSpecialArmorModel(LOTRItems.HARNENNOR_CHESTPLATE, HarnennorChestplateModel::new);
		addSpecialArmorModel(LOTRItems.DOL_AMROTH_HELMET, DolAmrothHelmetModel::new);
		addSpecialArmorModel(LOTRItems.DOL_AMROTH_CHESTPLATE, DolAmrothChestplateModel::new);
		addSpecialArmorModel(LOTRItems.ARNOR_HELMET, ArnorHelmetModel::new);
		addSpecialArmorModel(LOTRItems.FINE_PLATE, PlateOnHeadModel::new);
		addSpecialArmorModel(LOTRItems.STONEWARE_PLATE, PlateOnHeadModel::new);
		addSpecialArmorModel(LOTRItems.WOODEN_PLATE, PlateOnHeadModel::new);
	}
}
