JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Atm.java \
	Bank.java 

default: classes

classes: $(CLASSES:.java=.class)

compile:
	$(JC) $(JFLAGS) Atm.java
	$(JC) $(JFLAGS) Bank.java

run:
	java Bank 8080

clean:
	rm -f *.class