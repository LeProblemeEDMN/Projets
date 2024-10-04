CFLAGS=-I.
DEPS = OptimProblem.h Simplex.h BranchAndBound.h
OBJ = main.o OptimProblem.o Simplex.o BranchAndBound.o

%.o: %.c $(DEPS)
	gcc -Wall -fopenmp -O3 --fast-math -c -o $@ $< $(CFLAGS)

BB_HPC: $(OBJ)
	gcc -Wall -fopenmp -O3 --fast-math -o $@ $^ $(CFLAGS)
clean:
	-rm BB_HPC:


