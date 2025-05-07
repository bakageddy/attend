package org.example.util;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class MRUTest {
	@Test
	void mruCanConstruct() {
		MRU<Integer, String> cache = new MRU<>(10);

		assert(cache.cache_capacity() == 10);
		assert(cache.cache_size() == 0);
	}

	@Test
	void mruCanPut() {
		MRU<Integer, String> cache = new MRU<>(10);
		assert(cache.cache_capacity() == 10);
		assert(cache.cache_size() == 0);

		cache.put(10, "Dinesh");
		assert(cache.cache_size() == 1);
	}

	@Test
	void mruCanGet() {
		MRU<Integer, String> cache = new MRU<>(10);
		assert(cache.cache_capacity() == 10);
		assert(cache.cache_size() == 0);

		cache.put(10, "Dinesh");
		Optional<String> present = cache.get(10);

		assert(present.isPresent());
		assert(present.get().equals("Dinesh"));
	}

	@Test
	void mruCanEvict() {
		MRU<Integer, String> cache = new MRU<>(3);
		cache.put(1, "Dinesh");
		cache.put(2, "Lali");
		cache.put(3, "Govind");

		Optional<String> present = cache.get(3);
		cache.put(4, "Sathiya");

		Optional<String> absent = cache.get(3);
		assert(cache.cache_size() == 3);
		assert(present.isPresent());
		assert(present.get().equals("Govind"));
	}
}
