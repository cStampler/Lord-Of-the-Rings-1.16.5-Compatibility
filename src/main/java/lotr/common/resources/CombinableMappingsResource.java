package lotr.common.resources;

import java.util.*;

public abstract class CombinableMappingsResource {
	protected final Map mappings;
	private final int loadOrder;
	private final int numCombinedFrom;

	public CombinableMappingsResource(Map mappings, int loadOrder, int numCombinedFrom) {
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

	public static CombinableMappingsResource combine(List resources, CombinableMappingsResource.CombinableMappingsResourceFactory factory) {
		List sorted = new ArrayList(resources);
		Collections.sort(sorted, Comparator.comparingInt(CombinableMappingsResource::getLoadOrder));
		Map mappings = new HashMap();
		Iterator var4 = sorted.iterator();

		while (var4.hasNext()) {
			CombinableMappingsResource table = (CombinableMappingsResource) var4.next();
			mappings.putAll(table.mappings);
		}

		return factory.create(mappings, 0, sorted.size());
	}

	@FunctionalInterface
	public interface CombinableMappingsResourceFactory {
		CombinableMappingsResource create(Map var1, int var2, int var3);
	}
}
