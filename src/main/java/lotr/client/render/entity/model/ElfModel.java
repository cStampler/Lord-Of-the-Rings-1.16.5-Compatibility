package lotr.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.common.entity.npc.ElfEntity;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ElfModel<E extends ElfEntity> extends LOTRBipedModel<E> {
	private final ModelRenderer earRight;
	private final ModelRenderer earLeft;

	public ElfModel(boolean smallArms) {
		this(0.0F, false, smallArms);
	}

	public ElfModel(float f) {
		this(f, true, false);
	}

	public ElfModel(float f, boolean isArmor, boolean smallArms) {
		super(f, 0.0F, isArmor, smallArms);
		if (!isArmor) {
			earRight = new ModelRenderer(this, 0, 0);
			earRight.addBox(-4.0F, -6.5F, -1.0F, 1.0F, 4.0F, 2.0F);
			earRight.setPos(0.0F, 0.0F, 0.0F);
			earRight.zRot = (float) Math.toRadians(-15.0D);
			earLeft = new ModelRenderer(this, 26, 0);
			earLeft.addBox(3.0F, -6.5F, -1.0F, 1.0F, 4.0F, 2.0F);
			earLeft.setPos(0.0F, 0.0F, 0.0F);
			earLeft.zRot = (float) Math.toRadians(15.0D);
			head.addChild(earRight);
			head.addChild(earLeft);
		} else {
			earRight = earLeft = new ModelRenderer(this);
		}

		if (!isArmor) {
			createLongHairModel(0.0F, f);
		}

	}

	@Override
	public void preBodyCallback(MatrixStack matStack) {
		matStack.translate(0.0D, -0.125D, 0.0D);
		matStack.scale(1.0F, 1.0833334F, 1.0F);
	}

	@Override
	public void preHeadCallback(MatrixStack matStack) {
		matStack.translate(0.0D, -0.125D, 0.0D);
	}

	@Override
	public void preLeftArmCallback(MatrixStack matStack) {
		matStack.translate(0.0D, -0.125D, 0.0D);
		matStack.scale(1.0F, 1.0833334F, 1.0F);
	}

	@Override
	public void preLeftLegCallback(MatrixStack matStack) {
		matStack.translate(0.0D, -0.0625D, 0.0D);
		matStack.scale(1.0F, 1.0833334F, 1.0F);
		matStack.translate(0.0D, -0.0625D, 0.0D);
	}

	@Override
	public void preRightArmCallback(MatrixStack matStack) {
		matStack.translate(0.0D, -0.125D, 0.0D);
		matStack.scale(1.0F, 1.0833334F, 1.0F);
	}

	@Override
	public void preRightLegCallback(MatrixStack matStack) {
		matStack.translate(0.0D, -0.0625D, 0.0D);
		matStack.scale(1.0F, 1.0833334F, 1.0F);
		matStack.translate(0.0D, -0.0625D, 0.0D);
	}
}
