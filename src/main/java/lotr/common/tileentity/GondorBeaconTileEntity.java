package lotr.common.tileentity;

import java.util.Random;

import lotr.common.LOTRGameRules;
import lotr.common.block.GondorBeaconBlock;
import lotr.common.init.LOTRTileEntities;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class GondorBeaconTileEntity extends TileEntity implements ITickableTileEntity {
	private int ticksExisted;
	private boolean isBurning;
	private int litCounter;
	private int unlitCounter;
	private long lastManualStateChangeTime = -1L;

	public GondorBeaconTileEntity() {
		super((TileEntityType) LOTRTileEntities.GONDOR_BEACON.get());
	}

	private void addCampfireParticles() {
		World world = getLevel();
		if (world != null) {
			BlockPos pos = getBlockPos();
			if (!isTopCovered(pos)) {
				Random rand = world.random;
				if (rand.nextFloat() < 0.11F) {
					int numSmoke = rand.nextInt(2) + 2;

					for (int i = 0; i < numSmoke; ++i) {
						boolean isSignalFire = true;
						boolean spawnExtraSmoke = false;
						CampfireBlock.makeParticles(world, pos, isSignalFire, spawnExtraSmoke);
					}
				}
			}
		}

	}

	public void beginBurning() {
		if (!isBurning) {
			setBurning(true);
		}

	}

	public void extinguish() {
		if (isBurning) {
			updateFullyLit(false);
			setBurning(false);
		}

	}

	private int getLightingTime() {
		return level.getGameRules().getInt(LOTRGameRules.GONDOR_BEACON_LIGHTING_TIME);
	}

	private int getSpreadRange() {
		return level.getGameRules().getInt(LOTRGameRules.GONDOR_BEACON_RANGE);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		writeBurning(nbt);
		return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		writeBurning(nbt);
		return nbt;
	}

	public boolean isBurning() {
		return isBurning;
	}

	private boolean isFullyLit() {
		return getBlockState().getValue(GondorBeaconBlock.FULLY_LIT);
	}

	private boolean isTopCovered(BlockPos beaconPos) {
		worldPosition.above();
		return LOTRUtil.hasSolidSide(level, beaconPos.above(), Direction.DOWN) || LOTRUtil.hasSolidSide(level, beaconPos.above(), Direction.UP);
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		readBurning(nbt);
		litCounter = nbt.getByte("LitCounter");
		unlitCounter = nbt.getByte("UnlitCounter");
		lastManualStateChangeTime = nbt.getLong("StateChangeTime");
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readBurning(pkt.getTag());
	}

	private void readBurning(CompoundNBT nbt) {
		isBurning = nbt.getBoolean("IsBurning");
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		writeBurning(nbt);
		nbt.putByte("LitCounter", (byte) litCounter);
		nbt.putByte("UnlitCounter", (byte) unlitCounter);
		nbt.putLong("StateChangeTime", lastManualStateChangeTime);
		return nbt;
	}

	private void setBurning(boolean flag) {
		boolean wasBurning = isBurning;
		if (wasBurning != flag) {
			isBurning = flag;
			if (!isBurning) {
				litCounter = 0;
			} else {
				unlitCounter = 0;
			}

			lastManualStateChangeTime = level.getGameTime();
			setChanged();
			getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
			if (wasBurning) {
			}
		}

	}

	private boolean shouldSpreadBurning() {
		return isBurning && litCounter >= getLightingTime();
	}

	private boolean shouldSpreadExtinguishing() {
		return !isBurning && unlitCounter >= getLightingTime();
	}

	private void spreadStateTo(GondorBeaconTileEntity other) {
		if (lastManualStateChangeTime > other.lastManualStateChangeTime) {
			if (shouldSpreadBurning() && !other.isBurning()) {
				other.setBurning(true);
			} else if (shouldSpreadExtinguishing() && other.isBurning()) {
				other.setBurning(false);
			}
		}

	}

	@Override
	public void tick() {
		++ticksExisted;
		if (!level.isClientSide) {
			if (isBurning && litCounter < getLightingTime()) {
				++litCounter;
				if (litCounter >= getLightingTime()) {
					updateFullyLit(true);
				}
			} else if (!isBurning && unlitCounter < getLightingTime()) {
				++unlitCounter;
				if (unlitCounter >= getLightingTime()) {
					updateFullyLit(false);
				}
			}

			if (ticksExisted % 10 == 0 && (shouldSpreadBurning() || shouldSpreadExtinguishing())) {
				int range = getSpreadRange();
				int rangeSq = range * range;
				int chunkSearchRange = (range >> 4) + 1;
				int chunkX = getBlockPos().getX() >> 4;
				int chunkZ = getBlockPos().getZ() >> 4;

				for (int i = -chunkSearchRange; i <= chunkSearchRange; ++i) {
					for (int k = -chunkSearchRange; k <= chunkSearchRange; ++k) {
						int aChunkX = chunkX + i;
						int aChunkZ = chunkZ + k;
						if (level.hasChunk(aChunkX, aChunkZ)) {
							Chunk chunk = level.getChunk(aChunkX, aChunkZ);
							if (chunk != null) {
								for (TileEntity te : chunk.getBlockEntities().values()) {
									if (!te.isRemoved() && te instanceof GondorBeaconTileEntity) {
										GondorBeaconTileEntity beacon = (GondorBeaconTileEntity) te;
										if (getBlockPos().distSqr(beacon.getBlockPos()) <= rangeSq) {
											spreadStateTo(beacon);
										}
									}
								}
							}
						}
					}
				}
			}
		} else if (isFullyLit()) {
			addCampfireParticles();
		}

	}

	private void updateFullyLit(boolean flag) {
		level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(GondorBeaconBlock.FULLY_LIT, flag));
		setChanged();
	}

	private void writeBurning(CompoundNBT nbt) {
		nbt.putBoolean("IsBurning", isBurning);
	}
}
