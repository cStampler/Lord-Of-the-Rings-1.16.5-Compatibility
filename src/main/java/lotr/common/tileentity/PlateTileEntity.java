package lotr.common.tileentity;

import lotr.client.render.tileentity.PlateTileEntityRenderer;
import lotr.common.init.LOTRTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.common.util.LazyOptional;

public class PlateTileEntity extends TileEntity {
	private ItemStack foodItem;
	private LazyOptional fallingDataForRender;

	public PlateTileEntity() {
		super((TileEntityType) LOTRTileEntities.PLATE.get());
		foodItem = ItemStack.EMPTY;
		fallingDataForRender = LazyOptional.empty();
	}

	public LazyOptional getFallingDataForRender() {
		return fallingDataForRender;
	}

	public ItemStack getFoodItem() {
		return foodItem;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB bb = super.getRenderBoundingBox();
		if (!foodItem.isEmpty()) {
			float itemHeight = PlateTileEntityRenderer.getItemHeight(foodItem);
			bb = bb.expandTowards(0.0D, foodItem.getCount() * itemHeight, 0.0D);
		}

		return bb;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		writeFood(nbt);
		return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		readFood(nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readFood(pkt.getTag());
	}

	private void readFood(CompoundNBT nbt) {
		foodItem = ItemStack.of(nbt.getCompound("FoodItem"));
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		writeFood(nbt);
		return nbt;
	}

	public void setFallingDataForRender(LazyOptional fallingData) {
		fallingDataForRender = fallingData;
	}

	public void setFoodItem(ItemStack item) {
		foodItem = item;
		setChanged();
		if (hasLevel()) {
			getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}

	}

	private void writeFood(CompoundNBT nbt) {
		nbt.put("FoodItem", foodItem.save(new CompoundNBT()));
	}

	public static int getMaxStackSizeOnPlate(ItemStack itemstack) {
		return itemstack.getItem() == Items.BOWL ? 1 : itemstack.getMaxStackSize();
	}

	public static boolean isValidFoodItem(ItemStack itemstack) {
		if (!itemstack.isEmpty()) {
			Item item = itemstack.getItem();
			if (item.isEdible()) {
				if (!(item instanceof SoupItem) && !(item instanceof SuspiciousStewItem) && item.hasContainerItem(itemstack)) {
					return false;
				}

				return true;
			}

			if (item == Items.BOWL) {
				return true;
			}
		}

		return false;
	}
}
