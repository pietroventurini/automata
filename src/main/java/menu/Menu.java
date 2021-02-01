package menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.common.collect.ImmutableList;

import files.Benchmark;
import files.FileUtils;
import graph.BFAnetwork.BFANetwork;
import graph.bfa.BFA;

public class Menu {
    private boolean exit;
    private BFANetwork bfaNetwork;
    private List<BFA> bfas;
    private FileUtils fileUtils;

    private static final String OPTION1 = "1) Create a BFA";
    private static final String OPTION2 = "2) Create a new BFA Network";
    private static final String OPTION3 = "3) Get results from the BFA Network";
    private static final String OPTION4 = "4) Show past Benchmarks";
    private static final String OPTION0 = "0) Go back";

    private static final ImmutableList<String> OPTIONS = ImmutableList.of(OPTION1, OPTION2, OPTION3, OPTION4, OPTION0);

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
                // e.printStackTrace();
            }
        }
        try {
            bfaNetwork = fileUtils.loadBFANetwork();
        } catch (IOException e) {
            // e.printStackTrace();
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
                String name = "";
                Scanner keyboard = new Scanner(System.in);
                do {
                    Utility.showMessage("\nInsert the name of the bfa: ");
                    name = keyboard.nextLine();
                    if (name.equals(""))
                        Utility.showMessageln("Please write something! ");
                } while (name.equals(""));
                if (isNameNotAvailable(name)) {
                    Utility.showMessageln("You have already created a BFA with this name!");
                    break;
                }
                BFACreator bfaCreator = new BFACreator();
                BFA bfa = bfaCreator.createBFA(name);
                if (bfa != null) {
                    bfa.printDescription();
                    bfas.add(bfa);
                    fileUtils.storeBFA(bfa);
                }
                break;
            case 2:
                if (bfas.isEmpty()) {
                    Utility.showMessageln("You need to create at least one BFA in order to create a BFA Network!");
                    break;
                }
                BFANetworkCreator bfaNetworkCreator = new BFANetworkCreator();
                bfaNetwork = bfaNetworkCreator.createBFANetwork(bfas);
                if (bfaNetwork != null) {
                    if (bfaNetwork.getBFAs().isEmpty()) {
                        Utility.showMessageln("You can't create empty BFA networks!");
                        break;
                    }
                    bfaNetwork.printDescription();
                    fileUtils.storeBFANetwork(bfaNetwork);
                }
                break;
            case 3:
                if (bfaNetwork != null) {
                    Supervisor supervisor = new Supervisor(bfaNetwork, fileUtils);
                    supervisor.runMenu();
                } else {
                    Utility.showMessageln("No BFA Network available, please create one!");
                }
                break;
            case 4:
                List<Benchmark> benchmarks = fileUtils.loadBenchmarks();
                if (benchmarks.isEmpty())
                    System.out.println("No Benchmarks Available");
                benchmarks.forEach(b -> Utility.showMessageln(b.toString()));
                break;
            default:
                Utility.showMessageln("Unknown error has occured.");
        }
    }

    public boolean isNameNotAvailable(String name) {
        return bfas.stream().anyMatch(b -> b.getName().equals(name));
    }
}
