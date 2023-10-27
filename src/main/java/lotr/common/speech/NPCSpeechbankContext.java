package lotr.common.speech;

import java.util.Optional;

import lotr.common.data.AlignmentDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.dim.LOTRDimensionType;
import lotr.common.entity.npc.NPCEntity;
import lotr.common.fac.Faction;
import lotr.common.fac.RankGender;
import lotr.common.init.LOTRBiomes;
import lotr.common.speech.condition.BiomeWithTags;
import lotr.common.speech.condition.OptionallyUnderspecifiedFactionRank;
import lotr.curuquesta.SpeechbankContextProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class NPCSpeechbankContext implements SpeechbankContextProvider {
	private final NPCEntity npc;
	private final PlayerEntity player;

	public NPCSpeechbankContext(NPCEntity npc, PlayerEntity player) {
		this.npc = npc;
		this.player = player;
	}

	private AlignmentDataModule getAlignmentData() {
		return getPlayerData().getAlignmentData();
	}

	public NPCEntity getNPC() {
		return npc;
	}

	public Biome getNPCBiome() {
		return getWorld().getBiome(getNPCPosition());
	}

	public BiomeWithTags getNPCBiomeWithTags() {
		Biome biome = getNPCBiome();
		ResourceLocation biomeName = LOTRBiomes.getBiomeRegistryName(biome, getWorld());
		boolean isHomeBiome = getNPCFaction().isSpeechbankHomeBiome(biomeName);
		return new BiomeWithTags(biomeName, isHomeBiome);
	}

	private Faction getNPCFaction() {
		return npc.getFaction();
	}

	public BlockPos getNPCPosition() {
		return npc.blockPosition();
	}

	public PlayerEntity getPlayer() {
		return player;
	}

	public float getPlayerAlignmentWithNPCFaction() {
		return getAlignmentData().getAlignment(getNPCFaction());
	}

	private LOTRPlayerData getPlayerData() {
		return LOTRLevelData.sidedInstance(getWorld()).getData(player);
	}

	public float getPlayerHungerLevel() {
		return player.getFoodData().getFoodLevel() / 20.0F;
	}

	public OptionallyUnderspecifiedFactionRank getPlayerRankWithNPCFaction() {
		return OptionallyUnderspecifiedFactionRank.fullySpecified(getNPCFaction().getRankFor(getPlayerAlignmentWithNPCFaction()));
	}

	private Faction getPledgeFaction() {
		return getAlignmentData().getPledgeFaction();
	}

	public ResourceLocation getPledgeFactionName() {
		return Optional.ofNullable(getPledgeFaction()).map(Faction::getName).orElse((ResourceLocation) null);
	}

	public SpeechEnums.PledgeRelation getPledgeFactionRelation() {
		Faction pledgeFac = getPledgeFaction();
		if (pledgeFac == null) {
			return SpeechEnums.PledgeRelation.NONE;
		}
		if (pledgeFac == getNPCFaction()) {
			return SpeechEnums.PledgeRelation.THIS;
		}
		if (pledgeFac.isGoodRelation(getNPCFaction())) {
			return SpeechEnums.PledgeRelation.GOOD;
		}
		return pledgeFac.isBadRelation(getNPCFaction()) ? SpeechEnums.PledgeRelation.BAD : SpeechEnums.PledgeRelation.NEUTRAL;
	}

	public RankGender getPreferredRankGender() {
		return getPlayerData().getMiscData().getPreferredRankGender();
	}

	public World getWorld() {
		return npc.getCommandSenderWorld();
	}

	public boolean isLunarEclipse() {
		World world = getWorld();
		DimensionType dimType = world.dimensionType();
		return dimType instanceof LOTRDimensionType && ((LOTRDimensionType) dimType).isLunarEclipse(world);
	}

	public boolean isNPCDrunk() {
		return npc.isDrunk();
	}

	public boolean isPlayerDrunk() {
		return player.hasEffect(Effects.CONFUSION);
	}

	public boolean isUnderground() {
		World world = getWorld();
		BlockPos pos = getNPCPosition();
		return pos.getY() < world.getSeaLevel() && !world.canSeeSkyFromBelowWater(pos);
	}
}
