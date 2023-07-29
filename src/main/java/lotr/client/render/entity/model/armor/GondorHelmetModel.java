package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class GondorHelmetModel extends SpecialArmorModel {
	public GondorHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public GondorHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		head.texOffs(0, 16).addBox(-1.5F, -9.0F, -3.5F, 3.0F, 1.0F, 7.0F, f);
		head.texOffs(20, 16).addBox(-0.5F, -10.0F, -3.5F, 1.0F, 1.0F, 7.0F, f);
		head.texOffs(24, 0).addBox(-1.5F, -10.5F - f, -4.5F - f, 3.0F, 4.0F, 1.0F, 0.0F);
		head.texOffs(24, 5).addBox(-0.5F, -11.5F - f, -4.5F - f, 1.0F, 1.0F, 1.0F, 0.0F);
		head.texOffs(28, 5).addBox(-0.5F, -6.5F - f, -4.5F - f, 1.0F, 1.0F, 1.0F, 0.0F);
		head.texOffs(32, 0).addBox(-1.5F, -9.5F - f, 3.5F + f, 3.0F, 3.0F, 1.0F, 0.0F);
		head.texOffs(32, 4).addBox(-0.5F, -10.5F - f, 3.5F + f, 1.0F, 1.0F, 1.0F, 0.0F);
		head.texOffs(36, 4).addBox(-0.5F, -6.5F - f, 3.5F + f, 1.0F, 1.0F, 1.0F, 0.0F);
	}
}
