import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;

public class Sudoku {

    private Vector<Set<Integer>> sudoku = new Vector<>();

    public static final int LEN = 9;
    public static final int SIZE = LEN*LEN;
    public static final int BLOCKLEN = 3;

    private Sudoku() {}

    /** Crea il sudoku leggendo un file di origine opportunamente formattato. Il sudoku è composto (concettualmente) da una matrice
     * contenente per ogni cella i rispettivi candidati possibili. */
    Sudoku (String s) throws FileNotFoundException {
        FileReader file = new FileReader(s);
        Scanner in = new Scanner(file);
        while (in.hasNextLine()) {
            String line = in.nextLine();
            for (char el : line.toCharArray()) {
                Set<Integer> candidates = new HashSet<>(9);
                if (el != '.') {
                    candidates.add(Character.getNumericValue(el));
                }
                sudoku.add(candidates);
            }
        }
        // Inizializza i possibili candidati di ogni cella
        for (int i=0; i<LEN; i++) {
            for (int j=0; j<LEN; j++) {
                if (get(i,j).isEmpty()) {
                    for (int n = 1; n <= Sudoku.LEN; n++) {
                        if (checkNumber(n, i, j)) {
                            get(i,j).add(n);
                        }
                    }
                }
            }
        }

    }

    /** Crea una copia profonda del sudoku. */
    public Sudoku copy() {
        Sudoku s = new Sudoku();
        for (Set<Integer> v : sudoku) {
            s.sudoku.add(new HashSet<>(v));
        }
        return s;
    }

    public boolean isFull() {
        return Objects.isNull(getFirstFreeCell());
    }


    public Set<Integer> get(int i, int j) {
        return sudoku.get(i*LEN+j);
    }

    /** Oltre a settare il valore n come unico candidato nella posizione i,j, questo metodo aggiorna i candidati di tutte le celle "influenzate" dalla cella (i,j),
     * ovvero le celle nello stesso blocco o nella stessa riga/colonna di (i,j). Laddove il numero di candidati di una cella influenzata si riduca a 1,
     * il metodo chiama ricorsivamente se stesso su quella cella per aggiornare i candidati da essa influenzati.
     * Ritorna false se uno degli inserimenti ha generato una configurazione non valida, true altrimenti.*/
    public boolean set(Integer n, int i, int j) {
        if (!get(i,j).contains(n)) return false;
        get(i,j).clear();
        get(i,j).add(n);
        Vector<Tuple<Integer>> modified = new Vector<>();
        for (Tuple<Integer> influenced : getInfluenced(i,j)) {
            Set<Integer> pos = get(influenced.getX(),influenced.getY());
            if (pos.contains(n)) {
                pos.remove(n);
                modified.add(influenced);
                if (pos.isEmpty()) { // C'è una cella senza candidati, la configurazione non è valida
                    return false;
                }
            }
        }
        for (Tuple<Integer> t : modified) {
            Set<Integer> pos = get(t.getX(),t.getY());
            if (pos.size()==1) {
                if (!set(pos.iterator().next(), t.getX(), t.getY())) return false;
            }
        }
        return true;
    }

    public List<Set<Integer>> getBoard() {
        return Collections.unmodifiableList(sudoku);
    }

    /** Ritorna l'insieme delle posizioni che si trovano nello stesso blocco di (i,j). */
    public Vector<Tuple<Integer>> getBlock(int i, int j) {
        Vector<Tuple<Integer>> ret = new Vector<>();
        int x = (i/BLOCKLEN)*BLOCKLEN;
        int y = (j/BLOCKLEN)*BLOCKLEN;
        for (int k=0; k<BLOCKLEN; k++) {
            for (int q=0; q<BLOCKLEN; q++) {
                ret.add(new Tuple<>(x+k, y+q));
            }
        }
        return ret;
    }

    /** Ritorna l'insieme delle posizioni che si trovano nella stessa riga/colonna di (i,j). */
    public Vector<Tuple<Integer>> getCross(int i,int j) {
        Vector<Tuple<Integer>> ret = new Vector<>();
        for (int k=0; k<LEN;k++) {
            ret.add(new Tuple<>(i, k));
            if (k!=i) ret.add(new Tuple<>(k, j));
        }
        return ret;
    }

    /** Ritorna l'insieme delle posizioni che si trovano nello stesso blocco, riga o colonna della cella (i,j) */
    public Vector<Tuple<Integer>> getInfluenced(int i,int j) {
        Set<Tuple<Integer>> positions = new HashSet<>(getBlock(i,j));
        positions.addAll(getCross(i,j));
        positions.remove(new Tuple<>(i,j));
        return new Vector<>(positions);
    }

    /** Controlla se un numero n è un candidato valido per la cella (i,j) */
    public boolean checkNumber (Integer n, int i, int j) {
        for (Tuple<Integer> t : getInfluenced(i,j)) {
            if (get(t.getX(), t.getY()).size()==1 && get(t.getX(),t.getY()).contains(n)) {
                return false;
            }
        }
        return true;
    }

    public Tuple<Integer> getFirstFreeCell() {
        for (int i=0; i<LEN; i++) {
            for (int j=0; j<LEN; j++) {
                if (get(i,j).size()!=1) {
                    return new Tuple<>(i,j);
                }
            }
        }
        return null;
    }

    public BigInteger getSolutionSpace() {
        BigInteger ret = BigInteger.ONE;
        for (int i=0; i<LEN; i++) {
            for (int j=0; j< LEN; j++) {
                if (get(i,j).size()!=1) {
                    ret = ret.multiply(BigInteger.valueOf(get(i, j).size()));
                }
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (int i=0; i<LEN; i++) {
            if (i%3==0) ret.append("\n");
            for (int j=0; j<LEN; j++) {
                if (j%3==0) ret.append(" ");
                Set<Integer> n = get(i,j);
                ret.append(n.size() != 1 ? "_" : n.iterator().next());
                ret.append(" ");

            }
            ret.append("\n");
        }
        return ret.toString();
    }
}
