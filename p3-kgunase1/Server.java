

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

public class Server {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Incorrect number of arguments : This Program accepts one argument. Server Port");
            System.exit(0);
        }
        try {
            int serverPort = Integer.parseInt(args[0]);
            ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
            ServerSocket serverSocket = serverSocketFactory.createServerSocket(serverPort);
            System.out.println("SSL Server is listening on port " + serverPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress());
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                if (bytesRead > 0) {
                    String clientCredentialsData = new String(buffer, 0, bytesRead);
                    String[] credentialsArray = clientCredentialsData.split(",");
                    boolean isValid = GenPasswd.validateUserCredentials(credentialsArray[0], credentialsArray[1]);
                    if(isValid)
                        outputStream.write("Correct ID and password".getBytes());
                    else
                        outputStream.write("The ID/password is incorrect".getBytes());
                    outputStream.flush();
                    System.out.println("Received ID and password: " + clientCredentialsData);
                }
                clientSocket.close();
            }
        } catch(NumberFormatException numberFormatException) {
            System.err.println("Port number should be a number.");
            numberFormatException.printStackTrace();
            System.exit(0);
        } catch (IOException ioException) {
            System.err.println("Error occurred while establishing the connection.");
            ioException.printStackTrace();
            System.exit(0);
        }
    }
}
