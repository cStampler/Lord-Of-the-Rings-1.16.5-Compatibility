package lotr.client.render.entity.model;

import java.util.function.*;

import com.google.common.collect.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.entity.npc.ai.NPCTalkAnimations;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.*;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;

public class LOTRBipedModel extends BipedModel {
	protected static final int STANDARD_BIPED_SKIN_WIDTH = 128;
	protected static final int STANDARD_BIPED_SKIN_HEIGHT = 64;
	protected static final int STANDARD_BIPED_ARMOR_WIDTH = 64;
	protected static final int STANDARD_BIPED_ARMOR_HEIGHT = 32;
	protected ModelRenderer bipedChest;
	protected final boolean isArmor;
	public boolean showChest;
	public boolean isEating;
	private float talkingHeadYawRadians;
	private float talkingHeadPitchRadians;
	private float talkingGestureMainhand;
	private float talkingGestureOffhand;
	protected final ModelRenderer bipedLeftArmwear;
	protected final ModelRenderer bipedRightArmwear;
	protected final ModelRenderer bipedLeftLegwear;
	protected final ModelRenderer bipedRightLegwear;
	protected final ModelRenderer bipedBodywear;
	protected final boolean smallArms;

	public LOTRBipedModel(float modelSize, float yOff, boolean isArmor, boolean smallArms) {
		this(modelSize, yOff, isArmor ? 64 : 128, isArmor ? 32 : 64, isArmor, smallArms);
	}

	public LOTRBipedModel(float modelSize, float yOff, int texW, int texH, boolean isArmor, boolean smallArms) {
		this(hummel -> RenderType.entityCutoutNoCull((ResourceLocation) hummel), modelSize, yOff, texW, texH, isArmor, smallArms);
	}

	public LOTRBipedModel(Function renderType, float modelSize, float yOff, int texW, int texH, boolean isArmor, boolean smallArms) {
		super(renderType, modelSize, yOff, texW, texH);
		showChest = false;
		isEating = false;
		talkingHeadYawRadians = 0.0F;
		talkingHeadPitchRadians = 0.0F;
		talkingGestureMainhand = 0.0F;
		talkingGestureOffhand = 0.0F;
		this.isArmor = isArmor;
		this.smallArms = smallArms;
		bipedChest = new ModelRenderer(this, 32, 8);
		if (!isArmor) {
			bipedChest.addBox(-3.0F, 2.0F, -4.0F, 6.0F, 3.0F, 2.0F, modelSize);
			bipedChest.setPos(0.0F, 0.0F, 0.0F);
			body.addChild(bipedChest);
		}

		if (!isArmor) {
			if (smallArms) {
				leftArm = new ModelRenderer(this, 32, 48);
				leftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
				leftArm.setPos(5.0F, 2.5F, 0.0F);
				rightArm = new ModelRenderer(this, 40, 16);
				rightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize);
				rightArm.setPos(-5.0F, 2.5F, 0.0F);
				bipedLeftArmwear = new ModelRenderer(this, 48, 48);
				bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize + 0.25F);
				bipedLeftArmwear.setPos(5.0F, 2.5F, 0.0F);
				bipedRightArmwear = new ModelRenderer(this, 40, 32);
				bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize + 0.25F);
				bipedRightArmwear.setPos(-5.0F, 2.5F, 10.0F);
			} else {
				leftArm = new ModelRenderer(this, 32, 48);
				leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
				leftArm.setPos(5.0F, 2.0F, 0.0F);
				bipedLeftArmwear = new ModelRenderer(this, 48, 48);
				bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize + 0.25F);
				bipedLeftArmwear.setPos(5.0F, 2.0F, 0.0F);
				bipedRightArmwear = new ModelRenderer(this, 40, 32);
				bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize + 0.25F);
				bipedRightArmwear.setPos(-5.0F, 2.0F, 10.0F);
			}

			leftLeg = new ModelRenderer(this, 16, 48);
			leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize);
			leftLeg.setPos(1.9F, 12.0F, 0.0F);
			bipedLeftLegwear = new ModelRenderer(this, 0, 48);
			bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize + 0.25F);
			bipedLeftLegwear.setPos(1.9F, 12.0F, 0.0F);
			bipedRightLegwear = new ModelRenderer(this, 0, 32);
			bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize + 0.25F);
			bipedRightLegwear.setPos(-1.9F, 12.0F, 0.0F);
			bipedBodywear = new ModelRenderer(this, 16, 32);
			bipedBodywear.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, modelSize + 0.25F);
			bipedBodywear.setPos(0.0F, 0.0F, 0.0F);
		} else {
			bipedLeftArmwear = bipedRightArmwear = bipedLeftLegwear = bipedRightLegwear = bipedBodywear = new ModelRenderer(this, 0, 0);
		}

	}

	@Override
	protected Iterable bodyParts() {
		Iterable superBodyParts = super.bodyParts();
		return isArmor ? superBodyParts : Iterables.concat(superBodyParts, ImmutableList.of(bipedLeftLegwear, bipedRightLegwear, bipedLeftArmwear, bipedRightArmwear, bipedBodywear));
	}

	@Override
	public void copyPropertiesTo(EntityModel other) {
		super.copyPropertiesTo(other);
		if (other instanceof BipedModel) {
			BipedModel otherBiped = (BipedModel) other;
			otherBiped.leftArmPose = leftArmPose;
			otherBiped.rightArmPose = rightArmPose;
			otherBiped.crouching = crouching;
		}

		if (other instanceof LOTRBipedModel) {
			LOTRBipedModel otherBiped = (LOTRBipedModel) other;
			otherBiped.showChest = showChest;
			otherBiped.isEating = isEating;
			otherBiped.talkingHeadYawRadians = talkingHeadYawRadians;
			otherBiped.talkingHeadPitchRadians = talkingHeadPitchRadians;
			otherBiped.talkingGestureMainhand = talkingGestureMainhand;
			otherBiped.talkingGestureOffhand = talkingGestureOffhand;
		}

	}

	protected void createLongHairModel(float headRotationY, float f) {
		hat = new ModelRenderer(this, 56, 0);
		hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 24.0F, 8.0F, 0.5F + f);
		hat.setPos(0.0F, headRotationY, 0.0F);
	}

	private void onArm(LivingEntity entity, Hand handType, Consumer action) {
		HandSide mainHandSide = entity.getMainArm();
		if (mainHandSide == HandSide.RIGHT) {
			action.accept(handType == Hand.MAIN_HAND ? rightArm : leftArm);
		} else {
			action.accept(handType == Hand.MAIN_HAND ? leftArm : rightArm);
		}

	}

	public void postChildHeadCallback(MatrixStack matStack) {
	}

	public void preBodyCallback(MatrixStack matStack) {
	}

	public void preHeadCallback(MatrixStack matStack) {
	}

	public void preLeftArmCallback(MatrixStack matStack) {
	}

	public void preLeftLegCallback(MatrixStack matStack) {
	}

	public void preRenderAllCallback(MatrixStack matStack) {
	}

	public void preRightArmCallback(MatrixStack matStack) {
	}

	public void preRightLegCallback(MatrixStack matStack) {
	}

	@Override
	public void renderToBuffer(MatrixStack matStack, IVertexBuilder buf, int packedLight, int packedOverlay, float r, float g, float b, float a) {
		matStack.pushPose();
		preRenderAllCallback(matStack);
		matStack.pushPose();
		preHeadCallback(matStack);
		float scale;
		if (young) {
			scale = 0.75F;
			matStack.scale(scale, scale, scale);
			matStack.translate(0.0D, 1.0D, 0.0D);
			postChildHeadCallback(matStack);
		}

		head.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		hat.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		matStack.popPose();
		matStack.pushPose();
		if (young) {
			scale = 0.5F;
			matStack.scale(scale, scale, scale);
			matStack.translate(0.0D, 1.5D, 0.0D);
		}

		matStack.pushPose();
		preBodyCallback(matStack);
		body.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		bipedBodywear.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		matStack.popPose();
		matStack.pushPose();
		preRightArmCallback(matStack);
		rightArm.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		bipedRightArmwear.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		matStack.popPose();
		matStack.pushPose();
		preLeftArmCallback(matStack);
		leftArm.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		bipedLeftArmwear.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		matStack.popPose();
		matStack.pushPose();
		preRightLegCallback(matStack);
		rightLeg.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		bipedRightLegwear.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		matStack.popPose();
		matStack.pushPose();
		preLeftLegCallback(matStack);
		leftLeg.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		bipedLeftLegwear.render(matStack, buf, packedLight, packedOverlay, r, g, b, a);
		matStack.popPose();
		matStack.popPose();
		matStack.popPose();
	}

	@Override
	public void setAllVisible(boolean visible) {
		super.setAllVisible(visible);
		bipedLeftArmwear.visible = visible;
		bipedRightArmwear.visible = visible;
		bipedLeftLegwear.visible = visible;
		bipedRightLegwear.visible = visible;
		bipedBodywear.visible = visible;
	}

	public void setTalkAnimation(NPCTalkAnimations talkAnims, float f) {
		talkingHeadYawRadians = talkAnims.getHeadYawRadians(f);
		talkingHeadPitchRadians = talkAnims.getHeadPitchRadians(f);
		talkingGestureMainhand = talkAnims.getMainhandGestureAmount(f);
		talkingGestureOffhand = talkAnims.getOffhandGestureAmount(f);
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		bipedChest.visible = showChest;
		if (isEating) {
			onArm(entity, Hand.MAIN_HAND, arm -> {
				((ModelRenderer) arm).xRot = Math.min(((ModelRenderer) arm).xRot, (float) Math.toRadians(-60.0D));
			});
		}

		ModelRenderer var10000 = head;
		var10000.yRot += talkingHeadYawRadians;
		var10000 = head;
		var10000.xRot += talkingHeadPitchRadians;
		var10000 = hat;
		var10000.yRot += talkingHeadYawRadians;
		var10000 = hat;
		var10000.xRot += talkingHeadPitchRadians;
		float rightGestureAmount = entity.getMainArm() == HandSide.RIGHT ? talkingGestureMainhand : talkingGestureOffhand;
		float leftGestureAmount = entity.getMainArm() == HandSide.RIGHT ? talkingGestureOffhand : talkingGestureMainhand;
		var10000 = rightArm;
		var10000.xRot = (float) (var10000.xRot + rightGestureAmount * Math.toRadians(-45.0D));
		var10000 = rightArm;
		var10000.zRot = (float) (var10000.zRot + rightGestureAmount * Math.toRadians(20.0D));
		var10000 = leftArm;
		var10000.xRot = (float) (var10000.xRot + leftGestureAmount * Math.toRadians(-45.0D));
		var10000 = leftArm;
		var10000.zRot = (float) (var10000.zRot + leftGestureAmount * Math.toRadians(-20.0D));
		if (entity instanceof NPCEntity && !riding) {
			rightLeg.yRot = (float) Math.toRadians(5.0D);
			leftLeg.yRot = (float) Math.toRadians(-5.0D);
		}

		if (entity instanceof NPCEntity && ((NPCEntity) entity).isDrunk()) {
			float f6 = ageInTicks / 80.0F;
			float f7 = (ageInTicks + 40.0F) / 80.0F;
			f6 *= 6.2831855F;
			f7 *= 6.2831855F;
			float f8 = MathHelper.sin(f6) * 0.5F;
			float f9 = MathHelper.sin(f7) * 0.5F;
			var10000 = head;
			var10000.xRot += f8;
			var10000 = head;
			var10000.yRot += f9;
			var10000 = hat;
			var10000.xRot += f8;
			var10000 = hat;
			var10000.yRot += f9;
			Hand[] var13 = Hand.values();
			int var14 = var13.length;

			for (int var15 = 0; var15 < var14; ++var15) {
				Hand hand = var13[var15];
				if (!entity.getItemInHand(hand).isEmpty()) {
					onArm(entity, hand, arm -> {
						((ModelRenderer) arm).xRot = (float) Math.toRadians(-60.0D);
					});
				}
			}
		}

		bipedLeftLegwear.copyFrom(leftLeg);
		bipedRightLegwear.copyFrom(rightLeg);
		bipedLeftArmwear.copyFrom(leftArm);
		bipedRightArmwear.copyFrom(rightArm);
		bipedBodywear.copyFrom(body);
	}

	@Override
	public void translateToHand(HandSide side, MatrixStack matStack) {
		ModelRenderer arm = getArm(side);
		if (smallArms) {
			float f = 0.5F * (side == HandSide.RIGHT ? 1 : -1);
			arm.x += f;
			arm.translateAndRotate(matStack);
			arm.x -= f;
		} else {
			arm.translateAndRotate(matStack);
		}

	}
}
