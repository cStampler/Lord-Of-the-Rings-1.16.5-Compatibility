package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HarnennorHelmetModel extends SpecialArmorModel {
	public HarnennorHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public HarnennorHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		head.texOffs(0, 5).addBox(0.0F, -11.0F, -7.0F, 0.0F, 10.0F, 14.0F, 0.0F);
		head.texOffs(16, 19).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 0.0F, 12.0F, 0.0F);
	}
}
