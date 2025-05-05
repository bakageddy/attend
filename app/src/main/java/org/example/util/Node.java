package org.example.util;

import java.time.Instant;
import java.util.Optional;

public class Node<K, V> {
	private static final long DEFAULT_TTL = 5 * 60; // 5 minutes
	
	K key;
	V val;
	Node<K, V> prev;
	Node<K, V> next;
	private long expiry_time;

	@Override
	public String toString() {
		return key.toString() + " : " + val.toString();
	}

	public Node() {
		this.key = null;
		this.val = null;
		this.prev = null;
		this.next = null;
		this.expiry_time = Instant.now().getEpochSecond() + DEFAULT_TTL;
	}

	public Node(K key, V val) {
		this.key = key;
		this.val = val;
		this.prev = null;
		this.next = null;
		this.expiry_time = Instant.now().getEpochSecond() + DEFAULT_TTL;
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

	public long get_expiry_time() {
		return this.expiry_time;
	}

	public boolean is_stale() {
		return this.expiry_time < Instant.now().getEpochSecond();
	}
}
