package lotr.client.render.entity.model.armor;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HaradWarlordHelmetModel extends SpecialArmorModel {
	private ModelRenderer stickRight;
	private ModelRenderer stickCentre;
	private ModelRenderer stickLeft;

	public HaradWarlordHelmetModel(BipedModel referenceBipedModel) {
		this(referenceBipedModel, 1.0F);
	}

	public HaradWarlordHelmetModel(BipedModel referenceBipedModel, float f) {
		super(referenceBipedModel, f);
		clearNonHelmetParts();
		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, f);
		head.setPos(0.0F, 0.0F, 0.0F);
		head.texOffs(6, 24).addBox(-2.5F, -3.0F, 4.1F, 5.0F, 3.0F, 2.0F, 0.0F);
		head.texOffs(0, 16).addBox(-9.0F, -16.0F, 5.5F, 18.0F, 8.0F, 0.0F, 0.0F);
		stickRight = new ModelRenderer(this, 36, 0);
		stickRight.addBox(-0.5F, -19.0F, 5.0F, 1.0F, 18.0F, 1.0F, 0.0F);
		stickRight.texOffs(0, 24).addBox(-1.5F, -24.0F, 5.5F, 3.0F, 5.0F, 0.0F, 0.0F);
		stickRight.zRot = (float) Math.toRadians(-28.0D);
		head.addChild(stickRight);
		stickCentre = new ModelRenderer(this, 36, 0);
		stickCentre.addBox(-0.5F, -19.0F, 5.0F, 1.0F, 18.0F, 1.0F, 0.0F);
		stickCentre.texOffs(0, 24).addBox(-1.5F, -24.0F, 5.5F, 3.0F, 5.0F, 0.0F, 0.0F);
		stickCentre.zRot = (float) Math.toRadians(0.0D);
		head.addChild(stickCentre);
		stickLeft = new ModelRenderer(this, 36, 0);
		stickLeft.addBox(-0.5F, -19.0F, 5.0F, 1.0F, 18.0F, 1.0F, 0.0F);
		stickLeft.texOffs(0, 24).addBox(-1.5F, -24.0F, 5.5F, 3.0F, 5.0F, 0.0F, 0.0F);
		stickLeft.zRot = (float) Math.toRadians(28.0D);
		head.addChild(stickLeft);
	}
}
