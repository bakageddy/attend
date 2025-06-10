package org.example.util;

import java.util.concurrent.atomic.AtomicBoolean;

public class SieveNode<Key, Value>{
	Key key;
	Value value;
	SieveNode<Key, Value> next;
	SieveNode<Key, Value> prev;
	AtomicBoolean visited;

	public SieveNode(Key key, Value value) {
		this.key = key;
		this.value = value;
		this.next = null;
		this.prev = null;
		this.visited.setRelease(false);
	}

	public SieveNode() {
		this.key = null;
		this.value = null;
		this.next = null;
		this.prev = null;
		this.visited.setRelease(false);
	}

	public void setVisited() {
		this.visited.setRelease(true);
	}

	public void setUnvisited() {
		this.visited.setRelease(false);
	}

	public static<Key, Value> void insert_before(SieveNode<Key, Value> before, SieveNode<Key, Value> node) {
		node.next = before;
		node.prev = before.prev;
		before.prev.next=  node;
		before.prev = node;
	}

	public static<Key, Value> void remove(SieveNode<Key, Value> node) {
		SieveNode<Key, Value> next = node.next;
		SieveNode<Key, Value> prev = node.prev;
		next.prev = prev;
		prev.next = next;
	}
}
