import java.util.Arrays;

import org.checkerframework.checker.units.qual.Substance;

/**
 * This class represents a single transition between two states of a FA. Each
 * transition has associated a symbol of a finite alphabet.
 */
public class Transition implements Edge {

    private String symbol; // maybe we should convert it to a java.util.regex.Pattern (which is a compiled
                           // version of a regex)

    private String subscript; // this is the subscript needed in the second implementation of
                              // EspressioniRegolari

    public Transition(String symbol) {
        this.symbol = symbol;
    }

    public Transition(String symbol, String subscript) {
        this.symbol = symbol;
        this.subscript = subscript;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSubscript() {
        return subscript;
    }

    public void setSubscript(String subscript) {
        this.subscript = subscript;
    }

    public boolean hasSubscript() {
        return this.subscript != null;
    }

    /**
     * Add an alternative symbol through the pipe operator
     * 
     * @param newSymbol the non-empty symbol that will become an alternative to the
     *                  existing one
     */
    public void addAlternativeSymbol(String newSymbol) {
        // reduce (a|"") to (a)
        if (newSymbol.isEmpty())
            return;

        if (symbol.isEmpty()) {
            symbol = newSymbol;
        } else {
            symbol = symbol.concat("|").concat(newSymbol);
        }
    }

}
