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
    private static final String FILENAME = "tasks.dat";
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
        // Use provided filename if valid, otherwise use default
        String safeFilename = (filename != null && !filename.trim().isEmpty()) 
            ? filename.trim() 
            : FILENAME;
        
        // Basic validation - only allow simple filenames (no path separators)
        if (safeFilename.contains("/") || safeFilename.contains("\\") || safeFilename.contains("..")) {
            Messages.showMessage("Invalid filename: path separators not allowed", true);
            return false;
        }
        
        Path filePath = Paths.get(safeFilename);
        
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(taskList);
            return true;
        } catch (IOException e) {
            Messages.showMessage("Error saving file: " + e.getMessage(), true);
            return false;
        }
    }

    public boolean readFromFile(String filename) {
        // Use provided filename if valid, otherwise use default
        String safeFilename = (filename != null && !filename.trim().isEmpty()) 
            ? filename.trim() 
            : FILENAME;
        
        // Basic validation - only allow simple filenames (no path separators)
        if (safeFilename.contains("/") || safeFilename.contains("\\") || safeFilename.contains("..")) {
            Messages.showMessage("Invalid filename: path separators not allowed", true);
            return false;
        }
        
        Path filePath = Paths.get(safeFilename);
        
        if (!Files.exists(filePath)) {
            Messages.showMessage("File does not exist: " + safeFilename, true);
            return false;
        }

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            
            // Null check for deserialized object (fixes line 59 issue)
            if (obj == null) {
                Messages.showMessage("Invalid data: null object deserialized", true);
                return false;
            }

            if (obj instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<Task> loadedTasks = (ArrayList<Task>) obj;
                
                // Comprehensive validation for deserialized data (fixes line 64 issue)
                if (loadedTasks == null) {
                    Messages.showMessage("Invalid data: null list deserialized", true);
                    return false;
                }
                
                for (Object item : loadedTasks) {
                    if (item == null) {
                        Messages.showMessage("Invalid data: null item in list", true);
                        return false;
                    }
                    if (!(item instanceof Task)) {
                        Messages.showMessage("Invalid data: non-Task object found", true);
                        return false;
                    }
                }
                
                this.taskList = loadedTasks;
                return true;
            } else {
                Messages.showMessage("Invalid data: expected ArrayList, got " + obj.getClass().getSimpleName(), true);
                return false;
            }
        } catch (ClassNotFoundException e) {
            Messages.showMessage("Error: class not found - " + e.getMessage(), true);
            return false;
        } catch (IOException e) {
            Messages.showMessage("Error reading file: " + e.getMessage(), true);
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
}
