import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
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
        // BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = null;

        KeyGenerator aESKeyGenerator = KeyGenerator.getInstance("AES");
        aESKeyGenerator.init(256);
        SecretKey symmetricKey = aESKeyGenerator.generateKey();

        // File publicKeyFile = new File("public.key");
        
        // byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        // KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);

        FileInputStream fis = new FileInputStream("public.key");
        byte[] publicKeyBytes = fis.readAllBytes();
        fis.close();

        // Create X509EncodedKeySpec from the read bytes
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey  bankPublicKey= keyFactory.generatePublic(keySpec);

         System.out.println(publicKeyBytes.toString());

        Cipher rSACipher = Cipher.getInstance("RSA");
        rSACipher.init(Cipher.ENCRYPT_MODE, bankPublicKey);
        byte[] encryptedSymmetricKey = rSACipher.doFinal(symmetricKey.getEncoded());


        while(credentialsFlag) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            System.out.println("Enter User Id : ");
            scanner = new Scanner(System.in);
            String userId = scanner.next();
            System.out.println("Enter password : ");
            String password = scanner.next();
            Cipher aESCipher = Cipher.getInstance("AES");
            aESCipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
            byte[] userCredentials = aESCipher.doFinal((userId + "||" + password).getBytes());

            outputStream.write(encryptedSymmetricKey.length);
            outputStream.write(encryptedSymmetricKey);
            outputStream.flush();

            System.out.println("key sent");

            outputStream.write(userCredentials.length);
            outputStream.write(userCredentials);
            outputStream.flush();

            System.out.println("user cred sent");

            byte[] buffer = new byte[1024];
            int bytesRead = inputStream.read(buffer);
            if (bytesRead > 0) {
                String serverResponse = new String(buffer, 0, bytesRead);
                if(serverResponse.equalsIgnoreCase("“ID or password is incorrect")) {
                    System.out.println("Re-enter user Id and password");
                } else {
                    System.out.println(serverResponse);
                    credentialsFlag = false;
                    outputStream.close();
                    inputStream.close();
                    break;
                }
            }
        }
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
