package CS558.RowTranspositionCipher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class RowTranspositionCipher  {
    public static void main(String[] args) throws IOException {
        // Scanner scanner = new Scanner(System.in);
        // String input = scanner.next();
        // System.out.println(input);
        // String[] words = input.split("\\s+");
        // for(String word : words)
        //     System.out.println(word);
        int keyLength = Integer.parseInt(args[0]);
        String key = args[1];
        String inputFileName = args[2];
        String outputFileName = args[3];
        String operation = args[4];
        if(keyLength != key.length()) {
            System.err.println("keylength must match the length of the key");
            System.exit(1);
        }
        Set<Integer> keySet = new HashSet<>();
        for(char c : key.toCharArray()) {
            if(c < 49 || c > 57) {
                System.err.println("key should contain characters only from 1 to 9");
                System.exit(1);
            }
            if(!keySet.add(Integer.parseInt(String.valueOf(c)))) {
                System.err.println("key must include all digits from 1 to 9 with each digit occuring exactly once");
                System.exit(1);
            }
        }
        if(operation.equals("enc"))
            doEncryption(keyLength, key, inputFileName, outputFileName);
        else if(operation.equals("dec"))
            doDecryption(keyLength, key, inputFileName, outputFileName);
    }

    private static void doEncryption(int keyLength, String key, String inputFileName, String outputFileName) throws IOException {
        File file = new File("D:/MSCS/Fall 2023/Comp Sec/Projects/Assignment 1/CS558/" + inputFileName);
        Scanner fileScanner = new Scanner(file);
        fileScanner.useDelimiter("\\Z");
        String inputFile = fileScanner.next();
        double contentLength = (double) inputFile.length()/keyLength;
        int rowLength = (int) Math.ceil(contentLength);
        char[][] cipherMatrix = new char[rowLength][keyLength];
        StringBuilder encryptedText = new StringBuilder();
        Path outputPath = Path.of("D:/MSCS/Fall 2023/Comp Sec/Projects/Assignment 1/CS558/" + outputFileName);
        int iterator = 0;
        System.out.println(rowLength);
        for(int i = 0; i < rowLength; i++) {
            for(int j = 0; j < keyLength; j++) {
                if(iterator >= inputFile.length())
                    cipherMatrix[i][j] = 'z';
                else {
                    if((inputFile.charAt(iterator) > 96 && inputFile.charAt(iterator) < 123) || 
                    inputFile.charAt(iterator) > 47  && inputFile.charAt(iterator) < 58) {
                        cipherMatrix[i][j] = inputFile.charAt(iterator++);
                    } else {
                        System.err.println("inputfile must contain only lowercase letters (a-z) or digits (0-9)");
                        System.exit(1);
                    }
                }
                    
            }
        }
        for(char c : key.toCharArray()) {
            for(int i = 0; i < rowLength; i++) {
                encryptedText.append(cipherMatrix[i][Integer.parseInt(String.valueOf(c)) - 1]);
            }
        }
        Files.writeString(outputPath, encryptedText.toString());
        fileScanner.close();
        System.out.print(encryptedText.toString());
    }

    private static void doDecryption(int keyLength, String key, String inputFileName, String outputFileName) throws IOException {
        File output = new File("D:/MSCS/Fall 2023/Comp Sec/Projects/Assignment 1/CS558/" + outputFileName);
        Scanner outputFileScanner = new Scanner(output);
        outputFileScanner.useDelimiter("\\Z");
        String outputFileContent = outputFileScanner.next();
        double contentLength = (double) outputFileContent.length()/keyLength;
        int rowLength = (int) Math.ceil(contentLength);
        char[][] decipherMatrix = new char[rowLength][keyLength];
        StringBuilder decryptedText = new StringBuilder();
        Path outputPath = Path.of("D:/MSCS/Fall 2023/Comp Sec/Projects/Assignment 1/CS558/" + inputFileName);
        int iterator = 0;
        for(char c : key.toCharArray()) {
            for(int i = 0; i < rowLength; i++) {
                decipherMatrix[i][Integer.valueOf(String.valueOf(c)) - 1] =  outputFileContent.charAt(iterator++);
            }
        }
        for(int i = 0; i < rowLength; i++) {
            for(int j = 0; j < keyLength; j++) {
                decryptedText.append(decipherMatrix[i][j]);
            }
        }
        Files.writeString(outputPath, decryptedText.toString());
        outputFileScanner.close();
        System.out.println(decryptedText);
    }
}