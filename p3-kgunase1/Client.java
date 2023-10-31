

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments : This Program accepts two argument. Domain name and Server Port");
            System.exit(0);
        }
        String serverDomain = args[0];
        try {
            int serverPort = Integer.parseInt(args[1]);
            SocketFactory socketFactory = SSLSocketFactory.getDefault();
            Socket socket = socketFactory.createSocket(serverDomain, serverPort);
            System.out.println("Connected to " + serverDomain + " on port " + serverPort);
            OutputStream outputStream = socket.getOutputStream();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter User ID:");
            String userId = scanner.nextLine();
            System.out.println("Enter Password:");
            String password = scanner.nextLine();
            String credentials = userId + "," + password;
            outputStream.write(credentials.getBytes());
            outputStream.flush();
            scanner.close();
            socket.close();
        } catch(NumberFormatException numberFormatException) {
            System.err.println("Port number should be a number.");
            numberFormatException.printStackTrace();
            System.exit(0);
        } catch (IOException ioException) {
            System.err.println("Error occurred while establishing the connection.");
            ioException.printStackTrace();
            System.exit(0);
        } finally {

        }
    }
}
