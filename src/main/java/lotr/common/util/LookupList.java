package lotr.common.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LookupList extends AbstractList {
	private final List list = new ArrayList();
	private final Map lookup = new HashMap();
	private final Function keyExtractor;

	public LookupList(Function keyExtractor) {
		this.keyExtractor = keyExtractor;
	}

	@Override
	public void add(int index, Object element) {
		list.add(index, element);
		lookup.put(keyExtractor.apply(element), element);
	}

	@Override
	public Object get(int index) {
		return list.get(index);
	}

	public boolean hasKey(Object key) {
		return lookup.containsKey(key);
	}

	public Object lookup(Object key) {
		return lookup.get(key);
	}

	@Override
	public Object remove(int index) {
		Object removed = list.remove(index);
		if (removed != null) {
			lookup.remove(keyExtractor.apply(removed));
		}

		return removed;
	}

	@Override
	public Object set(int index, Object element) {
		list.set(index, element);
		lookup.put(keyExtractor.apply(element), element);
		return element;
	}

	@Override
	public int size() {
		return list.size();
	}
}
