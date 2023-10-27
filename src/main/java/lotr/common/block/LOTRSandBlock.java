package lotr.common.block;

import net.minecraft.block.SandBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class LOTRSandBlock extends SandBlock {
	public LOTRSandBlock(int particleRGB, MaterialColor mapColor) {
		super(particleRGB, Properties.of(Material.SAND, mapColor).strength(0.5F).sound(SoundType.SAND));
	}
}
