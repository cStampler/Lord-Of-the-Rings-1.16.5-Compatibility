package lotr.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.render.entity.model.ElfModel;
import lotr.common.entity.npc.ElfEntity;
import lotr.common.util.CalendarUtil;
import net.minecraft.client.renderer.entity.EntityRendererManager;

public abstract class AbstractElfRenderer extends LOTRBipedRenderer {
	public AbstractElfRenderer(EntityRendererManager mgr) {
		super(mgr, ElfModel::new, new ElfModel(0.5F), new ElfModel(1.0F), 0.5F);
	}

	protected void preRenderCallback(ElfEntity elf, MatrixStack matStack, float f) {
		super.preRenderCallback(elf, matStack, f);
		if (CalendarUtil.isAprilFools()) {
			float scale = 0.25F;
			matStack.scale(scale, scale, scale);
		}

	}
}
