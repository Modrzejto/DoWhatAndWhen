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
    static int tasksNumCounter;

    public static void main(String[] args) {
        createTaskList();
        displayOptions();
    }

    public static void createTaskList() {
        List<String[]> headers = new ArrayList<>();
        headers.add(new String[]{"Task name", "Importance", "Due"});

        try {
            if (tasksFile.createNewFile()) {
                try (CSVWriter writer = new CSVWriter(new FileWriter(tasksFile))) {
                    writer.writeAll(headers);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException error");
        }
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
        tasksList.clear();
        tasksNumCounter = 0;

        try {
            scanner = new Scanner(tasksFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(",");
                //adds parsed line from tasks.csv to values[]
                tasksList.add(Arrays.asList(values));
                //then moves values[] to tasksList list
                tasksNumCounter++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found");
        } finally {
            scanner.close();
        }
    }

    public static void listTasks(boolean displayOptions) {
        getTasks();
        int rowCounter = 0;

        for (List<String> line : tasksList) {
            if (rowCounter >= 1) {
                System.out.print(rowCounter + ".: ");
            }
            for (String value : line) {
                System.out.print(value.replaceAll("\"", "") + "\t");
            }
            System.out.print("\n");
            rowCounter++;
        }

        if (displayOptions) {
            displayOptions();
        }
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
        int tasksNum;

        System.out.println("Input the number of a task that you want to remove: (or type any number <= 0 to return back to menu)");
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.println("Please input a number.");
        }
        tasksNum = scanner.nextInt();

        if (tasksNum > 0) {
            if (tasksNum <= tasksNumCounter - 1) {
                try (CSVReader reader = new CSVReader(new FileReader(tasksFile))) {
                    List<String[]> newTasks = reader.readAll();
                    newTasks.remove(tasksNum);

                    CSVWriter csvWriter = new CSVWriter(new FileWriter(tasksFile));
                    csvWriter.writeAll(newTasks);
                    csvWriter.close();
                    System.out.println("Removed successfully");
                    displayOptions();
                } catch (IOException | CsvException e) {
                    System.err.println("IOError ");
                    e.printStackTrace();
                }
            } else {
                System.err.println("No task found at given number");
                displayOptions();
            }
        } else {
            displayOptions();
        }
    }
}