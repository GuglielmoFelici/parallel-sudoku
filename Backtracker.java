import java.math.BigInteger;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.RecursiveTask;

public class Backtracker extends RecursiveTask<Integer>{

    static final BigInteger CUTOFF = new BigInteger( "1000000000000000000000000000000"); // 30 zeroes
    static boolean goSequential = false;
    public static long threadCount=0;

    private Sudoku sudoku;

    Backtracker(Sudoku sudoku) {
        this.sudoku = sudoku;
    }

    /** Metodo ausiliario per gestire il caso sequenziale del metodo compute(). */
    public int recursion(Sudoku s) {
        int sol = 0;
        Tuple<Integer> cell = s.getFirstFreeCell();
        Vector<Integer> candidates = new Vector<>(s.get(cell.getX(), cell.getY()));
        Sudoku copy;
        for (Integer candidate : candidates) {
            copy = s.copy();
            if (copy.set(candidate, cell.getX(), cell.getY())) {
                if (copy.isFull()) {
                    sol++;
                }
                else
                    sol += recursion(copy);
            }
        }
        return sol;
    }

    @Override
    protected Integer compute() {
        // Caso sequenziale
        if (sudoku.getSolutionSpace().compareTo(CUTOFF) < 0 || goSequential ) {
            return recursion(sudoku);
        }
        // Caso parallelo (fork/join)
        Tuple<Integer> cell = sudoku.getFirstFreeCell();
        Integer sol = 0;
        Vector<Backtracker> pool = new Vector<>();
        Set<Integer> candidates = sudoku.get(cell.getX(), cell.getY());
        Backtracker sub;
        Sudoku copy;
        int i=0; // Utilizzata per utilizzare compute() al posto di fork() nell'ultima computazione creata dal ciclo
        for (Integer candidate: candidates) {
            copy = sudoku.copy();
            if (copy.set(candidate, cell.getX(), cell.getY())) { // Formula un'ipotesi
                if (copy.isFull())
                    sol++;
                else {
                    sub = new Backtracker(copy);
                    if (i < candidates.size() - 1) {
                        pool.add(sub);
                        sub.fork();
                        threadCount++;
                    } else {
                        sol = sub.compute();
                    }
                }
            }
            i++;
        }
        for (Backtracker b : pool) {
            sol+=b.join();
        }
        return sol;
    }
}
