package lotr.common.event;

import java.lang.reflect.*;
import java.util.*;

import lotr.common.LOTRLog;
import lotr.common.block.*;
import lotr.common.block.trees.*;
import lotr.common.data.*;
import lotr.common.entity.npc.*;
import lotr.common.entity.npc.data.NPCEntitySettingsManager;
import lotr.common.fac.*;
import lotr.common.init.*;
import lotr.common.inv.*;
import lotr.common.item.*;
import lotr.common.network.*;
import lotr.common.stat.LOTRStats;
import lotr.common.time.*;
import lotr.common.util.LOTRUtil;
import lotr.common.world.RingPortalTeleporter;
import lotr.common.world.map.*;
import net.minecraft.block.*;
import net.minecraft.block.trees.Tree;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.passive.horse.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.*;
import net.minecraft.item.*;
import net.minecraft.tags.ITag;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.common.*;
import net.minecraftforge.common.Tags.Items;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.event.entity.EntityEvent.Size;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.event.entity.player.PlayerEvent.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.*;
import net.minecraftforge.event.world.*;
import net.minecraftforge.event.world.BlockEvent.*;
import net.minecraftforge.event.world.ExplosionEvent.Detonate;
import net.minecraftforge.event.world.PistonEvent.Pre;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class LOTREventHandler {
	private final SpeechGarbler speechGarbler = new SpeechGarbler();
	private final BreakMallornResponse breakMallornResponse = new BreakMallornResponse();

	public LOTREventHandler() {
		registerHandlers(this);
	}

	private void adjustRelativeSpeedToMatch(BreakSpeed event, Block desiredBlockSpeed) {
		ItemStack heldItem = event.getPlayer().getItemInHand(Hand.MAIN_HAND);
		float desiredBaseSpeed = heldItem.getDestroySpeed(desiredBlockSpeed.defaultBlockState());
		float actualBaseSpeed = heldItem.getDestroySpeed(event.getState());
		float relativeSpeed = desiredBaseSpeed / actualBaseSpeed;
		event.setNewSpeed(event.getOriginalSpeed() * relativeSpeed);
	}

	private ActionResultType handleProxyFakeItemEntityInteraction(PlayerEntity player, Hand hand, ItemStack heldItem, AnimalEntity target, ITag vanillaTagToMatch, Item proxyVanillaItem) {
		if (heldItem.getItem().is(vanillaTagToMatch) && "lotr".equals(heldItem.getItem().getRegistryName().getNamespace())) {
			ItemStack faked = new ItemStack(proxyVanillaItem, heldItem.getCount());
			player.setItemInHand(hand, faked);
			ActionResultType result = target.mobInteract(player, hand);
			heldItem.setCount(faked.getCount());
			player.setItemInHand(hand, heldItem);
			return result;
		}
		return ActionResultType.PASS;
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		PlayerEntity player = event.getPlayer();
		World world = player.level;
		BlockPos pos = event.getPos();
		BlockState state = event.getState();
		breakMallornResponse.handleBlockBreak(world, player, pos, state);
	}

	@SubscribeEvent
	public void onBlockToolInteract(BlockToolInteractEvent event) {
		if (event.getToolType() == ToolType.SHOVEL && event.getState().getBlock() == Blocks.SNOW_BLOCK) {
			event.setFinalState(((Block) LOTRBlocks.SNOW_PATH.get()).defaultBlockState());
		}
	}

	@SubscribeEvent
	public void onBoneMealGrow(BonemealEvent event) {
		World world = event.getWorld();
		Random rand = world.random;
		BlockState state = event.getBlock();
		BlockPos pos = event.getPos();
		if (LOTRDimensions.isModDimension(world) && state.getBlock() == Blocks.GRASS_BLOCK) {
			IGrowable grassAsGrowable = (IGrowable) state.getBlock();
			if (grassAsGrowable.isValidBonemealTarget(world, pos, state, world.isClientSide)) {
				if (world instanceof ServerWorld && grassAsGrowable.isBonemealSuccess(world, world.random, pos, state)) {
					BlockPos above = pos.above();
					int tries = 128;

					label55: for (int i = 0; i < tries; ++i) {
						BlockPos plantPos = above;

						for (int triesHere = 0; triesHere < i / 16; ++triesHere) {
							plantPos = plantPos.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
							if (world.getBlockState(plantPos.below()).getBlock() != grassAsGrowable || world.getBlockState(plantPos).isCollisionShapeFullBlock(world, plantPos)) {
								continue label55;
							}
						}

						BlockState curBlock = world.getBlockState(plantPos);
						if (curBlock.getBlock() instanceof TallGrassBlock && rand.nextInt(10) == 0) {
							((IGrowable) curBlock.getBlock()).performBonemeal((ServerWorld) world, rand, plantPos, curBlock);
						}

						if (curBlock.isAir(world, plantPos)) {
							Biome biome = world.getBiome(plantPos);
							BlockState plant;
							if (rand.nextInt(8) == 0) {
								List flowerList = world.getBiome(plantPos).getGenerationSettings().getFlowerFeatures();
								if (flowerList.isEmpty()) {
									continue;
								}

								ConfiguredFeature configuredFeature = (ConfiguredFeature) flowerList.get(0);
								plant = ((FlowersFeature) configuredFeature.feature()).getRandomFlower(rand, plantPos, configuredFeature.config());
							} else {
								plant = LOTRBiomes.getWrapperFor(biome, world).getGrassForBonemeal(rand, plantPos);
							}

							if (plant.canSurvive(world, plantPos)) {
								world.setBlock(plantPos, plant, 3);
							}
						}
					}
				}

				event.setResult(Result.ALLOW);
			}
		}

	}

	@SubscribeEvent
	public void onBreakSpeedCheck(BreakSpeed event) {
		BlockState state = event.getState();
		PlayerEntity player = event.getPlayer();
		ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
		if (state.getBlock() instanceof HangingWebBlock && !heldItem.isEmpty()) {
			adjustRelativeSpeedToMatch(event, Blocks.COBWEB);
		} else if (state.getBlock() instanceof FallenLeavesBlock && !heldItem.isEmpty()) {
			adjustRelativeSpeedToMatch(event, ((FallenLeavesBlock) state.getBlock()).getBaseLeafBlock());
		}
	}

	@SubscribeEvent
	public void onCanLivingConvert(net.minecraftforge.event.entity.living.LivingConversionEvent.Pre event) {
		LivingEntity entity = event.getEntityLiving();
		EntityType outcome = event.getOutcome();
		World world = entity.level;
		if (LOTRDimensions.isModDimension(world) && entity instanceof PigEntity && outcome == EntityType.ZOMBIFIED_PIGLIN) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onEntityInitialSpawn(SpecialSpawn event) {
		Entity entity = event.getEntity();
		World world = entity.level;
		if (!world.isClientSide && entity instanceof FoxEntity && LOTRDimensions.isModDimension(world)) {
			FoxEntity fox = (FoxEntity) entity;
			ItemStack itemInMouth = fox.getItemBySlot(EquipmentSlotType.MAINHAND);
			boolean spawnedWithItem = !itemInMouth.isEmpty();
			if (itemInMouth.getItem() == net.minecraft.item.Items.EMERALD) {
				fox.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
				LOTRLog.info("Cancelled a fox spawning with emerald in mouth in %s at %s", world.dimension().location(), fox.blockPosition());
			}

			if (spawnedWithItem) {
				float f = fox.getRandom().nextFloat();
				ItemStack replacedItem = null;
				if (f < 0.02F) {
					replacedItem = new ItemStack((IItemProvider) LOTRItems.GOLD_RING.get());
				} else if (f < 0.04F) {
					replacedItem = new ItemStack((IItemProvider) LOTRItems.SILVER_RING.get());
				} else if (f < 0.0405F) {
					replacedItem = new ItemStack((IItemProvider) LOTRItems.MITHRIL_RING.get());
				}

				if (replacedItem != null) {
					fox.setItemSlot(EquipmentSlotType.MAINHAND, replacedItem);
				}
			}
		}

	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteract event) {
		PlayerEntity player = event.getPlayer();
		World world = event.getWorld();
		ItemStack heldItem = event.getItemStack();
		Hand hand = event.getHand();
		Entity target = event.getTarget();
		if (heldItem.getItem() instanceof RedBookItem && target instanceof ItemFrameEntity) {
			RedBookItem redBook = (RedBookItem) heldItem.getItem();
			ItemFrameEntity frame = (ItemFrameEntity) target;
			BlockPos frameSupportPos = CustomWaypointStructureHandler.getItemFrameSupportPos(frame);
			if (redBook.createCustomWaypointStructure(world, frameSupportPos, player)) {
				event.setCanceled(true);
				return;
			}
		}

		if (!player.abilities.instabuild && VesselOperations.isItemEmptyVessel(heldItem) && IEmptyVesselItem.canMilk(target)) {
			VesselType vesselType = ((IEmptyVesselItem) heldItem.getItem()).getVesselType();
			ItemStack milkDrink = new ItemStack((IItemProvider) LOTRItems.MILK_DRINK.get());
			VesselDrinkItem.setVessel(milkDrink, vesselType);
			player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
			heldItem.shrink(1);
			if (heldItem.isEmpty()) {
				player.setItemInHand(hand, milkDrink);
			} else if (!player.inventory.add(milkDrink)) {
				player.drop(milkDrink, false);
			}

			event.setCanceled(true);
			event.setCancellationResult(ActionResultType.SUCCESS);
		} else {
			ActionResultType useSeedsResult;
			if (target instanceof WolfEntity) {
				WolfEntity wolf = (WolfEntity) target;
				useSeedsResult = handleProxyFakeItemEntityInteraction(player, hand, heldItem, wolf, Items.BONES, net.minecraft.item.Items.BONE);
				if (useSeedsResult.consumesAction()) {
					event.setCanceled(true);
					event.setCancellationResult(useSeedsResult);
					return;
				}
			}

			if (target instanceof ParrotEntity) {
				ParrotEntity parrot = (ParrotEntity) target;
				useSeedsResult = handleProxyFakeItemEntityInteraction(player, hand, heldItem, parrot, Items.SEEDS, net.minecraft.item.Items.WHEAT_SEEDS);
				if (useSeedsResult.consumesAction()) {
					event.setCanceled(true);
					event.setCancellationResult(useSeedsResult);
				}
			}

		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity entity = event.getEntity();
		World world = event.getWorld();
		if (!world.isClientSide && LOTRDimensions.isModDimension(world) && entity instanceof SkeletonHorseEntity) {
			SkeletonHorseEntity skeleHorse = (SkeletonHorseEntity) entity;
			if (skeleHorse.isTrap()) {
				LOTRLog.info("Cancelled the spawn of a skeleton trap horse in %s at %s", world.dimension().location(), skeleHorse.blockPosition());
				event.setCanceled(true);
			}
		}

	}

	@SubscribeEvent
	public void onEntitySetSize(Size event) {
		Entity entity = event.getEntity();
		World world = entity.level;
		if (BeeAdjustments.shouldApply(entity, world)) {
			BeeAdjustments.adjustSize(event);
		}

	}

	@SubscribeEvent
	public void onExplosionDetonate(Detonate event) {
		Explosion explosion = event.getExplosion();
		World world = event.getWorld();
		if (!world.isClientSide) {
			List explodingPositions = explosion.getToBlow();
			explodingPositions.removeIf(pos -> TerrainProtections.isTerrainProtectedFromExplosion(world, (BlockPos) pos));
		}

	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		PlayerEntity player = event.getPlayer();
		World world = player.level;
		ItemEntity pickupEntity = event.getItem();
		ItemStack pickupStack = pickupEntity.getItem();
		if (!world.isClientSide && !pickupStack.isEmpty()) {
			for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
				ItemStack itemInSlot = player.inventory.getItem(i);
				if (itemInSlot.getItem() instanceof PouchItem) {
					PouchItem.AddItemResult result = PouchItem.tryAddItemToPouch(itemInSlot, pickupStack, true);
					if (result != PouchItem.AddItemResult.NONE_ADDED) {
						boolean markNewPickup = true;
						if (player.containerMenu instanceof OpenPouchContainer) {
							OpenPouchContainer pouchContainer = (OpenPouchContainer) player.containerMenu;
							if (pouchContainer.isOpenPouch(itemInSlot)) {
								pouchContainer.reloadPouchFromPickup();
								markNewPickup = false;
							}
						}

						if (markNewPickup) {
							PouchItem.setPickedUpNewItems(itemInSlot, true);
						}
					}

					if (pickupStack.isEmpty()) {
						break;
					}
				}
			}

			if (pickupStack.isEmpty()) {
				event.setResult(Result.ALLOW);
			}
		}

	}

	@SubscribeEvent
	public void onLivingAttacked(LivingAttackEvent event) {
		LivingEntity target = event.getEntityLiving();
		DamageSource source = event.getSource();
		LivingEntity attacker = source.getEntity() instanceof LivingEntity ? (LivingEntity) source.getEntity() : null;
		if (target instanceof AbstractHorseEntity && target.getPassengers().contains(attacker) || attacker != null && !EntityFactionHelper.canEntityCauseDamageToTarget(attacker, target, true)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntityLiving();
		DamageSource source = event.getSource();
		World world = entity.level;
		if (!world.isClientSide) {
			LivingEntity killerResponsible = null;
			PlayerEntity playerResponsible = null;
			boolean creditHiredUnit = false;
			boolean byNearbyUnit = false;
			if (source.getEntity() instanceof LivingEntity) {
				killerResponsible = (LivingEntity) source.getEntity();
			} else if (entity.getKillCredit() != null) {
				killerResponsible = entity.getKillCredit();
			}

			if (killerResponsible instanceof PlayerEntity) {
				playerResponsible = (PlayerEntity) killerResponsible;
			} else if (killerResponsible instanceof NPCEntity) {
			}

			if (playerResponsible == null && entity.getKillCredit() instanceof PlayerEntity) {
				playerResponsible = (PlayerEntity) entity.getKillCredit();
			}

			Faction entityFaction = EntityFactionHelper.getFaction(entity);
			boolean wasPlayerSelfDefenceAgainstAlliedUnit = false;
			if (playerResponsible != null) {
				LOTRPlayerData playerData = LOTRLevelData.getSidedData(playerResponsible);
				AlignmentDataModule alignData = playerData.getAlignmentData();
				alignData.getAlignment(entityFaction);
				List forcedBonusFactions = null;
				AlignmentBonus alignmentBonus = null;
				if (!wasPlayerSelfDefenceAgainstAlliedUnit) {
					float bonus = NPCEntitySettingsManager.getEntityTypeSettings(entity).getKillAlignmentBonus();
					if (bonus != 0.0F && (!creditHiredUnit || creditHiredUnit && byNearbyUnit)) {
						alignmentBonus = AlignmentBonus.forEntityKill(bonus, entity.getType().getDescription(), creditHiredUnit, EntityFactionHelper.isCivilian(entity));
					}
				}

				if (!creditHiredUnit && wasPlayerSelfDefenceAgainstAlliedUnit) {
				}

				if (alignmentBonus != null && alignmentBonus.bonus != 0.0F) {
					alignData.addAlignmentFromBonus((ServerPlayerEntity) playerResponsible, alignmentBonus, entityFaction, forcedBonusFactions, (Entity) entity);
				}

				if (!creditHiredUnit && entityFaction.isPlayableAlignmentFaction()) {
					playerData.getFactionStatsData().getFactionStats(entityFaction).addMemberKill();
					entityFaction.getBonusesForKilling().forEach(enemyFac -> {
						playerData.getFactionStatsData().getFactionStats((Faction) enemyFac).addEnemyKill();
					});
					if (!playerResponsible.abilities.instabuild && entityFaction.getAreasOfInfluence().isInAreaToRecordBountyKill(playerResponsible)) {
					}

					Faction pledgeFac = alignData.getPledgeFaction();
					if (pledgeFac != null && (pledgeFac == entityFaction || pledgeFac.isAlly(entityFaction))) {
						alignData.onPledgeKill((ServerPlayerEntity) playerResponsible);
					}
				}
			}

			if (killerResponsible != null && !wasPlayerSelfDefenceAgainstAlliedUnit && EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(killerResponsible) && !FactionPointers.UNALIGNED.matches(entityFaction)) {
				int sentSpeeches = 0;
				double angerRange = 16.0D;
				List nearbyAngerableNPCs = world.getLoadedEntitiesOfClass(MobEntity.class, entity.getBoundingBox().inflate(angerRange), NPCPredicates.selectAngerableByKill(entityFaction, killerResponsible));
				Iterator var24 = nearbyAngerableNPCs.iterator();

				while (var24.hasNext()) {
					MobEntity angered = (MobEntity) var24.next();
					if (angered.getTarget() == null) {
						angered.setTarget(killerResponsible);
						if (angered instanceof NPCEntity && angered.getTarget() == killerResponsible && killerResponsible instanceof PlayerEntity && sentSpeeches < 5) {
							NPCEntity npc = (NPCEntity) angered;
							if (npc.distanceToSqr(killerResponsible) <= angerRange * angerRange && npc.sendNormalSpeechTo((ServerPlayerEntity) killerResponsible)) {
								++sentSpeeches;
							}
						}
					}
				}
			}
		}

	}

	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
		LivingEntity entity = event.getEntityLiving();
		event.getTarget();
		if (!entity.level.isClientSide && entity instanceof MobEntity) {
			LOTRPacketHandler.sendToAllTrackingEntity(new SPacketSetAttackTarget((MobEntity) entity), entity);
		}

	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		LivingEntity entity = event.getEntityLiving();
		World world = entity.level;
		if (!world.isClientSide && entity instanceof FoxEntity && LOTRDimensions.isModDimension(world)) {
			FoxEntity fox = (FoxEntity) entity;
			String biomeCheckKey = String.format("%s:%s", "lotr", "FoxBiomeCheck");
			if (!fox.getPersistentData().getBoolean(biomeCheckKey)) {
				net.minecraft.entity.passive.FoxEntity.Type initialFoxType = fox.getFoxType();
				net.minecraft.entity.passive.FoxEntity.Type newFoxType = initialFoxType;
				BlockPos entityPos = entity.blockPosition();
				Biome biome = world.getBiome(entity.blockPosition());
				if (biome.getTemperature(entityPos) < 0.15F) {
					newFoxType = net.minecraft.entity.passive.FoxEntity.Type.SNOW;
				}

				if (newFoxType != initialFoxType) {
					try {
						Method m_setVariantType = ObfuscationReflectionHelper.findMethod(FoxEntity.class, "setFoxType", net.minecraft.entity.passive.FoxEntity.Type.class);
						LOTRUtil.unlockMethod(m_setVariantType);
						m_setVariantType.invoke(fox, newFoxType);
					} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var11) {
						LOTRLog.error("Error setting fox type based on biome");
						var11.printStackTrace();
					}
				}

				fox.getPersistentData().putBoolean(biomeCheckKey, true);
			}
		}

	}

	@SubscribeEvent
	public void onOpenContainer(Open event) {
		Container container = event.getContainer();
		PlayerEntity player = event.getPlayer();
		if (container instanceof ShulkerBoxContainer) {
			ShulkerBoxContainerFix.fixContainerSlots((ShulkerBoxContainer) container, player);
		}

	}

	@SubscribeEvent
	public void onPistonMoveCheck(Pre event) {
	}

	@SubscribeEvent
	public void onPlayerHarvestCheck(HarvestCheck event) {
		BlockState state = event.getTargetBlock();
		PlayerEntity player = event.getPlayer();
		ItemStack heldItem = player.getItemInHand(Hand.MAIN_HAND);
		if (state.getBlock() instanceof HangingWebBlock && !heldItem.isEmpty() && heldItem.isCorrectToolForDrops(Blocks.COBWEB.defaultBlockState())) {
			event.setCanHarvest(true);
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		PlayerEntity player = event.getPlayer();
		World world = event.getWorld();
		Hand hand = event.getHand();
		ItemStack heldItem = event.getItemStack();
		BlockPos pos = event.getPos();
		Direction side = event.getFace();
		boolean sneaking = player.isSecondaryUseActive();
		if ((event instanceof RightClickBlock || event instanceof LeftClickBlock) && TerrainProtections.isTerrainProtectedFromPlayerEdits(event, heldItem, pos, side)) {
			event.setCanceled(true);
		} else if (event instanceof RightClickBlock) {
			if (side != null && !player.mayUseItemAt(pos, side, heldItem)) {
				return;
			}

			BlockState state = world.getBlockState(pos);
			RayTraceResult hitVec = ((RightClickBlock) event).getHitVec();
			if (!heldItem.isEmpty() && heldItem.getItem() instanceof BlockItem) {
				Block itemBlock = Block.byItem(heldItem.getItem());
				if (itemBlock instanceof SlabBlock) {
					SlabBlock itemSlabBlock = (SlabBlock) itemBlock;
					if (VerticalOnlySlabBlock.getVerticalSlabFor(itemSlabBlock) != null) {
						VerticalOnlySlabBlock vSlab = VerticalOnlySlabBlock.getVerticalSlabFor(itemSlabBlock);
						if (hitVec instanceof BlockRayTraceResult) {
							BlockRayTraceResult blockRayTrace = (BlockRayTraceResult) hitVec;
							boolean holdingAnyItem = true;
							boolean sneakUsingItem = sneaking && holdingAnyItem && (!player.getMainHandItem().doesSneakBypassUse(world, pos, player) || !player.getOffhandItem().doesSneakBypassUse(world, pos, player));
							ActionResultType verticalPlaceResult;
							if (!sneakUsingItem) {
								verticalPlaceResult = state.use(world, player, hand, blockRayTrace);
								if (verticalPlaceResult.consumesAction()) {
									event.setCancellationResult(verticalPlaceResult);
									event.setCanceled(true);
									return;
								}
							}

							verticalPlaceResult = vSlab.placeVerticalOrVanilla(player, hand, heldItem, world, pos, side, blockRayTrace);
							if (verticalPlaceResult != ActionResultType.PASS) {
								event.setCancellationResult(verticalPlaceResult);
								event.setCanceled(true);
								return;
							}
						}
					}
				}
			}

			if (heldItem.getToolTypes().contains(ToolType.SHOVEL)) {
				ActionResultType snowPathResult = SnowPathBlock.makeSnowPathUnderSnowLayer(world, pos, side, player, hand, heldItem);
				if (snowPathResult != ActionResultType.PASS) {
					event.setCanceled(true);
					return;
				}
			}

			if (!world.isClientSide && hand == Hand.MAIN_HAND && state.getBlock() instanceof PlateBlock && sneaking && ((PlateBlock) state.getBlock()).popOffOneItem(world, pos, player)) {
				event.setCanceled(true);
				return;
			}

			if (state.getBlock() instanceof CauldronBlock && !heldItem.isEmpty() && !sneaking) {
				int waterLevel = state.getValue(CauldronBlock.LEVEL);
				if (waterLevel > 0) {
					boolean isCauldronUse = false;
					Item item = heldItem.getItem();
					if (item instanceof PouchItem && PouchItem.isPouchDyed(heldItem)) {
						if (!world.isClientSide) {
							PouchItem.removePouchDye(heldItem);
							player.awardStat(LOTRStats.CLEAN_POUCH);
						}

						isCauldronUse = true;
					} else if (item instanceof SmokingPipeItem && SmokingPipeItem.isSmokeDyed(heldItem)) {
						if (!world.isClientSide) {
							SmokingPipeItem.clearSmokeColor(heldItem);
							player.awardStat(LOTRStats.CLEAN_SMOKING_PIPE);
						}

						isCauldronUse = true;
					}

					if (isCauldronUse) {
						if (!world.isClientSide) {
							((CauldronBlock) state.getBlock()).setWaterLevel(world, pos, state, waterLevel - 1);
						}

						event.setCancellationResult(ActionResultType.sidedSuccess(world.isClientSide));
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();
		World world = player.level;
		if (!world.isClientSide) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			ServerWorld serverWorld = serverPlayer.getLevel();
			LOTRLevelData levelData = LOTRLevelData.serverInstance();
			MapSettingsManager.serverInstance().sendMapToPlayer(serverPlayer);
			FactionSettingsManager.serverInstance().sendFactionsToPlayer(serverPlayer);
			NPCEntitySettingsManager.serverInstance().sendEntitySettingsToPlayer(serverPlayer);
			if (LOTRWorldTypes.isInstantME(serverWorld) && LOTRDimensions.isDimension(serverWorld, World.OVERWORLD)) {
				MiscDataModule miscData = levelData.getData(serverPlayer).getMiscData();
				if (!miscData.getInitialSpawnedIntoME()) {
					RingPortalTeleporter.transferEntity(serverWorld, serverPlayer, Optional.empty(), false);
					miscData.setInitialSpawnedIntoME(true);
				}
			}

			levelData.sendLoginPacket(serverPlayer);
			levelData.playerDataHandleLogin(serverPlayer);
			levelData.sendAllOtherPlayerAlignmentsToPlayer(serverPlayer);
			levelData.sendPlayerAlignmentToAllOtherPlayers(serverPlayer);
			LOTRTime.sendLoginPacket(serverPlayer);
			LOTRDate.sendLoginPacket(serverPlayer);
		}

	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		PlayerEntity player = event.getPlayer();
		World world = player.level;
		if (!world.isClientSide && player.getVehicle() instanceof AbstractChestedHorseEntity) {
			AbstractChestedHorseEntity logoutHorse = (AbstractChestedHorseEntity) player.getVehicle();
			double checkRange = 64.0D;
			List nearbyPlayers = world.getEntitiesOfClass(PlayerEntity.class, player.getBoundingBox().expandTowards(checkRange, checkRange, checkRange));
			Iterator var8 = nearbyPlayers.iterator();

			while (var8.hasNext()) {
				PlayerEntity otherPlayer = (PlayerEntity) var8.next();
				if (otherPlayer != player && otherPlayer.containerMenu instanceof HorseInventoryContainer) {
					HorseInventoryContainer horseInv = (HorseInventoryContainer) otherPlayer.containerMenu;
					AbstractHorseEntity openHorse = (AbstractHorseEntity) ObfuscationReflectionHelper.getPrivateValue(HorseInventoryContainer.class, horseInv, "horse");
					if (openHorse == logoutHorse) {
						otherPlayer.closeContainer();
					}
				}
			}
		}

	}

	@SubscribeEvent
	public void onProjectileImpact(ProjectileImpactEvent event) {
		Entity entity = event.getEntity();
		RayTraceResult rayTrace = event.getRayTraceResult();
		if (entity instanceof ProjectileEntity) {
			ProjectileEntity projectile = (ProjectileEntity) entity;
			Entity shooter = projectile.getOwner();
			if (rayTrace.getType() == Type.ENTITY) {
				Entity hitEntity = ((EntityRayTraceResult) rayTrace).getEntity();
				if (shooter instanceof LivingEntity && !EntityFactionHelper.canEntityCauseDamageToTarget((LivingEntity) shooter, hitEntity, true)) {
					event.setCanceled(true);
				}
			}
		}

	}

	@SubscribeEvent
	public void onSaplingGrow(SaplingGrowTreeEvent event) {
		ServerWorld sWorld = (ServerWorld) event.getWorld();
		ChunkGenerator cg = sWorld.getChunkSource().getGenerator();
		BlockPos pos = event.getPos();
		BlockState state = sWorld.getBlockState(pos);
		Random rand = event.getRand();
		if (VanillaSaplingPartyTrees.attemptGrowPartyTree(state, sWorld, cg, pos, rand)) {
			event.setResult(Result.DENY);
		} else if (state.getBlock() == Blocks.BIRCH_SAPLING) {
			Tree altTree = new BirchTreeAlt();
			altTree.growTree(sWorld, cg, pos, state, rand);
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		speechGarbler.handle(event);
	}

	@SubscribeEvent
	public void onSleepFinished(SleepFinishedTimeEvent event) {
		ServerWorld world = (ServerWorld) event.getWorld();
		if (!world.isClientSide && LOTRDimensions.isModDimension(world)) {
			LOTRTime.advanceToMorning(world);
			if (world.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
				ServerWorld overworld = world.getServer().overworld();
				IServerWorldInfo overworldInfo = (IServerWorldInfo) overworld.getLevelData();
				overworldInfo.setRainTime(0);
				overworldInfo.setRaining(false);
				overworldInfo.setThunderTime(0);
				overworldInfo.setThundering(false);
			}
		}

	}

	@SubscribeEvent
	public void onStartTrackingEntity(StartTracking event) {
		Entity entity = event.getTarget();
		PlayerEntity player = event.getPlayer();
		if (!entity.level.isClientSide) {
			if (entity instanceof MobEntity) {
				LOTRPacketHandler.sendTo(new SPacketSetAttackTarget((MobEntity) entity), (ServerPlayerEntity) player);
			}

			if (entity instanceof NPCEntity) {
				((NPCEntity) entity).onPlayerStartTrackingNPC((ServerPlayerEntity) player);
			}
		}

	}

	@SubscribeEvent
	public void onUseBucket(FillBucketEvent event) {
		World world = event.getWorld();
		ItemStack heldItem = event.getEmptyBucket();
		RayTraceResult target = event.getTarget();
		if (target.getType() == Type.BLOCK) {
			BlockRayTraceResult blockRayTrace = (BlockRayTraceResult) target;
			BlockPos pos = blockRayTrace.getBlockPos();
			Direction side = blockRayTrace.getDirection();
			if (TerrainProtections.isTerrainProtectedFromPlayerEdits(event, heldItem, pos, side)) {
				event.setCanceled(true);
				BlockState state = world.getBlockState(pos);
				world.sendBlockUpdated(pos, state, state, 2);
			}
		}

	}

	@SubscribeEvent
	public void onWorldSave(Save event) {
		IWorld world = event.getWorld();
		if (world instanceof ServerWorld) {
			ServerWorld sWorld = (ServerWorld) world;
			boolean isCompleteGameSave = LOTRDimensions.isDimension(sWorld, World.OVERWORLD);
			if (isCompleteGameSave) {
				LOTRTime.save(sWorld);
			}
		}

	}

	private void registerHandlers(Object... handlers) {
		Object[] var2 = handlers;
		int var3 = handlers.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			Object handler = var2[var4];
			MinecraftForge.EVENT_BUS.register(handler);
		}

	}
}
