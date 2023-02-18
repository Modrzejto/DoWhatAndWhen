package pl.coderslab;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                case "add" -> {
                    addTask();
                    endLoop = true;
                }
                case "remove" -> {
                    removeTask();
                    endLoop = true;
                }
                case "list" -> {
                    listTasks(true);
                    endLoop = true;
                }
                case "exit" -> {
                    System.err.println("Bye bye");
                    System.exit(22);
                }
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

    public static void listTasks(boolean b) {
        getTasks();
        int rowCounter = 1;

        for (List<String> line : tasksList) {
            System.out.print(rowCounter + ".: ");
            for (String value : line) {
                System.out.print(value.replaceAll( "\"", "") + "\t");
            }
            System.out.print("\n");
            rowCounter++;
        }

        if (b) displayOptions();
    }

    public static void addTask() {
        scanner = new Scanner(System.in);
        String[] newTask = new String[3];
        String newValue;

        System.out.println("Task name:");
        newValue = scanner.nextLine();

        if (newValue.isEmpty() || newValue.isBlank()) {
            System.err.println("Task name cannot be empty. Try again");
            displayOptions();
        }

        newTask[0] = newValue.trim().replaceAll(",", "").replaceAll("\n", "");

        System.out.println("Is your task  important: true/false");
        newValue = scanner.next();

        if (newValue.equalsIgnoreCase("true")) {
            newTask[1] = "Important";
        } else if (newValue.equalsIgnoreCase("false")) {
            newTask[1] = "Not important";
        } else {
            System.err.println("Wrong input try again.");
            displayOptions();
        }

        System.out.println("Task due date: (format: RRRR-MM-DD)");
        Pattern datePattern = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]");
        newValue = scanner.next();
        Matcher dateMatch = datePattern.matcher(newValue);
        if (dateMatch.find()) {
            newTask[2] = newValue.trim().replaceAll(",", "");
        } else {
            System.err.println("Incorrect date format");
            displayOptions();
        }

        List<String[]> exportData = new ArrayList<>();
        exportData.add(newTask);

        try (FileWriter fW = new FileWriter(tasksFile, true)) {
            try (CSVWriter csvWriter = new CSVWriter(fW)) {
                csvWriter.writeAll(exportData);
            }
        } catch (IOException e) {
            System.err.println("IO error (probably FNF)");
        }

        displayOptions();
    }

    public static void removeTask() {
        listTasks(false);
        scanner = new Scanner(System.in);
        int tasksNum = -2;

        System.out.println("Input the number of a task that you want to remove: (or type '-1' to return back to menu)");
        while (!scanner.hasNextInt()) {
            try {
                tasksNum = scanner.nextInt() - 1;
            } catch (InputMismatchException e) {
                System.err.println("Please input a number.");
                scanner.next();
            }
        }

        if (tasksNum >= 0) {
            try (CSVReader csvReader = new CSVReader(new FileReader(tasksFile))) {
                List<String[]> tasks = csvReader.readAll();
                tasks.remove(tasksNum);

                try (FileWriter fileWriter = new FileWriter(tasksFile)) {
                    try (CSVWriter csvWriter = new CSVWriter(fileWriter)) {
                        csvWriter.writeAll(tasks);
                    }
                } catch (IOException e) {
                    System.err.println("Error overwriting file");
                }
            } catch (IOException e) {
                System.err.println("IO error (reading file)");
            } catch (CsvException e) {
                System.err.println("CSV exception (reading file)");
            }
        } else {
            displayOptions();
        }

        displayOptions();
    }
}
