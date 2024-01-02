package lotr.common.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LookupList<T, K> extends AbstractList<T> {
	private final List<T> list = new ArrayList<>();
	private final Map<K, T> lookup = new HashMap<>();
	private final Function<T, K> keyExtractor;

	public LookupList(Function<T, K> keyExtractor) {
		this.keyExtractor = keyExtractor;
	}

	@Override
	public void add(int index, T element) {
		list.add(index, element);
		lookup.put(keyExtractor.apply(element), element);
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}

	public boolean hasKey(K key) {
		return lookup.containsKey(key);
	}

	public T lookup(K key) {
		return lookup.get(key);
	}

	@Override
	public T remove(int index) {
		T removed = list.remove(index);
		if (removed != null) {
			lookup.remove(keyExtractor.apply(removed));
		}

		return removed;
	}

	@Override
	public T set(int index, T element) {
		list.set(index, element);
		lookup.put(keyExtractor.apply(element), element);
		return element;
	}

	@Override
	public int size() {
		return list.size();
	}
}
