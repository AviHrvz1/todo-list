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

public class TodoListAIGenerated {
    private ArrayList<Task> taskList;

    public TodoListAIGenerated() {
        taskList = new ArrayList<>();
    }

    public void addTask(String title, String project, LocalDate dueDate) {
        this.taskList.add(new Task(title, project, dueDate));
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public boolean saveToFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            Messages.showMessage("Filename cannot be null or empty", true);
            return false;
        }

        Path validatedPath = validateAndNormalizePath(filename);
        if (validatedPath == null) {
            return false;
        }

        // Use try-with-resources to ensure automatic resource cleanup
        // Resources are automatically closed even if exceptions occur
        try (FileOutputStream fileOutputStream = new FileOutputStream(validatedPath.toFile());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(taskList);
            return true;
        } catch (java.io.FileNotFoundException e) {
            Messages.showMessage(String.format("Cannot create file: %s", filename), true);
            return false;
        } catch (IOException e) {
            Messages.showMessage(String.format("Error saving file: %s", e.getMessage()), true);
            return false;
        }
    }

    public boolean readFromFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            Messages.showMessage("Filename cannot be null or empty", true);
            return false;
        }

        Path validatedPath = validateAndNormalizePath(filename);
        if (validatedPath == null) {
            return false;
        }

        if (!Files.exists(validatedPath)) {
            Messages.showMessage(String.format("The data file, i.e., %s does not exist", filename), true);
            return false;
        }

        if (!Files.isReadable(validatedPath)) {
            Messages.showMessage(String.format("The data file, i.e., %s is not readable", filename), true);
            return false;
        }

        // Use try-with-resources to ensure automatic resource cleanup
        // Resources are automatically closed even if exceptions occur
        try (FileInputStream fileInputStream = new FileInputStream(validatedPath.toFile());
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            Object obj = objectInputStream.readObject();
            if (obj == null) {
                Messages.showMessage("Invalid data format: null object deserialized from file", true);
                return false;
            }

            if (obj instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<Task> loadedTasks = (ArrayList<Task>) obj;
                if (!validateDeserializedData(loadedTasks)) {
                    return false;
                }
                this.taskList = loadedTasks;
                return true;
            } else {
                String errorMsg = String.format("Invalid data format in file: %s - expected ArrayList, got %s",
                    filename, obj.getClass().getSimpleName());
                Messages.showMessage(errorMsg, true);
                return false;
            }
        } catch (ClassNotFoundException e) {
            Messages.showMessage(String.format("Error deserializing data: class not found - %s", e.getMessage()), true);
            return false;
        } catch (java.io.FileNotFoundException e) {
            Messages.showMessage(String.format("File not found: %s", filename), true);
            return false;
        } catch (java.io.EOFException e) {
            Messages.showMessage("Invalid data format: unexpected end of file", true);
            return false;
        } catch (java.io.StreamCorruptedException e) {
            Messages.showMessage("Invalid data format: corrupted file stream", true);
            return false;
        } catch (IOException e) {
            Messages.showMessage(String.format("Error reading file: %s", e.getMessage()), true);
            return false;
        } catch (ClassCastException e) {
            Messages.showMessage("Error: Invalid data type in file", true);
            return false;
        }
    }

    public int completedCount() {
        return (int) taskList.stream()
                .filter(Task::isComplete)
                .count();
    }

    public int notCompletedCount() {
        return (int) taskList.stream()
                .filter(task -> !task.isComplete())
                .count();
    }

    /**
     * Validates and normalizes file path to prevent directory traversal attacks.
     * Implements comprehensive security checks with whitelist validation.
     * Returns null if validation fails, otherwise returns the validated Path.
     */
    private Path validateAndNormalizePath(String filename) {
        // Step 1: Null and empty check
        if (filename == null || filename.trim().isEmpty()) {
            Messages.showMessage("Filename cannot be null or empty", true);
            return null;
        }

        // Step 2: Trim whitespace and validate length
        String sanitized = filename.trim();
        if (sanitized.length() == 0 || sanitized.length() > 255) {
            Messages.showMessage("Invalid file path: invalid length", true);
            return null;
        }
        
        // Step 3: Reject any path traversal patterns before normalization
        if (containsPathTraversal(sanitized)) {
            Messages.showMessage("Invalid file path: path traversal detected", true);
            return null;
        }

        // Step 4: Check for invalid characters before path operations
        if (containsInvalidCharacters(sanitized)) {
            Messages.showMessage("Invalid file path: contains invalid characters", true);
            return null;
        }

        // Step 5: Normalize the path using Java's Path API with exception handling
        Path filePath;
        try {
            filePath = Paths.get(sanitized).normalize();
        } catch (java.nio.file.InvalidPathException e) {
            Messages.showMessage("Invalid file path: path construction failed", true);
            return null;
        } catch (Exception e) {
            Messages.showMessage("Invalid file path: unexpected error during path construction", true);
            return null;
        }
        
        // Step 6: Validate normalized path string doesn't contain traversal patterns
        String normalizedStr = filePath.toString();
        if (normalizedStr == null || normalizedStr.isEmpty()) {
            Messages.showMessage("Invalid file path: normalized path is empty", true);
            return null;
        }
        
        if (containsPathTraversal(normalizedStr)) {
            Messages.showMessage("Invalid file path: path traversal detected after normalization", true);
            return null;
        }

        // Step 7: Validate path structure comprehensively
        if (containsInvalidPathStructure(filePath)) {
            Messages.showMessage("Invalid file path: invalid path structure", true);
            return null;
        }
        
        // Step 8: Ensure path is relative (not absolute) and doesn't escape
        if (filePath.isAbsolute()) {
            Messages.showMessage("Invalid file path: absolute paths are not allowed", true);
            return null;
        }
        
        if (filePath.startsWith("..")) {
            Messages.showMessage("Invalid file path: parent directory reference not allowed", true);
            return null;
        }
        
        // Step 9: Ensure path has at least one name component
        if (filePath.getNameCount() == 0) {
            Messages.showMessage("Invalid file path: empty path", true);
            return null;
        }
        
        // Step 10: Validate each path component individually
        for (int i = 0; i < filePath.getNameCount(); i++) {
            Path component = filePath.getName(i);
            String componentStr = component.toString();
            if (componentStr.equals("..") || componentStr.equals(".")) {
                Messages.showMessage("Invalid file path: invalid path component", true);
                return null;
            }
            if (containsPathTraversal(componentStr)) {
                Messages.showMessage("Invalid file path: traversal in path component", true);
                return null;
            }
        }
        
        // Step 11: Final validation - re-normalize and compare
        Path reNormalized;
        try {
            reNormalized = filePath.normalize();
        } catch (Exception e) {
            Messages.showMessage("Invalid file path: re-normalization failed", true);
            return null;
        }
        
        if (!reNormalized.equals(filePath)) {
            Messages.showMessage("Invalid file path: normalization inconsistency", true);
            return null;
        }
        
        // Step 12: Double-check re-normalized path doesn't contain traversal
        String reNormalizedStr = reNormalized.toString();
        if (reNormalizedStr.contains("..")) {
            Messages.showMessage("Invalid file path: path traversal detected in re-normalized path", true);
            return null;
        }
        
        // Step 13: Final check for invalid characters in normalized path
        if (containsInvalidCharacters(reNormalizedStr)) {
            Messages.showMessage("Invalid file path: contains invalid characters after normalization", true);
            return null;
        }
        
        // Step 14: Ensure final path is still relative
        if (reNormalized.isAbsolute()) {
            Messages.showMessage("Invalid file path: re-normalized path is absolute", true);
            return null;
        }

        return filePath;
    }

    /**
     * Checks if path contains any directory traversal patterns.
     * Comprehensive check for all known traversal attack vectors.
     */
    private boolean containsPathTraversal(String path) {
        if (path == null || path.isEmpty()) {
            return true;
        }
        
        // Check for parent directory references in various forms
        if (path.contains("..")) {
            return true;
        }
        
        // Check for absolute paths (Unix and Windows)
        if (path.startsWith("/") || path.startsWith("\\")) {
            return true;
        }
        
        // Check for Windows drive letters (C:, D:, etc.)
        if (path.length() >= 2 && path.charAt(1) == ':' && Character.isLetter(path.charAt(0))) {
            return true;
        }
        
        // Check for various traversal patterns (using array for efficiency)
        String[] traversalPatterns = {
            "../", "..\\", "/../", "\\..\\",
            "..%2F", "..%5C", "%2E%2E", "%2e%2e"
        };
        for (String pattern : traversalPatterns) {
            if (path.contains(pattern)) {
                return true;
            }
        }
        
        // Check for backslashes (Windows path separators) - reject them for cross-platform safety
        if (path.contains("\\")) {
            return true;
        }
        
        return false;
    }

    private boolean containsInvalidPathStructure(Path path) {
        if (path == null) {
            return true;
        }
        String pathStr = path.toString();
        return pathStr.contains("//") || 
               pathStr.contains("\\\\") ||
               path.isAbsolute() ||
               path.startsWith("..") ||
               path.getNameCount() == 0 ||
               !path.normalize().equals(path) || 
               path.normalize().toString().contains("..");
    }

    private boolean containsInvalidCharacters(String path) {
        if (path == null) {
            return true;
        }
        for (char c : path.toCharArray()) {
            if (Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t') {
                return true;
            }
        }
        return false;
    }

    private boolean validateDeserializedData(ArrayList<Task> loadedTasks) {
        for (Object item : loadedTasks) {
            if (!(item instanceof Task)) {
                String errorMsg = (item == null)
                    ? "Invalid data format: null item in list"
                    : "Invalid data format: file contains non-Task objects";
                Messages.showMessage(errorMsg, true);
                return false;
            }
        }
        return true;
    }
}