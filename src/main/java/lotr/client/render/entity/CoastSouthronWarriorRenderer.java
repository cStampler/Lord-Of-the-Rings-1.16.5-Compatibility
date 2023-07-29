package lotr.client.render.entity;

import lotr.client.render.RandomTextureVariants;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CoastSouthronWarriorRenderer extends CoastSouthronRenderer {
	private static final RandomTextureVariants SKINS = RandomTextureVariants.loadSkinsList("lotr", "textures/entity/near_harad/coast_southron_warrior");

	public CoastSouthronWarriorRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity man) {
		return SKINS.getRandomSkin(man);
	}
}
