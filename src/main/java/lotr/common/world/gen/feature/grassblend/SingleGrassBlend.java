package lotr.common.world.gen.feature.grassblend;

import java.util.List;

public class SingleGrassBlend extends GrassBlend {
	public SingleGrassBlend(List<GrassBlend.Entry> entries) {
		super(entries);
	}

	public static SingleGrassBlend of(Object... weightedConfigs) {
	    return GrassBlend.<SingleGrassBlend>of(SingleGrassBlend::new, weightedConfigs);
	  }
}
