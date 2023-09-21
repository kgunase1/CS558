package CS558.RowTranspositionCipher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

public class Trans  {
    public static void main(String[] args) throws IOException {
        isEmptyArguments(args);
        int keyLength = Integer.parseInt(args[0]);
        String key = args[1];
        String inputFileName = args[2];
        String outputFileName = args[3];
        String operation = args[4];
        validateArguments(keyLength, key, inputFileName, outputFileName, operation);
        if(operation.equals("enc"))
            doEncryption(keyLength, key, inputFileName, outputFileName);
        else if(operation.equals("dec"))
            doDecryption(keyLength, key, inputFileName, outputFileName);
    }

    private static void isEmptyArguments(String[] args) {
        if (args.length != 5 || args[0].equals("${arg0}") || args[1].equals("${arg1}") || args[2].equals("${arg2}")
				|| args[3].equals("${arg3}") || args[4].equals("${arg4}")) {
			System.err.println("Error: Incorrect number of arguments. Program accepts 5 argumnets.");
			System.exit(0);
		}
    }

    private static void validateArguments(int keyLength, String key, String inputFileName, String outputFileName, String operation) {
        if(keyLength != key.length()) {
            System.err.println("keylength " + keyLength + " must match the length of the key " + key + ".");
            System.exit(0);
        }
        Set<Integer> keySet = new HashSet<>();
        for(char c : key.toCharArray()) {
            if(c < 49 || c > 57) {
                System.err.println("inputfile " + inputFileName + " must contain only lowercase letters (a-z) or digits (0-9).");
                System.exit(0);
            }
            if(!keySet.add(Integer.parseInt(String.valueOf(c)))) {
                System.err.println("key " + key + " must include all digits from 1 to " + keyLength + " with each digit occurring only once.");
                System.exit(0);
            }
        }

        if(!operation.equals("enc") || !operation.equals("dec")) {
            System.err.println("Invalid Operation type, the opertion type should be either enc or dec.");
            System.exit(0);
        }
    }

    private static void doEncryption(int keyLength, String key, String inputFileName, String outputFileName) throws IOException {
        String inputFile = readFile(inputFileName);
        double contentLength = (double) inputFile.length()/keyLength;
        int rowLength = (int) Math.ceil(contentLength);
        char[][] cipherMatrix = new char[rowLength][keyLength];
        StringBuilder encryptedText = new StringBuilder();
        int iterator = 0;
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
        writeFile(outputFileName, encryptedText.toString());
    }

    private static void doDecryption(int keyLength, String key, String inputFileName, String outputFileName) throws IOException {
        String inputFileContent = readFile(outputFileName);
        double contentLength = (double) inputFileContent.length()/keyLength;
        int rowLength = (int) Math.ceil(contentLength);
        char[][] decipherMatrix = new char[rowLength][keyLength];
        StringBuilder decryptedText = new StringBuilder();
        int iterator = 0;
        for(char c : key.toCharArray()) {
            for(int i = 0; i < rowLength; i++) {
                decipherMatrix[i][Integer.valueOf(String.valueOf(c)) - 1] =  inputFileContent.charAt(iterator++);
            }
        }
        for(int i = 0; i < rowLength; i++) {
            for(int j = 0; j < keyLength; j++) {
                decryptedText.append(decipherMatrix[i][j]);
            }
        }
        writeFile(inputFileName, decryptedText.toString());
    }

    private static String readFile(String inputFileName) throws FileNotFoundException {
        File output = new File("/home/kgunase1/CS558/" + inputFileName);
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

    private static void writeFile(String outputFileName, String content) throws IOException{
        Path outputPath = Path.of("/home/kgunase1/CS558/" + outputFileName);
        try {
            Files.writeString(outputPath, content);
        } catch (FileNotFoundException e) {
            System.err.println("Incorrect File Name / No such file or directory");
            System.exit(0);
        }
    }
}