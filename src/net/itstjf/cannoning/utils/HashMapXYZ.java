package net.itstjf.cannoning.utils;

import java.util.HashMap;
import java.util.Map;

public class HashMapXYZ<T, V> extends HashMap<T, Map<T, Map<T, V>>> {
	public void put(T x, T y, T z, V value) {
		if(containsKey(x)) {
			if(get(x).containsKey(y)) get(x).get(y).put(z, value);
			else {
				Map<T, V> mapZ = new HashMap<T, V>();
				mapZ.put(z, value);
				get(x).put(y, mapZ);
			}
		} else {
			Map<T, V> mapZ = new HashMap<T, V>();
			mapZ.put(z, value);
			
			Map<T, Map<T, V>> mapY = new HashMap<T, Map<T, V>>();
			mapY.put(y, mapZ);
			
			put(x, mapY);
		}
	}
	
	public V get(T x, T y, T z) {
		if(get(x) == null) return null;
		if(get(x).get(y) == null) return null;
		return get(x).get(y).get(z);
	}
	
	public boolean contains(T x, T y, T z) {
		if(get(x) == null) return false;
		if(get(x).get(y) == null) return false;
		if(get(x).get(y).get(z) == null) return false;
		return true;
	}
}