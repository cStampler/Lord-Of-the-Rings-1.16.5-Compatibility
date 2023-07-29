package lotr.client.render.entity.layers;

import java.util.function.Predicate;

import lotr.client.render.RandomTextureVariants;
import lotr.client.render.entity.ArmsStyleModelProvider;
import lotr.client.render.entity.model.ManModel;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.inventory.EquipmentSlotType;

public class ManOutfitLayer extends NPCOutfitLayer {
	public ManOutfitLayer(IEntityRenderer renderer, RandomTextureVariants skins, EquipmentSlotType slot) {
		super(renderer, newManOutfitModel(), skins, slot);
	}

	public ManOutfitLayer(IEntityRenderer renderer, RandomTextureVariants skins, EquipmentSlotType slot, float prop) {
		super(renderer, newManOutfitModel(), skins, slot, prop);
	}

	public ManOutfitLayer(IEntityRenderer renderer, RandomTextureVariants skins, EquipmentSlotType slot, float prop, Predicate gender) {
		super(renderer, newManOutfitModel(), skins, slot, prop, gender);
	}

	private static final ArmsStyleModelProvider newManOutfitModel() {
		return smallArms -> new ManModel(0.6F, false, smallArms);
	}
}
