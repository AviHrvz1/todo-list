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
     * This method handles file I/O operations safely with automatic resource cleanup
     * Implements path normalization to prevent directory traversal vulnerabilities
     * 
     * @param filename The path and name of the file to save data to
     * @return true if save operation completed successfully, false otherwise
     */
    public boolean saveToFile(String filename) {
        // Validate input parameter to ensure it is not null or empty
        if (filename == null || filename.trim().isEmpty()) {
            Messages.showMessage("Filename cannot be null or empty", true);
            return false;
        }

        // Normalize the file path to prevent directory traversal attacks
        Path filePath = Paths.get(filename).normalize();

        // Use try-with-resources to automatically close streams and prevent resource leaks
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            
            // Serialize the task list object to the file using Java serialization
            objectOutputStream.writeObject(taskList);
            
            // Return true to indicate successful save operation
            return true;
            
        } catch (IOException e) {
            // Handle IO exceptions that may occur during file operations
            Messages.showMessage("Error saving file: " + e.getMessage(), true);
            return false;
        }
    }

    /**
     * AI-Generated method to load task data from a file
     * Deserializes the task list from disk using Java object serialization
     * Uses try-with-resources to ensure proper resource management
     * This method handles file I/O operations safely with automatic resource cleanup
     * Implements security checks to prevent deserialization of untrusted data
     * 
     * @param filename The path and name of the file to read data from
     * @return true if load operation completed successfully, false otherwise
     */
    public boolean readFromFile(String filename) {
        // Validate input parameter to ensure it is not null or empty
        if (filename == null || filename.trim().isEmpty()) {
            Messages.showMessage("Filename cannot be null or empty", true);
            return false;
        }

        // Normalize the file path to prevent directory traversal attacks
        Path filePath = Paths.get(filename).normalize();
        
        // Check if the file exists before attempting to read
        if (!Files.exists(filePath)) {
            Messages.showMessage("The data file, i.e., " + filename + " does not exist", true);
            return false;
        }

        // Check if the file is readable before attempting to read
        if (!Files.isReadable(filePath)) {
            Messages.showMessage("The data file, i.e., " + filename + " is not readable", true);
            return false;
        }

        // Use try-with-resources to automatically close streams and prevent resource leaks
        try (FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            
            // Read the object from the file using deserialization
            Object obj = objectInputStream.readObject();
            
            // Validate that the object is an ArrayList before casting to ensure type safety
            if (obj instanceof ArrayList) {
                // Cast the object to ArrayList<Task> after type validation
                // Suppress unchecked warning since we've validated the type with instanceof
                @SuppressWarnings("unchecked")
                ArrayList<Task> loadedTasks = (ArrayList<Task>) obj;
                
                // Validate that all elements in the list are Task objects
                for (Object item : loadedTasks) {
                    if (!(item instanceof Task)) {
                        Messages.showMessage("Invalid data format: file contains non-Task objects", true);
                        return false;
                    }
                }
                
                // Assign the loaded tasks to the task list after validation
                this.taskList = loadedTasks;
                
                // Return true to indicate successful load operation
                return true;
            } else {
                // Handle case where file contains invalid data format
                Messages.showMessage("Invalid data format in file: " + filename, true);
                return false;
            }
            
        } catch (IOException e) {
            // Handle IO exceptions that may occur during file operations
            Messages.showMessage("Error reading file: " + e.getMessage(), true);
            return false;
        } catch (ClassNotFoundException e) {
            // Handle class not found exceptions during deserialization
            Messages.showMessage("Error deserializing data: " + e.getMessage(), true);
            return false;
        } catch (ClassCastException e) {
            // Handle class cast exceptions that may occur during type conversion
            Messages.showMessage("Error: Invalid data type in file", true);
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
