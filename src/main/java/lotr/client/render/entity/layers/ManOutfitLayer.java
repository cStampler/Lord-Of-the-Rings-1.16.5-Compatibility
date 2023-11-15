package lotr.client.render.entity.layers;

import java.util.function.Predicate;

import lotr.client.render.RandomTextureVariants;
import lotr.client.render.entity.ArmsStyleModelProvider;
import lotr.client.render.entity.model.ManModel;
import lotr.common.entity.npc.AbstractMannishEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.inventory.EquipmentSlotType;

public class ManOutfitLayer<E extends AbstractMannishEntity> extends NPCOutfitLayer<E, ManModel<E>> {
	public ManOutfitLayer(IEntityRenderer<E, ManModel<E>> renderer, RandomTextureVariants skins, EquipmentSlotType slot) {
		super(renderer, newManOutfitModel(), skins, slot);
	}

	public ManOutfitLayer(IEntityRenderer<E, ManModel<E>> renderer, RandomTextureVariants skins, EquipmentSlotType slot, float prop) {
		super(renderer, newManOutfitModel(), skins, slot, prop);
	}

	public ManOutfitLayer(IEntityRenderer<E, ManModel<E>> renderer, RandomTextureVariants skins, EquipmentSlotType slot, float prop, Predicate<E> gender) {
		super(renderer, newManOutfitModel(), skins, slot, prop, gender);
	}

	private static final <E extends AbstractMannishEntity> ArmsStyleModelProvider<E, ManModel<E>> newManOutfitModel() {
		return smallArms -> new ManModel<>(0.6F, false, smallArms);
	}
}
