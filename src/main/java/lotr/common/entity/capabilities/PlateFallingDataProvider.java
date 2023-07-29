package lotr.common.entity.capabilities;

import net.minecraft.nbt.INBT;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;

public class PlateFallingDataProvider implements ICapabilitySerializable {
	public static final ResourceLocation KEY = new ResourceLocation("lotr", "plate_falling_data");
	@CapabilityInject(PlateFallingData.class)
	public static final Capability CAPABILITY = null;
	private LazyOptional instance;

	public PlateFallingDataProvider() {
		Capability var10001 = CAPABILITY;
		var10001.getClass();
		instance = LazyOptional.of(var10001::getDefaultInstance);
	}

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
	public LazyOptional getCapability(Capability cap, Direction side) {
		return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
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
