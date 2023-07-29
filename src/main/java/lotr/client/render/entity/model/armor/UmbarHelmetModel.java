package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class UmbarHelmetModel extends SpecialArmorModel {
	public UmbarHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public UmbarHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		hat = new ModelRenderer(this, 32, 0);
		hat.setPos(0.0F, 0.0F, 0.0F);
		hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f + 0.5F);
		head.texOffs(0, 0);
		head.addBox(-0.5F, -11.0F - f, -3.0F, 1.0F, 3.0F, 1.0F, 0.0F);
		head.addBox(-0.5F, -10.0F - f, 2.0F, 1.0F, 2.0F, 1.0F, 0.0F);
		head.texOffs(0, 16).addBox(0.0F, -13.0F - f, -6.0F, 0.0F, 4.0F, 12.0F, 0.0F);
	}
}
