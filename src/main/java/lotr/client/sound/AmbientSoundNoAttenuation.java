package lotr.client.sound;

import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

public class AmbientSoundNoAttenuation extends SimpleSound {
	public AmbientSoundNoAttenuation(SoundEvent evt, SoundCategory cat, float vol, float pit, BlockPos pos) {
		super(evt, cat, vol, pit, pos);
		attenuation = AttenuationType.NONE;
	}

	public AmbientSoundNoAttenuation modifyAmbientVolume(PlayerEntity player, int maxRange) {
		float distFr = MathHelper.sqrt(player.distanceToSqr(x, y, z));
		distFr /= maxRange;
		distFr = Math.min(distFr, 1.0F);
		distFr = 1.0F - distFr;
		distFr *= 1.5F;
		distFr = MathHelper.clamp(distFr, 0.1F, 1.0F);
		volume *= distFr;
		return this;
	}
}
