import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAAlgorithm {
    
    public static void doRSAAlgorithm() throws IOException{
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            String publicKeyStr = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
            writeFile("publicKey.txt", publicKeyStr);
            System.out.println("Public Key: " + publicKey.getEncoded());
            System.out.println("Private Key: " + privateKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
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
}
