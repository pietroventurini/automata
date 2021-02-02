package menu;

import java.util.List;
import java.util.Scanner;

import com.google.common.collect.ImmutableList;

import files.FileUtils;

public class Main {
    private static final String OPTION1 = "1) Open an existing project";
    private static final String OPTION2 = "2) Create a new Project";
    private static final String OPTION0 = "0) Exit";

    private static final ImmutableList<String> OPTIONS = ImmutableList.of(OPTION1, OPTION2, OPTION0);

    private boolean exit;

    public static void main(String[] args) {
        Main main = new Main();
        main.runMenu();
    }

    public void runMenu() {
        while (!exit) {
            Utility.printMenu(OPTIONS);
            int choice = Utility.getMenuChoice(OPTIONS.size() - 1);
            performAction(choice);
        }
    }

    private void performAction(int choice) {
        switch (choice) {
            case 0:
                Utility.showMessageln("Thank you for using our application.");
                exit = true;
                break;
            case 1:
                if (FileUtils.getProjectsList().isEmpty()) {
                    Utility.showMessage("There isn't any project available!");
                    break;
                }
                openExistingProject();
                break;
            case 2:
                createNewProject();
                break;
            default:
                Utility.showMessageln("Unknown error has occured.");
        }
    }

    private void openExistingProject() {
        List<String> projects = FileUtils.getProjectsList();
        for (int i = 0; i < projects.size(); i++) {
            Utility.showMessageln(i + ") " + projects.get(i));
        }
        int choice = Utility.getMenuChoice(projects.size() - 1);
        Menu menu = new Menu(projects.get(choice));
        menu.runMenu();
    }

    private void createNewProject() {
        List<String> projects = FileUtils.getProjectsList();
        Scanner keyboard = new Scanner(System.in);
        boolean out;
        String name;
        do {
            out = false;
            Utility.showMessage("\nInsert the name of the project: ");
            name = keyboard.nextLine();
            if (name.equals("")) {
                out = true;
                Utility.showMessageln("Please write something! ");
            }
        } while (out);
        if (projects.contains(name)) {
            Utility.showMessageln("There is already a project with this name! ");
            return;
        }
        Menu menu = new Menu(name);
        menu.runMenu();
    }

}
