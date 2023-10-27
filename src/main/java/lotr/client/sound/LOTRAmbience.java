package lotr.client.sound;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import lotr.common.LOTRLog;
import lotr.common.config.LOTRConfig;
import lotr.common.dim.LOTRDimensionType;
import lotr.common.init.LOTRBiomes;
import lotr.common.init.LOTRSoundEvents;
import lotr.common.world.biome.ExtendedWeatherType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class LOTRAmbience {
	private List playingWindSounds = new ArrayList();

	public LOTRAmbience() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private SimpleSound copySoundPropertiesToNew(SimpleSound srcSound, SoundEvent newSoundLocation) {
		float actualVolume = 1.0F;
		float actualPitch = 1.0F;

		try {
			actualVolume = ObfuscationReflectionHelper.findField(LocatableSound.class, "volume").getFloat(srcSound);
			actualPitch = ObfuscationReflectionHelper.findField(LocatableSound.class, "pitch").getFloat(srcSound);
		} catch (IllegalAccessException | IllegalArgumentException var6) {
			LOTRLog.error("Failed to retrieve sound properties for replacement sound");
			var6.printStackTrace();
		}

		return new SimpleSound(newSoundLocation.getLocation(), srcSound.getSource(), actualVolume, actualPitch, srcSound.isLooping(), srcSound.getDelay(), srcSound.getAttenuation(), srcSound.getX(), srcSound.getY(), srcSound.getZ(), srcSound.isRelative());
	}

	private void doWindAmbience(SoundHandler soundHandler, World world, Random rand, PlayerEntity player, BlockPos pos) {
		if (playingWindSounds.size() < 4) {
			int xzRange = 16;
			int yRange = 16;
			int minWindHeight = 100;
			int fullWindHeight = 180;
			if (rand.nextInt(20) == 0) {
				minWindHeight -= 10;
				if (rand.nextInt(10) == 0) {
					minWindHeight -= 10;
				}
			}

			if (world.isRaining()) {
				minWindHeight = 80;
				fullWindHeight = 120;
				if (rand.nextInt(20) == 0) {
					minWindHeight -= 20;
				}

				Biome biome = world.getBiome(pos);
				if (LOTRBiomes.getWrapperFor(biome, world).getExtendedWeatherVisually() == ExtendedWeatherType.SANDSTORM) {
					minWindHeight = 60;
					fullWindHeight = 80;
				}
			}

			Mutable movingPos = new Mutable();

			for (int l = 0; l < 2; ++l) {
				int dx = MathHelper.nextInt(rand, -xzRange, xzRange);
				int dz = MathHelper.nextInt(rand, -xzRange, xzRange);
				int dy = MathHelper.nextInt(rand, -yRange, yRange);
				movingPos.set(pos).move(dx, dy, dz);
				int windY = movingPos.getY();
				if (windY >= minWindHeight && world.canSeeSkyFromBelowWater(movingPos)) {
					float windHeightLerp = (float) (windY - minWindHeight) / (float) (fullWindHeight - minWindHeight);
					windHeightLerp = MathHelper.clamp(windHeightLerp, 0.0F, 1.0F);
					if (windHeightLerp >= rand.nextFloat()) {
						float vol = 1.0F * Math.max(0.25F, windHeightLerp);
						float pitch = 0.8F + rand.nextFloat() * 0.4F;
						ISound wind = new AmbientSoundNoAttenuation(LOTRSoundEvents.AMBIENCE_WIND, SoundCategory.AMBIENT, vol, pitch, movingPos).modifyAmbientVolume(player, xzRange);
						soundHandler.play(wind);
						playingWindSounds.add(wind);
						break;
					}
				}
			}
		} else {
			Set removes = new HashSet();
			Iterator var21 = playingWindSounds.iterator();

			while (var21.hasNext()) {
				ISound wind = (ISound) var21.next();
				if (!soundHandler.isActive(wind)) {
					removes.add(wind);
				}
			}

			playingWindSounds.removeAll(removes);
		}

	}

	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent event) {
		ISound sound = event.getSound();
		if (sound instanceof SimpleSound) {
			SimpleSound simpleSound = (SimpleSound) sound;
			ResourceLocation name = simpleSound.getLocation();
			Minecraft mc = Minecraft.getInstance();
			World world = mc.level;
			if (world != null && world.dimensionType() instanceof LOTRDimensionType && name.equals(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation())) {
				event.setResultSound(copySoundPropertiesToNew(simpleSound, LOTRSoundEvents.NEW_THUNDER));
			}
		}

	}

	public void updateAmbience(Minecraft mc, World world, PlayerEntity player) {
		IProfiler profiler = world.getProfiler();
		profiler.push("lotrAmbience");
		SoundHandler soundHandler = mc.getSoundManager();
		Random rand = world.random;
		player.getX();
		player.getY();
		player.getZ();
		BlockPos pos = player.blockPosition();
		pos.getX();
		pos.getY();
		pos.getZ();
		boolean isLOTRDimension = world.dimensionType() instanceof LOTRDimensionType;
		if (isLOTRDimension && (Boolean) LOTRConfig.CLIENT.windAmbience.get()) {
			profiler.push("wind");
			doWindAmbience(soundHandler, world, rand, player, pos);
			profiler.pop();
		}

		profiler.pop();
	}
}
