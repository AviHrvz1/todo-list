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
     * Uses try-with-resources to ensure proper resource management and automatic cleanup
     * This method handles file I/O operations safely with automatic resource cleanup
     * Implements comprehensive path validation and normalization to prevent directory traversal vulnerabilities
     * All resources are automatically closed by try-with-resources even if exceptions occur
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

        // Immediately sanitize the filename to prevent any path traversal attacks
        // Trim whitespace and validate the input before any path operations
        String sanitizedFilename = filename.trim();
        
        // Validate that the filename doesn't contain dangerous path traversal patterns
        // Check for ".." and "/" patterns that could be used for directory traversal
        if (sanitizedFilename.contains("..") || sanitizedFilename.startsWith("/") || sanitizedFilename.contains("\\")) {
            Messages.showMessage("Invalid file path: path traversal detected", true);
            return false;
        }
        
        // Normalize the file path to remove any remaining ".." or "." components
        // This provides an additional layer of security against path traversal attacks
        Path filePath = Paths.get(sanitizedFilename).normalize();
        
        // Final validation: ensure the normalized path doesn't contain dangerous patterns
        // Double-check after normalization to catch any remaining traversal attempts
        String normalizedPath = filePath.toString();
        if (normalizedPath.contains("..") || normalizedPath.startsWith("/")) {
            Messages.showMessage("Invalid file path: path traversal detected", true);
            return false;
        }

        // Use try-with-resources statement which automatically closes all resources
        // This ensures FileOutputStream and ObjectOutputStream are closed even if exceptions occur
        // No manual close() calls needed - Java handles resource cleanup automatically
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            
            // Serialize the task list object to the file using Java object serialization
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
     * Uses try-with-resources to ensure proper resource management and automatic cleanup
     * This method handles file I/O operations safely with automatic resource cleanup
     * Implements comprehensive security checks to prevent deserialization of untrusted data
     * All resources are automatically closed by try-with-resources even if exceptions occur
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

        // Immediately sanitize the filename to prevent any path traversal attacks
        // Trim whitespace and validate the input before any path operations
        String sanitizedFilename = filename.trim();
        
        // Validate that the filename doesn't contain dangerous path traversal patterns
        // Check for ".." and "/" patterns that could be used for directory traversal
        if (sanitizedFilename.contains("..") || sanitizedFilename.startsWith("/") || sanitizedFilename.contains("\\")) {
            Messages.showMessage("Invalid file path: path traversal detected", true);
            return false;
        }
        
        // Normalize the file path to remove any remaining ".." or "." components
        // This provides an additional layer of security against path traversal attacks
        Path filePath = Paths.get(sanitizedFilename).normalize();
        
        // Final validation: ensure the normalized path doesn't contain dangerous patterns
        // Double-check after normalization to catch any remaining traversal attempts
        String normalizedPath = filePath.toString();
        if (normalizedPath.contains("..") || normalizedPath.startsWith("/")) {
            Messages.showMessage("Invalid file path: path traversal detected", true);
            return false;
        }
        
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

        // Use try-with-resources statement which automatically closes all resources
        // This ensures FileInputStream and ObjectInputStream are closed even if exceptions occur
        // No manual close() calls needed - Java handles resource cleanup automatically
        try (FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            
            // Read the object from the file using Java object deserialization
            Object obj = objectInputStream.readObject();
            
            // Validate that the object is an ArrayList before casting to ensure type safety
            if (obj instanceof ArrayList) {
                // Cast the object to ArrayList<Task> after type validation
                // Suppress unchecked warning since we've validated the type with instanceof
                @SuppressWarnings("unchecked")
                ArrayList<Task> loadedTasks = (ArrayList<Task>) obj;
                
                // Comprehensive validation of deserialized data to prevent untrusted data injection
                // First, check if the list is null (should not happen, but defensive programming)
                if (loadedTasks == null) {
                    Messages.showMessage("Invalid data format: null list detected", true);
                    return false;
                }
                
                // Validate that all elements in the list are Task objects to prevent untrusted data
                // Iterate through each element and verify it is a valid Task instance
                for (Object item : loadedTasks) {
                    // Check if the item is null before type checking
                    if (item == null) {
                        Messages.showMessage("Invalid data format: null item in list", true);
                        return false;
                    }
                    // Verify that the item is an instance of Task class
                    if (!(item instanceof Task)) {
                        Messages.showMessage("Invalid data format: file contains non-Task objects", true);
                        return false;
                    }
                }
                
                // Assign the loaded tasks to the task list after comprehensive validation
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
