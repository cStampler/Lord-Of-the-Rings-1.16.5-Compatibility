package lotr.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;

public class HobbitModel extends LOTRBipedModel {
	public HobbitModel(boolean smallArms) {
		this(0.0F, false, smallArms);
	}

	public HobbitModel(float f) {
		this(f, true, false);
	}

	public HobbitModel(float f, boolean isArmor, boolean smallArms) {
		super(f, 0.0F, isArmor, smallArms);
		if (!isArmor) {
			createLongHairModel(2.0F, f);
		}

		if (!isArmor) {
			ModelRenderer rightFoot = new ModelRenderer(this, 64, 48);
			rightFoot.addBox(-2.0F, 10.0F, -5.0F, 4.0F, 2.0F, 3.0F, f);
			rightFoot.yRot = (float) Math.toRadians(10.0D);
			rightLeg.addChild(rightFoot);
			ModelRenderer leftFoot = new ModelRenderer(this, 78, 48);
			leftFoot.addBox(-2.0F, 10.0F, -5.0F, 4.0F, 2.0F, 3.0F, f);
			leftFoot.yRot = (float) Math.toRadians(-10.0D);
			leftLeg.addChild(leftFoot);
		}

	}

	@Override
	public void postChildHeadCallback(MatrixStack matStack) {
		matStack.translate(0.0D, -0.0625D, 0.0D);
	}

	@Override
	public void preBodyCallback(MatrixStack matStack) {
		matStack.scale(1.0F, 0.8333333F, 1.0F);
	}

	@Override
	public void preLeftArmCallback(MatrixStack matStack) {
		matStack.scale(1.0F, 0.8333333F, 1.0F);
	}

	@Override
	public void preLeftLegCallback(MatrixStack matStack) {
		matStack.scale(1.0F, 0.8333333F, 1.0F);
	}

	@Override
	public void preRenderAllCallback(MatrixStack matStack) {
		matStack.translate(0.0D, 0.25D, 0.0D);
	}

	@Override
	public void preRightArmCallback(MatrixStack matStack) {
		matStack.scale(1.0F, 0.8333333F, 1.0F);
	}

	@Override
	public void preRightLegCallback(MatrixStack matStack) {
		matStack.scale(1.0F, 0.8333333F, 1.0F);
	}

	@Override
	public void translateToHand(HandSide handSide, MatrixStack matStack) {
		ModelRenderer arm = getArm(handSide);
		float y = arm.y;
		arm.y += 3.0F;
		arm.translateAndRotate(matStack);
		arm.y = y;
	}
}
