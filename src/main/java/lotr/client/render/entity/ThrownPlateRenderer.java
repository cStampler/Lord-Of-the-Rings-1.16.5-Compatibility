package lotr.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.common.entity.projectile.ThrownPlateEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class ThrownPlateRenderer extends EntityRenderer {
	private static final double ONE_OVER_SQRT_2;

	static {
		ONE_OVER_SQRT_2 = 1.0D / MathHelper.SQRT_OF_TWO;
	}

	public ThrownPlateRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity plate) {
		return PlayerContainer.BLOCK_ATLAS;
	}

	@Override
	public void render(Entity plate, float yaw, float ticks, MatrixStack matStack, IRenderTypeBuffer buf, int light) {
		matStack.pushPose();
		float deg = ((ThrownPlateEntity) plate).getPlateSpin(ticks);
		double radsToUse = Math.toRadians(deg - 45.0F);
		double transX = -MathHelper.cos((float) radsToUse) * ONE_OVER_SQRT_2;
		double transZ = MathHelper.sin((float) radsToUse) * ONE_OVER_SQRT_2;
		matStack.translate(transX, 0.0D, transZ);
		matStack.mulPose(Vector3f.YP.rotationDegrees(deg));
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(((ThrownPlateEntity) plate).getPlateBlockState(), matStack, buf, light, OverlayTexture.NO_OVERLAY);
		matStack.popPose();
	}
}
