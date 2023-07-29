package lotr.client.render.entity.model.armor;

import java.util.*;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.render.entity.model.LOTRBipedModel;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.vector.Vector3f;

public abstract class SpecialArmorModel extends LOTRBipedModel {
	private final BipedModel referenceBipedModel;
	private boolean savedDefaultAngles;
	private Vector3f defaultHeadRotationPoint;
	private Vector3f defaultRightLegRotationPoint;
	private Vector3f defaultLeftLegRotationPoint;

	public SpecialArmorModel(BipedModel referenceBipedModel, float modelSize) {
		this(referenceBipedModel, modelSize, 0.0F, 64, 32);
	}

	protected SpecialArmorModel(BipedModel referenceBipedModel, float modelSize, float yOffset, int texWidth, int texHeight) {
		super(modelSize, yOffset, texWidth, texHeight, true, false);
		this.referenceBipedModel = referenceBipedModel;
	}

	protected final void clearArmorPartsExcept(ModelRenderer... exceptions) {
		List exceptList = Arrays.asList(exceptions);
		if (!exceptList.contains(head)) {
			head = new ModelRenderer(this, 0, 0);
		}

		if (!exceptList.contains(hat)) {
			hat = new ModelRenderer(this, 0, 0);
		}

		if (!exceptList.contains(body)) {
			body = new ModelRenderer(this, 0, 0);
		}

		if (!exceptList.contains(rightArm)) {
			rightArm = new ModelRenderer(this, 0, 0);
		}

		if (!exceptList.contains(leftArm)) {
			leftArm = new ModelRenderer(this, 0, 0);
		}

		if (!exceptList.contains(rightLeg)) {
			rightLeg = new ModelRenderer(this, 0, 0);
		}

		if (!exceptList.contains(leftLeg)) {
			leftLeg = new ModelRenderer(this, 0, 0);
		}

	}

	protected final void clearNonChestplateParts() {
		clearArmorPartsExcept(body, rightArm, leftArm);
	}

	protected final void clearNonHelmetParts() {
		clearArmorPartsExcept(head);
	}

	private void copyArmorStandRotation(ModelRenderer part, Rotations rotation) {
		part.xRot = (float) Math.toRadians(rotation.getX());
		part.yRot = (float) Math.toRadians(rotation.getY());
		part.zRot = (float) Math.toRadians(rotation.getZ());
	}

	@Override
	public void postChildHeadCallback(MatrixStack matStack) {
		if (referenceBipedModel instanceof LOTRBipedModel) {
			((LOTRBipedModel) referenceBipedModel).postChildHeadCallback(matStack);
		}

	}

	@Override
	public void preBodyCallback(MatrixStack matStack) {
		if (referenceBipedModel instanceof LOTRBipedModel) {
			((LOTRBipedModel) referenceBipedModel).preBodyCallback(matStack);
		}

	}

	@Override
	public void preHeadCallback(MatrixStack matStack) {
		if (referenceBipedModel instanceof LOTRBipedModel) {
			((LOTRBipedModel) referenceBipedModel).preHeadCallback(matStack);
		}

	}

	@Override
	public void preLeftArmCallback(MatrixStack matStack) {
		if (referenceBipedModel instanceof LOTRBipedModel) {
			((LOTRBipedModel) referenceBipedModel).preLeftArmCallback(matStack);
		}

	}

	@Override
	public void preLeftLegCallback(MatrixStack matStack) {
		if (referenceBipedModel instanceof LOTRBipedModel) {
			((LOTRBipedModel) referenceBipedModel).preLeftLegCallback(matStack);
		}

	}

	@Override
	public void preRenderAllCallback(MatrixStack matStack) {
		if (referenceBipedModel instanceof LOTRBipedModel) {
			((LOTRBipedModel) referenceBipedModel).preRenderAllCallback(matStack);
		}

	}

	@Override
	public void preRightArmCallback(MatrixStack matStack) {
		if (referenceBipedModel instanceof LOTRBipedModel) {
			((LOTRBipedModel) referenceBipedModel).preRightArmCallback(matStack);
		}

	}

	@Override
	public void preRightLegCallback(MatrixStack matStack) {
		if (referenceBipedModel instanceof LOTRBipedModel) {
			((LOTRBipedModel) referenceBipedModel).preRightLegCallback(matStack);
		}

	}

	private void restoreRotationPoint(ModelRenderer part, Vector3f savedPos) {
		part.setPos(savedPos.x(), savedPos.y(), savedPos.z());
	}

	private Vector3f saveRotationPoint(ModelRenderer part) {
		return new Vector3f(part.x, part.y, part.z);
	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!savedDefaultAngles) {
			savedDefaultAngles = true;
			defaultHeadRotationPoint = saveRotationPoint(head);
			defaultRightLegRotationPoint = saveRotationPoint(rightLeg);
			defaultLeftLegRotationPoint = saveRotationPoint(leftLeg);
		}

		if (entity instanceof ArmorStandEntity) {
			ArmorStandEntity stand = (ArmorStandEntity) entity;
			copyArmorStandRotation(head, stand.getHeadPose());
			head.setPos(0.0F, 1.0F, 0.0F);
			copyArmorStandRotation(body, stand.getBodyPose());
			copyArmorStandRotation(leftArm, stand.getLeftArmPose());
			copyArmorStandRotation(rightArm, stand.getRightArmPose());
			copyArmorStandRotation(leftLeg, stand.getLeftLegPose());
			leftLeg.setPos(1.9F, 11.0F, 0.0F);
			copyArmorStandRotation(rightLeg, stand.getRightLegPose());
			rightLeg.setPos(-1.9F, 11.0F, 0.0F);
			hat.copyFrom(head);
		} else {
			restoreRotationPoint(head, defaultHeadRotationPoint);
			restoreRotationPoint(rightLeg, defaultRightLegRotationPoint);
			restoreRotationPoint(leftLeg, defaultLeftLegRotationPoint);
			hat.copyFrom(head);
			super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		}

	}
}
