public class Tuple<T> {
    private T x;
    private T y;

    Tuple(T x, T y) {
        this.x = x;
        this. y = y;
    }


    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }

    @Override
    public String toString() {
        return "( " + x + ", "+ y+ " )";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?> tuple = (Tuple<?>) o;

        if (!x.equals(tuple.x)) return false;
        return y.equals(tuple.y);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }
}
