package lotr.common.world.gen.feature.grassblend;

import java.util.List;

public class SingleGrassBlend extends GrassBlend {
	public SingleGrassBlend(List entries) {
		super(entries);
	}

	public static SingleGrassBlend of(Object... weightedConfigs) {
		return (SingleGrassBlend) GrassBlend.of(hummel -> new SingleGrassBlend((List) hummel), weightedConfigs);
	}
}
