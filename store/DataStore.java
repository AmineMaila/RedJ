package store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private final Map<String, Entry> STORE = new ConcurrentHashMap<>();

}
