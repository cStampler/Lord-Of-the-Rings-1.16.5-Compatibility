package lotr.client.render.entity;

import lotr.client.render.RandomTextureVariants;
import lotr.client.render.entity.layers.ManOutfitLayer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.inventory.EquipmentSlotType;

public class DunlendingWithOutfitRenderer extends DunlendingRenderer {
	private static final RandomTextureVariants OUTFITS = RandomTextureVariants.loadSkinsList("lotr", "textures/entity/dunland/outfit");

	public DunlendingWithOutfitRenderer(EntityRendererManager mgr) {
		super(mgr);
		addLayer(new ManOutfitLayer(this, OUTFITS, EquipmentSlotType.CHEST));
	}
}
