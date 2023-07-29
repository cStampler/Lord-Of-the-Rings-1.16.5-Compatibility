package lotr.client.render.entity;

import lotr.client.render.RandomTextureVariants;
import lotr.client.render.entity.layers.ManOutfitLayer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.inventory.EquipmentSlotType;

public class HarnedhrimWithOutfitRenderer extends HarnedhrimRenderer {
	private static final RandomTextureVariants OUTFITS = RandomTextureVariants.loadSkinsList("lotr", "textures/entity/near_harad/harnennor_outfit");

	public HarnedhrimWithOutfitRenderer(EntityRendererManager mgr) {
		super(mgr);
		addLayer(new ManOutfitLayer(this, OUTFITS, EquipmentSlotType.CHEST, 0.5F));
	}
}
