package lotr.common.data;

import lotr.common.LOTRLog;
import lotr.common.fac.RankGender;
import lotr.common.network.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;

public class MiscDataModule extends PlayerDataModule {
	private boolean initialSpawnedIntoME;
	private int alcoholTolerance;
	private RankGender preferredRankGender;
	private boolean showMapLocation;

	protected MiscDataModule(LOTRPlayerData pd) {
		super(pd);
		preferredRankGender = RankGender.MASCULINE;
		showMapLocation = true;
	}

	public int getAlcoholTolerance() {
		return alcoholTolerance;
	}

	public boolean getInitialSpawnedIntoME() {
		return initialSpawnedIntoME;
	}

	public RankGender getPreferredRankGender() {
		return preferredRankGender;
	}

	public boolean getShowMapLocation() {
		return showMapLocation;
	}

	@Override
	public void load(CompoundNBT playerNBT) {
		initialSpawnedIntoME = playerNBT.getBoolean("InitialSpawnedIntoME");
		alcoholTolerance = playerNBT.getInt("Alcohol");
		if (playerNBT.contains("RankGender")) {
			String genderName = playerNBT.getString("RankGender");
			RankGender loadedGender = RankGender.forSaveName(genderName);
			if (loadedGender != null) {
				preferredRankGender = loadedGender;
			} else {
				playerData.logPlayerError("No preferred rank gender by name %s", genderName);
			}
		}

		showMapLocation = (Boolean) DataUtil.getIfNBTContains(showMapLocation, playerNBT, "ShowMapLocation", (hummel, hummel2) -> ((CompoundNBT) hummel).getBoolean((String) hummel2));
	}

	@Override
	protected void onUpdate(ServerPlayerEntity player, ServerWorld world, int tick) {
		if (tick % 24000 == 0 && alcoholTolerance > 0) {
			--alcoholTolerance;
			setAlcoholTolerance(alcoholTolerance);
		}

	}

	@Override
	protected void receiveLoginData(PacketBuffer buf) {
		int genderId = buf.readVarInt();
		RankGender gender = RankGender.forNetworkID(genderId);
		if (gender != null) {
			preferredRankGender = gender;
		} else {
			LOTRLog.warn("Received nonexistent preferred rank gender ID %d from server", genderId);
		}

		showMapLocation = buf.readBoolean();
	}

	@Override
	public void save(CompoundNBT playerNBT) {
		playerNBT.putBoolean("InitialSpawnedIntoME", initialSpawnedIntoME);
		playerNBT.putInt("Alcohol", alcoholTolerance);
		playerNBT.putString("RankGender", preferredRankGender.getSaveName());
		playerNBT.putBoolean("ShowMapLocation", showMapLocation);
	}

	@Override
	protected void sendLoginData(PacketBuffer buf) {
		buf.writeVarInt(preferredRankGender.networkID);
		buf.writeBoolean(showMapLocation);
	}

	public void setAlcoholTolerance(int i) {
		alcoholTolerance = i;
		markDirty();
		if (alcoholTolerance >= 250) {
		}

	}

	public void setInitialSpawnedIntoME(boolean flag) {
		initialSpawnedIntoME = flag;
		markDirty();
	}

	public void setPreferredRankGender(RankGender gender) {
		if (gender != null) {
			preferredRankGender = gender;
			markDirty();
			sendPacketToClient(new SPacketPreferredRankGender(preferredRankGender));
		}

	}

	public void setPreferredRankGenderAndSendToServer(RankGender gender) {
		if (gender != null) {
			setPreferredRankGender(gender);
			LOTRPacketHandler.sendToServer(new CPacketChoosePreferredRankGender(preferredRankGender));
		}

	}

	public void setShowMapLocation(boolean flag) {
		if (showMapLocation != flag) {
			showMapLocation = flag;
			markDirty();
			sendPacketToClient(new SPacketToggle(SidedTogglePacket.ToggleType.SHOW_MAP_LOCATION, showMapLocation));
		}

	}

	public void toggleShowMapLocationAndSendToServer() {
		showMapLocation = !showMapLocation;
		LOTRPacketHandler.sendToServer(new CPacketToggle(SidedTogglePacket.ToggleType.SHOW_MAP_LOCATION, showMapLocation));
	}
}
