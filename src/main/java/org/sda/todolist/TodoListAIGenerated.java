package org.sda.todolist;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class TodoListAIGenerated {
    private static final Pattern VALID_PATH_PATTERN = Pattern.compile("^[a-zA-Z0-9_.-]+$");
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
        Path validatedPath = validateAndNormalizePath(filename);
        if (validatedPath == null) {
            return false;
        }

        // Use try-with-resources to ensure automatic resource cleanup
        // File will be overwritten if it exists (expected behavior for save operation)
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

    private Path validateAndNormalizePath(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            Messages.showMessage("Filename cannot be null or empty", true);
            return null;
        }

        String sanitized = filename.trim();
        if (!isValidPath(sanitized)) {
            Messages.showMessage("Invalid file path: contains invalid characters", true);
            return null;
        }

        Path filePath;
        try {
            filePath = Paths.get(sanitized).normalize();
        } catch (InvalidPathException e) {
            Messages.showMessage("Invalid file path: path construction failed", true);
            return null;
        }

        String normalizedStr = filePath.toString();
        if (normalizedStr == null || normalizedStr.isEmpty()) {
            Messages.showMessage("Invalid file path: normalized path is empty", true);
            return null;
        }

        if (filePath.isAbsolute()) {
            Messages.showMessage("Invalid file path: absolute paths are not allowed", true);
            return null;
        }

        if (filePath.startsWith("..")) {
            Messages.showMessage("Invalid file path: parent directory reference not allowed", true);
            return null;
        }

        if (filePath.getNameCount() == 0) {
            Messages.showMessage("Invalid file path: empty path", true);
            return null;
        }

        for (int i = 0; i < filePath.getNameCount(); i++) {
            Path component = filePath.getName(i);
            String componentStr = component.toString();
            if (componentStr.equals("..") || componentStr.equals(".")) {
                Messages.showMessage("Invalid file path: invalid path component", true);
                return null;
            }
            if (!isValidPath(componentStr)) {
                Messages.showMessage("Invalid file path: invalid characters in path component", true);
                return null;
            }
        }

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

        String reNormalizedStr = reNormalized.toString();
        if (reNormalizedStr.contains("..")) {
            Messages.showMessage("Invalid file path: path traversal detected in re-normalized path", true);
            return null;
        }

        if (!isValidPath(reNormalizedStr)) {
            Messages.showMessage("Invalid file path: contains invalid characters after normalization", true);
            return null;
        }

        return filePath;
    }

    private boolean isValidPath(String path) {
        return VALID_PATH_PATTERN.matcher(path).matches();
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