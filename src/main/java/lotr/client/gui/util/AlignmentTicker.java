package lotr.client.gui.util;

import lotr.common.LOTRLog;
import lotr.common.data.LOTRLevelData;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionSettingsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class AlignmentTicker {
	private final ResourceLocation factionName;
	private float prevAlign;
	private float currentAlign;
	private int moveTick = 0;
	private int prevMoveTick = 0;
	private int flashTick;
	private int displayNumericalTick;

	public AlignmentTicker(ResourceLocation facName) {
		factionName = facName;
	}

	public int getDisplayNumericalTick() {
		return displayNumericalTick;
	}

	public int getFlashTick() {
		return flashTick;
	}

	public float getInterpolatedAlignment(float f) {
		if (moveTick == 0) {
			return prevAlign;
		}
		float tickF = prevMoveTick + (moveTick - prevMoveTick) * f;
		tickF /= 20.0F;
		tickF = 1.0F - tickF;
		return prevAlign + (currentAlign - prevAlign) * tickF;
	}

	private Faction resolveFactionReference() {
		Faction faction = FactionSettingsManager.clientInstance().getCurrentLoadedFactions().getFactionByName(factionName);
		if (faction == null) {
			LOTRLog.warn("Alignment ticker couldn't resolve reference to faction %s. Potential world leak?", factionName);
		}

		return faction;
	}

	public void update(PlayerEntity player, boolean forceInstant) {
		Faction faction = resolveFactionReference();
		float playerCurrentAlign = faction != null ? LOTRLevelData.clientInstance().getData(player).getAlignmentData().getAlignment(faction) : 0.0F;
		if (forceInstant) {
			prevAlign = currentAlign = playerCurrentAlign;
			prevMoveTick = moveTick = 0;
			flashTick = 0;
			displayNumericalTick = 0;
		} else {
			if (currentAlign != playerCurrentAlign) {
				prevAlign = currentAlign;
				currentAlign = playerCurrentAlign;
				prevMoveTick = moveTick = 20;
				flashTick = 30;
				displayNumericalTick = 200;
			}

			prevMoveTick = moveTick;
			if (moveTick > 0) {
				--moveTick;
				if (moveTick <= 0) {
					prevAlign = currentAlign;
				}
			}

			if (flashTick > 0) {
				--flashTick;
			}

			if (displayNumericalTick > 0) {
				--displayNumericalTick;
			}
		}

	}
}
