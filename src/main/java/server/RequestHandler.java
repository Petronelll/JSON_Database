package server;

import com.google.gson.Gson;
import server.data.JsonDatabaseDao;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RequestHandler {

    private static final String ERROR_MESSAGE = "ERROR";
    private static final String SUCCESS_MESSAGE = "OK";
    private static final String KEY_NOT_FOUND_MESSAGE = "No such key";
    private static final Gson gson = new Gson();
    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static final Lock r = rwl.readLock();
    private static final Lock w = rwl.writeLock();
    private final JsonDatabaseDao jsonDatabaseDao;
    private final Map<String, Object> requestMap;


    public RequestHandler(JsonDatabaseDao jsonDatabaseDao, Map<String, Object> requestMap) {
        this.jsonDatabaseDao = jsonDatabaseDao;
        this.requestMap = requestMap;
    }

    @SuppressWarnings("unchecked")
    public String get() {

        r.lock();

        Map<String, Object> responseMap;
        Object key = requestMap.get("key");

        if (key == null) {
            responseMap = Collections.singletonMap(
                    "response", ERROR_MESSAGE
            );
        } else {
            Object responseValue = null;
            if (key instanceof String) {
                responseValue = jsonDatabaseDao.get((String) key);
            } else if (key instanceof List) {
                responseValue = jsonDatabaseDao.get((List<String>) key);
            }

            responseMap = new LinkedHashMap<>();
            if (responseValue == null) {
                responseMap.put("response", ERROR_MESSAGE);
                responseMap.put("reason", KEY_NOT_FOUND_MESSAGE);
            } else {
                responseMap.put("response", SUCCESS_MESSAGE);
                responseMap.put("value", responseValue);
            }
        }

        try {
            return gson.toJson(responseMap);
        } finally {
            r.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public String set() {

        w.lock();

        Map<String, Object> responseMap;

        Object key = requestMap.get("key");
        Object requestValue = requestMap.get("value");

        if (key == null || requestValue == null) {
            responseMap = Collections.singletonMap(
                    "response", ERROR_MESSAGE
            );
        } else {
            responseMap = Collections.singletonMap(
                    "response", SUCCESS_MESSAGE
            );

            if (key instanceof String) {
                jsonDatabaseDao.set((String) key, requestValue);
            } else if (key instanceof List) {
                jsonDatabaseDao.set((List<String>) key, requestValue);
            } else {
                responseMap = Collections.singletonMap(
                        "response", ERROR_MESSAGE
                );
            }
        }
        try {
            return gson.toJson(responseMap);
        } finally {
            w.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public String delete() {

        w.lock();

        Map<String, Object> responseMap;
        //jos
        Object key = requestMap.get("key");
        if (key == null) {
            responseMap = Collections.singletonMap(
                    "response", ERROR_MESSAGE
            );
        } else {
            Object responseValue = null;
            if (key instanceof String) {
                responseValue = jsonDatabaseDao.get((String) key);
                jsonDatabaseDao.delete((String) key);
            } else if (key instanceof List) {
                responseValue = jsonDatabaseDao.get((List<String>) key);
                jsonDatabaseDao.delete((List<String>) key);
            }

            if (responseValue == null) {
                responseMap = new LinkedHashMap<>();
                responseMap.put("response", ERROR_MESSAGE);
                responseMap.put("reason", KEY_NOT_FOUND_MESSAGE);
            } else {
                responseMap = Collections.singletonMap(
                        "response", SUCCESS_MESSAGE
                );
            }
        }
        try {
            return gson.toJson(responseMap);
        } finally {
            w.unlock();
        }
    }

    public String exit() {
        Map<String, String> responseMap = Collections.singletonMap(
                "response", SUCCESS_MESSAGE
        );
        return gson.toJson(responseMap);
    }

    public String badRequest() {
        Map<String, String> responseMap = Collections.singletonMap(
                "response", ERROR_MESSAGE
        );
        return gson.toJson(responseMap);
    }

}
