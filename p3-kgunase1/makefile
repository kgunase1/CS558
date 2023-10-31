JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES = \
	Client.java
	Server.java
	GenPasswd.java
default: classes
classes: $(CLASSES:.java=.class)
compile:
	javac Client.java
	javac Server.java
	javac GenPasswd.java
run:
	java GenPasswd.java 
clean:
	rm -f *.class