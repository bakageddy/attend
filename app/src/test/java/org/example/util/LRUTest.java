package org.example.util;

import org.junit.jupiter.api.Test;

// import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class LRUTest {
	@Test
	void lruCanConstruct() {
		LRU<Integer, String> cache = new LRU<>(10);

		assert(cache.cache_capacity() == 10);
		assert(cache.cache_size() == 0);
	}

	@Test
	void lruCanPut() {
		LRU<Integer, String> cache = new LRU<>(10);
		assert(cache.cache_capacity() == 10);
		assert(cache.cache_size() == 0);

		cache.put(10, "Dinesh");
		assert(cache.cache_size() == 1);
	}

	@Test
	void lruCanGet() {
		LRU<Integer, String> cache = new LRU<>(10);
		assert(cache.cache_capacity() == 10);
		assert(cache.cache_size() == 0);

		Optional<String> absent = cache.get(1);
		assert(absent.isEmpty());

		cache.put(1, "Dinesh");
		Optional<String> present = cache.get(1);
		assert(present.isPresent());
		assert(present.get().equals("Dinesh"));
	}

	@Test
	void lruCanEvict() {
		// Can make this larger, but this is fine
		LRU<Integer, String> cache = new LRU<>(3);
		cache.put(1, "Dinesh");
		cache.put(2, "Lali");
		cache.put(3, "Sathiya");
		cache.put(4, "Govind"); // This will evict the cache with key => 1
		
		Optional<String> absent = cache.get(1);
		assert(absent.isEmpty());

		Optional<String> present = cache.get(2); // This will be the newest key
		assert(present.isPresent());
		assert(present.get().equals("Lali"));

		cache.put(5, "Vasantha");

		Optional<String> absent_2 = cache.get(3);
		assert(absent_2.isEmpty());
	}

	@Test
	void lruCanPurge() {
		LRU<Integer, String> cache = new LRU<>(3);
		cache.put(1, "Dinesh");
		cache.put(2, "Something");
		cache.put(3, "Something else");

		cache.purge(); // Nothing happens, need to rewrite Node to use a set TTL
		assert(cache.cache_size() == 3);
		assert(cache.cache_capacity() == 3);
	}
}
