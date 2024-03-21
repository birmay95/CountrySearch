package com.mishail.country_search.cache;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CacheService {

    private final HashMap<String, Object> cache = new HashMap<>();

    public void put(final String key, final Object value) {
        cache.put(key, value);
    }

    public Object get(final String key) {
        return cache.get(key);
    }

    public boolean containsKey(final String key) {
        return cache.containsKey(key);
    }

    public void remove(final String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}

