package lotr.client.render.entity.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lotr.client.event.LOTRTickHandlerClient;
import lotr.common.entity.npc.WargEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class WargModel extends SegmentedModel {
	private final ModelRenderer frontBody;
	private final ModelRenderer backHairL;
	private final ModelRenderer backHairR;
	private final ModelRenderer head;
	private final ModelRenderer earR;
	private final ModelRenderer earL;
	private final ModelRenderer maneTop;
	private final ModelRenderer maneR;
	private final ModelRenderer maneL;
	private final ModelRenderer frontLegR;
	private final ModelRenderer frontLegL;
	private final ModelRenderer backBody;
	private final ModelRenderer tail;
	private final ModelRenderer backLegR;
	private final ModelRenderer backLegL;
	private final List topLevelParts;

	public WargModel(float f) {
		texWidth = 128;
		texHeight = 64;
		frontBody = new ModelRenderer(this, 0, 38);
		frontBody.setPos(0.0F, 7.5F, 2.0F);
		frontBody.addBox(-6.0F, -5.5F, -14.0F, 12.0F, 11.0F, 15.0F, f);
		backHairL = new ModelRenderer(this, 36, 13);
		backHairL.mirror = true;
		backHairL.setPos(1.0F, -5.5F, -7.0F);
		backHairL.addBox(0.0F, -4.0F, -7.0F, 0.0F, 4.0F, 14.0F, 0.0F);
		backHairL.zRot = (float) Math.toRadians(22.5D);
		frontBody.addChild(backHairL);
		backHairR = new ModelRenderer(this, 36, 13);
		backHairR.setPos(-1.0F, -5.5F, -7.0F);
		backHairR.addBox(0.0F, -4.0F, -7.0F, 0.0F, 4.0F, 14.0F, 0.0F);
		backHairR.zRot = (float) Math.toRadians(-22.5D);
		frontBody.addChild(backHairR);
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 3.5F, -14.0F);
		head.addBox(-5.0F, -8.0F, -8.0F, 10.0F, 8.0F, 8.0F, f);
		head.texOffs(0, 16).addBox(-3.0F, -5.0F, -14.0F, 6.0F, 5.0F, 6.0F, f);
		frontBody.addChild(head);
		earR = new ModelRenderer(this, 36, 4);
		earR.setPos(-1.8301F, -9.5F, -2.7679F);
		earR.addBox(-0.5F, -2.5F, -3.5F, 2.0F, 5.0F, 4.0F, f);
		earR.xRot = (float) Math.toRadians(-165.0D);
		earR.yRot = (float) Math.toRadians(60.0D);
		earR.zRot = (float) Math.toRadians(-180.0D);
		head.addChild(earR);
		earL = new ModelRenderer(this, 36, 4);
		earL.setPos(2.5562F, -9.3876F, -3.0242F);
		earL.addBox(-1.0F, -2.5F, -1.0F, 2.0F, 5.0F, 4.0F, f);
		earL.xRot = (float) Math.toRadians(-15.0D);
		earL.yRot = (float) Math.toRadians(60.0D);
		head.addChild(earL);
		maneTop = new ModelRenderer(this, 44, 31);
		maneTop.setPos(0.0F, -8.0F, -1.0F);
		maneTop.addBox(-5.0F, -5.0F, 0.0F, 10.0F, 5.0F, 0.0F, 0.0F);
		maneTop.xRot = (float) Math.toRadians(-45.0D);
		head.addChild(maneTop);
		maneR = new ModelRenderer(this, 28, 0);
		maneR.setPos(-5.0F, -4.0F, -3.0F);
		maneR.addBox(-5.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.0F, 0.0F);
		maneR.yRot = (float) Math.toRadians(45.0D);
		head.addChild(maneR);
		maneL = new ModelRenderer(this, 28, 0);
		maneL.mirror = true;
		maneL.setPos(5.0F, -4.0F, -3.0F);
		maneL.addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.0F, 0.0F);
		maneL.yRot = (float) Math.toRadians(-45.0D);
		head.addChild(maneL);
		frontLegR = new ModelRenderer(this, 66, 0);
		frontLegR.setPos(-5.0F, -1.5F, -6.5F);
		frontLegR.addBox(-2.0F, -5.0F, -4.5F, 4.0F, 11.0F, 8.0F, f);
		frontLegR.texOffs(70, 19).addBox(-2.0F, 6.0F, -1.5F, 4.0F, 12.0F, 4.0F, f);
		frontBody.addChild(frontLegR);
		frontLegL = new ModelRenderer(this, 66, 0);
		frontLegL.mirror = true;
		frontLegL.setPos(5.0F, -1.5F, -6.5F);
		frontLegL.addBox(-2.0F, -5.0F, -4.5F, 4.0F, 11.0F, 8.0F, f);
		frontLegL.texOffs(70, 19).addBox(-2.0F, 6.0F, -1.5F, 4.0F, 12.0F, 4.0F, f);
		frontBody.addChild(frontLegL);
		backBody = new ModelRenderer(this, 54, 39);
		backBody.setPos(0.0F, 7.5F, 2.0F);
		backBody.addBox(-5.0F, -4.7F, -1.0F, 10.0F, 10.0F, 15.0F, f);
		tail = new ModelRenderer(this, 92, 33);
		tail.setPos(-0.5F, -2.0F, 13.5F);
		tail.addBox(-2.0F, -1.5F, -1.5F, 5.0F, 5.0F, 13.0F, f);
		tail.xRot = (float) Math.toRadians(-45.0D);
		backBody.addChild(tail);
		backLegR = new ModelRenderer(this, 102, 16);
		backLegR.setPos(-4.0F, 1.5F, 9.0F);
		backLegR.addBox(-2.0F, 5.0F, 0.0F, 4.0F, 10.0F, 4.0F, f);
		backLegR.texOffs(99, 0).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 9.0F, 7.0F, f);
		backBody.addChild(backLegR);
		backLegL = new ModelRenderer(this, 102, 16);
		backLegL.mirror = true;
		backLegL.setPos(4.0F, 1.5F, 9.0F);
		backLegL.addBox(-2.0F, 5.0F, 0.0F, 4.0F, 10.0F, 4.0F, f);
		backLegL.texOffs(99, 0).addBox(-2.0F, -4.0F, -3.0F, 4.0F, 9.0F, 7.0F, f);
		backBody.addChild(backLegL);
		topLevelParts = ImmutableList.of(backBody, frontBody);
	}

	@Override
	public Iterable parts() {
		return topLevelParts;
	}

	public void setLivingAnimations(WargEntity warg, float limbSwing, float limbSwingAmount, float partialTick) {
	}

	@Override
	public void setupAnim(Entity warg, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		head.xRot = (float) Math.toRadians(headPitch);
		head.yRot = (float) Math.toRadians(netHeadYaw);
		backLegL.xRot = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
		backLegR.xRot = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * limbSwingAmount;
		frontLegL.xRot = MathHelper.cos(limbSwing * 0.6662F + 3.1415927F) * limbSwingAmount;
		frontLegR.xRot = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
		tail.xRot = (float) Math.toRadians(-40.0D + MathHelper.sin(limbSwing * 0.6662F) * limbSwingAmount * 10.0D);
		if (((WargEntity) warg).isAggressive()) {
			frontBody.xRot = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount * (float) Math.toRadians(7.0D);
			backBody.xRot = -frontBody.xRot;
		} else {
			backBody.xRot = frontBody.xRot = 0.0F;
		}

		float leapLerp = ((WargEntity) warg).getLeapingProgress(LOTRTickHandlerClient.renderPartialTick);
		if (leapLerp > 0.0F) {
			float leapingBackLegRotX = (float) Math.toRadians(50.0D);
			float leapingFrontLegRotX = (float) Math.toRadians(-50.0D);
			float leapingBodyRotX = 0.0F;
			float leapingTailRotX = (float) Math.toRadians(-30.0D);
			backLegL.xRot = MathHelper.lerp(leapLerp, backLegL.xRot, leapingBackLegRotX);
			backLegR.xRot = MathHelper.lerp(leapLerp, backLegR.xRot, leapingBackLegRotX);
			frontLegL.xRot = MathHelper.lerp(leapLerp, frontLegL.xRot, leapingFrontLegRotX);
			frontLegR.xRot = MathHelper.lerp(leapLerp, frontLegR.xRot, leapingFrontLegRotX);
			backBody.xRot = MathHelper.lerp(leapLerp, backBody.xRot, leapingBodyRotX);
			frontBody.xRot = MathHelper.lerp(leapLerp, frontBody.xRot, leapingBodyRotX);
			tail.xRot = MathHelper.lerp(leapLerp, tail.xRot, leapingTailRotX);
		}

	}
}
