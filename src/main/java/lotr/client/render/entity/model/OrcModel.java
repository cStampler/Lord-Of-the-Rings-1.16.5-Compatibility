package lotr.client.render.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;

public class OrcModel extends LOTRBipedModel {
	private ModelRenderer nose;
	private ModelRenderer earRight;
	private ModelRenderer earLeft;

	public OrcModel(boolean smallArms) {
		this(0.0F, false, smallArms);
	}

	public OrcModel(float f) {
		this(f, true, false);
	}

	public OrcModel(float f, boolean isArmor, boolean smallArms) {
		super(f, 0.0F, isArmor, smallArms);
		if (!isArmor) {
			createLongHairModel(0.0F, f);
		}

		nose = new ModelRenderer(this, 14, 17);
		nose.addBox(-0.5F, -4.0F, -4.8F, 1.0F, 2.0F, 1.0F, f);
		nose.setPos(0.0F, 0.0F, 0.0F);
		earRight = new ModelRenderer(this, 0, 0);
		earRight.addBox(-3.5F, -5.5F, 2.0F, 1.0F, 2.0F, 3.0F, f);
		earRight.setPos(0.0F, 0.0F, 0.0F);
		earRight.xRot = (float) Math.toRadians(15.0D);
		earRight.yRot = (float) Math.toRadians(-30.0D);
		earRight.zRot = (float) Math.toRadians(-13.0D);
		earLeft = new ModelRenderer(this, 24, 0);
		earLeft.addBox(2.5F, -5.5F, 2.0F, 1.0F, 2.0F, 3.0F, f);
		earLeft.setPos(0.0F, 0.0F, 0.0F);
		earLeft.xRot = earRight.xRot;
		earLeft.yRot = -earRight.yRot;
		earLeft.zRot = -earRight.zRot;
		head.addChild(nose);
		head.addChild(earRight);
		head.addChild(earLeft);
	}
}
