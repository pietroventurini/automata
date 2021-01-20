package graph.BFAnetwork;

import graph.fa.FA;
import graph.fa.FAState;

import java.util.Map;

/**
 * FIXME: scegliere tra
 *  1) usare una classe Diagnostician contenente un FA e una mappa <FAState, diagnosi>       <-- IMPLEMENTAZIONE ATTUALE
 *  2) creare una classe StatoDiagnosticatore che oltre al nome abbia una diagnosi e riciclare FA
 *     costruendo un FA<StatoDiagnosticatore, DSCTransition>
 *  3) tenere traccia delle chiusure silenziose sottostanti (cioè i nodi non hanno solo nome e diagnosi ma anche
 *     la chiusura corrispondente)
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
        StringBuilder sb = new StringBuilder();
        diagnosis.values().stream().distinct().map(
                s -> sb.append(s + "|")
        );
        // remove last char ("|")
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
