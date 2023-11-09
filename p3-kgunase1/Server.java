

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Incorrect number of arguments : This Program accepts one argument. Server Port");
            System.exit(0);
        }
        String keystorePath = "keystore.jks";
        char[] keystorePass = "12345678".toCharArray();
        try {
            int serverPort = Integer.parseInt(args[0]);
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(keystorePath), keystorePass);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keystorePass);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(serverPort);

            System.out.println("SSL Server is listening on port " + serverPort);
            SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
            while (true) {
                System.out.println("Client connected from " + clientSocket.getInetAddress());
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                if (bytesRead > 0) {
                    String clientCredentialsData = new String(buffer, 0, bytesRead);
                    String[] credentialsArray = clientCredentialsData.split(",");
                    boolean isValid = GenPasswd.validateUserCredentials(credentialsArray[0], credentialsArray[1]);
                    if(!isValid) {
                        outputStream.write("Correct ID and password".getBytes());
                        outputStream.close();
                        break;
                    }
                    else
                        outputStream.write("The ID/password is incorrect".getBytes());
                    outputStream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
