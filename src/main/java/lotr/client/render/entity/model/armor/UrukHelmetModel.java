package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class UrukHelmetModel extends SpecialArmorModel {
	private ModelRenderer crest;
	private ModelRenderer jaw;

	public UrukHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public UrukHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		crest = new ModelRenderer(this, 0, 22);
		crest.addBox(-10.0F, -16.0F, -1.0F, 20.0F, 10.0F, 0.0F, 0.0F);
		crest.xRot = (float) Math.toRadians(-10.0D);
		head.addChild(crest);
		jaw = new ModelRenderer(this, 0, 16);
		jaw.addBox(-6.0F, 2.0F, -4.0F, 12.0F, 6.0F, 0.0F, 0.0F);
		jaw.xRot = (float) Math.toRadians(-60.0D);
		head.addChild(jaw);
	}
}
