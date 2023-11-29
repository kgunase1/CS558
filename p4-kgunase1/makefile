JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	ATMClient.java \
	BankServer.java 

default: classes

classes: $(CLASSES:.java=.class)

compile:
	$(JC) $(JFLAGS) ATMClient.java
	$(JC) $(JFLAGS) BankServer.java

run:
	java GenPasswd

clean:
	rm -f *.class