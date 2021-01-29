package menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.common.collect.ImmutableList;

import graph.BFAnetwork.BFANetwork;
import graph.BFAnetwork.BFANetworkBuilder;
import graph.BFAnetwork.Link;
import graph.bfa.BFA;

public class BFANetworkCreator {
    private List<Link> links;
    private List<BFA> bfas;
    private BFANetwork bfaNetwork;
    private BFANetworkBuilder bfaNetworkBuilder;
    private boolean exit;

    private static final String OPTION1 = "1) Create a Link";
    private static final String OPTION2 = "2) Put a Link between BFAs";
    private static final String OPTION3 = "3) Create the BFANetwork";
    private static final String OPTION0 = "0) Cancel";

    public static final ImmutableList<String> OPTIONS = ImmutableList.of(OPTION1, OPTION2, OPTION3, OPTION0);

    public BFANetwork createBFANetwork(List<BFA> bfas) {
        bfaNetworkBuilder = new BFANetworkBuilder();
        links = new ArrayList<>();
        this.bfas = bfas;
        while (!exit) {
            Utility.printMenu(OPTIONS);
            int choice = Utility.getMenuChoice(OPTIONS.size() - 1);
            performAction(choice);
        }
        return bfaNetwork;

    }

    private void performAction(int choice) {
        switch (choice) {
            case 0:
                exit = true;
                break;
            case 1:
                createLink();
                break;
            case 2:
                putLink();
                break;
            case 3:
                try {
                    bfaNetwork = bfaNetworkBuilder.build();
                    exit = true;
                } catch (Exception e) {
                    Utility.showMessageln("An error has occured");
                }
                break;
            default:
                Utility.showMessage("Unknown error has occured.");
        }
    }

    public void createLink() {
        Link link;
        boolean out;
        String name = "";
        Scanner keyboard = new Scanner(System.in);
        do {
            out = false;
            Utility.showMessage("\nInsert the name of the link: ");
            name = keyboard.nextLine();
            if (name.equals("")) {
                Utility.showMessageln("Please write something!");
                out = true;
            } else if (checkIfExistsLinkWithSameName(name)) {
                Utility.showMessageln("You have already created a link with this name! ");
                out = true;
            }
        } while (out);

        link = new Link(name);
        links.add(link);
    }

    public boolean checkIfExistsLinkWithSameName(String name) {
        return links.stream().anyMatch(s -> s.getName().equals(name));
    }

    public Link selectLink() {
        Utility.showMessageln("\nSelect the number of the link: ");

        for (int i = 0; i < links.size(); i++) {
            Utility.showMessageln(i + ") " + links.get(i).getName());
        }
        int choice = Utility.getMenuChoice(links.size() - 1);
        Link link = links.get(choice);
        links.remove(choice);
        return link;
    }

    public BFA selectBFA() {
        for (int i = 0; i < bfas.size(); i++) {
            Utility.showMessageln(i + ") " + bfas.get(i).getName());
        }
        int choice = Utility.getMenuChoice(bfas.size() - 1);
        return bfas.get(choice);

    }

    public void putLink() {
        Link link = selectLink();
        Utility.showMessageln("\nSelect the number of the source bfa: ");
        BFA bfaU = selectBFA();
        BFA bfaV;
        do {
            Utility.showMessageln("\nSelect the number of the destination bfa: ");
            bfaV = selectBFA();
            if (bfaV.getName().equals(bfaU.getName())) {
                Utility.showMessageln("\nYou can't select the source bfa as destination!");
            }
        } while (bfaV.getName().equals(bfaU.getName()));

        bfaNetworkBuilder.putLink(bfaU, bfaV, link);
    }

}