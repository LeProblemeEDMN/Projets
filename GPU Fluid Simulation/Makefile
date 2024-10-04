# Compiler options
CC = nvcc
CFLAGS = -std=c++11 -O3 -Xcompiler -fopenmp

# Include directories
INCLUDES = -I.

# Source files
SOURCES = main.cu Fields.cu
DEPS = derivative.h Fields.h

# Object files
OBJ = $(SOURCES:.cu=.o)

# Executable name
EXECUTABLE = Simul

# Default target
all: $(EXECUTABLE)

# Compilation
%.o: %.cu $(DEPS)
	$(CC) $(CFLAGS) $(INCLUDES) -c -o $@ $<

# Linking
$(EXECUTABLE): $(OBJ)
	$(CC) $(CFLAGS) $(INCLUDES) -O3 -o $@ $^

# Clean
clean:
	rm -f $(OBJ) $(EXECUTABLE)


