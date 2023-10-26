package SecureSocketLayer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class GenPasswd {
    public static void main(String[] args) 
    {
      generatePassword();
    }

    public static void generatePassword() {
      String currentDirectory = System.getProperty("user.dir") + "/";
      String generatedPassword = null;
      String userId = null;
      String password = null;
      Scanner scanner = new Scanner(System.in);
      System.out.println("Enter User ID : ");
      userId = scanner.next();
      while(!userId.matches("^[a-z]+$")) {
          System.err.println("The ID should only contain lower-case letters.");
          System.out.println("Enter User ID again : ");
          userId = scanner.next();
      } 
      System.out.println("Enter Password : ");
      password = scanner.next();
      while(password.length() < 8) {
        System.err.println("The password should contain at least 8 characters.");
        System.out.println("Enter Password again : ");
        password = scanner.next();
      }
      boolean isValid = validateUserCredentials(userId, password);
      if(isValid) {
        StringBuilder hashedString = new StringBuilder();
        hashedString.append(userId).append(" : ");
        generatedPassword = hashMD5(hashedString, password);
      if(!generatedPassword.isEmpty()) {
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(currentDirectory + "hashpasswd.txt", true));
            bufferedWriter.write(hashedString.toString());
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            
        }
      }
      System.out.println("Would you like to enter another ID and Password (Y/N)?");
      String userInput = scanner.next();
      if(userInput.equalsIgnoreCase("Y")) {
        generatePassword();
      } else {
        System.exit(0);
      }
      } else {
        System.out.println("The ID already exists");
        System.out.println("Would you like to enter another ID and password (Y/N)?");
        String userInput = scanner.next();
        if(userInput.equalsIgnoreCase("Y")) {
          generatePassword();
        } else {
          System.exit(0);
        }
      }
      scanner.close();
    }

    private static String hashMD5(StringBuilder hashedString, String password) {
      String generatedPassword = null;
      try 
      {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(password.getBytes());
        byte[] bytes = messageDigest.digest();
        for (int i = 0; i < bytes.length; i++) {
          hashedString.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        generatedPassword = hashedString.toString();
        hashedString.append(" : ").append(LocalDateTime.now()).append("\n");
      } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
      }
      return generatedPassword;
    }

    private static boolean validateUserCredentials(String userId, String password) {
      String currentDirectory = System.getProperty("user.dir") + "/";
      File file = new File(currentDirectory + "hashpasswd.txt");
      if(file.exists()) {
        String currentLine = null;
        BufferedReader bufferedReader;
        try {
          bufferedReader = new BufferedReader(new FileReader(currentDirectory + "hashpasswd.txt"));
          while((currentLine = bufferedReader.readLine()) != null) {
            String[] userArray = currentLine.split(" : ");
            String generatedPassword = hashMD5(new StringBuilder(), password);
            if(userArray.length > 0 && userArray[0].equals(userId) && userArray[1].equals(generatedPassword)) {
              bufferedReader.close();
              return false;
            }
          }
          bufferedReader.close();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch(IOException e) {
          e.printStackTrace();
        } finally {

        }
      } else
        return true;
      return true;
    }  
}
