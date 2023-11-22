# CS558
Computer Security Projects

Name : Karthick Gunasekar
Email Address : kgunase1@binghamton.edu
B Number : B00976718

Programming Language used : JAVA

Code was tested on remote.cs.binghamton.edu

With the help of makefile, 
1) Compile : make compile
    Note : All the three java files will be compiled
2) Run : make run 
    Note : only the GenPasswd.java will run
3) clean : make clean

Another way is to run the below commands directly in the command line:

1) To Compile use the below command :
    javac GenPasswd.java
    javac Server.java
    javac Client.java

2) To run the program :
    1) java GenPasswd.java
    2) java Server.java 8080
    3) java Client.java remote*.cs.binghamton.edu 8080
        Note: replace * with the corresponding domain name.

keytool commands for generating public key certificate: 

    1) keytool -genkey -keyalg RSA -keysize 2048 -validity 365 -alias myserver -keystore keystore.jks
    2) keytool -export -keystore keystore.jks -alias myserver -file cert.pem -rfc
    
    No additional software is needed to be installed on remote.cs to execute the program.

