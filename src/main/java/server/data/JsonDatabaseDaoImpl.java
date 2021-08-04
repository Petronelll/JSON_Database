package server.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonDatabaseDaoImpl implements JsonDatabaseDao {

    String databasePath;
    private Map<String, Object> map;

    @SuppressWarnings("unchecked")
    public JsonDatabaseDaoImpl(String databasePath) {
        this.databasePath = databasePath;
        Gson gson = new Gson();

        try (Reader reader = Files.newBufferedReader(Path.of(databasePath))) {
            map = gson.fromJson(reader, Map.class);
        } catch (IOException e) {
            map = new HashMap<>();
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void updateDatabaseFile() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try (FileWriter fileWriter = new FileWriter(databasePath)){
            gson.toJson(map, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(List<String> complexKey) {

        if (complexKey == null) {
            return null;
        }

        Object object = map.get(complexKey.get(0));
        for (var key : complexKey.subList( 1, complexKey.size())) {
            if (!(object instanceof Map)) {
                return null;
            }
            Map<String, Object> helperMap = (Map<String, Object>) object;
            object = helperMap.get(key);
        }
        return object;
    }

    @Override
    public void set(String key, Object value) {
        map.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void set(List<String> complexKey, Object value) {

        if (complexKey == null) {
            return;
        }

        Map<String, Object> helperMap = map;
        Object object = helperMap.get(complexKey.get(0));
        int i;
        for (i = 1; i < complexKey.size(); i++) {

            if (!(object instanceof Map)) {
                Map<String, Object> createdMap = new LinkedHashMap<>();
                createdMap.put(complexKey.get(complexKey.size() - 1), value);

                for (int j = complexKey.size() - 2; j >= i; j--) {
                    Map<String, Object> auxMap = new LinkedHashMap<>();
                    auxMap.put(complexKey.get(j), createdMap);
                    createdMap = auxMap;
                }
                helperMap.put(complexKey.get(i), createdMap);
                return;
            }
            helperMap = (Map<String, Object>) object;
            object = helperMap.get(complexKey.get(i));
        }

        helperMap.put(complexKey.get(i - 1), value);
    }

    @Override
    public void delete(String key) {
        map.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void delete(List<String> complexKey) {

        if (complexKey == null) {
            return;
        }

        Map<String, Object> helperMap = map;
        Object object = helperMap.get(complexKey.get(0));
        for (var key : complexKey.subList( 1, complexKey.size())) {
            if (!(object instanceof Map)) {
                return;
            }
            helperMap = (Map<String, Object>) object;
            object = helperMap.get(key);
        }
        helperMap.remove(complexKey.get(complexKey.size() - 1));
    }

}
