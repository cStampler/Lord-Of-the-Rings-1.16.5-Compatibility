package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;

public class WingedGondorHelmetModel extends GondorHelmetModel {
	public WingedGondorHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public WingedGondorHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		head.texOffs(32, 8).addBox(-6.0F - f, -4.0F, -0.5F, 2.0F, 2.0F, 1.0F, 0.0F);
		head.texOffs(38, 8).addBox(-7.0F - f, -13.0F, -0.5F, 3.0F, 9.0F, 1.0F, 0.0F);
		head.texOffs(46, 8).addBox(-5.5F - f, -17.0F, -0.5F, 2.0F, 4.0F, 1.0F, 0.0F);
		head.mirror = true;
		head.texOffs(32, 8).addBox(4.0F + f, -4.0F, -0.5F, 2.0F, 2.0F, 1.0F, 0.0F);
		head.texOffs(38, 8).addBox(4.0F + f, -13.0F, -0.5F, 3.0F, 9.0F, 1.0F, 0.0F);
		head.texOffs(46, 8).addBox(3.5F + f, -17.0F, -0.5F, 2.0F, 4.0F, 1.0F, 0.0F);
	}
}
