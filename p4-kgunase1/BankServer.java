import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

public class BankServer {
    private static final String PUBLIC_KEY_FILE = "public_key.txt";
    private  static Utils utils;

    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                System.err.println("Incorrect number of arguments. This server program accepts one argument");
                System.exit(0);
            }
            utils = new Utils();
            int portNumber = Integer.parseInt(args[0]);
            Socket socket = null;
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
            publicKeyOS.writeObject(publicKey);
            publicKeyOS.close();
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Server is listening on port " + portNumber);
            Map<String, String> credentialsMap = utils.readPasswordFile("password");
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Client connected.");
                new Thread(new BankServerWorker(socket, privateKey, credentialsMap)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
