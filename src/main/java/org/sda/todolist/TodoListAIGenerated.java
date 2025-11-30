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

        // Comprehensive path validation to prevent directory traversal attacks
        // Step 1: Immediately sanitize the filename by trimming whitespace
        String sanitizedFilename = filename.trim();
        
        // Step 2: Validate that the filename doesn't contain dangerous path traversal patterns
        // Check for ".." (parent directory), "/" (absolute path), and "\" (Windows path) patterns
        // This prevents directory traversal attacks by rejecting any path manipulation attempts
        if (sanitizedFilename.contains("..") || sanitizedFilename.startsWith("/") || sanitizedFilename.contains("\\")) {
            Messages.showMessage("Invalid file path: path traversal detected", true);
            return false;
        }
        
        // Step 3: Normalize the file path to remove any remaining ".." or "." components
        // This provides an additional layer of security against path traversal attacks
        // Paths.get().normalize() will resolve ".." and "." but we validate again after
        Path filePath = Paths.get(sanitizedFilename).normalize();
        
        // Step 4: Final validation after normalization to catch any remaining traversal attempts
        // Double-check the normalized path doesn't contain dangerous patterns
        // This ensures that even if normalization didn't catch everything, we reject unsafe paths
        String normalizedPathStr = filePath.toString();
        if (normalizedPathStr.contains("..") || normalizedPathStr.startsWith("/") || 
            normalizedPathStr.startsWith("\\") || filePath.isAbsolute()) {
            Messages.showMessage("Invalid file path: path traversal detected after normalization", true);
            return false;
        }
        
        // Step 5: Additional security check - ensure path doesn't escape current directory
        // Verify the normalized path is relative (not absolute) and doesn't contain parent references
        // Also check for any remaining dangerous patterns after all normalization
        if (filePath.startsWith("..") || filePath.getNameCount() == 0 || 
            filePath.toString().contains("//") || filePath.toString().contains("\\\\")) {
            Messages.showMessage("Invalid file path: invalid path structure", true);
            return false;
        }
        
        // Step 6: Final security check - ensure no hidden characters or encoding issues
        // Check for any non-printable characters or suspicious patterns
        String finalPath = filePath.toString();
        for (char c : finalPath.toCharArray()) {
            if (Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t') {
                Messages.showMessage("Invalid file path: contains invalid characters", true);
                return false;
            }
        }
        
        // Step 7: All path validation complete - safe to use the file path
        // Use try-with-resources statement which automatically closes all resources
        // This ensures FileOutputStream and ObjectOutputStream are closed even if exceptions occur
        // No manual close() calls needed - Java handles resource cleanup automatically
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            
            // Serialize the task list object to the file using Java object serialization
            objectOutputStream.writeObject(taskList);
            
            // Return true to indicate successful save operation
            return true;
            
        } catch (java.io.FileNotFoundException e) {
            // Handle file not found exceptions specifically (should not happen in write, but handle defensively)
            Messages.showMessage(String.format("Cannot create file: %s", filename), true);
            return false;
        } catch (java.io.IOException e) {
            // Handle all IO exceptions that may occur during file operations
            Messages.showMessage(String.format("Error saving file: %s", e.getMessage()), true);
            return false;
        } catch (Exception e) {
            // Catch-all for any other unexpected exceptions
            Messages.showMessage(String.format("Unexpected error saving file: %s", e.getMessage()), true);
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

        // Comprehensive path validation to prevent directory traversal attacks
        // Step 1: Immediately sanitize the filename by trimming whitespace
        String sanitizedFilename = filename.trim();
        
        // Step 2: Validate that the filename doesn't contain dangerous path traversal patterns
        // Check for ".." (parent directory), "/" (absolute path), and "\" (Windows path) patterns
        if (sanitizedFilename.contains("..") || sanitizedFilename.startsWith("/") || sanitizedFilename.contains("\\")) {
            Messages.showMessage("Invalid file path: path traversal detected", true);
            return false;
        }
        
        // Step 3: Normalize the file path to remove any remaining ".." or "." components
        // This provides an additional layer of security against path traversal attacks
        Path filePath = Paths.get(sanitizedFilename).normalize();
        
        // Step 4: Final validation after normalization to catch any remaining traversal attempts
        // Double-check the normalized path doesn't contain dangerous patterns
        // This ensures that even if normalization didn't catch everything, we reject unsafe paths
        String normalizedPathStr = filePath.toString();
        if (normalizedPathStr.contains("..") || normalizedPathStr.startsWith("/") || 
            normalizedPathStr.startsWith("\\") || filePath.isAbsolute()) {
            Messages.showMessage("Invalid file path: path traversal detected after normalization", true);
            return false;
        }
        
        // Step 5: Additional security check - ensure path doesn't escape current directory
        // Verify the normalized path is relative (not absolute) and doesn't contain parent references
        // Also check for any remaining dangerous patterns after all normalization
        if (filePath.startsWith("..") || filePath.getNameCount() == 0 || 
            filePath.toString().contains("//") || filePath.toString().contains("\\\\")) {
            Messages.showMessage("Invalid file path: invalid path structure", true);
            return false;
        }
        
        // Step 6: Final security check - ensure no hidden characters or encoding issues
        // Check for any non-printable characters or suspicious patterns
        String finalPath = filePath.toString();
        for (char c : finalPath.toCharArray()) {
            if (Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t') {
                Messages.showMessage("Invalid file path: contains invalid characters", true);
                return false;
            }
        }
        
        // Step 7: All path validation complete - safe to use the file path
        // Check if the file exists before attempting to read
        if (!Files.exists(filePath)) {
            Messages.showMessage(String.format("The data file, i.e., %s does not exist", filename), true);
            return false;
        }
        
        // Check if the file is readable before attempting to read
        if (!Files.isReadable(filePath)) {
            Messages.showMessage(String.format("The data file, i.e., %s is not readable", filename), true);
            return false;
        }

        // Use try-with-resources statement which automatically closes all resources
        // This ensures FileInputStream and ObjectInputStream are closed even if exceptions occur
        // No manual close() calls needed - Java handles resource cleanup automatically
        try (FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            
            // Read the object from the file using Java object deserialization
            Object obj = objectInputStream.readObject();
            
            // Explicit null check first to prevent NullPointerException
            // This is defensive programming - even though instanceof handles null, explicit check prevents NPE
            // and provides a clearer error message for null objects
            if (obj == null) {
                Messages.showMessage("Invalid data format: null object deserialized from file", true);
                return false;
            }
            
            // Validate that the object is an ArrayList before casting to ensure type safety
            // This prevents ClassCastException by verifying the type before casting
            // Note: obj is guaranteed to be non-null here due to explicit check above
            // Note: instanceof checks the raw type ArrayList, but we need to cast to ArrayList<Task>
            // The cast is necessary because Java's type erasure means we can't check generic types at runtime
            if (obj instanceof ArrayList) {
                // Safe cast after instanceof validation - this will not throw ClassCastException
                // because we've already verified obj is an ArrayList
                // The @SuppressWarnings is necessary because Java cannot verify generic type at runtime
                // We validate the contents are Task objects in the loop below
                @SuppressWarnings("unchecked")
                ArrayList<Task> loadedTasks = (ArrayList<Task>) obj;
                
                // Comprehensive validation of deserialized data to prevent untrusted data injection
                // Note: loadedTasks cannot be null here since obj passed instanceof check
                // Validate that all elements in the list are Task objects to prevent untrusted data
                // Combined null and type check for efficiency - instanceof handles null (returns false)
                for (Object item : loadedTasks) {
                    // Combined validation: check if item is null or not a Task instance
                    // instanceof returns false for null, so this handles both cases efficiently
                    if (!(item instanceof Task)) {
                        String errorMsg = (item == null) 
                            ? "Invalid data format: null item in list"
                            : "Invalid data format: file contains non-Task objects";
                        Messages.showMessage(errorMsg, true);
                        return false;
                    }
                }
                
                // Assign the loaded tasks to the task list after comprehensive validation
                this.taskList = loadedTasks;
                
                // Return true to indicate successful load operation
                return true;
            } else {
                // Handle case where file contains invalid data format (not an ArrayList)
                // Note: obj is guaranteed to be non-null here due to explicit null check above
                // This else block only executes if obj is not null and not an ArrayList
                String errorMsg = String.format("Invalid data format in file: %s - expected ArrayList, got %s", 
                    filename, obj.getClass().getSimpleName());
                Messages.showMessage(errorMsg, true);
                return false;
            }
            
        } catch (ClassNotFoundException e) {
            // Handle class not found exceptions during deserialization
            // This must be caught before IOException since it's a separate exception type
            Messages.showMessage(String.format("Error deserializing data: class not found - %s", e.getMessage()), true);
            return false;
        } catch (java.io.FileNotFoundException e) {
            // Handle file not found exceptions specifically
            Messages.showMessage(String.format("File not found: %s", filename), true);
            return false;
        } catch (java.io.EOFException e) {
            // Handle end of file exceptions during deserialization
            Messages.showMessage("Invalid data format: unexpected end of file", true);
            return false;
        } catch (java.io.StreamCorruptedException e) {
            // Handle stream corruption exceptions
            Messages.showMessage("Invalid data format: corrupted file stream", true);
            return false;
        } catch (IOException e) {
            // Handle all other IO exceptions that may occur during file operations
            Messages.showMessage(String.format("Error reading file: %s", e.getMessage()), true);
            return false;
        } catch (ClassCastException e) {
            // Handle class cast exceptions that may occur during type conversion
            // This should not happen due to instanceof check, but handle defensively
            Messages.showMessage("Error: Invalid data type in file", true);
            return false;
        } catch (Exception e) {
            // Catch-all for any other unexpected exceptions
            Messages.showMessage(String.format("Unexpected error reading file: %s", e.getMessage()), true);
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
