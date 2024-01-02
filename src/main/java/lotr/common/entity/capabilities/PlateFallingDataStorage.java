package lotr.common.entity.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlateFallingDataStorage implements IStorage<PlateFallingData> {
	@Override
	public void readNBT(Capability<PlateFallingData> capability, PlateFallingData instance, Direction side, INBT nbt) {
	}

	@Override
	public INBT writeNBT(Capability<PlateFallingData> capability, PlateFallingData instance, Direction side) {
		return new CompoundNBT();
	}
}
