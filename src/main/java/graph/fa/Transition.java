package graph.fa;

import graph.edges.EdgeWithSymbol;

/**
 * This class represents a single transition between two states of a FA. Each
 * transition has associated a symbol of a finite alphabet.
 *
 * @author Pietro Venturini
 */
public class Transition implements EdgeWithSymbol {

    private String symbol; // maybe we should convert it to a java.util.regex.Pattern (which is a compiled
                           // version of a regex) or create a class Symbol

    public Transition(String symbol) {
        this.symbol = symbol;
    }

    public Transition() {
        this.symbol = "";
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Add an alternative symbol through the pipe operator
     * 
     * @param newSymbol the non-empty symbol that will become an alternative to the
     *                  existing one
     * FIXME: move method to a utility class
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
