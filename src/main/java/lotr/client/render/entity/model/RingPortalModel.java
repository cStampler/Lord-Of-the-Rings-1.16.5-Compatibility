package lotr.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.matrix.MatrixStack.Entry;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import lotr.common.entity.item.RingPortalEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;

public class RingPortalModel extends EntityModel<RingPortalEntity> {
	private boolean isScript;
	private ModelRenderer[] ringParts = new ModelRenderer[60];
	private Vector3f[][] scriptParts = new Vector3f[60][4];

	public RingPortalModel(boolean flag) {
		isScript = flag;
		if (!isScript) {
			for (int i = 0; i < 60; ++i) {
				ModelRenderer part = new ModelRenderer(this, 0, 0).setTexSize(64, 32);
				part.addBox(-2.0F, -3.5F, -38.0F, 4.0F, 7.0F, 3.0F);
				part.setPos(0.0F, 0.0F, 0.0F);
				part.yRot = i / 60.0F * 3.1415927F * 2.0F;
				ringParts[i] = part;
			}
		} else {
			float depth = 38.0F;
			float halfX = 2.0F;
			float halfY = 2.5F;
			Vector3f[] parts = { new Vector3f(halfX, -halfY, -depth), new Vector3f(-halfX, -halfY, -depth), new Vector3f(-halfX, halfY, -depth), new Vector3f(halfX, halfY, -depth) };

			for (int i = 0; i < 60; ++i) {
				float rotate = i / 60.0F * 3.1415927F * 2.0F;

				for (int j = 0; j < parts.length; ++j) {
					Vector3f srcPart = parts[j];
					Vector3f rotatedPart = new Vector3f(srcPart.x(), srcPart.y(), srcPart.z());
					rotatedPart.transform(new Quaternion(Vector3f.YP, -rotate, false));
					scriptParts[i][j] = rotatedPart;
				}
			}
		}

	}

	private void addVertexUV(IVertexBuilder buf, float x, float y, float z, float u, float v, Matrix4f matrix, float r, float g, float b, float a, int light, int overlay, Vector3f normal) {
		Vector4f v4 = new Vector4f(x, y, z, 1.0F);
		v4.transform(matrix);
		buf.vertex(v4.x(), v4.y(), v4.z(), r, g, b, a, u, v, overlay, light, normal.x(), normal.y(), normal.z());
	}

	@Override
	public void renderToBuffer(MatrixStack mat, IVertexBuilder buf, int light, int overlay, float r, float g, float b, float a) {
		if (!isScript) {
			for (ModelRenderer ringPart : ringParts) {
				ringPart.render(mat, buf, light, overlay, r, g, b, a);
			}
		} else {
			mat.pushPose();
			Entry mEntry = mat.last();
			Matrix4f matrix = mEntry.pose();
			Matrix3f matrixNormal = mEntry.normal();
			Vector3f normal = Vector3f.YN.copy();
			normal.transform(matrixNormal);
			normal.x();
			normal.y();
			normal.z();

			for (int i = 0; i < 60; ++i) {
				Vector3f[] parts = scriptParts[i];
				float uMin = i / 60.0F;
				float uMax = (i + 1) / 60.0F;
				float vMin = 0.0F;
				float vMax = 1.0F;
				float f5 = 0.0625F;
				addVertexUV(buf, parts[0].x() * f5, parts[0].y() * f5, parts[0].z() * f5, uMax, vMin, matrix, r, g, b, a, light, overlay, normal);
				addVertexUV(buf, parts[1].x() * f5, parts[1].y() * f5, parts[1].z() * f5, uMin, vMin, matrix, r, g, b, a, light, overlay, normal);
				addVertexUV(buf, parts[2].x() * f5, parts[2].y() * f5, parts[2].z() * f5, uMin, vMax, matrix, r, g, b, a, light, overlay, normal);
				addVertexUV(buf, parts[3].x() * f5, parts[3].y() * f5, parts[3].z() * f5, uMax, vMax, matrix, r, g, b, a, light, overlay, normal);
			}

			mat.popPose();
		}

	}

	@Override
	public void setupAnim(RingPortalEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
}
