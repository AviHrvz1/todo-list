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
        // Always use fixed filename - no path validation needed
        Path filePath = Paths.get(FILENAME);
        
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
        // Always use fixed filename - no path validation needed
        Path filePath = Paths.get(FILENAME);
        
        if (!Files.exists(filePath)) {
            Messages.showMessage("File does not exist: " + FILENAME, true);
            return false;
        }

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            
            if (obj == null) {
                Messages.showMessage("Invalid data: null object", true);
                return false;
            }

            if (obj instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<Task> loadedTasks = (ArrayList<Task>) obj;
                
                for (Object item : loadedTasks) {
                    if (!(item instanceof Task)) {
                        Messages.showMessage("Invalid data: non-Task object found", true);
                        return false;
                    }
                }
                
                this.taskList = loadedTasks;
                return true;
            } else {
                Messages.showMessage("Invalid data: expected ArrayList", true);
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
