package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HighElvenHelmetModel extends SpecialArmorModel {
	private ModelRenderer crest;

	public HighElvenHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public HighElvenHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-0.5F, -11.0F, -2.0F, 1.0F, 3.0F, 1.0F, 0.0F);
		head.texOffs(0, 4).addBox(-0.5F, -10.0F, 2.0F, 1.0F, 2.0F, 1.0F, 0.0F);
		crest = new ModelRenderer(this, 32, 0);
		crest.addBox(-1.0F, -11.0F, -8.0F, 2.0F, 1.0F, 11.0F, 0.0F);
		crest.texOffs(32, 12).addBox(-1.0F, -10.0F, -8.0F, 2.0F, 1.0F, 1.0F, 0.0F);
		crest.xRot = (float) Math.toRadians(-16.0D);
		head.addChild(crest);
	}
}
