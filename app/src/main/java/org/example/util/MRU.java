package org.example.util;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// oldest <-> node(s) <-> latest
// remove from latest
// add to latest

// TODO: Implement Lockless Caching
// TODO: Implement Cache invalidation
public class MRU<Key, Value> implements Cache<Key, Value> {
	private long size;
	private Map<Key, Node<Key, Value>> cache;
	private Node<Key, Value> latest;
	private Node<Key, Value> oldest;

	public MRU(long capacity) {
		this.size = capacity;
		this.cache = new ConcurrentHashMap<>((int) this.size);

		latest = new Node<>(null, null);
		oldest = new Node<>(null, null);

		this.latest.prev = oldest;
		this.oldest.next = latest;
	}

	public String toString() {
		return cache.toString();
	}

	public synchronized void purge() {
		for (var item : this.cache.values()) {
			if (item.is_stale()) {
				Node.remove_self(item);
				this.cache.remove(item.key);
			}
		}
	}

	@Override
	public synchronized Optional<Value> get(Key key) {
		if (!cache.containsKey(key)) {
			return Optional.empty();
		}

		Node<Key, Value> node = cache.get(key);
		if (node.is_stale()) {
			Node.remove_self(node);
			cache.remove(node.key);
			return Optional.empty();
		}

		Node.remove_self(node);
		Node.insert_before(node, this.latest);
		return node.unwrap();
	}

	@Override
	public synchronized void put(Key key, Value val) {
		if (cache.containsKey(key)) {
			Node<Key, Value> node = cache.get(key);
			Node.remove_self(node);
		}

		Node<Key, Value> new_value = new Node<>(key, val);
		cache.put(key, new_value);
		Node.insert_before(new_value, latest);

		if (cache.size() > size) {
			Node<Key, Value> mru = latest.prev;
			Node.remove_self(mru);
			cache.remove(mru.key);
		}
	}
}
