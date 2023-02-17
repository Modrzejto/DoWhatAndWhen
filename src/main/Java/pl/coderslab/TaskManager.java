package pl.coderslab;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.*;
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

        System.out.println("Task name:");
        String newValue = scanner.nextLine();

        newTask[0] = newValue.trim().replaceAll(",", "");

        System.out.println("Is your task  important: true/false");
        newValue = scanner.nextLine();
        if (newValue.equalsIgnoreCase("true")) {
            newTask[1] = "Important";
        } else {
            newTask[1] = "Not important";
        }

        System.out.println("Task due date:");
        newValue = scanner.nextLine();

        newTask[2] = newValue.trim().replaceAll(",", "");

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

        System.out.println("Input the number of a task that you want to remove: (or type '-1' to return back to menu)");
        tasksNum = scanner.nextInt() - 1;

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
