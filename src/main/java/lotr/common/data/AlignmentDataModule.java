package lotr.common.data;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.LOTRLog;
import lotr.common.config.LOTRConfig;
import lotr.common.entity.misc.AlignmentBonusEntity;
import lotr.common.fac.*;
import lotr.common.init.*;
import lotr.common.network.*;
import lotr.common.util.LOTRUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.server.ServerWorld;

public class AlignmentDataModule extends PlayerDataModule {
	public static final int PLEDGE_KILL_COOLDOWN_MAX = 24000;
	private Faction currentViewedFaction;
	private final Map regionLastViewedFactions = new HashMap();
	private final Map alignments = new HashMap();
	private Faction pledgeFaction;
	private int pledgeKillCooldown;
	private int pledgeBreakCooldown;
	private int pledgeBreakCooldownStart;
	private Faction brokenPledgeFaction;
	private boolean friendlyFire;

	protected AlignmentDataModule(LOTRPlayerData pd) {
		super(pd);
	}

	public void addAlignment(Faction faction, float alignmentAdd) {
		setAlignment(faction, this.getAlignment(faction) + alignmentAdd);
	}

	public AlignmentBonusMap addAlignmentFromBonus(ServerPlayerEntity player, AlignmentBonus source, Faction mainFaction, List forcedBonusFactions, Entity entity) {
		return this.addAlignmentFromBonus(player, source, mainFaction, forcedBonusFactions, entity.position().add(0.0D, entity.getBbHeight() * 0.7D, 0.0D));
	}

	public AlignmentBonusMap addAlignmentFromBonus(ServerPlayerEntity player, AlignmentBonus source, Faction mainFaction, List forcedBonusFactions, Vector3d pos) {
		float bonus = source.bonus;
		AlignmentBonusMap factionBonusMap = new AlignmentBonusMap();
		float prevMainAlignment = this.getAlignment(mainFaction);
		float conquestBonus = 0.0F;
		if (source.isKill) {
			mainFaction.getBonusesForKilling().stream().filter(hummel -> ((Faction) hummel).isPlayableAlignmentFaction()).filter(fac -> (((Faction) fac).approvesCivilianKills() || !source.isCivilianKill)).forEach(bonusFaction -> {
				if (!source.isKillByHiredUnit) {
					float mplier = 0.0F;
					if (forcedBonusFactions != null && forcedBonusFactions.contains(bonusFaction)) {
						mplier = 1.0F;
					} else {
						mplier = ((Faction) bonusFaction).getAreasOfInfluence().getAlignmentMultiplier(player);
					}

					if (mplier > 0.0F) {
						float alignment = this.getAlignment((Faction) bonusFaction);
						float factionBonus = Math.abs(bonus);
						factionBonus *= mplier;
						if (alignment >= ((Faction) bonusFaction).getPledgeAlignment() && !isPledgedTo((Faction) bonusFaction)) {
							factionBonus *= 0.5F;
						}

						factionBonus = checkBonusForPledgeEnemyLimit((Faction) bonusFaction, factionBonus);
						alignment += factionBonus;
						setAlignment((Faction) bonusFaction, alignment);
						factionBonusMap.put(bonusFaction, factionBonus);
					}
				}

				if (bonusFaction == getPledgeFaction()) {
				}

			});
			mainFaction.getPenaltiesForKilling().stream().filter(hummel -> ((Faction) hummel).isPlayableAlignmentFaction()).forEach(penaltyFaction -> {
				if (!source.isKillByHiredUnit) {
					float mplier = 0.0F;
					if (penaltyFaction == mainFaction) {
						mplier = 1.0F;
					} else {
						mplier = ((Faction) penaltyFaction).getAreasOfInfluence().getAlignmentMultiplier(player);
					}

					if (mplier > 0.0F) {
						float alignment = this.getAlignment((Faction) penaltyFaction);
						float factionPenalty = -Math.abs(bonus);
						factionPenalty *= mplier;
						factionPenalty = AlignmentBonus.scaleKillPenalty(factionPenalty, alignment);
						alignment += factionPenalty;
						setAlignment((Faction) penaltyFaction, alignment);
						factionBonusMap.put(penaltyFaction, factionPenalty);
					}
				}

			});
		} else if (mainFaction.isPlayableAlignmentFaction()) {
			float alignment = this.getAlignment(mainFaction);
			float factionBonus = bonus;
			if (bonus > 0.0F && alignment >= mainFaction.getPledgeAlignment() && !isPledgedTo(mainFaction)) {
				factionBonus = bonus * 0.5F;
			}

			factionBonus = checkBonusForPledgeEnemyLimit(mainFaction, factionBonus);
			alignment += factionBonus;
			setAlignment(mainFaction, alignment);
			factionBonusMap.put(mainFaction, factionBonus);
		}

		if (!factionBonusMap.isEmpty() || conquestBonus != 0.0F) {
			int entityId = AlignmentBonusEntity.getNextSafeEntityIdForBonusSpawn(player.getLevel());
			sendPacketToClient(new SPacketAlignmentBonus(entityId, source, mainFaction, prevMainAlignment, factionBonusMap, conquestBonus, pos));
		}

		return factionBonusMap;
	}

	public AlignmentBonusMap addAlignmentFromBonus(ServerPlayerEntity player, AlignmentBonus source, Faction mainFaction, Vector3d pos) {
		return this.addAlignmentFromBonus(player, source, mainFaction, (List) null, pos);
	}

	public boolean canMakeNewPledge() {
		return pledgeBreakCooldown <= 0;
	}

	public boolean canPledgeToNow(Faction faction) {
		if (!isValidPledgeFaction(faction)) {
			return false;
		}
		return hasPledgeAlignment(faction) && getFactionsPreventingPledgeTo(faction).isEmpty();
	}

	private float checkBonusForPledgeEnemyLimit(Faction fac, float bonus) {
		if (isPledgeEnemyAlignmentLimited(fac)) {
			float alignment = this.getAlignment(fac);
			float limit = getPledgeEnemyAlignmentLimit(fac);
			if (alignment > limit) {
				bonus = 0.0F;
			} else if (alignment + bonus > limit) {
				bonus = limit - alignment;
			}
		}

		return bonus;
	}

	public boolean displayAlignmentAboveHead() {
		return true;
	}

	private boolean doesFactionPreventPledge(Faction pledgeFac, Faction otherFac) {
		return pledgeFac.isMortalEnemy(otherFac);
	}

	private boolean doFactionsDrain(Faction fac1, Faction fac2) {
		return fac1.isMortalEnemy(fac2);
	}

	public float getAlignment(Faction otherFac) {
		return (Float) alignments.getOrDefault(otherFac, 0.0F);
	}

	public float getAlignment(FactionPointer pointer) {
		Optional faction = pointer.resolveFaction(currentFactionSettings());
		if (faction.isPresent()) {
			return this.getAlignment((Faction) faction.get());
		}
		LOTRLog.warn("Tried to get player %s alignment for faction %s - but faction isn't defined in the current world!", getPlayerUUID(), pointer.getName());
		return 0.0F;
	}

	public Map getAlignmentsView() {
		return new HashMap(alignments);
	}

	public Faction getBrokenPledgeFaction() {
		return brokenPledgeFaction;
	}

	public Faction getCurrentViewedFaction() {
		return currentViewedFaction;
	}

	public Faction getCurrentViewedFactionOrFallbackToFirstIn(RegistryKey currentDimension) {
		if (currentViewedFaction == null || !currentViewedFaction.getDimension().equals(currentDimension)) {
			updateCurrentViewedFactionToNewDimension(currentDimension);
		}

		return currentViewedFaction;
	}

	public List getFactionsPreventingPledgeTo(Faction faction) {
		return (List) currentFactionSettings().getFactions().stream().filter(otherFac -> (((Faction) otherFac).isPlayableAlignmentFaction() && doesFactionPreventPledge(faction, (Faction) otherFac) && this.getAlignment((Faction) otherFac) > 0.0F)).collect(Collectors.toList());
	}

	public float getHighestAlignmentAmong(Collection factions) {
		return (Float) factions.stream().map(hummel -> this.getAlignment((Faction) hummel)).sorted(Comparator.reverseOrder()).findFirst().orElse(Float.MIN_VALUE);
	}

	public int getPledgeBreakCooldown() {
		return pledgeBreakCooldown;
	}

	public float getPledgeBreakCooldownFraction() {
		return pledgeBreakCooldownStart == 0 ? 0.0F : (float) pledgeBreakCooldown / (float) pledgeBreakCooldownStart;
	}

	public int getPledgeBreakCooldownStart() {
		return pledgeBreakCooldownStart;
	}

	public float getPledgeEnemyAlignmentLimit(Faction faction) {
		return 0.0F;
	}

	public Faction getPledgeFaction() {
		return pledgeFaction;
	}

	public Faction getRegionLastViewedFaction(FactionRegion region) {
		Faction fac = (Faction) regionLastViewedFactions.get(region);
		if (fac == null) {
			List regionFacs = currentFactionSettings().getFactionsForRegion(region);
			if (!regionFacs.isEmpty()) {
				fac = (Faction) regionFacs.get(0);
				regionLastViewedFactions.put(region, fac);
			}
		}

		return fac;
	}

	public boolean hasAlignment(Faction faction, AlignmentPredicate predicate) {
		return predicate.test(this.getAlignment(faction));
	}

	public boolean hasAlignmentWithAll(Collection factions, AlignmentPredicate predicate) {
		return factions.stream().allMatch(fac -> hasAlignment((Faction) fac, predicate));
	}

	public boolean hasAlignmentWithAny(Collection factions, AlignmentPredicate predicate) {
		return factions.stream().anyMatch(fac -> hasAlignment((Faction) fac, predicate));
	}

	public boolean hasPledgeAlignment(Faction faction) {
		return this.getAlignment(faction) >= faction.getPledgeAlignment();
	}

	public boolean isFriendlyFireEnabled() {
		return friendlyFire;
	}

	public boolean isPledgedTo(Faction faction) {
		return faction == pledgeFaction;
	}

	public boolean isPledgeEnemyAlignmentLimited(Faction faction) {
		return pledgeFaction != null && doesFactionPreventPledge(pledgeFaction, faction);
	}

	public boolean isValidPledgeFaction(Faction faction) {
		return faction.isPlayableAlignmentFaction();
	}

	@Override
	public void load(CompoundNBT playerNBT) {
		currentViewedFaction = loadFactionFromNBT(playerNBT, "CurrentFaction", "Loaded nonexistent viewing faction %s");
		DataUtil.loadMapFromListNBT(regionLastViewedFactions, playerNBT.getList("PrevRegionFactions", 10), nbt -> {
			String regionName = ((CompoundNBT) nbt).getString("Region");
			String facName = ((CompoundNBT) nbt).getString("Faction");
			FactionRegion region = currentFactionSettings().getRegionByName(new ResourceLocation(regionName));
			if (region == null) {
				playerData.logPlayerError("Loaded nonexistent faction region ID %s", regionName);
				return null;
			}
			Faction faction = currentFactionSettings().getFactionByName(new ResourceLocation(facName));
			if (faction != null) {
				return Pair.of(region, faction);
			}
			playerData.logPlayerError("Loaded nonexistent faction ID %s", facName);
			return null;
		});
		DataUtil.loadMapFromListNBT(alignments, playerNBT.getList("AlignmentMap", 10), nbt -> {
			String facName = ((CompoundNBT) nbt).getString("Faction");
			Faction faction = currentFactionSettings().getFactionByName(new ResourceLocation(facName));
			if (faction != null) {
				float alignment = ((CompoundNBT) nbt).getFloat("AlignF");
				return Pair.of(faction, alignment);
			}
			playerData.logPlayerError("Loaded nonexistent faction ID %s", facName);
			return null;
		});
		pledgeFaction = loadFactionFromNBT(playerNBT, "PledgeFac", "Loaded nonexistent pledge faction %s");
		pledgeKillCooldown = playerNBT.getInt("PledgeKillCD");
		pledgeBreakCooldown = playerNBT.getInt("PledgeBreakCD");
		pledgeBreakCooldownStart = playerNBT.getInt("PledgeBreakCDStart");
		brokenPledgeFaction = loadFactionFromNBT(playerNBT, "BrokenPledgeFac", "Loaded nonexistent broken pledge faction %s");
		friendlyFire = playerNBT.getBoolean("FriendlyFire");
	}

	public void onPledgeKill(ServerPlayerEntity player) {
		pledgeKillCooldown += 24000;
		markDirty();
		if (pledgeKillCooldown > 24000) {
			revokePledgeFaction(player, false);
		} else if (pledgeFaction != null) {
			ITextComponent msg = new TranslationTextComponent("chat.lotr.pledge.killWarn", pledgeFaction.getDisplayName());
			LOTRUtil.sendMessage(player, msg);
		}

	}

	@Override
	protected void onUpdate(ServerPlayerEntity player, ServerWorld world, int tick) {
		RegistryKey currentDimension = LOTRDimensions.getCurrentLOTRDimensionOrFallback(world);
		if (currentViewedFaction != null) {
			FactionRegion currentRegion = currentViewedFaction.getRegion();
			if (!currentRegion.getDimension().equals(currentDimension)) {
				updateCurrentViewedFactionToNewDimension(currentDimension);
			}
		} else {
			updateCurrentViewedFactionToNewDimension(currentDimension);
		}

		runAlignmentDraining(player, tick);
		if (pledgeKillCooldown > 0) {
			--pledgeKillCooldown;
			if (pledgeKillCooldown == 0 || isTimerAutosaveTick()) {
				markDirty();
			}
		}

		if (pledgeBreakCooldown > 0) {
			setPledgeBreakCooldown(pledgeBreakCooldown - 1);
		}

	}

	@Override
	protected void receiveLoginData(PacketBuffer buf) {
		currentViewedFaction = readFactionFromBuffer(buf, "Received nonexistent viewing faction ID %d from server");
		DataUtil.fillMapFromBuffer(buf, regionLastViewedFactions, () -> {
			int regionId = buf.readVarInt();
			int factionId = buf.readVarInt();
			FactionRegion region = currentFactionSettings().getRegionByID(regionId);
			Faction faction = currentFactionSettings().getFactionByID(factionId);
			if (region == null) {
				LOTRLog.warn("Received nonexistent faction region ID %d from server", regionId);
				return null;
			}
			if (faction == null) {
				LOTRLog.warn("Received nonexistent faction ID %d from server", factionId);
				return null;
			}
			return Pair.of(region, faction);
		});
		DataUtil.fillMapFromBuffer(buf, alignments, () -> {
			int factionId = buf.readVarInt();
			Faction faction = currentFactionSettings().getFactionByID(factionId);
			float alignment = buf.readFloat();
			if (faction == null) {
				LOTRLog.warn("Received nonexistent faction ID %d from server", factionId);
				return null;
			}
			return Pair.of(faction, alignment);
		});
		pledgeFaction = readFactionFromBuffer(buf, "Received nonexistent pledge faction ID %d from server");
		friendlyFire = buf.readBoolean();
	}

	public void revokePledgeFaction(ServerPlayerEntity player, boolean intentional) {
		Faction wasPledgedTo = pledgeFaction;
		float pledgeLvl = wasPledgedTo.getPledgeAlignment();
		float prevAlign = this.getAlignment(wasPledgedTo);
		float diff = prevAlign - pledgeLvl;
		float cdProportion = diff / 5000.0F;
		cdProportion = MathHelper.clamp(cdProportion, 0.0F, 1.0F);
		int minCdTicks = LOTRUtil.minutesToTicks(30);
		int maxCdTicks = LOTRUtil.minutesToTicks(180);
		int cdTicks = Math.round(MathHelper.lerp(cdProportion, minCdTicks, maxCdTicks));
		setPledgeFaction((Faction) null);
		setBrokenPledgeFaction(wasPledgedTo);
		setPledgeBreakCooldown(cdTicks);
		FactionRank rank = wasPledgedTo.getRankFor(prevAlign);
		FactionRank rank2Below = wasPledgedTo.getRankNBelow(rank, 2);
		float newAlign = rank2Below.getAlignment();
		newAlign = Math.max(newAlign, pledgeLvl / 2.0F);
		float alignPenalty = newAlign - prevAlign;
		if (alignPenalty < 0.0F) {
			AlignmentBonus penalty = AlignmentBonus.createPledgePenalty(alignPenalty);
			double lookRange = 2.0D;
			Vector3d posEye = new Vector3d(player.getX(), player.getEyeY(), player.getZ());
			Vector3d look = player.getViewVector(1.0F);
			Vector3d posSight = posEye.add(look.multiply(lookRange, lookRange, lookRange));
			RayTraceResult blockLookAt = player.pick(lookRange, 1.0F, true);
			Vector3d penaltyPos;
			if (blockLookAt != null && blockLookAt.getType() == Type.BLOCK) {
				penaltyPos = blockLookAt.getLocation();
			} else {
				penaltyPos = posSight;
			}

			this.addAlignmentFromBonus(player, penalty, wasPledgedTo, penaltyPos);
		}

		player.level.playSound((PlayerEntity) null, player.blockPosition(), LOTRSoundEvents.UNPLEDGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
		TranslationTextComponent msg;
		if (intentional) {
			msg = new TranslationTextComponent("chat.lotr.pledge.break.intentional", wasPledgedTo.getDisplayName());
		} else {
			msg = new TranslationTextComponent("chat.lotr.pledge.break.unintentional", wasPledgedTo.getDisplayName());
		}
		LOTRUtil.sendMessage(player, msg);

	}

	private void runAlignmentDraining(ServerPlayerEntity player, int tick) {
		if ((Boolean) LOTRConfig.COMMON.alignmentDraining.get() && tick % 1000 == 0) {
			List drainFactions = new ArrayList();
			List allFacs = currentFactionSettings().getAllPlayableAlignmentFactions();
			Iterator var6 = allFacs.iterator();

			Faction fac1;
			while (var6.hasNext()) {
				fac1 = (Faction) var6.next();
				Iterator var8 = allFacs.iterator();

				while (var8.hasNext()) {
					Faction fac2 = (Faction) var8.next();
					if (doFactionsDrain(fac1, fac2)) {
						float align1 = this.getAlignment(fac1);
						float align2 = this.getAlignment(fac2);
						if (align1 > 0.0F && align2 > 0.0F) {
							if (!drainFactions.contains(fac1)) {
								drainFactions.add(fac1);
							}

							if (!drainFactions.contains(fac2)) {
								drainFactions.add(fac2);
							}
						}
					}
				}
			}

			if (!drainFactions.isEmpty()) {
				var6 = drainFactions.iterator();

				while (var6.hasNext()) {
					fac1 = (Faction) var6.next();
					float align = this.getAlignment(fac1);
					float alignLoss = 5.0F;
					alignLoss = Math.min(alignLoss, align - 0.0F);
					align -= alignLoss;
					setAlignment(fac1, align);
				}

				LOTRPacketHandler.sendTo(new SPacketAlignmentDrain(drainFactions.size()), player);
				playerData.getMessageData().sendMessageIfNotReceived(PlayerMessageType.ALIGN_DRAIN);
			}
		}

	}

	@Override
	public void save(CompoundNBT playerNBT) {
		writeFactionToNBT(playerNBT, "CurrentFaction", currentViewedFaction);
		playerNBT.put("PrevRegionFactions", DataUtil.saveMapAsListNBT(regionLastViewedFactions, (nbt, region, fac) -> {
			((CompoundNBT) nbt).putString("Region", ((FactionRegion) region).getName().toString());
			((CompoundNBT) nbt).putString("Faction", ((Faction) fac).getName().toString());
		}));
		playerNBT.put("AlignmentMap", DataUtil.saveMapAsListNBT(alignments, (nbt, fac, alignment) -> {
			((CompoundNBT) nbt).putString("Faction", ((Faction) fac).getName().toString());
			((CompoundNBT) nbt).putFloat("AlignF", (float) alignment);
		}));
		writeFactionToNBT(playerNBT, "PledgeFac", pledgeFaction);
		playerNBT.putInt("PledgeKillCD", pledgeKillCooldown);
		playerNBT.putInt("PledgeBreakCD", pledgeBreakCooldown);
		playerNBT.putInt("PledgeBreakCDStart", pledgeBreakCooldownStart);
		writeFactionToNBT(playerNBT, "BrokenPledgeFac", brokenPledgeFaction);
		playerNBT.putBoolean("FriendlyFire", friendlyFire);
	}

	@Override
	protected void sendLoginData(PacketBuffer buf) {
		writeFactionToBuffer(buf, currentViewedFaction);
		DataUtil.writeMapToBuffer(buf, regionLastViewedFactions, (region, faction) -> {
			buf.writeVarInt(((FactionRegion) region).getAssignedId());
			buf.writeVarInt(((Faction) faction).getAssignedId());
		});
		DataUtil.writeMapToBuffer(buf, alignments, (faction, alignment) -> {
			buf.writeVarInt(((Faction) faction).getAssignedId());
			buf.writeFloat((float) alignment);
		});
		writeFactionToBuffer(buf, pledgeFaction);
		buf.writeBoolean(friendlyFire);
	}

	public void sendViewedFactionsToServer() {
		CPacketViewedFactions packet = new CPacketViewedFactions(currentViewedFaction, regionLastViewedFactions);
		LOTRPacketHandler.sendToServer(packet);
	}

	public void setAlignment(Faction faction, float alignment) {
		if (faction.isPlayableAlignmentFaction()) {
			float prevAlignment = this.getAlignment(faction);
			if (alignment != prevAlignment) {
				alignments.put(faction, alignment);
				markDirty();
				sendPacketToClient(new SPacketAlignment(faction, alignment));
				executeIfPlayerExistsServerside(player -> {
					SPacketAlignment packetForOtherPlayers = new SPacketAlignment(faction, alignment, (PlayerEntity) player);
					LOTRPacketHandler.sendToAllExcept(packetForOtherPlayers, (ServerPlayerEntity) player);
				});
			}
		}

		if (pledgeFaction != null && !canPledgeToNow(pledgeFaction)) {
			executeIfPlayerExistsServerside(player -> {
				revokePledgeFaction((ServerPlayerEntity) player, false);
			});
		}

	}

	public void setBrokenPledgeFaction(Faction f) {
		brokenPledgeFaction = f;
		markDirty();
	}

	public void setCurrentViewedFaction(Faction fac) {
		if (fac != null) {
			currentViewedFaction = fac;
			markDirty();
			sendPacketToClient(new SPacketViewingFaction(currentViewedFaction));
		}

	}

	public void setFriendlyFireEnabled(boolean flag) {
		if (friendlyFire != flag) {
			friendlyFire = flag;
			markDirty();
			sendPacketToClient(new SPacketToggle(SidedTogglePacket.ToggleType.FRIENDLY_FIRE, friendlyFire));
		}

	}

	public void setPledgeBreakCooldown(int i) {
		int preCD = pledgeBreakCooldown;
		Faction preBroken = brokenPledgeFaction;
		i = Math.max(0, i);
		pledgeBreakCooldown = i;
		boolean bigChange = (pledgeBreakCooldown == 0 || preCD == 0) && pledgeBreakCooldown != preCD;
		if (pledgeBreakCooldown > pledgeBreakCooldownStart) {
			setPledgeBreakCooldownStart(pledgeBreakCooldown);
			bigChange = true;
		}

		if (pledgeBreakCooldown <= 0 && preBroken != null) {
			setPledgeBreakCooldownStart(0);
			setBrokenPledgeFaction((Faction) null);
			bigChange = true;
		}

		if (bigChange || isTimerAutosaveTick()) {
			markDirty();
		}

		if (bigChange || pledgeBreakCooldown % 5 == 0) {
			sendPacketToClient(new SPacketPledgeBreak(pledgeBreakCooldown, pledgeBreakCooldownStart, brokenPledgeFaction));
		}

		if (pledgeBreakCooldown == 0 && preCD != pledgeBreakCooldown) {
			executeIfPlayerExistsServerside(player -> {
				ITextComponent msg = new TranslationTextComponent("chat.lotr.pledge.breakCooldown", Faction.getFactionOrUnknownDisplayName(preBroken));
				LOTRUtil.sendMessage((PlayerEntity) player, msg);
			});
		}

	}

	public void setPledgeBreakCooldownStart(int i) {
		pledgeBreakCooldownStart = i;
		markDirty();
	}

	public void setPledgeFaction(Faction faction) {
		pledgeFaction = faction;
		pledgeKillCooldown = 0;
		markDirty();
		sendPacketToClient(new SPacketPledge(faction));
		executeIfPlayerExistsServerside(player -> {
			if (faction != null) {
				((PlayerEntity) player).level.playSound((PlayerEntity) null, ((Entity) player).blockPosition(), LOTRSoundEvents.PLEDGE, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}

		});
	}

	public void setRegionLastViewedFaction(FactionRegion region, Faction fac) {
		List regionFacs = currentFactionSettings().getFactionsForRegion(region);
		if (regionFacs.contains(fac)) {
			regionLastViewedFactions.put(region, fac);
			markDirty();
			sendPacketToClient(new SPacketRegionLastViewedFaction(region, fac));
		}

	}

	public void toggleFriendlyFireEnabledAndSendToServer() {
		friendlyFire = !friendlyFire;
		LOTRPacketHandler.sendToServer(new CPacketToggle(SidedTogglePacket.ToggleType.FRIENDLY_FIRE, friendlyFire));
	}

	private void updateCurrentViewedFactionToNewDimension(RegistryKey currentDimension) {
		List dimensionRegions = currentFactionSettings().getRegionsForDimension(currentDimension);
		if (dimensionRegions != null && !dimensionRegions.isEmpty()) {
			Iterator var3 = dimensionRegions.iterator();

			while (var3.hasNext()) {
				FactionRegion region = (FactionRegion) var3.next();
				List regionFacs = currentFactionSettings().getFactionsForRegion(region);
				if (regionFacs != null && !regionFacs.isEmpty()) {
					Faction fac = getRegionLastViewedFaction(region);
					if (fac != null) {
						setCurrentViewedFaction(fac);
						break;
					}
				}
			}
		}

	}
}
