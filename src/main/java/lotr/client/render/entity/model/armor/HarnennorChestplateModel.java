package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HarnennorChestplateModel extends SpecialArmorModel {
	public HarnennorChestplateModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public HarnennorChestplateModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonChestplateParts();
		body = new ModelRenderer(this, 16, 16);
		body.setPos(0.0F, 0.0F, 0.0F);
		body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, f);
		rightArm = new ModelRenderer(this, 40, 16);
		rightArm.setPos(-5.0F, 2.0F, 0.0F);
		rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		rightArm.texOffs(46, 0);
		rightArm.addBox(-4.0F - f, -3.0F - f, -2.0F, 5.0F, 1.0F, 4.0F, 0.0F);
		ModelRenderer rightBarbs1 = new ModelRenderer(this, 29, 0);
		rightBarbs1.setPos(-1.5F, -2.5F - f, -2.0F);
		rightBarbs1.addBox(-2.5F, 0.0F, -2.0F, 5.0F, 0.0F, 2.0F, 0.0F);
		rightBarbs1.xRot = (float) Math.toRadians(30.0D);
		rightArm.addChild(rightBarbs1);
		ModelRenderer rightBarbs2 = new ModelRenderer(this, 29, 3);
		rightBarbs2.setPos(-1.5F, -2.5F - f, 2.0F);
		rightBarbs2.addBox(-2.5F, 0.0F, 0.0F, 5.0F, 0.0F, 2.0F, 0.0F);
		rightBarbs2.xRot = (float) Math.toRadians(-30.0D);
		rightArm.addChild(rightBarbs2);
		leftArm = new ModelRenderer(this, 40, 16);
		leftArm.setPos(5.0F, 2.0F, 0.0F);
		leftArm.mirror = true;
		leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		leftArm.texOffs(46, 0);
		leftArm.addBox(-1.0F + f, -3.0F - f, -2.0F, 5.0F, 1.0F, 4.0F, 0.0F);
		ModelRenderer leftBarbs1 = new ModelRenderer(this, 29, 0);
		leftBarbs1.setPos(1.5F, -2.5F - f, -2.0F);
		leftBarbs1.mirror = true;
		leftBarbs1.addBox(-2.5F, 0.0F, -2.0F, 5.0F, 0.0F, 2.0F, 0.0F);
		leftBarbs1.xRot = (float) Math.toRadians(30.0D);
		leftArm.addChild(leftBarbs1);
		ModelRenderer leftBarbs2 = new ModelRenderer(this, 29, 3);
		leftBarbs2.setPos(1.5F, -2.5F - f, 2.0F);
		leftBarbs2.mirror = true;
		leftBarbs2.addBox(-2.5F, 0.0F, 0.0F, 5.0F, 0.0F, 2.0F, 0.0F);
		leftBarbs2.xRot = (float) Math.toRadians(-30.0D);
		leftArm.addChild(leftBarbs2);
	}
}
