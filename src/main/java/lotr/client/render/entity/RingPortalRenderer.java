package lotr.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.client.LOTRClientProxy;
import lotr.client.render.entity.model.RingPortalModel;
import lotr.client.util.LOTRClientUtil;
import lotr.common.entity.item.RingPortalEntity;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;

public class RingPortalRenderer extends EntityRenderer {
	public static final ResourceLocation RING_TEXTURE = new ResourceLocation("lotr", "textures/entity/portal/ring.png");
	public static final ResourceLocation SCRIPT_TEXTURE = new ResourceLocation("lotr", "textures/entity/portal/script.png");
	private static final RingPortalModel MODEL_RING = new RingPortalModel(false);
	private static final RingPortalModel MODEL_SCRIPT = new RingPortalModel(true);

	public RingPortalRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	protected int getBlockLight(Entity entity, BlockPos pos) {
		return 15;
	}

	@Override
	public ResourceLocation getTextureLocation(Entity entity) {
		return RING_TEXTURE;
	}

	@Override
	public void render(Entity portal, float yaw, float ticks, MatrixStack matStack, IRenderTypeBuffer buf, int packedLight) {
		matStack.pushPose();
		matStack.translate(0.0D, 0.75D, 0.0D);
		matStack.scale(1.0F, -1.0F, -1.0F);
		float portalScale = ((RingPortalEntity) portal).getPortalScale(ticks);
		matStack.scale(portalScale, portalScale, portalScale);
		matStack.mulPose(Vector3f.YP.rotationDegrees(((RingPortalEntity) portal).getPortalRotation(ticks)));
		matStack.mulPose(Vector3f.XP.rotationDegrees(10.0F));
		renderRingModel(MODEL_RING, RenderType.entityCutoutNoCull(RING_TEXTURE), matStack, buf, packedLight, 1.0F);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		int fullbright = LOTRClientProxy.MAX_LIGHTMAP;
		float scriptBr = ((RingPortalEntity) portal).getScriptBrightness(ticks);
		matStack.pushPose();
		matStack.scale(1.05F, 1.05F, 1.05F);
		renderRingModel(MODEL_SCRIPT, RenderType.entityTranslucent(SCRIPT_TEXTURE), matStack, buf, fullbright, scriptBr);
		matStack.popPose();
		matStack.pushPose();
		matStack.scale(0.85F, 0.85F, 0.85F);
		renderRingModel(MODEL_SCRIPT, RenderType.entityTranslucent(SCRIPT_TEXTURE), matStack, buf, fullbright, scriptBr);
		matStack.popPose();
		RenderSystem.disableBlend();
		matStack.popPose();
	}

	private void renderRingModel(Model model, RenderType renType, MatrixStack mat, IRenderTypeBuffer buf, int light, float alpha) {
		IVertexBuilder vb = buf.getBuffer(renType);
		int overlay = LOTRClientUtil.getPackedNoOverlay();
		model.renderToBuffer(mat, vb, light, overlay, 1.0F, 1.0F, 1.0F, alpha);
	}
}
