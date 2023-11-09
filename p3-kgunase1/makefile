JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Client.java \
	Server.java \
	GenPasswd.java

default: classes

classes: $(CLASSES:.java=.class)

compile:
	$(JC) $(JFLAGS) Client.java
	$(JC) $(JFLAGS) Server.java
	$(JC) $(JFLAGS) GenPasswd.java

run:
	java GenPasswd

clean:
	rm -f *.class
