package org.example.util;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Sieve<Key, Value> implements Cache<Key, Value> {
	ConcurrentHashMap<Key, SieveNode<Key, Value>> map;
	SieveNode<Key, Value> head;
	SieveNode<Key, Value> tail;
	private int capacity;

	SieveNode<Key, Value> sieve;

	public Sieve(int capacity) {
		this.head = new SieveNode<>(null, null);
		this.tail = new SieveNode<>(null, null);
		this.sieve = this.head;
		this.map = new ConcurrentHashMap<>(capacity);
		this.capacity = capacity;

		this.head.next = tail;
		this.tail.prev = head;
	}

	@Override
	public Optional<Value> get(Key key) {
		SieveNode<Key, Value> val = map.get(key);
		if (val == null) {
			return Optional.empty();
		}
		val.setVisited();
		return Optional.of(val.value);
	}

	@Override
	public synchronized void put(Key key, Value val) {
		int size = map.size();
		if (this.map.containsKey(key) && size <= capacity) {
			SieveNode<Key, Value> node = this.map.get(key);
			node.value = val;
			node.setUnvisited();
			return;
		} else if (size == capacity) {
			this.evict();
			SieveNode<Key, Value> node = new SieveNode<>(key, val);
			SieveNode.insert_before(this.tail, node);
		}
	}

	public SieveNode<Key, Value> evict() {
		if (this.sieve == this.head) {
			this.sieve = this.head.next;
		}

		// This is Sequential: O(n)
		while (this.sieve.visited.getAcquire()) {
			this.sieve.setUnvisited();
			if (this.sieve.next == this.tail) {
				this.sieve = this.head.next;
			} else {
				this.sieve = this.sieve.next;
			}
		}

		SieveNode<Key, Value> return_value = this.sieve;
		SieveNode<Key, Value> next = this.sieve.next;

		SieveNode.remove(this.sieve);
		this.sieve = next;
		this.map.remove(return_value.key);
		return return_value;
	} 
}
