import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class ATMClient {
    private static final String PUBLIC_KEY_FILE = "public_key.txt";

    public static void main(String[] args) throws InvalidKeySpecException {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments : This Program accepts two argument. Domain name and Server Port");
            System.exit(0);
        }
        boolean credentialsFlag = true;
        try {
            Scanner scanner = null;
            String domainName = args[0];
            int portNumber = Integer.parseInt(args[1]);
            Socket socket = new Socket(domainName, portNumber);
            System.out.println("Connected to " + domainName + " on port " + portNumber);

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey symmetricKey = keyGen.generateKey();

            ObjectInputStream publicKeyIS = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            PublicKey serverPublicKey = (PublicKey) publicKeyIS.readObject();
            publicKeyIS.close();

            Cipher rsaCipher = Cipher.getInstance("RSA");
            rsaCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] encryptedSymmetricKey = rsaCipher.doFinal(symmetricKey.getEncoded());


            while(credentialsFlag) {
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                System.out.println("Enter User Id : ");
                scanner = new Scanner(System.in);
                String userId = scanner.next();
                System.out.println("Enter password : ");
                String password = scanner.next();
                Cipher aesCipher = Cipher.getInstance("AES");
                aesCipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
                byte[] userCredentials = aesCipher.doFinal((userId +"||"+ password).getBytes());
    
                outputStream.writeObject(encryptedSymmetricKey);
                System.out.println("key sent");
                outputStream.writeObject(userCredentials);
                System.out.println("user cred sent");
                outputStream.flush();

                byte[] serverResponseByte = (byte[]) inputStream.readObject();
                String serverResponse = new String(serverResponseByte, StandardCharsets.UTF_8); // Assuming UTF-8 encoding, change as needed

                if(serverResponse.equalsIgnoreCase("ID or password is incorrect")) {
                    System.out.println("Re-enter user Id and password");
                } else {
                    System.out.println(serverResponse);
                    credentialsFlag = false;
                    outputStream.close();
                    inputStream.close();
                    break;
                }
            }
            socket.close();
            // scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
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
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            
        }
    }
}
