# Compiler and flags
JAVAC = javac
JFLAGS = -d bin -sourcepath src -cp bin

# List of Java source files
SOURCES := $(wildcard src/*.java)

# Targets
all: bin/rpal20.class

bin/rpal20.class: $(SOURCES)
	$(JAVAC) $(JFLAGS) $(SOURCES)

clean:
	rm -rf bin/*

.PHONY: all clean
