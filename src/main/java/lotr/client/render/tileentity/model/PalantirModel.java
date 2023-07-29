package lotr.client.render.tileentity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.*;

public class PalantirModel extends Model {
	private final boolean innerOrbOnly;
	private ModelRenderer outer;
	private ModelRenderer middle;
	private ModelRenderer inner;
	private ModelRenderer stand1;
	private ModelRenderer stand2;

	public PalantirModel(boolean innerOrbOnly) {
		super(innerOrbOnly ? RenderType::entityCutoutNoCull : RenderType::entityTranslucent);
		this.innerOrbOnly = innerOrbOnly;
		float innerOrbSize = 8.0F;
		float middleOrbSize = 9.0F;
		float outerOrbSize = 10.0F;
		if (innerOrbOnly) {
			texWidth = 32;
			texHeight = 16;
		} else {
			texWidth = 64;
			texHeight = 64;
			outer = new ModelRenderer(this, 0, 44);
			outer.setPos(0.0F, 0.0F, 0.0F);
			outer.yRot = (float) Math.toRadians(-45.0D);
			outer.addBox(-outerOrbSize / 2.0F, -outerOrbSize / 2.0F, -outerOrbSize / 2.0F, outerOrbSize, outerOrbSize, outerOrbSize);
			middle = new ModelRenderer(this, 0, 26);
			middle.setPos(0.0F, 0.0F, 0.0F);
			middle.yRot = (float) Math.toRadians(-45.0D);
			middle.addBox(-middleOrbSize / 2.0F, -middleOrbSize / 2.0F, -middleOrbSize / 2.0F, middleOrbSize, middleOrbSize, middleOrbSize);
			stand1 = new ModelRenderer(this, 0, 0);
			stand1.setPos(0.0F, 0.0F, 0.0F);
			stand1.yRot = (float) Math.toRadians(-45.0D);
			stand1.addBox(0.0F, -2.0F, -8.0F, 0.0F, 10.0F, 16.0F);
			stand2 = new ModelRenderer(this, 0, 0);
			stand2.setPos(0.0F, 0.0F, 0.0F);
			stand2.yRot = (float) Math.toRadians(45.0D);
			stand2.mirror = true;
			stand2.addBox(0.0F, -2.0F, -8.0F, 0.0F, 10.0F, 16.0F);
		}

		inner = new ModelRenderer(this, 0, 0);
		inner.setPos(0.0F, 0.0F, 0.0F);
		inner.yRot = (float) Math.toRadians(-45.0D);
		inner.addBox(-innerOrbSize / 2.0F, -innerOrbSize / 2.0F, -innerOrbSize / 2.0F, innerOrbSize, innerOrbSize, innerOrbSize);
	}

	@Override
	public void renderToBuffer(MatrixStack mat, IVertexBuilder buf, int light, int overlay, float r, float g, float b, float a) {
		if (innerOrbOnly) {
			inner.render(mat, buf, light, overlay, r, g, b, a);
		} else {
			outer.render(mat, buf, light, overlay, r, g, b, a);
			middle.render(mat, buf, light, overlay, r, g, b, a);
			stand1.render(mat, buf, light, overlay, r, g, b, a);
			stand2.render(mat, buf, light, overlay, r, g, b, a);
		}

	}
}
