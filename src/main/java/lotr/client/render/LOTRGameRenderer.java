package lotr.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;

public class LOTRGameRenderer extends GameRenderer {
	private final Minecraft mc;

	public LOTRGameRenderer(Minecraft mc, IResourceManager resMgr, RenderTypeBuffers buffers) {
		super(mc, resMgr, buffers);
		this.mc = mc;
	}

	@Override
	public void pick(float partialTicks) {
		Entity entity = mc.getCameraEntity();
		if (entity != null && mc.level != null) {
			mc.getProfiler().push("pick");
			mc.crosshairPickEntity = null;
			double blockReachDistance = mc.gameMode.getPickRange();
			mc.hitResult = entity.pick(blockReachDistance, partialTicks, false);
			Vector3d eyePos = entity.getEyePosition(partialTicks);
			boolean useSurvivalReachLimit = false;
			double survivalReachLimit = mc.player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue() - 2.0D;
			double entityReachDistance = blockReachDistance;
			if (mc.gameMode.hasFarPickRange()) {
				entityReachDistance = blockReachDistance + 1.0D;
				blockReachDistance = entityReachDistance;
			} else if (blockReachDistance > survivalReachLimit) {
				useSurvivalReachLimit = true;
			}

			double entityReachDistanceSq = entityReachDistance * entityReachDistance;
			if (mc.hitResult != null) {
				entityReachDistanceSq = mc.hitResult.getLocation().distanceToSqr(eyePos);
			}

			Vector3d lookVec = entity.getViewVector(1.0F);
			Vector3d fullReachPos = eyePos.add(lookVec.x * blockReachDistance, lookVec.y * blockReachDistance, lookVec.z * blockReachDistance);
			float f = 1.0F;
			AxisAlignedBB fullReachBoundingBox = entity.getBoundingBox().expandTowards(lookVec.scale(blockReachDistance)).inflate(f, f, f);
			EntityRayTraceResult entityRayTraceResult = ProjectileHelper.getEntityHitResult(entity, eyePos, fullReachPos, fullReachBoundingBox, e -> (!e.isSpectator() && e.isPickable()), entityReachDistanceSq);
			if (entityRayTraceResult != null) {
				Entity targetEntity = entityRayTraceResult.getEntity();
				Vector3d targetEntityPos = entityRayTraceResult.getLocation();
				double dSqToTargetEntity = eyePos.distanceToSqr(targetEntityPos);
				if (useSurvivalReachLimit && dSqToTargetEntity > survivalReachLimit * survivalReachLimit) {
					mc.hitResult = BlockRayTraceResult.miss(targetEntityPos, Direction.getNearest(lookVec.x, lookVec.y, lookVec.z), new BlockPos(targetEntityPos));
				} else if (dSqToTargetEntity < entityReachDistanceSq || mc.hitResult == null) {
					mc.hitResult = entityRayTraceResult;
					if (targetEntity instanceof LivingEntity || targetEntity instanceof ItemFrameEntity) {
						mc.crosshairPickEntity = targetEntity;
					}
				}
			}

			mc.getProfiler().pop();
		}

	}
}
