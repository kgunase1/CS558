

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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
            Socket socket;
            socket = socketFactory.createSocket(serverDomain, serverPort);
            System.out.println("Connected to " + serverDomain + " on port " + serverPort);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            sendInputData( outputStream, inputStream);
            socket.close();
        }  catch(NumberFormatException numberFormatException) {
            System.err.println("Port number should be a number.");
            numberFormatException.printStackTrace();
            System.exit(0);
        }  catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
            
    }

    private static void sendInputData(OutputStream outputStream, InputStream inputStream) {
         try{
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter User ID:");
            String userId = scanner.nextLine();
            System.out.println("Enter Password:");
            String password = scanner.nextLine();
            String credentials = userId + "," + password;
            outputStream.write(credentials.getBytes());
            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            if (bytesRead > 0) {
                String serverMessage = new String(buffer, 0, bytesRead);
                if(serverMessage.equals("Correct ID and password")) {
                    System.out.println("Correct ID and password");
                    System.exit(0);
                } else {
                    sendInputData(outputStream, inputStream);
                }
            }
            outputStream.flush();
            scanner.close();
        } catch (IOException ioException) {
            System.err.println("Error occurred while establishing the connection.");
            ioException.printStackTrace();
            System.exit(0);
        } finally {

        }
    }
}
