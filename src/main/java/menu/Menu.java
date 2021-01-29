package menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import files.FileUtils;
import graph.BFAnetwork.BFANetwork;
import graph.bfa.BFA;

public class Menu {
    private boolean exit;
    private BFANetwork bfaNetwork;
    private List<BFA> bfas;
    private FileUtils fileUtils;

    private static final String OPTION1 = "1) Load a BFA Network";
    private static final String OPTION2 = "2) Store a BFA Network";
    private static final String OPTION3 = "3) Create a BFA Network";
    private static final String OPTION4 = "4) Create a BFA";
    private static final String OPTION5 = "5) Get results from the BFA Network";
    private static final String OPTION0 = "0) Go back";

    private static final ImmutableList<String> OPTIONS = ImmutableList.of(OPTION1, OPTION2, OPTION3, OPTION4, OPTION5,
            OPTION0);

    public Menu(String project) {
        fileUtils = new FileUtils(project);
        bfas = new ArrayList<>();
    }

    public void runMenu() {
        List<String> bfaNames = fileUtils.getBFAsList();
        for (String bfaName : bfaNames) {
            try {
                bfas.add(fileUtils.loadBFA(bfaName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        printHeader();
        while (!exit) {
            Utility.printMenu(OPTIONS);
            int choice = Utility.getMenuChoice(OPTIONS.size() - 1);
            performAction(choice);
        }

    }

    private void printHeader() {
        Utility.showMessageln("\n+----------------------------------------+");
        Utility.showMessageln("|         Welcome to our project         |");
        Utility.showMessageln("+----------------------------------------+");
    }

    private void performAction(int choice) {
        switch (choice) {
            case 0:
                exit = true;
                break;
            case 1:
                try {
                    bfaNetwork = fileUtils.loadBFANetwork();
                } catch (IOException e) {
                    System.err.println("Unable to load the BFA network, check that file exists and it is not corrupted, otherwise, create a new BFA network");
                }
                break;
            case 2:
                if (bfaNetwork != null) {
                    fileUtils.storeBFANetwork(bfaNetwork);
                } else
                    Utility.showMessageln(
                            "You didn't select any BFA Network! Please create or load a network from file!");
                break;
            case 3:
                BFANetworkCreator bfaNetworkCreator = new BFANetworkCreator();
                bfaNetwork = bfaNetworkCreator.createBFANetwork(bfas);
                bfaNetwork.printDescription();
                break;
            case 4:
                BFACreator bfaCreator = new BFACreator();
                BFA bfa = bfaCreator.createBFA();
                bfa.printDescription();
                bfas.add(bfa);
                fileUtils.storeBFA(bfa);
                break;
            case 5:
                if (bfaNetwork != null) {
                    Supervisor supervisor = new Supervisor(bfaNetwork);
                    supervisor.runMenu();
                } else {
                    Utility.showMessageln(
                            "You didn't select any BFA Network! Please create or load a network from file!");
                }
                break;
            default:
                Utility.showMessageln("Unknown error has occured.");
        }
    }

}
