package server.data;

import java.util.List;

public interface JsonDatabaseDao {
    Object get(String key);
    Object get(List<String> complexKey);
    void set(String key, Object value);
    void set(List<String> complexKey, Object value);
    void delete(String key);
    void delete(List<String> complexKey);
    void updateDatabaseFile();
}
