

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.util.Scanner;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments : This Program accepts two argument. Domain name and Server Port");
            System.exit(0);
        }
        String serverDomain = args[0];
        try {
            int serverPort = Integer.parseInt(args[1]);
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream("keystore.jks"), "12345678".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket(serverDomain, serverPort);

            System.out.println("Connected to " + serverDomain + " on port " + serverPort);
            sendInputData(socket);
            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
            
    }

    private static void sendInputData(SSLSocket socket) {
         try{
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
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
                if(serverMessage.contains("Correct")) {
                    System.out.println(serverMessage);
                    System.exit(0);
                } else {
                    System.out.println(serverMessage);
                    sendInputData(socket);
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
