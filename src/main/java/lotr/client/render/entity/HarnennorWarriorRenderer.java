package lotr.client.render.entity;

import lotr.client.render.RandomTextureVariants;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class HarnennorWarriorRenderer extends HarnedhrimRenderer {
	private static final RandomTextureVariants SKINS = RandomTextureVariants.loadSkinsList("lotr", "textures/entity/near_harad/harnennor_warrior");

	public HarnennorWarriorRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity man) {
		return SKINS.getRandomSkin(man);
	}
}
