package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.*;

public class LOTRSandBlock extends SandBlock {
	public LOTRSandBlock(int particleRGB, MaterialColor mapColor) {
		super(particleRGB, Properties.of(Material.SAND, mapColor).strength(0.5F).sound(SoundType.SAND));
	}
}
