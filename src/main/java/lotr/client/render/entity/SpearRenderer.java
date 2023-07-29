package lotr.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.common.entity.projectile.SpearEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class SpearRenderer extends EntityRenderer {
	public SpearRenderer(EntityRendererManager mgr) {
		super(mgr);
	}

	@Override
	public ResourceLocation getTextureLocation(Entity spear) {
		return PlayerContainer.BLOCK_ATLAS;
	}

	@Override
	public void render(Entity spear, float yaw, float ticks, MatrixStack matStack, IRenderTypeBuffer buf, int light) {
		matStack.pushPose();
		matStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(ticks, spear.yRotO, spear.yRot) - 90.0F));
		matStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(ticks, spear.xRotO, spear.xRot)));
		float shake = ((SpearEntity) spear).shakeTime - ticks;
		if (shake > 0.0F) {
			float shakeAngle = -MathHelper.sin(shake * 3.0F) * shake;
			matStack.mulPose(Vector3f.ZP.rotationDegrees(shakeAngle));
		}

		matStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		matStack.mulPose(Vector3f.XP.rotationDegrees(100.0F));
		matStack.translate(0.0D, -1.0D, 0.0D);
		ItemStack spearItem = ((SpearEntity) spear).getSpearItem();
		Minecraft.getInstance().getItemRenderer().renderStatic(spearItem, TransformType.THIRD_PERSON_RIGHT_HAND, light, OverlayTexture.NO_OVERLAY, matStack, buf);
		matStack.popPose();
	}
}
