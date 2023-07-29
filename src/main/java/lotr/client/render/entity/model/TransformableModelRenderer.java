package lotr.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.*;

public class TransformableModelRenderer extends ModelRenderer {
	private float scaleX;
	private float scaleY;
	private float scaleZ;
	private double transX;
	private double transY;
	private double transZ;

	public TransformableModelRenderer(Model model, int texOffX, int texOffY) {
		super(model, texOffX, texOffY);
		resetScaleAndTranslation();
	}

	private boolean hasTransform() {
		return scaleX != 1.0F || scaleY != 1.0F || scaleZ != 1.0F || transX != 0.0D || transY != 0.0D || transZ != 0.0D;
	}

	@Override
	public void render(MatrixStack matStack, IVertexBuilder buf, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		boolean hasTransform = hasTransform();
		if (hasTransform) {
			matStack.pushPose();
			matStack.scale(scaleX, scaleY, scaleZ);
			matStack.translate(transX / 16.0D, transY / 16.0D, transZ / 16.0D);
		}

		super.render(matStack, buf, packedLight, packedOverlay, red, green, blue, alpha);
		if (hasTransform) {
			matStack.popPose();
		}

	}

	public void resetScaleAndTranslation() {
		setScaleAndTranslation(1.0F, 1.0F, 1.0F, 0.0D, 0.0D, 0.0D);
	}

	public void setScaleAndTranslation(float x, float y, float z, double tx, double ty, double tz) {
		scaleX = x;
		scaleY = y;
		scaleZ = z;
		transX = tx;
		transY = ty;
		transZ = tz;
	}
}
