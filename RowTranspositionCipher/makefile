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
	javac Trans.java
run:
	java Trans.java 7 3412567 input output enc
clean:
	rm -f *.class