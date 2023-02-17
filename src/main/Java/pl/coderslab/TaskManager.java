package pl.coderslab;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TaskManager {

    static final String[] options = {ConsoleColors.WHITE + "add", "remove", "list", "exit"};
    static File tasksFile = new File("tasks.csv");
    static List<List<String>> tasksList = new ArrayList<>();
    static Scanner scanner;

    public static void main(String[] args) {
        displayOptions();
    }

    public static void displayOptions() {
        scanner = new Scanner(System.in);
        boolean endLoop = false;

        System.out.println(ConsoleColors.BLUE + "Please select an option:");
        for (String s : options) {
            System.out.println(s);
        }

        while (!endLoop) {
            switch (scanner.next()) {
                case "add" ->
                    //addTask();
                        endLoop = true;
                case "remove" ->
                    //removeTask();
                        endLoop = true;
                case "list" -> {
                    listTasks();
                    endLoop = true;
                }
                case "exit" -> System.exit(22);
                default -> System.out.println("Please select a valid option");
            }
        }
    }

    public static void getTasks() {
        try {
            scanner = new Scanner(tasksFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");
                //adds parsed line from tasks.csv to values[]
                tasksList.add(Arrays.asList(values));
                //then moves values[] to tasksList list
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } finally {
            scanner.close();
        }
    }

    public static void listTasks() {
        getTasks();

        for (List<String> line : tasksList) {
            for (String value : line) {
                System.out.println(value.trim());
            }
        }
    }
}
