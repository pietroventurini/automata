package menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MoreCollectors;

import graph.BFAnetwork.BFANetwork;
import graph.BFAnetwork.BFANetworkSupervisor;
import graph.BFAnetwork.BSState;
import graph.BFAnetwork.BSTransition;
import graph.BFAnetwork.DBSState;
import graph.BFAnetwork.DSCTransition;
import graph.BFAnetwork.Diagnostician;
import graph.BFAnetwork.LOBSState;
import graph.bfa.BFA;
import graph.fa.FA;

public class Supervisor {
    private BFANetwork bfaNetwork;
    private FA<BSState, BSTransition> behavioralSpace;
    private FA<FA<DBSState, BSTransition>, DSCTransition> decoratedSpaceOfClosures;
    private Diagnostician diagnostician;
    private List<ArrayList<String>> linearObservations;
    private FA<LOBSState, BSTransition> linearObservationBehavioralSpaces;

    private boolean exit;

    private static final String OPTION1 = "1) Compute Behavioral Space";
    private static final String OPTION2 = "2) Compute Decorated Silent Closure of a state";
    private static final String OPTION3 = "3) Compute Decorated Space of Closures";
    private static final String OPTION4 = "4) Compute Diagnostician";
    private static final String OPTION5 = "5) Change name of a State in the Behavioral Space";
    private static final String OPTION6 = "6) Change name of a Decorated Silent Closure";
    private static final String OPTION7 = "7) Create Linear Observation";
    private static final String OPTION8 = "8) Compute Behavioral Space related to a Linear Observation";
    private static final String OPTION9 = "9) Compute Linear Diagnosis with Diagnostician";
    private static final String OPTION0 = "0) Go back";

    public static final ImmutableList<String> OPTIONS = ImmutableList.of(OPTION1, OPTION2, OPTION3, OPTION4, OPTION5,
            OPTION6, OPTION7, OPTION8, OPTION9, OPTION0);

    public Supervisor(BFANetwork bfaNetwork) {
        this.bfaNetwork = bfaNetwork;
    }

    public void runMenu() {
        behavioralSpace = BFANetworkSupervisor.getBehavioralSpace(bfaNetwork);
        BFANetworkSupervisor.pruneFA(behavioralSpace);
        decoratedSpaceOfClosures = BFANetworkSupervisor.decoratedSpaceOfClosures(behavioralSpace);
        diagnostician = BFANetworkSupervisor.diagnostician(decoratedSpaceOfClosures);
        linearObservations = new ArrayList<>();
        while (!exit) {
            Utility.printMenu(OPTIONS);
            int choice = Utility.getMenuChoice(OPTIONS.size() - 1);
            performAction(choice);
        }

    }

    private void performAction(int choice) {
        switch (choice) {
            case 0:
                exit = true;
                break;
            case 1:
                Utility.printBehavioralSpaceDescription(behavioralSpace);
                break;
            case 2:
                Utility.printDecoratedSilentClosure(selectDecoratedSilentClosureFromState());
                break;
            case 3:
                Utility.printDecoratedSpaceDescription(decoratedSpaceOfClosures);
                break;
            case 4:
                Utility.printDiagnosticianDescription(diagnostician);
                break;
            case 5:
                changeBehavioralSpaceStateName();
                break;
            case 6:
                changeDecoratedSilenceClosureName();
                diagnostician = BFANetworkSupervisor.diagnostician(decoratedSpaceOfClosures);
                break;
            case 7:
                createLinearObservation();
                break;
            case 8:
                if (linearObservations.isEmpty()) {
                    Utility.showMessageln("\n You didn't create any linear observation!");
                    break;
                }
                ArrayList<String> linObs1 = selectLinearObservation();
                linearObservationBehavioralSpaces = BFANetworkSupervisor
                        .getBehavioralSpaceForLinearObservation(bfaNetwork, linObs1);
                break;
            case 9:
                if (linearObservations.isEmpty()) {
                    Utility.showMessageln("\n You didn't create any linear observation!");
                    break;
                }
                ArrayList<String> linObs2 = selectLinearObservation();
                String linearDiagnosis = BFANetworkSupervisor.linearDiagnosis(diagnostician, linObs2);
                Utility.showMessageln("\nLinear diagnosis: " + linearDiagnosis);
                break;
            default:
                Utility.showMessageln("Unknown error has occured.");
        }
    }

    private BSState selectStateInBehavioralSpace() {
        Utility.showMessageln("\nSelect a state: ");
        List<BSState> states = new ArrayList<>();
        states.addAll(behavioralSpace.getNodes());
        for (int i = 0; i < states.size(); i++) {
            Utility.showMessageln(i + ") " + states.get(i).getName());
        }
        int choice = Utility.getMenuChoice(states.size());
        return states.get(choice);
    }

    public boolean checkIfExistsStateWithSameName(String name) {
        return behavioralSpace.getNodes().stream().anyMatch(s -> s.getName().equals(name));
    }

    private void changeBehavioralSpaceStateName() {
        boolean out;
        Scanner keyboard = new Scanner(System.in);
        String name;
        do {
            out = false;
            BSState state = selectStateInBehavioralSpace();
            Utility.showMessage("Insert the new name: ");
            name = keyboard.nextLine();
            if (name.equals("")) {
                Utility.showMessageln("Please write something!");
                out = true;
            } else if (checkIfExistsStateWithSameName(name)) {
                Utility.showMessageln("There is already a state with this name! ");
                out = true;
            } else {
                state.setName(name);
                Utility.showMessage("\nType 1 to continue or 0 to stop: ");
                int choice = Utility.getMenuChoice(1);
                if (choice == 1)
                    out = true;
            }

        } while (out);
    }

    private BSState selectEntryInBehavioralSpace() {
        Utility.showMessageln("\nSelect an entry state: ");
        List<BSState> states = new ArrayList<>();
        states.addAll(behavioralSpace.getStates().stream().filter(
                s -> behavioralSpace.getNetwork().inEdges(s).stream().anyMatch(BSTransition::hasObservabilityLabel))
                .collect(Collectors.toSet()));
        states.add(behavioralSpace.getInitialState());
        for (int i = 0; i < states.size(); i++) {
            Utility.showMessageln(i + ") " + states.get(i).getName());
        }
        int choice = Utility.getMenuChoice(states.size());
        return states.get(choice);
    }

    private FA<DBSState, BSTransition> selectDecoratedSilentClosureFromState() {
        BSState state = selectEntryInBehavioralSpace();
        return decoratedSpaceOfClosures.getStates().stream().filter(d -> d.getInitialState().getBSState() == state)
                .collect(MoreCollectors.onlyElement());
    }

    private FA<DBSState, BSTransition> selectDecoratedSilentClosure() {
        Utility.showMessageln("\nSelect a decorated silent closure: ");
        List<FA<DBSState, BSTransition>> closures = new ArrayList<>();
        closures.addAll(decoratedSpaceOfClosures.getNodes());
        for (int i = 0; i < closures.size(); i++) {
            Utility.showMessageln(i + ") " + closures.get(i).getName());
        }
        int choice = Utility.getMenuChoice(closures.size());
        return closures.get(choice);
    }

    public boolean checkIfExistsClosureWithSameName(String name) {
        return decoratedSpaceOfClosures.getNodes().stream().anyMatch(s -> s.getName().equals(name));
    }

    private void changeDecoratedSilenceClosureName() {
        boolean out;
        Scanner keyboard = new Scanner(System.in);
        String name;
        do {
            out = false;
            FA<DBSState, BSTransition> closure = selectDecoratedSilentClosure();
            Utility.showMessage("Insert the new name: ");
            name = keyboard.nextLine();
            if (name.equals("")) {
                Utility.showMessageln("Please write something!");
                out = true;
            } else if (checkIfExistsClosureWithSameName(name)) {
                Utility.showMessageln("There is already a closure with this name! ");
                out = true;
            } else {
                closure.setName(name);
                Utility.showMessage("\nType 1 to continue or 0 to stop: ");
                int choice = Utility.getMenuChoice(1);
                if (choice == 1)
                    out = true;
            }

        } while (out);

    }

    private List<String> getObservabilityLabels() {
        Set<String> observabilityLabels = new HashSet<>();
        for (BFA bfa : bfaNetwork.getBFAs()) {
            observabilityLabels.addAll(
                    bfa.getTransitions().stream().map(t -> t.getObservabilityLabel()).collect(Collectors.toSet()));
        }
        List<String> listObservabilityLabels = new ArrayList<>();
        listObservabilityLabels.addAll(observabilityLabels);
        Collections.replaceAll(listObservabilityLabels, "", "eps");
        return listObservabilityLabels;
    }

    private void createLinearObservation() {
        ArrayList<String> linearObservation = new ArrayList<>();
        List<String> observabilityLabels = getObservabilityLabels();
        int choice = -1;
        do {
            Utility.showMessageln("\nCurrent Linear Observation: " + linearObservation.toString());
            Utility.showMessageln("Select the number of the observability label you want to add (type 0 to stop): ");
            for (int i = 0; i < observabilityLabels.size(); i++) {
                Utility.showMessageln((i + 1) + ") " + observabilityLabels.get(i));
            }
            choice = Utility.getMenuChoice(observabilityLabels.size());
            if (choice != 0)
                linearObservation.add(observabilityLabels.get(choice - 1));
        } while (choice != 0);

        linearObservations.add(linearObservation);
    }

    private ArrayList<String> selectLinearObservation() {
        Utility.showMessageln("\nSelect the number of the Linear Observation: ");
        for (int i = 0; i < linearObservations.size(); i++) {
            Utility.showMessageln(i + ") " + linearObservations.get(i).toString());
        }
        int choice = Utility.getMenuChoice(linearObservations.size() - 1);
        return linearObservations.get(choice);

    }

}
