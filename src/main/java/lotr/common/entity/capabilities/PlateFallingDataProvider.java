package lotr.common.entity.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlateFallingDataProvider implements ICapabilitySerializable<INBT> {
	public static final ResourceLocation KEY = new ResourceLocation("lotr", "plate_falling_data");
	@CapabilityInject(PlateFallingData.class)
	public static final Capability<PlateFallingData> CAPABILITY = null;
	private LazyOptional<PlateFallingData> instance = LazyOptional.of(CAPABILITY::getDefaultInstance);

	@Override
	public void deserializeNBT(INBT nbt) {
		try {
			CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), (Direction) null, nbt);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return (cap == CAPABILITY) ? this.instance.cast() : LazyOptional.empty();
	}

	@Override
	public INBT serializeNBT() {
		try {
			return CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), (Direction) null);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
