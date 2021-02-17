package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListMap<V, T> extends HashMap<V, List<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2422804433946688156L;

	public void add(V key, T value) {
		List<T> list = get(key);
		if (list == null) {
			list = new ArrayList<>();
		}
		if (!list.contains(value)) {
			list.add(value);
		}
		put(key, list);

	}
}
