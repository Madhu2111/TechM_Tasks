package com.example;

import java.util.Map;
import java.util.HashMap;

public class JUnit_Task10_Component1{
	private final Map<Integer, String> dataStore = new HashMap<>();

    public void save(int id, String value) {
        dataStore.put(id, value);
    }

    public String findById(int id) {
        return dataStore.get(id);
    }

}
