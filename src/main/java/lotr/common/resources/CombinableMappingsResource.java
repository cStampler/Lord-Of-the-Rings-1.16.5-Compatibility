package lotr.common.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CombinableMappingsResource<K, V> {
	protected final Map<K, V> mappings;
	private final int loadOrder;
	private final int numCombinedFrom;

	public CombinableMappingsResource(Map<K, V> mappings, int loadOrder, int numCombinedFrom) {
		this.mappings = mappings;
		this.loadOrder = loadOrder;
		this.numCombinedFrom = numCombinedFrom;
	}

	private int getLoadOrder() {
		return loadOrder;
	}

	public final int getNumCombinedFrom() {
		return numCombinedFrom;
	}

	public final int size() {
		return mappings.size();
	}

	public static <K, V, T extends CombinableMappingsResource<K, V>> T combine(List<T> resources, CombinableMappingsResource.CombinableMappingsResourceFactory<K, V, T> factory) {
		List<T> sorted = new ArrayList<>(resources);
		Collections.sort(sorted, Comparator.comparingInt(CombinableMappingsResource::getLoadOrder));
		Map<K, V> mappings = new HashMap<>();
		for (CombinableMappingsResource<K, V> table : sorted)
		      mappings.putAll(table.mappings); 

		return factory.create(mappings, 0, sorted.size());
	}

	@FunctionalInterface
	public interface CombinableMappingsResourceFactory<K, V, T extends CombinableMappingsResource<K, V>> {
		T create(Map<K, V> param1Map, int param1Int1, int param1Int2);
	}
}
