# CS558
Computer Security Projects

Name : Karthick Gunasekar
Email Address : kgunase1@binghamton.edu
B Number : B00976718

Programming Language used : JAVA

Code was tested on remote.cs.binghamton.edu

With the help of makefile, 
1) Compile : make compile
2) Run : make run 
    Note : default key, input and output files are given
3) clean : make clean

Another way is to run the below commands directly in the command line:

1) To Compile use the below command :
    javac Trans.java

2) To run the program :
    1) java Trans.java 7 3412567 input output enc
    or
    2) java Trans.java 7 3412567 output input1 dec

keytool
keytool -genkey -keyalg RSA -keysize 2048 -validity 365 -alias myserver -keystore keystore.jks
keytool -export -keystore keystore.jks -alias myserver -file cert.pem -rfc

