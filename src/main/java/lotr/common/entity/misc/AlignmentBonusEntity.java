package lotr.common.entity.misc;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;

import lotr.common.data.AlignmentDataModule;
import lotr.common.fac.AlignmentBonus;
import lotr.common.fac.AlignmentBonusMap;
import lotr.common.fac.Faction;
import lotr.common.init.LOTREntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class AlignmentBonusEntity extends Entity {
	private AlignmentBonus bonusSource;
	private Faction mainFaction;
	private float prevMainAlignment;
	private AlignmentBonusMap factionBonusMap;
	private float conquestBonus;
	private int particleAge;
	private int particlePrevAge;
	private int particleMaxAge;

	public AlignmentBonusEntity(EntityType type, World w) {
		super(type, w);
		particlePrevAge = particleAge = 0;
	}

	@Override
	protected void addAdditionalSaveData(CompoundNBT nbt) {
	}

	private void calcMaxAge() {
		float mostSignificantBonus = 0.0F;
		Iterator var2 = factionBonusMap.getChangedFactions().iterator();

		while (var2.hasNext()) {
			Faction fac = (Faction) var2.next();
			float bonus = Math.abs((Float) factionBonusMap.get(fac));
			if (bonus > mostSignificantBonus) {
				mostSignificantBonus = bonus;
			}
		}

		float conq = Math.abs(conquestBonus);
		if (conq > mostSignificantBonus) {
			mostSignificantBonus = conq;
		}

		particleMaxAge = 80;
		int extra = (int) (Math.min(1.0F, mostSignificantBonus / 50.0F) * 220.0F);
		particleMaxAge += extra;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public IPacket getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public float getAlignmentBonusFor(Faction faction) {
		return (Float) factionBonusMap.getOrDefault(faction, 0.0F);
	}

	public float getBonusAgeF(float f) {
		return (particlePrevAge + (particleAge - particlePrevAge) * f) / particleMaxAge;
	}

	public ITextComponent getBonusDisplayText() {
		return bonusSource.name;
	}

	public float getConquestBonus() {
		return conquestBonus;
	}

	public Faction getFactionToDisplay(AlignmentDataModule alignData) {
		if (!factionBonusMap.isEmpty()) {
			Faction currentViewedFaction = alignData.getCurrentViewedFaction();
			if (factionBonusMap.containsKey(currentViewedFaction)) {
				return currentViewedFaction;
			}

			if (factionBonusMap.size() == 1 && mainFaction.isPlayableAlignmentFaction()) {
				return mainFaction;
			}

			if (mainFaction.isPlayableAlignmentFaction() && prevMainAlignment >= 0.0F && (Float) factionBonusMap.get(mainFaction) < 0.0F) {
				return mainFaction;
			}

			Optional highestFactionWithBonus = factionBonusMap.keySet().stream().filter(hummel -> ((Faction) hummel).isPlayableAlignmentFaction()).filter(fac -> ((Float) factionBonusMap.get(fac) > 0.0F)).sorted(Comparator.comparingDouble(fac -> ((double) alignData.getAlignment((Faction) fac))).reversed()).findFirst();
			if (highestFactionWithBonus.isPresent()) {
				return (Faction) highestFactionWithBonus.get();
			}

			if (mainFaction.isPlayableAlignmentFaction() && (Float) factionBonusMap.get(mainFaction) < 0.0F) {
				return mainFaction;
			}

			Optional highestFactionWithPenalty = factionBonusMap.keySet().stream().filter(hummel -> ((Faction) hummel).isPlayableAlignmentFaction()).filter(fac -> ((Float) factionBonusMap.get(fac) < 0.0F)).sorted(Comparator.comparingDouble(fac -> ((double) alignData.getAlignment((Faction) fac))).reversed()).findFirst();
			if (highestFactionWithPenalty.isPresent()) {
				return (Faction) highestFactionWithPenalty.get();
			}
		}

		return null;
	}

	@Override
	public boolean isInvulnerable() {
		return true;
	}

	@Override
	protected boolean isMovementNoisy() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void readAdditionalSaveData(CompoundNBT nbt) {
	}

	public boolean shouldDisplayConquestBonus(AlignmentDataModule alignData) {
		Faction currentViewedFaction = alignData.getCurrentViewedFaction();
		if (conquestBonus > 0.0F && alignData.isPledgedTo(currentViewedFaction)) {
			return true;
		}
		return conquestBonus < 0.0F && (currentViewedFaction == mainFaction || alignData.isPledgedTo(currentViewedFaction));
	}

	public boolean shouldShowBonusText(boolean showAlign, boolean showConquest) {
		return showAlign || showConquest && !bonusSource.isKillByHiredUnit;
	}

	@Override
	public void tick() {
		super.tick();
		particlePrevAge = particleAge++;
		if (particleAge >= particleMaxAge) {
			removeAfterChangingDimensions();
		}

	}

	public static AlignmentBonusEntity createBonusEntityForClientSpawn(World world, int entityId, AlignmentBonus bonusSource, Faction mainFaction, float prevMainAlignment, AlignmentBonusMap factionBonusMap, float conquestBonus, Vector3d pos) {
		if (!world.isClientSide) {
			throw new IllegalArgumentException("Alignment bonus entities cannot be spawned on the server side!");
		}
		AlignmentBonusEntity entity = new AlignmentBonusEntity((EntityType) LOTREntities.ALIGNMENT_BONUS.get(), world);
		entity.setId(entityId);
		entity.bonusSource = bonusSource;
		entity.mainFaction = mainFaction;
		entity.prevMainAlignment = prevMainAlignment;
		entity.factionBonusMap = factionBonusMap;
		entity.conquestBonus = conquestBonus;
		entity.setPos(pos.x, pos.y, pos.z);
		entity.calcMaxAge();
		return entity;
	}

	public static int getNextSafeEntityIdForBonusSpawn(ServerWorld world) {
		AlignmentBonusEntity entity = new AlignmentBonusEntity((EntityType) LOTREntities.ALIGNMENT_BONUS.get(), world);
		return entity.getId();
	}
}
