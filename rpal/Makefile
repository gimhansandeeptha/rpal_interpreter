# Define the Java compiler and flags
JC = javac
JFLAGS = -d bin -sourcepath src

# Define the source directory
SRCDIR = src

# Find all .java files in the source directory
SOURCES := $(wildcard $(SRCDIR)/*.java)

# Convert .java files to .class files in the bin directory
CLASSES := $(SOURCES:$(SRCDIR)/%.java=bin/%.class)

# Default target - compile the Java classes
all: $(CLASSES)

# Rule for compiling .java files to .class files
bin/%.class: $(SRCDIR)/%.java
	$(JC) $(JFLAGS) $<

# Rule to run the Java program
run: $(CLASSES)
	java -cp bin rpal20

# Clean the bin directory
clean:
	rm -rf bin

# PHONY targets to prevent conflicts with filenames
.PHONY: all run clean