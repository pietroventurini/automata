package graph.BFAnetwork;

import graph.fa.FA;
import graph.fa.FAState;

import java.util.Map;


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
        res = res.substring(0, res.length() - 1);
        return res;
    }
}
