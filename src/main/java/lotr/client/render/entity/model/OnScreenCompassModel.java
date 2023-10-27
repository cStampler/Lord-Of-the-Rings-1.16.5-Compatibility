package lotr.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class OnScreenCompassModel extends Model {
	private ModelRenderer compass;

	public OnScreenCompassModel() {
		super(RenderType::entityCutoutNoCull);
		texWidth = 32;
		texHeight = 32;
		compass = new ModelRenderer(this, 0, 0);
		compass.setPos(0.0F, 0.0F, 0.0F);
		compass.addBox(-16.0F, 0.0F, -16.0F, 32.0F, 0.0F, 32.0F);
	}

	@Override
	public void renderToBuffer(MatrixStack mat, IVertexBuilder buf, int light, int overlay, float r, float g, float b, float a) {
		compass.render(mat, buf, light, overlay, r, g, b, a);
	}
}
