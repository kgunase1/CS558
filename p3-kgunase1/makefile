JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES = \
	Trans.java 
default: classes
classes: $(CLASSES:.java=.class)
compile:
	javac GenPasswd.java
run:
	java GenPasswd.java 
clean:
	rm -f *.class