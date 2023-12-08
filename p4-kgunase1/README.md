Name : Karthick Gunasekar 
Email Address : kgunase1@binghamton.edu 
B Number : B00976718

Programming Language used : JAVA

Code was tested on remote.cs.binghamton.edu


BankServer : RSA is used for generating private and public keys 
Encryption:
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        byte[] encryptedSymmetricKey = rsaCipher.doFinal(symmetricKey.getEncoded());

ATMClient : AES is used for generating symmetric key
Decryption:
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedSymmetricKey = rsaCipher.doFinal(encryptedSymmetricKey);

        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedCredentials = aesCipher.doFinal(encryptedData);


With the help of makefile,

1) Compile : 

make compile 
Note : All the two java files will be compiled

2) Run : 

make run 

Note : only the Bank.java will run with default port 8080


3) clean : 

make clean


Another way is to run the below commands directly in the command line:

To Compile use the below command : 

javac Bank.java 
javac Atm.java

To run the program :

java Bank <port_number>
java Atm <domain> <port_number>