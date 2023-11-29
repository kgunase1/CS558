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

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                byte[] serverResponseByte = (byte[]) inputStream.readObject();
                String serverResponse = new String(serverResponseByte, StandardCharsets.UTF_8); // Assuming UTF-8 encoding, change as needed

                if(serverResponse.equalsIgnoreCase("ID or password is incorrect")) {
                    System.out.println("Re-enter user Id and password");
                } else {
                    System.out.println(serverResponse);
                    displayATMMenu(outputStream, inputStream, socket);
                    credentialsFlag = false;
                    // outputStream.close();
                    // inputStream.close();
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

    private static void displayATMMenu(ObjectOutputStream outputStream, ObjectInputStream inputStream, Socket socket) {
        boolean exitFlag = true;
        while(exitFlag) {
            try {
                System.out.println("Please select one of the following actions (enter 1, 2, or 3):");
                System.out.println("1. Transfer money");
                System.out.println("2. Check account balance");
                System.out.println("3. Exit");
                Scanner scanner = new Scanner(System.in);
                String optionSelected = scanner.nextLine();
                switch(optionSelected) {
                    case "1":
                        transferMoney(outputStream, inputStream);
                        break;
                    case "2":
                        checkAccountBalance(outputStream, inputStream);
                        break;
                    case "3":
                        exitFlag = false;
                        socket.close();
                        // scanner.close();
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // scanner.close();
            }
        }
        // scanner.close();
    }

    private static void transferMoney(ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please select an account (enter 1 or 2):");
        System.out.println("1. Savings");
        System.out.println("2. Checking");
        String optionSelected = scanner.next();
        switch(optionSelected) {
            case "1":
                transferMoney(outputStream, inputStream, "savings");
                break;
            case "2":
                transferMoney(outputStream, inputStream, "checking");
                break;
            default:
                System.out.println("Incorrect Option selected.");
                scanner.close();
                transferMoney(outputStream, inputStream);
                break;
        }
    }

    private static void transferMoney(ObjectOutputStream outputStream, ObjectInputStream inputStream, String accountType) {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Enter the Recipients ID : ");
            String recipientsId = scanner.next();
            System.out.println("Enter the amount to be transferred : ");
            int amount = scanner.nextInt();
            outputStream.writeObject((accountType + "||" + recipientsId + "||" + amount).getBytes());
            byte[] serverResponseByte = (byte[]) inputStream.readObject();
            String serverResponse = new String(serverResponseByte, StandardCharsets.UTF_8);
            System.out.println(serverResponse);
        } catch(IOException e) {

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
        

    }

    private static void checkAccountBalance(ObjectOutputStream outputStream, ObjectInputStream inputStream) {

    }
}
