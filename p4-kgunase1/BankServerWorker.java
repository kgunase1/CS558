import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BankServerWorker implements Runnable {
    private Socket socket;
    private PrivateKey privateKey;
    private Map<String, String> credentialsMap;
    private Utils utils;

    public BankServerWorker(Socket socketIn, PrivateKey privateKeyIn, Map<String, String> credentialsMapIn) {
        socket = socketIn;
        privateKey = privateKeyIn;
        credentialsMap = credentialsMapIn;
        utils = new Utils();
    } 

    public void run() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            String userId = null;
            while(true) {
                byte[] operationByte = (byte[]) inputStream.readObject();
                String operation = new String(operationByte, StandardCharsets.UTF_8);
                switch(operation) {
                    case "credentials":
                        userId = validateCredentials(inputStream, outputStream);
                        break;
                    case "transferMoney":
                        transferMoney(inputStream, outputStream, userId);
                        break;
                    case "checkBalance":
                        checkBalance(inputStream, outputStream, userId);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void checkBalance(ObjectInputStream inputStream, ObjectOutputStream outputStream, String userId) {
        try {
            Map<String, Map<String, Integer>> balanceMap = utils.readBalanceFile("balance");
            if(balanceMap.containsKey(userId)) {
                outputStream.writeObject(String.valueOf((balanceMap.get(userId).get("savings"))).getBytes());
                outputStream.writeObject(String.valueOf((balanceMap.get(userId).get("checking"))).getBytes());
            }
        } catch(IOException ioException) {
            ioException.printStackTrace();
        } finally {

        }
    }

    private void transferMoney(ObjectInputStream inputStream, ObjectOutputStream outputStream, String userId) {
        try {
            byte[] clientRequestByte = (byte[]) inputStream.readObject();
            String clientRequest = new String(clientRequestByte, StandardCharsets.UTF_8);
            String[] clientData = clientRequest.split("\\|\\|");
            String accountType = clientData[0];
            String recipientsId = clientData[1];
            int transferAmount = Integer.parseInt(clientData[2]);
            System.out.println(transferAmount);

            Map<String, Map<String, Integer>> balanceMap = utils.readBalanceFile("balance");
            if (balanceMap.containsKey(recipientsId)) {
                if (balanceMap.containsKey(userId)) {
                    int userBalance = balanceMap.get(userId).get(accountType);
                    if (userBalance >= transferAmount) {
                        balanceMap.get(recipientsId).put(accountType,
                                balanceMap.get(recipientsId).get(accountType) + transferAmount);
                        balanceMap.get(userId).put(accountType,
                                balanceMap.get(userId).get(accountType) - transferAmount);
                        writeIntoFile(balanceMap);
                        outputStream.writeObject(("Your transaction is successful").getBytes());
                    } else {
                        outputStream.writeObject(("Your account does not have enough funds").getBytes());
                    }
                } else {

                }
            } else {
                outputStream.writeObject(("The recipient’s ID does not exist.").getBytes());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }
    }

    private String validateCredentials(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        try {
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
            String userId = userDataArr[0];
            String password = userDataArr[1];
            if(credentialsMap.containsKey(userId) && credentialsMap.get(userId).equals(password)) {
                outputStream.writeObject(("ID and password are correct").getBytes());
            } else {
                outputStream.writeObject(("ID or password is incorrect").getBytes());
            }
            System.out.println(userCred);
            return userId;
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
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
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    private synchronized void writeIntoFile(Map<String, Map<String, Integer>> balanceMap) {
        StringBuilder balance = new StringBuilder();
        balanceMap.entrySet().stream().forEach(userId -> {
            balance.append(userId.getKey()).append(" ");
            userId.getValue().entrySet().stream().forEach(account -> {
                balance.append(account.getValue()).append(" ");
            });
            balance.append("\n");
        });
        utils.writeIntoFile("balance", balance.toString());
    }

    

}
