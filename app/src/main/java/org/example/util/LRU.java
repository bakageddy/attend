package org.example.util;

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class Node<K, V> {
	K key;
	V val;
	Node<K, V> prev;
	Node<K, V> next;

	@Override
	public String toString() {
		return key.toString() + " : " + val.toString();
	}

	public Node() {
		this.key = null;
		this.val = null;
		this.prev = null;
		this.next = null;
	}

	public Node(K key, V val) {
		this.key = key;
		this.val = val;
		this.prev = null;
		this.next = null;
	}

	public Optional<V> unwrap() {
		if (key == null) return Optional.empty();
		return Optional.of(this.val);
	}

	public static<K, V> void remove_self(Node<K, V> node) {
		Node<K, V> prev = node.prev;
		Node<K, V> next = node.next;

		prev.next = next;
		next.prev = prev;
	}

	public static<K, V> void insert_before(Node<K, V> node, Node<K, V> before) {
		Node<K, V> before_prev = before.prev;
		node.prev = before_prev;
		node.next = before;

		before_prev.next = node;
		before.prev = node;
	}
}

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

	public void resize_cache(long new_size) {
		if (new_size <= 0) {
			throw new InvalidParameterException();
		}
		this.size = new_size;
	}

	@Override
	public Optional<Value> get(Key key) {
		if (!cache.containsKey(key)) {
			return Optional.empty();
		}

		// TODO: Implement Lockless Caching
		// TODO: Implement Cache Invalidation
	
		// NOTE: I do not know if this is worth it :(
		// locking the entire method so that we want to read is 
		// a bit counter intuitive with the aspect of performance
		// so implementing a lock-free version of LRU might be worth it!
		synchronized (this) {
			Node<Key,Value> value = cache.get(key);
			Node.remove_self(value);
			Node.insert_before(value, latest);
			return value.unwrap();
		}
	}

	@Override
	public synchronized void put(Key key, Value val) {
		if (cache.containsKey(key)) {
			Node<Key,Value> old_value = cache.get(key);
			Node.remove_self(old_value);
		}

		Node<Key,Value> new_value = new Node<>(key,val);
		cache.put(key, new_value);
		Node.insert_before(new_value, latest);

		if (cache.size() > size) {
			Node<Key, Value> lru = oldest.next;
			Node.remove_self(lru);
			cache.remove(lru.key);
		}
	}
}

