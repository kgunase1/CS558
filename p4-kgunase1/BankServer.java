import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BankServer {
    private static final String PUBLIC_KEY_FILE = "public_key.txt";

    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                System.err.println("Incorrect number of arguments. This server program accepts one argument");
                System.exit(0);
            }
            int portNumber = Integer.parseInt(args[0]);
            boolean serverFlag = true;
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
            publicKeyOS.writeObject(publicKey);
            publicKeyOS.close();

            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is listening on port " + portNumber);


            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected.");
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                byte[] encryptedSymmetricKey = (byte[]) inputStream.readObject();
                byte[] encryptedData = (byte[]) inputStream.readObject();
                Cipher rsaCipher = Cipher.getInstance("RSA");
                rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decryptedSymmetricKey = rsaCipher.doFinal(encryptedSymmetricKey);
                SecretKey secretKey = new SecretKeySpec(decryptedSymmetricKey, 0, decryptedSymmetricKey.length, "AES");
                Cipher aesCipher = Cipher.getInstance("AES");
                aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decryptedCredentials = aesCipher.doFinal(encryptedData);
                String userCred = new String(decryptedCredentials, StandardCharsets.UTF_8); // Assuming UTF-8 encoding, change as needed


                String[] userDataArr = userCred.split("\\|\\|");
                String passwordContent = readFile("password");
                String[] credentials = passwordContent.split("\\s+");
                System.out.println(userDataArr[0]);
                System.out.println(userDataArr[1]);
                System.out.println(credentials[0]);
                System.out.println(credentials[1]);
                if(userDataArr[0].equals(credentials[0]) && userDataArr[1].equals(credentials[1])) {
                    outputStream.writeObject(("ID and password are correct").getBytes());
                    serverFlag = false;
                    socket.close();
                    serverSocket.close();
                    break;
                } else {
                    outputStream.writeObject(("ID or password is incorrect").getBytes());
                }

                System.out.println(userCred);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFile(String inputFileName) throws FileNotFoundException {
        File output = new File(inputFileName);
        String outputFileContent = "";
        try (Scanner outputFileScanner = new Scanner(output)) {
            outputFileScanner.useDelimiter("\\Z");
            outputFileContent = outputFileScanner.next();
        } catch (NoSuchElementException e) {
            System.err.println("Incorrect File Name / No such file or directory");
            System.exit(0);
        } catch (FileNotFoundException e) {
            System.err.println("Incorrect File Name / No such file or directory");
            System.exit(0);
        }
        return outputFileContent;
    }
}
