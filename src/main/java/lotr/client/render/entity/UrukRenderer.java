package lotr.client.render.entity;

import lotr.client.render.RandomTextureVariants;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class UrukRenderer extends LargeOrcRenderer {
	private static final RandomTextureVariants SKINS = RandomTextureVariants.loadSkinsList("lotr", "textures/entity/orc/uruk_hai");

	public UrukRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity orc) {
		return SKINS.getRandomSkin(orc);
	}
}
