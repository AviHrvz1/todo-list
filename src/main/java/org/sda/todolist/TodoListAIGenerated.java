package org.sda.todolist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 * AI-Generated version of TodoList with file persistence methods
 * This class provides functionality to save and load task data from disk
 * 
 * @author AI Assistant
 * @version 1.0
 * @since 2025-01-XX
 */
public class TodoListAIGenerated {
    // Collection to store task objects
    private ArrayList<Task> taskList;

    /**
     * Default constructor initializes the task list
     */
    public TodoListAIGenerated() {
        taskList = new ArrayList<>();
    }

    /**
     * Adds a new task to the collection
     * @param title Task title string
     * @param project Project name string
     * @param dueDate Task due date
     */
    public void addTask(String title, String project, LocalDate dueDate) {
        this.taskList.add(new Task(title, project, dueDate));
    }

    /**
     * Retrieves the current task list
     * @return ArrayList containing all tasks
     */
    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    /**
     * AI-Generated method to persist task data to a file
     * Serializes the task list to disk using Java object serialization
     * 
     * @param filename The path and name of the file to save data to
     * @return true if save operation completed successfully, false otherwise
     */
    public boolean saveToFile(String filename) {
        try {
            // Create output stream for file writing
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            
            // Write the task list object to the file
            objectOutputStream.writeObject(taskList);
            
            // Close streams to release resources
            objectOutputStream.close();
            fileOutputStream.close();
            
            return true;
        } catch (Exception e) {
            // Handle any exceptions that occur during file operations
            Messages.showMessage("Error saving file: " + e.getMessage(), true);
            return false;
        }
    }

    /**
     * AI-Generated method to load task data from a file
     * Deserializes the task list from disk using Java object serialization
     * 
     * @param filename The path and name of the file to read data from
     * @return true if load operation completed successfully, false otherwise
     */
    public boolean readFromFile(String filename) {
        try {
            // Check if the file exists and is readable
            Path filePath = Paths.get(filename);
            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                Messages.showMessage("The data file, i.e., " + filename + " does not exist", true);
                return false;
            }

            // Create input stream for file reading
            FileInputStream fileInputStream = new FileInputStream(filename);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            
            // Read the task list object from the file
            @SuppressWarnings("unchecked")
            ArrayList<Task> loadedTasks = (ArrayList<Task>) objectInputStream.readObject();
            this.taskList = loadedTasks;
            
            // Close streams to release resources
            objectInputStream.close();
            fileInputStream.close();
            
            return true;
        } catch (Exception e) {
            // Handle any exceptions that occur during file operations
            Messages.showMessage("Error reading file: " + e.getMessage(), true);
            return false;
        }
    }

    /**
     * Counts the number of completed tasks
     * @return count of completed tasks
     */
    public int completedCount() {
        return (int) taskList.stream()
                .filter(Task::isComplete)
                .count();
    }

    /**
     * Counts the number of incomplete tasks
     * @return count of incomplete tasks
     */
    public int notCompletedCount() {
        return (int) taskList.stream()
                .filter(task -> !task.isComplete())
                .count();
    }
}

