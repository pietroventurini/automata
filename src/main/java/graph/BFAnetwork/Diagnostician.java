package graph.BFAnetwork;

import graph.fa.FA;
import graph.fa.FAState;

import java.util.Map;
/**
 * This class represents a diagnostician. A diagnostician is a FA, whose states inherit their name from
 * the corresponding silent closure, and whose transitions are the same decorated transitions of the space
 * of silent closures.
 * The map {@code diagnosis} maps each state to its diagnosis (that is the alternative between the decorations).
 * In order to keep track of which states correspond to each decoration, instead of coding the diagnosis as a String of
 * the form "dec1 | dec2 | dec3" we store the diagnosis as a map <DBSState, String>
 *     of the form <state1 -> dec1; state2 -> dec2; state3 -> dec3>.
 */
public class Diagnostician {
    private FA<FAState, DSCTransition> fa;
    private Map<FAState, Map<DBSState, String>> diagnosis;

    public Diagnostician(FA<FAState, DSCTransition> fa, Map<FAState, Map<DBSState, String>> diagnosis) {
        this.fa = fa;
        this.diagnosis = diagnosis;
    }

    public FA<FAState, DSCTransition> getFa() {
        return fa;
    }

    public Map<FAState, Map<DBSState, String>> getDiagnosis() {
        return diagnosis;
    }

    public String getDiagnosisOf(FAState s) {
        return diagnosisToString(diagnosis.get(s));
    }

    private String diagnosisToString(Map<DBSState, String> diagnosis) {
        String res = "";
        for (String s : diagnosis.values()) {
            res = res + s + "|";
        }
        res = res.equals("") ? "" : res.substring(0, res.length() - 1);
        return res;
    }
}
