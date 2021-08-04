package server;

import com.google.gson.Gson;
import server.commands.*;
import server.data.JsonDatabaseDao;
import server.data.JsonDatabaseDaoImpl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    static final ReentrantLock sendLock = new ReentrantLock();
    static final int PORT = 23456;
    static String DATABASE_PATH = System.getProperty("user.dir") + "/src/main/java/server/data/db.json";
    static JsonDatabaseDao jsonDatabaseDao;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            jsonDatabaseDao = new JsonDatabaseDaoImpl(DATABASE_PATH);
            run(serverSocket, jsonDatabaseDao);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void run(ServerSocket serverSocket, JsonDatabaseDao jsonDatabaseDao) {

        int poolSize = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        Gson gson = new Gson();
        Command command = null;

        while (!(command instanceof ExitCommand)) {

            try {
                Socket socket = serverSocket.accept();
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                String request = inputStream.readUTF();
                Map<String, Object> requestMap = gson.fromJson(request, Map.class);

                RequestHandler requestHandler = new RequestHandler(jsonDatabaseDao, requestMap);
                command = createCommand(requestMap, requestHandler);

                Command finalCommand = command;
                executor.submit(() -> executeRequest(socket, finalCommand));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }

    public static void executeRequest(Socket socket, Command command) {

        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            RequestController requestController = new RequestController();
            requestController.setCommand(command);
            requestController.executeCommand();

            if (command instanceof SetCommand || command instanceof DeleteCommand) {
                jsonDatabaseDao.updateDatabaseFile();
            }
            String response = requestController.getResponse();
            sendResponse(response, outputStream);

            outputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendResponse(String response, DataOutputStream outputStream) {

        try {
            sendLock.lock();
            outputStream.writeUTF(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sendLock.unlock();
        }
    }

    public static Command createCommand(Map<String, Object> requestMap, RequestHandler requestHandler) {

        String type = String.valueOf(requestMap.getOrDefault("type", "bad_request")).toUpperCase();
        RequestType requestType = RequestType.valueOf(type);
        Command command;

        switch (requestType) {
            case GET:
                command = new GetCommand(requestHandler);
                break;
            case SET:
                command = new SetCommand(requestHandler);
                break;
            case DELETE:
                command = new DeleteCommand(requestHandler);
                break;
            case EXIT:
                command = new ExitCommand(requestHandler);
                break;
            case BAD_REQUEST:
            default:
                command = new BadRequestCommand(requestHandler);
        }
        return command;
    }

    enum RequestType {
        SET,
        GET,
        DELETE,
        EXIT,
        BAD_REQUEST
    }
}
