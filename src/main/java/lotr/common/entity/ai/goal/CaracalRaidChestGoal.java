package lotr.common.entity.ai.goal;

import java.util.Random;
import java.util.stream.IntStream;

import lotr.common.entity.animal.CaracalEntity;
import lotr.common.util.LOTRUtil;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class CaracalRaidChestGoal extends MoveToBlockGoal {
	private final CaracalEntity theCaracal;
	private final Random rand;
	private int raidingTick = 0;
	private boolean isChestEmpty = false;

	public CaracalRaidChestGoal(CaracalEntity caracal, double speed) {
		super(caracal, speed, 8, 2);
		theCaracal = caracal;
		rand = theCaracal.getRandom();
	}

	@Override
	public double acceptedDistance() {
		return 1.5D;
	}

	@Override
	public boolean canContinueToUse() {
		if (raidingTick > 200 || isChestEmpty) {
			return false;
		}
		return theCaracal.hasItemInMouth() ? false : super.canContinueToUse();
	}

	@Override
	public boolean canUse() {
		return !theCaracal.hasItemInMouth() && !theCaracal.isOrderedToSit() && super.canUse();
	}

	@Override
	protected boolean isValidTarget(IWorldReader world, BlockPos pos) {
		if (world.isEmptyBlock(pos.above())) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof IInventory) {
				IInventory inv = (IInventory) te;
				IntStream var10000 = IntStream.range(0, inv.getContainerSize());
				inv.getClass();
				return var10000.mapToObj(inv::getItem).anyMatch(this::isWorthRaidingChestFor);
			}
		}

		return false;
	}

	private boolean isWorthRaidingChestFor(ItemStack stack) {
		return !stack.isEmpty() && CaracalEntity.WANTS_TO_EAT.test(stack);
	}

	@Override
	protected int nextStartTick(CreatureEntity entity) {
		return LOTRUtil.secondsToTicks(15 + rand.nextInt(30));
	}

	@Override
	public boolean shouldRecalculatePath() {
		return super.shouldRecalculatePath() && !isReachedTarget();
	}

	@Override
	public void start() {
		raidingTick = 0;
		isChestEmpty = false;
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
		theCaracal.setIsRaidingChest(false);
	}

	@Override
	public void tick() {
		super.tick();
		theCaracal.setInSittingPose(false);
		if (isReachedTarget()) {
			theCaracal.getNavigation().stop();
			theCaracal.getLookControl().setLookAt(Vector3d.atCenterOf(blockPos));
			theCaracal.setIsRaidingChest(true);
			++raidingTick;
			if (raidingTick > 20 && rand.nextInt(10) == 0) {
				World world = theCaracal.level;
				TileEntity te = world.getBlockEntity(blockPos);
				if (te instanceof IInventory) {
					IInventory inv = (IInventory) te;
					int[] occupiedSlots = IntStream.range(0, inv.getContainerSize()).filter(slotx -> !inv.getItem(slotx).isEmpty()).toArray();
					if (occupiedSlots.length > 0) {
						int slot = Util.getRandom(occupiedSlots, rand);
						ItemStack dropStack = inv.removeItem(slot, 1 + rand.nextInt(4));
						if (theCaracal.canEatItem(dropStack) && rand.nextInt(3) == 0) {
							theCaracal.setItemInMouth(dropStack);
						} else {
							ItemEntity dropEntity = new ItemEntity(world, blockPos.getX() + 0.5D, blockPos.getY() + 1, blockPos.getZ() + 0.5D, dropStack);
							dropEntity.setDeltaMovement(dropEntity.getDeltaMovement().scale(2.0D));
							world.addFreshEntity(dropEntity);
							world.playSound((PlayerEntity) null, blockPos, SoundEvents.WOOL_PLACE, SoundCategory.NEUTRAL, 0.5F, 0.8F + rand.nextFloat() * 0.2F);
						}
					} else {
						isChestEmpty = true;
					}
				}
			}
		} else {
			if (raidingTick > 0) {
				--raidingTick;
			}

			theCaracal.setIsRaidingChest(false);
		}

	}
}
