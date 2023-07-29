package lotr.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.render.entity.model.DwarfModel;
import lotr.common.entity.npc.DwarfEntity;
import lotr.common.util.CalendarUtil;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.vector.Vector3f;

public abstract class AbstractDwarfRenderer extends LOTRBipedRenderer {
	public AbstractDwarfRenderer(EntityRendererManager mgr) {
		super(mgr, DwarfModel::new, new DwarfModel(0.5F), new DwarfModel(1.0F), 0.385F);
	}

	protected void preRenderCallback(DwarfEntity dwarf, MatrixStack matStack, float f) {
		super.preRenderCallback(dwarf, matStack, f);
		matStack.scale(0.77F, 0.77F, 0.77F);
		if (CalendarUtil.isAprilFools()) {
			matStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
		}

	}
}
