package menu;

import java.util.Scanner;

import com.google.common.collect.ImmutableList;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableNetwork;

import graph.BFAnetwork.BSState;
import graph.BFAnetwork.BSTransition;
import graph.BFAnetwork.DBSState;
import graph.BFAnetwork.DSCTransition;
import graph.BFAnetwork.Diagnostician;
import graph.fa.FA;
import graph.fa.FAState;

public class Utility {
    private Utility() {
    }

    // This utility method handles inputs from keyboard. It reads only integer
    // values from 0 to totalOptions

    public static int getMenuChoice(int totalOptions) {
        Scanner keyboard = new Scanner(System.in);
        int choice = -1;
        do {
            showMessage("\nEnter your choice: ");
            try {
                choice = Integer.parseInt(keyboard.nextLine());
                if (choice < 0 || choice > totalOptions) {
                    showMessageln("Choice outside of range. Please choose again.");
                }
            } catch (NumberFormatException e) {
                showMessageln("Invalid selection. Numbers only please.");
            }
        } while (choice < 0 || choice > totalOptions);

        return choice;
    }

    public static void showMessageln(String message) {
        System.out.println(message);
    }

    public static void showMessage(String message) {
        System.out.print(message);
    }

    public static void printMenu(ImmutableList<String> options) {
        showMessageln("\nPlease make a selection:");
        for (String option : options) {
            showMessageln(option);
        }
    }

    public static void printBehavioralSpaceDescription(FA<BSState, BSTransition> behavioralSpace) {
        MutableNetwork<BSState, BSTransition> network = behavioralSpace.getNetwork();
        Utility.showMessageln("\nList of states:");
        for (BSState s : behavioralSpace.getNodes()) {
            Utility.showMessageln("- " + s.getName());
        }
        Utility.showMessageln("\nList of transitions:");
        for (BSTransition e : behavioralSpace.getEdges()) {
            EndpointPair<BSState> pair = network.incidentNodes(e);
            Utility.showMessageln(
                    "- " + pair.nodeU().getName() + " -> " + e.getName() + " ->  " + pair.nodeV().getName());
        }
        Utility.showMessageln("\nInitial state: " + behavioralSpace.getInitialState().getName());

        Utility.showMessageln("\nList of  final states:");
        for (BSState s : behavioralSpace.getFinalStates()) {
            Utility.showMessageln("- " + s.getName());
        }
        Utility.showMessage("\n");
    }

    public static void printDecoratedSilentClosure(FA<DBSState, BSTransition> decoratedSilentClosure) {
        MutableNetwork<DBSState, BSTransition> network = decoratedSilentClosure.getNetwork();
        Utility.showMessageln("\nList of states:");
        for (DBSState s : decoratedSilentClosure.getNodes()) {
            Utility.showMessageln("- " + s.getName());
        }
        Utility.showMessageln("\nList of transitions:");
        for (BSTransition e : decoratedSilentClosure.getEdges()) {
            EndpointPair<DBSState> pair = network.incidentNodes(e);
            Utility.showMessageln(
                    "- " + pair.nodeU().getName() + " -> " + e.getName() + " ->  " + pair.nodeV().getName());
        }
        Utility.showMessageln("\nInitial state: " + decoratedSilentClosure.getInitialState().getName());

        Utility.showMessageln("\nList of  final states:");
        for (DBSState s : decoratedSilentClosure.getFinalStates()) {
            Utility.showMessageln("- " + s.getName());
        }
        Utility.showMessage("\n");
    }

    public static void printDecoratedSpaceDescription(
            FA<FA<DBSState, BSTransition>, DSCTransition> decoratedSpaceOfClosures) {
        MutableNetwork<FA<DBSState, BSTransition>, DSCTransition> network = decoratedSpaceOfClosures.getNetwork();
        Utility.showMessageln("\nList of closures:");
        for (FA<DBSState, BSTransition> s : decoratedSpaceOfClosures.getNodes()) {
            Utility.showMessageln("- " + s.getName());
        }
        Utility.showMessageln("\nList of transitions:");
        for (DSCTransition e : decoratedSpaceOfClosures.getEdges()) {
            EndpointPair<FA<DBSState, BSTransition>> pair = network.incidentNodes(e);
            Utility.showMessageln(
                    "- " + pair.nodeU().getName() + " -> " + e.getName() + " ->  " + pair.nodeV().getName());
        }
        Utility.showMessageln("\nInitial closure: " + decoratedSpaceOfClosures.getInitialState().getName());

        Utility.showMessageln("\nList of  acceptance closures:");
        for (FA<DBSState, BSTransition> s : decoratedSpaceOfClosures.getAcceptanceStates()) {
            Utility.showMessageln("- " + s.getName());
        }
        Utility.showMessage("\n");
    }

    public static void printDiagnosticianDescription(Diagnostician diagnostician) {
        MutableNetwork<FAState, DSCTransition> network = diagnostician.getFa().getNetwork();
        Utility.showMessageln("\nList of states:");
        for (FAState s : diagnostician.getFa().getNodes()) {
            Utility.showMessageln("- " + s.getName());
        }
        Utility.showMessageln("\nList of transitions:");
        for (DSCTransition e : diagnostician.getFa().getEdges()) {
            EndpointPair<FAState> pair = network.incidentNodes(e);
            Utility.showMessageln(
                    "- " + pair.nodeU().getName() + " -> " + e.getName() + " ->  " + pair.nodeV().getName());
        }
        Utility.showMessageln("\nInitial state: " + diagnostician.getFa().getInitialState().getName());

        Utility.showMessageln("\nList of  final states:");
        for (FAState s : diagnostician.getFa().getAcceptanceStates()) {
            Utility.showMessageln("- " + s.getName());
        }
        Utility.showMessage("\n");
    }
}
