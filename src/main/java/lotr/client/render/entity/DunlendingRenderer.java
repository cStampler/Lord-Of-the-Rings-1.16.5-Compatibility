package lotr.client.render.entity;

import lotr.client.render.GenderedRandomTextureVariants;
import lotr.common.entity.npc.NPCEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class DunlendingRenderer extends AbstractManRenderer {
	private static final GenderedRandomTextureVariants SKINS = new GenderedRandomTextureVariants("lotr", "textures/entity/dunland/dunlending");

	public DunlendingRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	public ResourceLocation getTextureLocation(Entity man) {
		return SKINS.getRandomSkin((NPCEntity) man);
	}
}
