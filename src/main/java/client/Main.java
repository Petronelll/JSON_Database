package client;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static client.Utils.createJsonRequest;
import static client.Utils.readJsonRequestFromFile;

public class Main {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 23456;
    @Parameter(names = {"-t", "--type"}, description = "request type")
    String type;
    @Parameter(names = {"-k", "--key"}, description = "json key")
    String key;
    @Parameter(names = {"-v", "--value"}, description = "json value used in a 'SET' request")
    String value;
    @Parameter(names = {"-in", "--input"}, description = "read request from input file located in /src/client/data")
    String inputFileName;

    public static void main(String[] args) {

        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(args);
        main.run();
    }

    public void run() {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client started!");

            String request;
            if (inputFileName == null) {
                request = createJsonRequest(type, key, value);
            } else {
                request = readJsonRequestFromFile(inputFileName);
            }

            outputStream.writeUTF(request);
            System.out.printf("Sent: %s\n", request);

            String response = inputStream.readUTF();
            System.out.printf("Received: %s\n", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
