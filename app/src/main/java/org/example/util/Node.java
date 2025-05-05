package org.example.util;

import java.util.Optional;

public class Node<K, V> {
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
