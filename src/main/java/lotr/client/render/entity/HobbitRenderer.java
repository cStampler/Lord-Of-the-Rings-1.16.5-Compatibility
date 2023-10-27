package lotr.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.render.GenderedRandomTextureVariants;
import lotr.client.render.entity.model.HobbitModel;
import lotr.common.entity.npc.HobbitEntity;
import lotr.common.entity.npc.NPCEntity;
import lotr.common.util.CalendarUtil;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class HobbitRenderer extends LOTRBipedRenderer {
	private static final GenderedRandomTextureVariants SKINS = new GenderedRandomTextureVariants("lotr", "textures/entity/hobbit/hobbit");
	private static final GenderedRandomTextureVariants CHILD_SKINS = new GenderedRandomTextureVariants("lotr", "textures/entity/hobbit/child");

	public HobbitRenderer(EntityRendererManager mgr) {
		super(mgr, HobbitModel::new, new HobbitModel(0.5F), new HobbitModel(1.0F), 0.325F);
	}

	public ResourceLocation getTextureLocation(Entity hobbit) {
		return ((NPCEntity) hobbit).isBaby() ? CHILD_SKINS.getRandomSkin((NPCEntity) hobbit) : SKINS.getRandomSkin((NPCEntity) hobbit);
	}

	protected void preRenderCallback(HobbitEntity hobbit, MatrixStack matStack, float f) {
		super.preRenderCallback(hobbit, matStack, f);
		float hobbitScale = CalendarUtil.isAprilFools() ? 2.0F : 0.65F;
		matStack.scale(hobbitScale, hobbitScale, hobbitScale);
	}
}
