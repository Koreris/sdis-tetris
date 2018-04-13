package com.sdis.tetris.audio;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V>
{
	public interface CacheEntryRemovedListener<K, V>
	{
		void notifyEntryRemoved(K key, V value);
	}

	private Map<K, V> cache;
	private CacheEntryRemovedListener<K, V> entryRemovedListener;

	public LRUCache(final int maxEntries, final CacheEntryRemovedListener<K, V> paramListener)
	{
		cache = new LinkedHashMap<K, V>((int) Math.ceil(maxEntries * 1.75), .75f, true)
		{
			private static final long serialVersionUID = -1650698049637132983L;

			public boolean removeEldestEntry(Map.Entry<K, V> eldest)
			{
				if (size() > maxEntries)
				{
					if (entryRemovedListener != null)
					{
						entryRemovedListener.notifyEntryRemoved(eldest.getKey(), eldest.getValue());
					}

					return true;
				}

				return false;
			}
		};

		entryRemovedListener = paramListener;
	}

	public void add(final K key, final V value)
	{
		cache.put(key, value);
	}

	public V get(final K key)
	{
		return cache.get(key);
	}
}