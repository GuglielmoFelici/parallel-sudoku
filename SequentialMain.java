import java.io.FileNotFoundException;
import java.util.Set;

public class SequentialMain {

    public static final int SIZE = Sudoku.SIZE;

    public static void main(String[] args) {
        Sudoku sudoku;
        int filling=0;
        try {
            sudoku = new Sudoku(args[0]);
        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            return;
        }
        Integer legalSol;
        System.out.println("Dimensione dello spazio delle soluzioni: " + sudoku.getSolutionSpace());
        for (Set<Integer> s : sudoku.getBoard()) {
            if (s.size()==1) filling++;
        }
        System.out.println("Fattore di riempimento iniziale: "+(int)((filling/(double)SIZE)*100)+"% ("+filling+" celle inizializzate.)");
        long startTime = System.nanoTime();
        Backtracker.goSequential = true;
        legalSol = new Backtracker(sudoku).compute();
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        String s;
        System.out.println(String.format("Il puzzle ha %s soluzion%s legal%s.\n", legalSol, s =
                legalSol==1? "e" : "i", s ));
        System.out.println("Tempo di esecuzione in ms: " + timeElapsed/1000000);
        System.out.println("Thread generati: " + Backtracker.threadCount);
    }
}
