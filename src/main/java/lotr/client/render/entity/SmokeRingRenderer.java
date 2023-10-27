package lotr.client.render.entity;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.matrix.MatrixStack.Entry;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.client.render.LOTRRenderTypes;
import lotr.client.render.entity.model.SmokeShipModel;
import lotr.common.entity.projectile.SmokeRingEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class SmokeRingRenderer extends EntityRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("lotr", "textures/entity/misc/smoke_ring.png");
	private final Model shipModel = new SmokeShipModel();

	public SmokeRingRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity smokeRing) {
		return TEXTURE;
	}

	@Override
	public void render(Entity smokeRing, float yaw, float ticks, MatrixStack matStack, IRenderTypeBuffer buf, int light) {
		matStack.pushPose();
		matStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(ticks, smokeRing.yRotO, smokeRing.yRot)));
		matStack.mulPose(Vector3f.XP.rotationDegrees(-MathHelper.lerp(ticks, smokeRing.xRotO, smokeRing.xRot)));
		float age = ((SmokeRingEntity) smokeRing).getRenderSmokeAge(ticks);
		float alpha = 1.0F - age;
		float[] rgb = Arrays.copyOf(((SmokeRingEntity) smokeRing).getSmokeColor().getTextureDiffuseColors(), 3);
		float colorIntensity = 0.65F;

		int overlay;
		for (overlay = 0; overlay < rgb.length; ++overlay) {
			rgb[overlay] = MathHelper.lerp(colorIntensity, 1.0F, rgb[overlay]);
		}

		overlay = OverlayTexture.NO_OVERLAY;
		float scale = ((SmokeRingEntity) smokeRing).getSmokeScale();
		float shipScale;
		RenderType renderType;
		if (((SmokeRingEntity) smokeRing).isMagicSmoke()) {
			shipScale = 0.3F * scale;
			matStack.scale(shipScale, shipScale, shipScale);
			matStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
			renderType = LOTRRenderTypes.ENTITY_TRANSLUCENT_NO_TEXTURE;
			shipModel.renderToBuffer(matStack, buf.getBuffer(renderType), light, overlay, rgb[0], rgb[1], rgb[2], alpha * 0.75F);
		} else {
			shipScale = (0.1F + 0.9F * age) * scale;
			matStack.scale(shipScale, shipScale, shipScale);
			renderType = RenderType.entityTranslucent(getTextureLocation(smokeRing));
			renderSprite(matStack, buf.getBuffer(renderType), light, overlay, rgb[0], rgb[1], rgb[2], alpha);
		}

		matStack.popPose();
	}

	private void renderSprite(MatrixStack matStack, IVertexBuilder vb, int light, int overlay, float r, float g, float b, float a) {
		Entry last = matStack.last();
		Matrix4f mat = last.pose();
		Matrix3f normal = last.normal();
		float halfWidth = 0.5F;
		float z = 0.0F;
		float u0 = 0.0F;
		float u1 = 1.0F;
		float v0 = 0.0F;
		float v1 = 1.0F;
		vb.vertex(mat, -halfWidth, halfWidth, z).color(r, g, b, a).uv(u0, v1).overlayCoords(overlay).uv2(light).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		vb.vertex(mat, halfWidth, halfWidth, z).color(r, g, b, a).uv(u1, v1).overlayCoords(overlay).uv2(light).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		vb.vertex(mat, halfWidth, -halfWidth, z).color(r, g, b, a).uv(u1, v0).overlayCoords(overlay).uv2(light).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
		vb.vertex(mat, -halfWidth, -halfWidth, z).color(r, g, b, a).uv(u0, v0).overlayCoords(overlay).uv2(light).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
	}
}
