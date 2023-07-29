package lotr.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SmokeShipModel extends EntityModel {
	private ModelRenderer hull = new ModelRenderer(this);
	private ModelRenderer deck;
	private ModelRenderer mast1;
	private ModelRenderer sail1;
	private ModelRenderer mast2;
	private ModelRenderer sail2;
	private ModelRenderer mast3;
	private ModelRenderer sail3;
	private ModelRenderer bow;
	private ModelRenderer stern;

	public SmokeShipModel() {
		hull.addBox(-3.5F, 1.0F, -8.0F, 7.0F, 5.0F, 16.0F);
		hull.setPos(0.0F, 0.0F, 0.0F);
		deck = new ModelRenderer(this);
		deck.addBox(-5.0F, -0.01F, -8.0F, 10.0F, 1.0F, 16.0F);
		deck.setPos(0.0F, 0.0F, 0.0F);
		mast1 = new ModelRenderer(this);
		mast1.addBox(-1.0F, -8.99F, -6.0F, 2.0F, 9.0F, 2.0F);
		mast1.setPos(0.0F, 0.0F, 0.0F);
		sail1 = new ModelRenderer(this);
		sail1.addBox(-6.0F, -8.0F, -5.5F, 12.0F, 6.0F, 1.0F);
		sail1.setPos(0.0F, 0.0F, 0.0F);
		mast2 = new ModelRenderer(this);
		mast2.addBox(-1.0F, -11.99F, -1.0F, 2.0F, 12.0F, 2.0F);
		mast2.setPos(0.0F, 0.0F, 0.0F);
		sail2 = new ModelRenderer(this);
		sail2.addBox(-8.0F, -11.0F, -0.5F, 16.0F, 8.0F, 1.0F);
		sail2.setPos(0.0F, 0.0F, 0.0F);
		mast3 = new ModelRenderer(this);
		mast3.addBox(-1.0F, -8.99F, 4.0F, 2.0F, 9.0F, 2.0F);
		mast3.setPos(0.0F, 0.0F, 0.0F);
		sail3 = new ModelRenderer(this);
		sail3.addBox(-6.0F, -8.0F, 4.5F, 12.0F, 6.0F, 1.0F);
		sail3.setPos(0.0F, 0.0F, 0.0F);
		bow = new ModelRenderer(this);
		bow.addBox(-3.5F, -1.0F, -11.99F, 7.0F, 3.0F, 4.0F);
		bow.setPos(0.0F, 0.0F, 0.0F);
		stern = new ModelRenderer(this);
		stern.addBox(-3.5F, -1.0F, 7.99F, 7.0F, 3.0F, 4.0F);
		stern.setPos(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void renderToBuffer(MatrixStack mat, IVertexBuilder buf, int light, int overlay, float r, float g, float b, float a) {
		hull.render(mat, buf, light, overlay, r, g, b, a);
		deck.render(mat, buf, light, overlay, r, g, b, a);
		mast1.render(mat, buf, light, overlay, r, g, b, a);
		sail1.render(mat, buf, light, overlay, r, g, b, a);
		mast2.render(mat, buf, light, overlay, r, g, b, a);
		sail2.render(mat, buf, light, overlay, r, g, b, a);
		mast3.render(mat, buf, light, overlay, r, g, b, a);
		sail3.render(mat, buf, light, overlay, r, g, b, a);
		bow.render(mat, buf, light, overlay, r, g, b, a);
		stern.render(mat, buf, light, overlay, r, g, b, a);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
}
