package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DorwinionElvenHelmetModel extends SpecialArmorModel {
	public DorwinionElvenHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public DorwinionElvenHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		head.texOffs(20, 16).addBox(0.0F, -10.0F, 4.0F, 0.0F, 10.0F, 4.0F, 0.0F);
		head.texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f + 0.5F);
		ModelRenderer crest = new ModelRenderer(this, 0, 16);
		crest.setPos(0.0F, -f, 0.0F);
		crest.addBox(-1.0F, -11.0F, -6.0F, 2.0F, 5.0F, 8.0F, 0.0F);
		crest.xRot = (float) Math.toRadians(-15.0D);
		head.addChild(crest);
	}
}
