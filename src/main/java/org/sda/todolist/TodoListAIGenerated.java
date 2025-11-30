package org.sda.todolist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * AI-Generated version of TodoList with file persistence methods
 * This class provides functionality to save and load task data from disk
 * 
 * @author AI Assistant
 * @version 2.0
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
     * Uses try-with-resources to ensure proper resource management
     * 
     * @param filename The path and name of the file to save data to
     * @return true if save operation completed successfully, false otherwise
     */
    public boolean saveToFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            Messages.showMessage("Filename cannot be null or empty", true);
            return false;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            
            objectOutputStream.writeObject(taskList);
            return true;
            
        } catch (IOException e) {
            Messages.showMessage("Error saving file: " + e.getMessage(), true);
            return false;
        }
    }

    /**
     * AI-Generated method to load task data from a file
     * Deserializes the task list from disk using Java object serialization
     * Uses try-with-resources to ensure proper resource management
     * 
     * @param filename The path and name of the file to read data from
     * @return true if load operation completed successfully, false otherwise
     */
    public boolean readFromFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            Messages.showMessage("Filename cannot be null or empty", true);
            return false;
        }

        Path filePath = Paths.get(filename);
        if (!Files.exists(filePath)) {
            Messages.showMessage("The data file, i.e., " + filename + " does not exist", true);
            return false;
        }

        if (!Files.isReadable(filePath)) {
            Messages.showMessage("The data file, i.e., " + filename + " is not readable", true);
            return false;
        }

        try (FileInputStream fileInputStream = new FileInputStream(filename);
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            
            Object obj = objectInputStream.readObject();
            if (obj instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<Task> loadedTasks = (ArrayList<Task>) obj;
                this.taskList = loadedTasks;
                return true;
            } else {
                Messages.showMessage("Invalid data format in file: " + filename, true);
                return false;
            }
            
        } catch (IOException e) {
            Messages.showMessage("Error reading file: " + e.getMessage(), true);
            return false;
        } catch (ClassNotFoundException e) {
            Messages.showMessage("Error deserializing data: " + e.getMessage(), true);
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
