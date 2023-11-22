import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class ATMClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments : This Program accepts two argument. Domain name and Server Port");
            System.exit(0);
        }
        boolean credentialsFlag = true;
    try {
        String domainName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        Socket socket = new Socket(domainName, portNumber);
        System.out.println("Connected to " + domainName + " on port " + portNumber);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = null;
        String bankPublicKey = readFile("publicKey.txt");
        byte[] bankPubKey = java.util.Base64.getDecoder().decode(bankPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bankPubKey);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey symmetricKey = keyGen.generateKey();
        while(credentialsFlag) {
            System.out.println("Enter User Id : ");
            scanner = new Scanner(System.in);
            String userId = scanner.next();
            System.out.println("Enter password : ");
            String password = scanner.next();
            out.write(userId + " " + password);
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                if(serverResponse.equalsIgnoreCase("“ID or password is incorrect")) {
                    System.out.println("Re-enter user Id and password");
                } else {
                    System.out.println(serverResponse);
                    credentialsFlag = false;
                    break;
                }
            }
        }
        in.close();
        out.close();
        socket.close();
        scanner.close();
    } catch (IOException e) {
        e.printStackTrace();
    } catch(NumberFormatException numberFormatException) {
        numberFormatException.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
    } catch (InvalidKeySpecException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } finally {
        
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
