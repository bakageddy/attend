package org.example.util;

import java.util.Optional;

public interface Cache<Key, Value> {
	public Optional<Value> get(Key key);
	public void put(Key key, Value val);
}
