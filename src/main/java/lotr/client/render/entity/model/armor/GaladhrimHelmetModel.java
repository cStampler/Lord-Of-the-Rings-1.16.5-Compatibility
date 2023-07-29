package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class GaladhrimHelmetModel extends SpecialArmorModel {
	public GaladhrimHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public GaladhrimHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		ModelRenderer horn = new ModelRenderer(this, 32, 0);
		horn.addBox(-0.5F, -9.0F - f, 2.0F - f, 1.0F, 3.0F, 3.0F, 0.0F);
		horn.texOffs(32, 6).addBox(-0.5F, -10.0F - f, 3.5F - f, 1.0F, 1.0F, 3.0F, 0.0F);
		horn.texOffs(32, 10).addBox(-0.5F, -11.0F - f, 5.5F - f, 1.0F, 1.0F, 4.0F, 0.0F);
		horn.xRot = (float) Math.toRadians(45.0D);
		head.addChild(horn);
	}
}
