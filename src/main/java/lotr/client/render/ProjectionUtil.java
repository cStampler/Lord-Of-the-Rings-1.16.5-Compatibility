package lotr.client.render;

import java.lang.reflect.*;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.*;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ProjectionUtil {
	public static Matrix4f getProjection(Minecraft mc, float partialTicks, float farField) {
		GameRenderer gr = mc.gameRenderer;
		MatrixStack projectStack = new MatrixStack();
		projectStack.last().pose().setIdentity();
		double fov = 0;
		try {
			Method getFovM = ObfuscationReflectionHelper.findMethod(GameRenderer.class, "getFov", ActiveRenderInfo.class, float.class, boolean.class);
			getFovM.setAccessible(true);
			fov = (int) getFovM.invoke(gr, gr.getMainCamera(), partialTicks, true);
		} catch (Exception var5) {
			var5.printStackTrace();
		}
		float nearField = 0.05F;
		projectStack.last().pose().multiply(Matrix4f.perspective(fov, (float) mc.getWindow().getWidth() / (float) mc.getWindow().getHeight(), nearField, farField));

		try {
			Method bobHurtM = ObfuscationReflectionHelper.findMethod(GameRenderer.class, "bobHurt", MatrixStack.class, float.class);
			bobHurtM.setAccessible(true);
			bobHurtM.invoke(gr, projectStack, partialTicks);
		} catch (Exception var5) {
			var5.printStackTrace();
		}

		if (mc.options.bobView) {
			try {
				Method bobViewM = ObfuscationReflectionHelper.findMethod(GameRenderer.class, "bobView", MatrixStack.class, float.class);
				bobViewM.setAccessible(true);
				bobViewM.invoke(gr, projectStack, partialTicks);
			} catch (Exception var5) {
				var5.printStackTrace();
			}
		}

		float f = MathHelper.lerp(partialTicks, mc.player.oPortalTime, mc.player.portalTime);
		if (f > 0.0F) {
			int i = 20;
			if (mc.player.hasEffect(Effects.CONFUSION)) {
				i = 7;
			}

			int updateCount = 0;
			try {
				Field tickM = ObfuscationReflectionHelper.findField(GameRenderer.class, "tick");
				tickM.setAccessible(true);
				updateCount = (int) tickM.get(gr);
			} catch (Exception var5) {
				var5.printStackTrace();
			}
			float f1 = 5.0F / (f * f + 5.0F) - f * 0.04F;
			f1 *= f1;
			Vector3f axis = new Vector3f(0.0F, MathHelper.SQRT_OF_TWO / 2.0F, MathHelper.SQRT_OF_TWO / 2.0F);
			projectStack.mulPose(axis.rotationDegrees((updateCount + partialTicks) * i));
			projectStack.scale(1.0F / f1, 1.0F, 1.0F);
			float f2 = -(updateCount + partialTicks) * i;
			projectStack.mulPose(axis.rotationDegrees(f2));
		}

		return projectStack.last().pose();
	}
}
