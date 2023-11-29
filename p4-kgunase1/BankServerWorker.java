import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BankServerWorker implements Runnable {
    private Socket socket;
    private PrivateKey privateKey;
    private Map<String, String> credentialsMap;

    public BankServerWorker(Socket socketIn, PrivateKey privateKeyIn, Map<String, String> credentialsMapIn) {
        socket = socketIn;
        privateKey = privateKeyIn;
        credentialsMap = credentialsMapIn;
    } 

    public void run() {
        try {
            while(true) {
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
                String passwordContent;
                passwordContent = readFile("password");
                String[] credentials = passwordContent.split("\\s+");
                System.out.println(userDataArr[0]);
                System.out.println(userDataArr[1]);
                System.out.println(credentials[0]);
                System.out.println(credentials[1]);
                if(credentialsMap.containsKey(userDataArr[0]) && credentialsMap.get(userDataArr[0]).equals(credentials[1])) {
                    outputStream.writeObject(("ID and password are correct").getBytes());
                } else {
                    outputStream.writeObject(("ID or password is incorrect").getBytes());
                }
                System.out.println(userCred);

                byte[] clientRequestByte = (byte[]) inputStream.readObject();
                String clientRequest = new String(clientRequestByte, StandardCharsets.UTF_8);
                String[] clientData = clientRequest.split("\\|\\|");
                String accountType = clientData[0];
                String recipientsId = clientData[1];
                int amount = Integer.parseInt(clientData[2]);

                String accountBalance = readFile("balance");
            }
        } catch(Exception e) {
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
