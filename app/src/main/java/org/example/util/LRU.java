package org.example.util;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.errorprone.annotations.ThreadSafe;
import com.google.errorprone.annotations.concurrent.GuardedBy;

// oldest <-> node(s) <-> latest
// remove from latest
// add to latest

@ThreadSafe
public class LRU<Key, Value> implements Cache<Key, Value> {
	private long size;
	private Map<Key, Node<Key,Value>> cache;
	private Node<Key,Value> latest;
	private Node<Key,Value> oldest;

	public LRU(long capacity) {
		this.size = capacity;
		this.cache = new ConcurrentHashMap<>((int) this.size);

		latest = new Node<>(null,null);
		oldest = new Node<>(null,null);

		this.latest.prev = oldest;
		this.oldest.next = latest;
	}

	public String toString() {
		return cache.toString();
	}

	public void print_list() {
		Node<Key, Value> temp = oldest.next;
		System.out.print("oldest ->");
		while (temp != latest) {
			System.out.print(temp + "->");
			temp = temp.next;
		}
		System.out.println("newest");
	}

	public long cache_capacity() {
		return this.size;
	}

	public long cache_size() {
		return this.cache.size();
	}

	// I do not wanna resize the cache. If i can, i will just use ConcurrentHashMap
	// public void resize_cache(long new_size) {
	// 	if (new_size <= 0) {
	// 		throw new InvalidParameterException();
	// 	}
	// 	this.size = new_size;
	// }

	// NOTE: I do not know if this is worth it :(
	// locking the entire method so that we want to read is 
	// a bit counter intuitive with the aspect of performance
	// so implementing a lock-free version of LRU might be worth it!
	// or any algorithm that is lock-free and/or is optimal for the access patterns
	
	@GuardedBy("this")
	public synchronized void purge() {
		// Make sure to purge only a fixed amount of nodes
		// instead of traversing all the nodes.
		// to promote wait-free-ness of the cache
		Node<Key, Value> lru = this.oldest.next;
		if (lru.is_stale()) {
			Node.remove_self(lru);
		}
	}

	@GuardedBy("this")
	public synchronized void flush() {
		for (var item : this.cache.values()) {
			Node.remove_self(item);
			this.cache.remove(item.key);
		}
	}

	@GuardedBy("this")
	@Override
	public synchronized Optional<Value> get(Key key) {
		
		if (!cache.containsKey(key)) {
			return Optional.empty();
		}

		// TODO: Implement Lockless Caching
		Node<Key,Value> value = cache.get(key);
		if (value.is_stale()) {
			Node.remove_self(value);
			cache.remove(value.key);
			return Optional.empty();
		}

		Node.remove_self(value);
		Node.insert_before(value, latest);
		return value.unwrap();
	}

	@GuardedBy("this")
	@Override
	public synchronized void put(Key key, Value val) {
		if (cache.containsKey(key)) {
			Node<Key,Value> old_value = cache.get(key);
			Node.remove_self(old_value);
		}

		// Create new object to update TTL
		Node<Key,Value> new_value = new Node<>(key,val);
		cache.put(key, new_value);
		Node.insert_before(new_value, latest);

		if (cache.size() > size) {
			Node<Key, Value> lru = oldest.next;
			Node.remove_self(lru);
			cache.remove(lru.key);
		}

		// Assuming that the cache is more frequently read and not written to
		this.purge();
	}
}
