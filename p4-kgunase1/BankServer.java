import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class BankServer {
    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                System.err.println("Incorrect number of arguments. This server program accepts one argument");
            }
            int portNumber = Integer.parseInt(args[0]);
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is listening on port " + portNumber);

            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            String publicKeyStr = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
            writeFile("publicKey.txt", publicKeyStr);
            System.out.println("Public Key: " + publicKey.getEncoded());
            System.out.println("Private Key: " + privateKey.getEncoded());

            while (true) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Client: " + message);
                    out.println("Server received: " + message);
                }
            in.close();
            out.close();
            socket.close();
            serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }
    } 
    
    private static void writeFile(String outputFileName, String content) throws IOException {
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outputFileName, false));
            bufferedWriter.write(content);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            System.err.println("Incorrect File Name / No such file or directory");
            System.exit(0);
        }
    }
}
