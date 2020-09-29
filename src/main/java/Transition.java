/**
 * This class represents a single transition between two states of a FA.
 * Each transition has associated a symbol of a finite alphabet.
 */
public class Transition implements Edge {

    private String symbol; // maybe we should convert it to a java.util.regex.Pattern (which is a compiled version of a regex)

    public Transition(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
