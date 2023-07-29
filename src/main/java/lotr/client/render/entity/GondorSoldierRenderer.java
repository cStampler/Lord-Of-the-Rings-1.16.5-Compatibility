package lotr.client.render.entity;

import lotr.client.render.RandomTextureVariants;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class GondorSoldierRenderer extends GondorManRenderer {
	private static final RandomTextureVariants SKINS = RandomTextureVariants.loadSkinsList("lotr", "textures/entity/gondor/gondor_soldier");

	public GondorSoldierRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity man) {
		return SKINS.getRandomSkin(man);
	}
}
