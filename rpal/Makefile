# Define the Java compiler and flags
JC = javac
JFLAGS = -d . -sourcepath src

# Define the source directory
SRCDIR = src

# Find all .java files in the source directory
SOURCES := $(wildcard $(SRCDIR)/*.java)

# Convert .java files to .class files in the root directory
CLASSES := $(SOURCES:$(SRCDIR)/%.java=%.class)

# Default target - compile the Java classes
all: $(CLASSES)

# Rule for compiling .java files to .class files
%.class: $(SRCDIR)/%.java
	$(JC) $(JFLAGS) $<

# Rule to run the Java program
run: $(CLASSES)
	java rpal20

# Clean the root directory (remove all generated .class files)
clean:
	rm -f *.class

# PHONY targets to prevent conflicts with filenames
.PHONY: all run clean
