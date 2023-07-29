package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class RohanMarshalHelmetModel extends SpecialArmorModel {
	private ModelRenderer[] manes;

	public RohanMarshalHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public RohanMarshalHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		head.texOffs(0, 16).addBox(-1.0F, -11.5F - f, -4.5F - f, 2.0F, 7.0F, 6.0F, 0.0F);
		manes = new ModelRenderer[3];

		for (int i = 0; i < manes.length; ++i) {
			ModelRenderer mane = new ModelRenderer(this, 32, 0);
			manes[i] = mane;
			mane.setPos(0.0F, -f, f);
			mane.addBox(0.0F, -11.0F, -1.0F, 0.0F, 14.0F, 12.0F, 0.0F);
			head.addChild(mane);
		}

	}

	@Override
	public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		float mid = manes.length / 2.0F - 0.5F;

		for (int i = 0; i < manes.length; ++i) {
			ModelRenderer mane = manes[i];
			mane.xRot = (mid - Math.abs(i - mid)) / mid * 0.22F;
			mane.yRot = (i - mid) / mid * 0.17F;
			mane.xRot += MathHelper.sin(limbSwing * 0.4F) * limbSwingAmount * 0.2F;
		}

	}
}
