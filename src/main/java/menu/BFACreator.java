package menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import graph.bfa.BFA;
import graph.bfa.BFABuilder;
import graph.bfa.EventTransition;
import graph.fa.FAState;

public class BFACreator {
    private List<FAState> states;
    private List<EventTransition> transitions;
    private BFA bfa;
    private FAState initialState;
    private BFABuilder bfaBuilder;
    private boolean exit;

    private static final String OPTION1 = "1) Create a state";
    private static final String OPTION2 = "2) Create a transition";
    private static final String OPTION3 = "3) Put a transition between states";
    private static final String OPTION4 = "4) Select the initial state";
    private static final String OPTION5 = "5) Create the BFA";
    private static final String OPTION0 = "0) Cancel";

    public static final ImmutableList<String> OPTIONS = ImmutableList.of(OPTION1, OPTION2, OPTION3, OPTION4, OPTION5,
            OPTION0);

    public BFA createBFA(String name) {
        states = new ArrayList<>();
        transitions = new ArrayList<>();
        bfaBuilder = new BFABuilder(name);

        while (!exit) {
            Utility.printMenu(OPTIONS);
            int choice = Utility.getMenuChoice(OPTIONS.size() - 1);
            performAction(choice);
        }
        return bfa;

    }

    private void performAction(int choice) {
        switch (choice) {
            case 0:
                exit = true;
                break;
            case 1:
                createState();
                break;
            case 2:
                createTransition();
                break;
            case 3:
                if (states.isEmpty()) {
                    Utility.showMessageln("You need to create at least one state before placing a transition!");
                    break;
                } else if (transitions.isEmpty()) {
                    Utility.showMessageln("You need to create at least one transition!");
                    break;
                }
                putTransition();
                break;
            case 4:
                if (states.isEmpty()) {
                    Utility.showMessageln("You need to create at least one state before selecting an initial state!");
                    break;
                }
                selectInitialState();
                break;
            case 5:
                try {
                    bfa = bfaBuilder.build();
                    exit = true;
                } catch (Exception e) {
                    Utility.showMessageln("An error has occured");
                }
                break;
            default:
                Utility.showMessageln("Unknown error has occured.");
        }
    }

    public void createState() {
        FAState state;
        boolean out;
        String name = "";
        do {
            out = false;
            Utility.showMessage("\nInsert the name of the state: ");
            Scanner keyboard = new Scanner(System.in);
            name = keyboard.nextLine();
            if (name.equals("")) {
                Utility.showMessageln("Please write something! ");
                out = true;
            } else if (checkIfExistsStateWithSameName(name)) {
                Utility.showMessageln("You have already created a state with this name! ");
                return;
            }
        } while (out);
        state = new FAState(name);
        states.add(state);

        bfaBuilder.putState(state);
    }

    public boolean checkIfExistsStateWithSameName(String name) {
        return states.stream().anyMatch(s -> s.getName().equals(name));
    }

    public void createTransition() {
        EventTransition transition;
        Scanner keyboard = new Scanner(System.in);
        boolean out = false;
        String name = "";
        do {
            out = false;
            Utility.showMessage("\nInsert the name of the transition: ");
            name = keyboard.nextLine();
            if (name.equals("")) {
                out = true;
                Utility.showMessageln("Please write something!");
            } else if (checkIfExistsTransitionWithSameName(name)) {
                Utility.showMessageln("You have already created a transition with this name!");
                return;
            }
        } while (out);

        Utility.showMessage("Insert the transition observability label: ");
        String obs = keyboard.nextLine();
        Utility.showMessage("Insert the transition relevance label: ");
        String rel = keyboard.nextLine();

        Utility.showMessage("Insert the transition input event (press enter to skip this step):");
        String inEvent = keyboard.nextLine();

        Set<String> outEvents = new HashSet<>();
        String outEvent;
        do {
            Utility.showMessage("Insert an output event to the transition (press enter to stop this process):");
            outEvent = keyboard.nextLine();
            if (!outEvent.equals(""))
                outEvents.add(outEvent);
        } while (!outEvent.equals(""));

        EventTransition.Builder transBuilder = new EventTransition.Builder(name);
        transBuilder.observabilityLabel(obs).relevanceLabel(rel).outEvents(outEvents);
        if (!inEvent.equals(""))
            transBuilder.inEvent(inEvent);
        transition = transBuilder.build();
        transitions.add(transition);
    }

    public boolean checkIfExistsTransitionWithSameName(String name) {
        return transitions.stream().anyMatch(s -> s.getName().equals(name));
    }

    public EventTransition selectTransition() {
        Utility.showMessageln("\nSelect the number of the transition: ");

        for (int i = 0; i < transitions.size(); i++) {
            Utility.showMessageln(i + ") " + transitions.get(i).getName());
        }
        int choice = Utility.getMenuChoice(transitions.size() - 1);
        EventTransition transition = transitions.get(choice);
        transitions.remove(choice);
        return transition;
    }

    public FAState selectState() {
        for (int i = 0; i < states.size(); i++) {
            Utility.showMessageln(i + ") " + states.get(i).getName());
        }
        int choice = Utility.getMenuChoice(states.size() - 1);
        return states.get(choice);

    }

    public void putTransition() {
        EventTransition transition = selectTransition();
        Utility.showMessageln("\nSelect the number of the source state: ");
        FAState stateU = selectState();
        Utility.showMessageln("\nSelect the number of the destination state: ");
        FAState stateV = selectState();
        bfaBuilder.putTransition(stateU, stateV, transition);

    }

    public void selectInitialState() {
        Utility.showMessageln("\nSelect the inital state: ");
        initialState = selectState();
        bfaBuilder.putInitialState(initialState);
    }

}
