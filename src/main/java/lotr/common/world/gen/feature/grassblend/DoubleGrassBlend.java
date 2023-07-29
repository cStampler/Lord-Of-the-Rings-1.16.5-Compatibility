package lotr.common.world.gen.feature.grassblend;

import java.util.List;

public class DoubleGrassBlend extends GrassBlend {
	public DoubleGrassBlend(List entries) {
		super(entries);
	}

	public static DoubleGrassBlend of(Object... weightedConfigs) {
		return (DoubleGrassBlend) GrassBlend.of(hummel -> new DoubleGrassBlend((List) hummel), weightedConfigs);
	}
}
