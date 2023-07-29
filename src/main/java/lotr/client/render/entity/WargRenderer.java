package lotr.client.render.entity;

import lotr.client.render.entity.model.WargModel;
import lotr.common.entity.npc.WargEntity;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class WargRenderer extends MobRenderer {
	public WargRenderer(EntityRendererManager mgr) {
		super(mgr, new WargModel(0.0F), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity warg) {
		return ((WargEntity) warg).getWargType().getTexture();
	}
}
