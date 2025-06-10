package org.example.util;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FIFO<Key, Value> implements Cache<Key, Value> {
	ConcurrentHashMap<Key, Value> map;
	ConcurrentLinkedQueue<Key> queue;
	int capacity;

	public FIFO(int capacity) {
		this.capacity = capacity;
		this.map = new ConcurrentHashMap<>(capacity);
		this.queue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public Optional<Value> get(Key key) {
		Value value = this.map.get(key);
		return Optional.ofNullable(value);
	}

	@Override
	public void put(Key key, Value val) {
		if (this.map.size() == capacity) {
			Key head = this.queue.poll();
			this.map.remove(head);
		}

		this.map.put(key, val);
	}
}
