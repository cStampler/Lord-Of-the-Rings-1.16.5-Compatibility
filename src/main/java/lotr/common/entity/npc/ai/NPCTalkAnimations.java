package lotr.common.entity.npc.ai;

import java.util.Random;
import java.util.stream.*;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.init.LOTRParticles;
import lotr.common.network.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class NPCTalkAnimations {
	private final NPCEntity npc;
	private boolean isTalking;
	private NPCTalkAnimations.TalkAction talkAction;
	private float actionSlow;
	private int actionTime;
	private boolean doGestureMain;
	private boolean doGestureOff;
	private int timeUntilGesture;
	private int actionTick = 0;
	private int totalTalkingTime = 0;
	private float headYaw;
	private float headPitch;
	private float prevHeadYaw;
	private float prevHeadPitch;
	private int gestureMainTime;
	private int gestureMainTick;
	private int prevGestureMainTick;
	private int gestureOffTime;
	private int gestureOffTick;
	private int prevGestureOffTick;
	private int timeSinceParticle = 0;
	private boolean spawnedFirstParticle;
	private boolean sendData = true;

	public NPCTalkAnimations(NPCEntity npc) {
		this.npc = npc;
	}

	public float getHeadPitchRadians(float f) {
		return prevHeadPitch + (headPitch - prevHeadPitch) * f;
	}

	public float getHeadYawRadians(float f) {
		return prevHeadYaw + (headYaw - prevHeadYaw) * f;
	}

	public float getMainhandGestureAmount(float f) {
		if (gestureMainTime > 0) {
			float gesture = prevGestureMainTick + (gestureMainTick - prevGestureMainTick) * f;
			return MathHelper.sin(gesture / gestureMainTime * 3.1415927F);
		}
		return 0.0F;
	}

	public float getOffhandGestureAmount(float f) {
		if (gestureOffTime > 0) {
			float gesture = prevGestureOffTick + (gestureOffTick - prevGestureOffTick) * f;
			return MathHelper.sin(gesture / gestureOffTime * 3.1415927F);
		}
		return 0.0F;
	}

	private final int getRandomGestureDuration() {
		return 10 + npc.getRandom().nextInt(30);
	}

	private int getRandomTimeUntilGesture() {
		return 20 + npc.getRandom().nextInt(180);
	}

	private IParticleData getSpeechParticle(Random rand) {
		float f = rand.nextFloat();
		if (f < 0.1F) {
			return (IParticleData) LOTRParticles.NPC_QUESTION.get();
		}
		return f < 0.2F ? (IParticleData) LOTRParticles.NPC_EXCLAMATION.get() : (IParticleData) LOTRParticles.NPC_SPEECH.get();
	}

	public boolean isTalking() {
		return isTalking;
	}

	private void markDirty() {
		if (!npc.level.isClientSide) {
			sendData = true;
		}

	}

	public void sendData(ServerPlayerEntity player) {
		SPacketNPCTalkAnimations packet = new SPacketNPCTalkAnimations(this);
		LOTRPacketHandler.sendTo(packet, player);
	}

	private void sendDataToAllWatchers() {
		SPacketNPCTalkAnimations packet = new SPacketNPCTalkAnimations(this);
		LOTRPacketHandler.sendToAllTrackingEntity(packet, npc);
	}

	private void spawnParticle(Random rand) {
		Vector3d eyePos = npc.getEyePosition(1.0F);
		Vector3d look = npc.getViewVector(1.0F);
		look.x();
		eyePos.x();
		look.z();
		eyePos.z();
		float sideFovAngle = (float) Math.toRadians(MathHelper.nextDouble(rand, 45.0D, 90.0D) * (rand.nextBoolean() ? -1 : 1));
		Vector3d sideLook = look.yRot(sideFovAngle);
		double nx = sideLook.x;
		double nz = sideLook.z;
		double r = MathHelper.nextDouble(rand, 0.25D, 0.5D);
		double px = eyePos.x() + r * nx;
		double py = eyePos.y() + MathHelper.nextDouble(rand, -0.2D, 0.2D);
		double pz = eyePos.z() + r * nz;
		npc.level.addParticle(getSpeechParticle(rand), px, py, pz, nx * 0.03D, 0.05D, nz * 0.03D);
	}

	private void startClientGestureBoth() {
		if (gestureMainTime <= 0 && gestureOffTime <= 0) {
			gestureMainTime = gestureOffTime = getRandomGestureDuration();
		} else {
			startClientGestureMain();
			startClientGestureOff();
		}

	}

	private void startClientGestureMain() {
		if (gestureMainTime <= 0) {
			gestureMainTime = getRandomGestureDuration();
		}

	}

	private void startClientGestureOff() {
		if (gestureOffTime <= 0) {
			gestureOffTime = getRandomGestureDuration();
		}

	}

	public void startTalking() {
		isTalking = true;
		talkAction = null;
		totalTalkingTime = 0;
		timeUntilGesture = getRandomTimeUntilGesture();
		markDirty();
	}

	public void stopTalking() {
		isTalking = false;
		talkAction = null;
		totalTalkingTime = 0;
		timeUntilGesture = 0;
		markDirty();
	}

	public void updateAnimation() {
		npc.level.getProfiler().push("NPCTalkAnimations#updateAnimation");
		Random rand = npc.getRandom();
		if (!npc.level.isClientSide) {
			if (!isTalking) {
				totalTalkingTime = 0;
				timeUntilGesture = 0;
			} else {
				++totalTalkingTime;
				if (talkAction == null) {
					if (totalTalkingTime < 10 || rand.nextInt(30) == 0) {
						talkAction = NPCTalkAnimations.TalkAction.getRandomAction(rand);
						if (talkAction == NPCTalkAnimations.TalkAction.TALKING) {
							actionTime = 40 + rand.nextInt(60);
							actionSlow = 1.0F;
						} else if (talkAction == NPCTalkAnimations.TalkAction.LOOKING_AROUND) {
							actionTime = 60 + rand.nextInt(60);
							actionSlow = 1.0F;
						} else if (talkAction == NPCTalkAnimations.TalkAction.SHAKING_HEAD) {
							actionTime = 100 + rand.nextInt(60);
							actionSlow = 1.0F;
						} else if (talkAction == NPCTalkAnimations.TalkAction.LOOKING_UP) {
							actionTime = 30 + rand.nextInt(50);
							actionSlow = 1.0F;
						}

						markDirty();
					}
				} else {
					++actionTick;
				}

				if (talkAction != null) {
					if (actionTick >= actionTime) {
						talkAction = null;
						actionTick = 0;
						actionTime = 0;
						markDirty();
					} else if (talkAction == NPCTalkAnimations.TalkAction.TALKING && actionTick % 20 == 0) {
						actionSlow = 0.7F + rand.nextFloat() * 1.5F;
						markDirty();
					}
				}

				--timeUntilGesture;
				if (timeUntilGesture <= 0) {
					if (rand.nextFloat() < 0.1F) {
						doGestureMain = doGestureOff = true;
					} else if (rand.nextInt(3) == 0) {
						doGestureOff = true;
					} else {
						doGestureMain = true;
					}

					timeUntilGesture = getRandomTimeUntilGesture();
					markDirty();
				}
			}

			if (sendData) {
				sendDataToAllWatchers();
				sendData = false;
				doGestureMain = doGestureOff = false;
			}
		} else {
			prevHeadYaw = headYaw;
			prevHeadPitch = headPitch;
			if (isTalking) {
				++totalTalkingTime;
				if (talkAction != null) {
					++actionTick;
					float slow;
					if (talkAction == NPCTalkAnimations.TalkAction.TALKING) {
						slow = actionSlow * 2.0F;
						headYaw = MathHelper.sin(actionTick / slow) * (float) Math.toRadians(10.0D);
						headPitch = (MathHelper.sin(actionTick / slow * 2.0F) + 1.0F) / 2.0F * (float) Math.toRadians(-20.0D);
					} else if (talkAction == NPCTalkAnimations.TalkAction.SHAKING_HEAD) {
						actionSlow += 0.01F;
						headYaw = MathHelper.sin(actionTick / actionSlow) * (float) Math.toRadians(30.0D);
						headPitch += (float) Math.toRadians(0.4D);
					} else if (talkAction == NPCTalkAnimations.TalkAction.LOOKING_AROUND) {
						slow = actionSlow * 16.0F;
						headYaw = MathHelper.sin(actionTick / slow) * (float) Math.toRadians(50.0D);
						headPitch = (MathHelper.sin(actionTick / slow * 2.0F) + 1.0F) / 2.0F * (float) Math.toRadians(-15.0D);
					} else if (talkAction == NPCTalkAnimations.TalkAction.LOOKING_UP) {
						headYaw = 0.0F;
						headPitch = (float) Math.toRadians(-20.0D);
					}
				} else {
					actionTick = 0;
					headYaw = 0.0F;
					headPitch = MathHelper.sin(totalTalkingTime * 0.07F) * (float) Math.toRadians(5.0D);
				}
			} else {
				headYaw = headPitch = 0.0F;
				totalTalkingTime = actionTick = 0;
			}

			prevGestureMainTick = gestureMainTick;
			prevGestureOffTick = gestureOffTick;
			if (gestureMainTime > 0) {
				++gestureMainTick;
				if (prevGestureMainTick >= gestureMainTime) {
					gestureMainTime = prevGestureMainTick = gestureMainTick = 0;
				}
			}

			if (gestureOffTime > 0) {
				++gestureOffTick;
				if (prevGestureOffTick > gestureOffTime) {
					gestureOffTime = prevGestureOffTick = gestureOffTick = 0;
				}
			}

			if (isTalking) {
				++timeSinceParticle;
				if (!spawnedFirstParticle && timeSinceParticle > 20 || timeSinceParticle > 30 + rand.nextInt(150)) {
					spawnParticle(rand);
					timeSinceParticle = 0;
					spawnedFirstParticle = true;
				}
			} else {
				timeSinceParticle = 0;
				spawnedFirstParticle = false;
			}
		}

		npc.level.getProfiler().pop();
	}

	public void write(PacketBuffer buf) {
		buf.writeVarInt(npc.getId());
		buf.writeBoolean(isTalking);
		buf.writeByte(talkAction != null ? talkAction.ordinal() : -1);
		buf.writeFloat(actionSlow);
		buf.writeBoolean(doGestureMain);
		buf.writeBoolean(doGestureOff);
	}

	public static void read(PacketBuffer buf, World world) {
		int entityId = buf.readVarInt();
		Entity entity = world.getEntity(entityId);
		if (entity instanceof NPCEntity) {
			NPCEntity npc = (NPCEntity) entity;
			NPCTalkAnimations talkAnim = npc.getTalkAnimations();
			talkAnim.isTalking = buf.readBoolean();
			int actionId = buf.readByte();
			talkAnim.talkAction = actionId >= 0 ? NPCTalkAnimations.TalkAction.forId(actionId) : null;
			talkAnim.actionSlow = buf.readFloat();
			boolean doGestureMain = buf.readBoolean();
			boolean doGestureOff = buf.readBoolean();
			if (doGestureMain && doGestureOff) {
				talkAnim.startClientGestureBoth();
			} else {
				if (doGestureMain) {
					talkAnim.startClientGestureMain();
				}

				if (doGestureOff) {
					talkAnim.startClientGestureOff();
				}
			}
		}

	}

	public enum TalkAction {
		TALKING(1.0F), SHAKING_HEAD(0.1F), LOOKING_AROUND(0.3F), LOOKING_UP(0.4F);

		private static final Double totalWeight = Stream.of(values()).collect(Collectors.summingDouble(action -> action.weight));
		private final float weight;

		TalkAction(float w) {
			weight = w;
		}

		public static NPCTalkAnimations.TalkAction forId(int i) {
			return values()[MathHelper.clamp(i, 0, values().length - 1)];
		}

		public static NPCTalkAnimations.TalkAction getRandomAction(Random rand) {
			float f = (float) (rand.nextFloat() * totalWeight);
			NPCTalkAnimations.TalkAction chosen = null;
			NPCTalkAnimations.TalkAction[] var3 = values();
			int var4 = var3.length;

			for (int var5 = 0; var5 < var4; ++var5) {
				NPCTalkAnimations.TalkAction action = var3[var5];
				f -= action.weight;
				if (f <= 0.0F) {
					chosen = action;
					break;
				}
			}

			return chosen;
		}
	}
}
