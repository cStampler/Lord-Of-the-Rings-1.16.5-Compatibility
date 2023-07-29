package lotr.common.event;

import java.util.Random;

import lotr.common.data.*;
import lotr.common.entity.npc.GaladhrimWarriorEntity;
import lotr.common.fac.*;
import lotr.common.init.*;
import lotr.common.speech.EventSpeechbanks;
import lotr.common.world.biome.LOTRBiomeWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;

public class BreakMallornResponse {
	private static final FactionPointer FACTION;

	static {
		FACTION = FactionPointers.LOTHLORIEN;
	}

	public void handleBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state) {
		if (!world.isClientSide && state.is(LOTRTags.Blocks.BREAK_MALLORN_RESPONSES) && !player.abilities.instabuild) {
			ServerWorld sWorld = (ServerWorld) world;
			Random rand = world.random;
			LOTRBiomeWrapper biomeWrapper = LOTRBiomes.getWrapperFor(world.getBiome(pos), world);
			LOTRPlayerData pd = LOTRLevelData.getSidedData(player);
			if (rand.nextInt(3) == 0 && biomeWrapper.hasBreakMallornResponse() && pd.getAlignmentData().getAlignment(FACTION) < 0.0F) {
				int elves = 2 + world.random.nextInt(4);
				boolean sentMessage = false;

				for (int l = 0; l < elves; ++l) {
					GaladhrimWarriorEntity warrior = spawnWarrior(sWorld, rand, player);
					if (warrior != null && !sentMessage) {
						warrior.sendSpeechTo((ServerPlayerEntity) player, EventSpeechbanks.GALADHRIM_WARRIOR_DEFEND_MALLORN);
						sentMessage = true;
					}
				}
			}
		}

	}

	private GaladhrimWarriorEntity spawnWarrior(ServerWorld world, Random rand, PlayerEntity player) {
		GaladhrimWarriorEntity warrior = (GaladhrimWarriorEntity) ((EntityType) LOTREntities.GALADHRIM_WARRIOR.get()).create(world);
		int range = 6;
		int x = MathHelper.floor(player.getX()) + MathHelper.nextInt(rand, -range, range);
		int z = MathHelper.floor(player.getZ()) + MathHelper.nextInt(rand, -range, range);
		int y = world.getHeight(Type.MOTION_BLOCKING_NO_LEAVES, x, z);
		BlockPos topPos = new BlockPos(x, y, z);
		if (world.getBlockState(topPos.below()).isFaceSturdy(world, topPos.below(), Direction.UP) && !world.getBlockState(topPos).isRedstoneConductor(world, topPos) && !world.getBlockState(topPos.above()).isRedstoneConductor(world, topPos.above())) {
			warrior.moveTo(x + 0.5D, y, z + 0.5D, rand.nextFloat() * 360.0F, 0.0F);
			if (warrior.checkSpawnRules(world, SpawnReason.EVENT)) {
				warrior.spawnRidingHorse = false;
				warrior.finalizeSpawn(world, world.getCurrentDifficultyAt(warrior.blockPosition()), SpawnReason.EVENT, (ILivingEntityData) null, (CompoundNBT) null);
				world.addFreshEntityWithPassengers(warrior);
				warrior.setDefendingTree();
				warrior.setTarget(player);
				return warrior;
			}
		}

		return null;
	}
}
