import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BankServer {
    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                System.err.println("Incorrect number of arguments. This server program accepts one argument");
            }
            int portNumber = Integer.parseInt(args[0]);
            boolean serverFlag = true;
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            try (FileOutputStream fos = new FileOutputStream("public.key")) {
                fos.write(publicKey.getEncoded());
                fos.close();
            }

            // int portNumber = 8081;
            
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is listening on port " + portNumber);

            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            // System.out.println("Public Key: " + publicKey.getEncoded());
            // System.out.println("Private Key: " + privateKey.getEncoded());

            while (serverFlag) {
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();
                System.out.println("going to read");
                int symmetricKeyLength = inputStream.read();
                byte[] encryptedSymmetricKey = new byte[symmetricKeyLength];
                inputStream.read(encryptedSymmetricKey, 0, symmetricKeyLength);
                System.out.println(encryptedSymmetricKey.toString());
                System.out.println("key received");

                int dataLength = inputStream.read();
                byte[] userCredentials = new byte[dataLength];
                inputStream.read(userCredentials, 0, dataLength);
                System.out.println(userCredentials.toString());

                Cipher rSACipher = Cipher.getInstance("RSA");
                rSACipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] symmetricKey = rSACipher.doFinal(encryptedSymmetricKey);
                SecretKey receivedSymmetricKey = new SecretKeySpec(symmetricKey, 0, symmetricKey.length, "AES");

                Cipher aESCipher = Cipher.getInstance("AES");
                aESCipher.init(Cipher.DECRYPT_MODE, receivedSymmetricKey);
                byte[] userData = aESCipher.doFinal(userCredentials);
                String userCred = userData.toString();
                String[] userDataArr = userCred.split("||");

                String passwordContent = readFile("password");
                String[] credentials = passwordContent.split("\\s+");
                if(userDataArr[0].equals(credentials[0]) && userDataArr[1].equals(credentials[1])) {
                    outputStream.write(("ID and password are correct").getBytes());
                    serverFlag = false;
                    socket.close();
                    serverSocket.close();
                    break;
                } else {
                    outputStream.write(("ID or password is incorrect").getBytes());
                }

                System.out.println(userCred);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
