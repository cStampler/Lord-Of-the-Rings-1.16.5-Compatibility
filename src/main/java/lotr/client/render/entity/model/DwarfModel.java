package lotr.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.util.HandSide;

public class DwarfModel extends LOTRBipedModel {
	public DwarfModel(boolean smallArms) {
		this(0.0F, false, smallArms);
	}

	public DwarfModel(float f) {
		this(f, true, false);
	}

	public DwarfModel(float f, boolean isArmor, boolean smallArms) {
		super(f, 0.0F, isArmor, smallArms);
		if (!isArmor) {
			createLongHairModel(2.0F, f);
		}

	}

	@Override
	public void preBodyCallback(MatrixStack matStack) {
		matStack.scale(1.25F, 1.0F, 1.0F);
	}

	@Override
	public void preLeftArmCallback(MatrixStack matStack) {
		matStack.translate(0.0625D, 0.0D, 0.0D);
	}

	@Override
	public void preLeftLegCallback(MatrixStack matStack) {
		matStack.translate(0.015625D, 0.0D, 0.0D);
		matStack.scale(1.25F, 1.0F, 1.0F);
	}

	@Override
	public void preRightArmCallback(MatrixStack matStack) {
		matStack.translate(-0.0625D, 0.0D, 0.0D);
	}

	@Override
	public void preRightLegCallback(MatrixStack matStack) {
		matStack.translate(-0.015625D, 0.0D, 0.0D);
		matStack.scale(1.25F, 1.0F, 1.0F);
	}

	@Override
	public void translateToHand(HandSide side, MatrixStack matStack) {
		double x = 0.0625D * (side == HandSide.RIGHT ? -1 : 1);
		matStack.translate(x, 0.0D, 0.0D);
		super.translateToHand(side, matStack);
	}
}
