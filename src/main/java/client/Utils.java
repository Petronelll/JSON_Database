package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class Utils {

    public static String createJsonRequest(String type, String key, String value) {

        Map<String, String> requestMap = new LinkedHashMap<>();
        if (type != null) requestMap.put("type", type);
        if (key != null) requestMap.put("key", key);
        if (value != null) requestMap.put("value", value);
        Gson gson = new Gson();
        return gson.toJson(requestMap);
    }

    public static String readJsonRequestFromFile(String inputFileName) {

        String path = System.getProperty("user.dir") + "/src/main/java/client/data/%s";
        String filePath = String.format(path, inputFileName);
        Path fileName = Path.of(filePath);

        String request = null;
        try {
            request = Files.readString(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return request;
    }
}
