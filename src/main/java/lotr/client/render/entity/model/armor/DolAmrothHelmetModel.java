package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DolAmrothHelmetModel extends SpecialArmorModel {
	public DolAmrothHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public DolAmrothHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		head.texOffs(32, 0).addBox(-0.5F, -9.0F, -3.5F, 1.0F, 1.0F, 7.0F, f);
		ModelRenderer wingRight = new ModelRenderer(this, 0, 16);
		wingRight.addBox(-4.0F - f, -6.0F, 1.0F + f, 1.0F, 1.0F, 9.0F, 0.0F);
		wingRight.texOffs(20, 16).addBox(-3.5F - f, -5.0F, 1.9F + f, 0.0F, 6.0F, 8.0F, 0.0F);
		ModelRenderer wingLeft = new ModelRenderer(this, 0, 16);
		wingLeft.mirror = true;
		wingLeft.addBox(3.0F + f, -6.0F, 1.0F + f, 1.0F, 1.0F, 9.0F, 0.0F);
		wingLeft.texOffs(20, 16).addBox(3.5F + f, -5.0F, 1.9F + f, 0.0F, 6.0F, 8.0F, 0.0F);
		wingRight.yRot = (float) Math.toRadians(-25.0D);
		wingLeft.yRot = -wingRight.yRot;
		wingRight.xRot = wingLeft.xRot = (float) Math.toRadians(20.0D);
		head.addChild(wingRight);
		head.addChild(wingLeft);
	}
}
