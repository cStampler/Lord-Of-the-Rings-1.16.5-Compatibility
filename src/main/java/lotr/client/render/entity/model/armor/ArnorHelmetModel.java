package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ArnorHelmetModel extends SpecialArmorModel {
	public ArnorHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public ArnorHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		head.texOffs(32, 0).addBox(-4.5F - f, -13.0F - f, -1.0F, 1.0F, 8.0F, 1.0F, 0.0F);
		head.texOffs(36, 0).addBox(-4.5F - f, -12.0F - f, 0.0F, 1.0F, 7.0F, 1.0F, 0.0F);
		head.texOffs(40, 0).addBox(-4.5F - f, -11.0F - f, 1.0F, 1.0F, 5.0F, 1.0F, 0.0F);
		head.mirror = true;
		head.texOffs(32, 0).addBox(3.5F + f, -13.0F - f, -1.0F, 1.0F, 8.0F, 1.0F, 0.0F);
		head.texOffs(36, 0).addBox(3.5F + f, -12.0F - f, 0.0F, 1.0F, 7.0F, 1.0F, 0.0F);
		head.texOffs(40, 0).addBox(3.5F + f, -11.0F - f, 1.0F, 1.0F, 5.0F, 1.0F, 0.0F);
	}
}
