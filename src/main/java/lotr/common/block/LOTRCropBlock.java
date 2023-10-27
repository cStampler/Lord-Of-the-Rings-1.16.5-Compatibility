package lotr.common.block;

import java.util.function.Supplier;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IItemProvider;

public class LOTRCropBlock extends CropsBlock {
	private final Supplier seedsItemSup;

	public LOTRCropBlock(Properties properties, Supplier sup) {
		super(properties);
		seedsItemSup = sup;
		CompostingHelper.prepareCompostable(this, 0.65F);
	}

	public LOTRCropBlock(Supplier sup) {
		this(Properties.of(Material.PLANT).noCollission().randomTicks().strength(0.0F).sound(SoundType.CROP), sup);
	}

	@Override
	protected IItemProvider getBaseSeedId() {
		return (IItemProvider) seedsItemSup.get();
	}
}
