Name : Karthick Gunasekar 
Email Address : kgunase1@binghamton.edu 
B Number : B00976718

Programming Language used : JAVA

Code was tested on remote.cs.binghamton.edu


BankServer : RSA is used for generating private and public keys 
ATMClient : AES is used for generating symmetric key

With the help of makefile,

1) Compile : 

make compile 
Note : All the two java files will be compiled

2) Run : 

make run 

Note : only the BankServer.java will run with default port 8080


3) clean : 

make clean


Another way is to run the below commands directly in the command line:

To Compile use the below command : 

javac BankServer.java 
javac ATMClient.java

To run the program :

java BankServer <port_number>
java ATMClient <domain> <port_number>