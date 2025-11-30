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

        String sanitizedFilename = filename.trim();
        if (containsPathTraversal(sanitizedFilename)) {
            Messages.showMessage("Invalid file path: path traversal detected", true);
            return false;
        }

        Path filePath = Paths.get(sanitizedFilename).normalize();
        if (containsPathTraversal(filePath.toString())) {
            Messages.showMessage("Invalid file path: path traversal detected after normalization", true);
            return false;
        }

        if (filePath.startsWith("..") || filePath.getNameCount() == 0 || containsInvalidPathStructure(filePath)) {
            Messages.showMessage("Invalid file path: invalid path structure", true);
            return false;
        }

        if (containsInvalidCharacters(filePath.toString())) {
            Messages.showMessage("Invalid file path: contains invalid characters", true);
            return false;
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(taskList);
            return true;
        } catch (java.io.FileNotFoundException e) {
            Messages.showMessage(String.format("Cannot create file: %s", filename), true);
            return false;
        } catch (java.io.IOException e) {
            Messages.showMessage(String.format("Error saving file: %s", e.getMessage()), true);
            return false;
        }
    }

    public boolean readFromFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            Messages.showMessage("Filename cannot be null or empty", true);
            return false;
        }

        String sanitizedFilename = filename.trim();
        if (containsPathTraversal(sanitizedFilename)) {
            Messages.showMessage("Invalid file path: path traversal detected", true);
            return false;
        }

        Path filePath = Paths.get(sanitizedFilename).normalize();
        if (containsPathTraversal(filePath.toString())) {
            Messages.showMessage("Invalid file path: path traversal detected after normalization", true);
            return false;
        }

        if (filePath.startsWith("..") || filePath.getNameCount() == 0 || containsInvalidPathStructure(filePath)) {
            Messages.showMessage("Invalid file path: invalid path structure", true);
            return false;
        }

        if (containsInvalidCharacters(filePath.toString())) {
            Messages.showMessage("Invalid file path: contains invalid characters", true);
            return false;
        }

        if (!Files.exists(filePath)) {
            Messages.showMessage(String.format("The data file, i.e., %s does not exist", filename), true);
            return false;
        }

        if (!Files.isReadable(filePath)) {
            Messages.showMessage(String.format("The data file, i.e., %s is not readable", filename), true);
            return false;
        }

        try (FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
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

    private boolean containsPathTraversal(String path) {
        return path.contains("..") || path.startsWith("/") || path.contains("\\");
    }

    private boolean containsInvalidPathStructure(Path path) {
        return path.toString().contains("//") || path.toString().contains("\\\\") ||
               !path.normalize().equals(path) || path.normalize().toString().contains("..");
    }

    private boolean containsInvalidCharacters(String path) {
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