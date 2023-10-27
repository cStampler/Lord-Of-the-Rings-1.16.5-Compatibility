package lotr.common.tileentity;

import lotr.common.init.LOTRItems;
import lotr.common.init.LOTRTileEntities;
import lotr.common.item.VesselDrinkItem;
import lotr.common.item.VesselOperations;
import lotr.common.item.VesselType;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IItemProvider;

public class VesselDrinkTileEntity extends TileEntity {
	private ItemStack drinkItem;
	private VesselType vesselType;

	public VesselDrinkTileEntity() {
		super((TileEntityType) LOTRTileEntities.VESSEL_DRINK.get());
		drinkItem = ItemStack.EMPTY;
		vesselType = VesselType.WOODEN_MUG;
	}

	public void fillFromRain() {
		if (!level.isClientSide && isEmpty()) {
			ItemStack waterItem = new ItemStack((IItemProvider) LOTRItems.WATER_DRINK.get());
			VesselDrinkItem.setVessel(waterItem, getVesselType());
			setVesselItem(waterItem);
		}

	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		writeVessel(nbt);
		return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return save(new CompoundNBT());
	}

	public ItemStack getVesselItem() {
		if (drinkItem.isEmpty()) {
			return vesselType.createEmpty();
		}
		ItemStack copy = drinkItem.copy();
		if (copy.getItem() instanceof VesselDrinkItem) {
			VesselDrinkItem.setVessel(copy, vesselType);
		}

		copy.setCount(1);
		return copy;
	}

	public VesselType getVesselType() {
		return vesselType;
	}

	public boolean isEmpty() {
		return VesselOperations.isItemEmptyVessel(getVesselItem());
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		readVessel(nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		readVessel(pkt.getTag());
	}

	private void readVessel(CompoundNBT nbt) {
		drinkItem = ItemStack.of(nbt.getCompound("DrinkItem"));
		vesselType = VesselType.forName(nbt.getString("Vessel"));
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		writeVessel(nbt);
		return nbt;
	}

	public void setEmpty() {
		setVesselItem(getVesselType().createEmpty());
	}

	public void setVesselItem(ItemStack itemstack) {
		drinkItem = itemstack.copy();
		drinkItem.setCount(1);
		setChanged();
		if (hasLevel()) {
			getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}

	}

	public void setVesselType(VesselType vessel) {
		vesselType = vessel;
		setChanged();
		if (hasLevel()) {
			getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}

	}

	private void writeVessel(CompoundNBT nbt) {
		nbt.put("DrinkItem", drinkItem.save(new CompoundNBT()));
		nbt.putString("Vessel", vesselType.getCodeName());
	}
}
