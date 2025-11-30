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
        // Always use fixed filename - eliminates all path validation and traversal concerns
        Path filePath = Paths.get(FILENAME);
        
        // Try-with-resources automatically closes FileOutputStream and ObjectOutputStream
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(taskList);
            return true;
        } catch (java.io.FileNotFoundException e) {
            Messages.showMessage("Cannot create file: " + FILENAME, true);
            return false;
        } catch (IOException e) {
            Messages.showMessage("Error saving file: " + e.getMessage(), true);
            return false;
        }
    }

    public boolean readFromFile(String filename) {
        // Always use fixed filename - eliminates all path validation and traversal concerns
        Path filePath = Paths.get(FILENAME);
        
        if (!Files.exists(filePath)) {
            Messages.showMessage("File does not exist: " + FILENAME, true);
            return false;
        }

        // Try-with-resources automatically closes FileInputStream and ObjectInputStream
        try (FileInputStream fileInputStream = new FileInputStream(filePath.toFile());
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            
            Object obj = objectInputStream.readObject();
            
            // Null check for deserialized object
            if (obj == null) {
                Messages.showMessage("Invalid data: null object deserialized", true);
                return false;
            }

            // Type validation before casting
            if (!(obj instanceof ArrayList<?>)) {
                Messages.showMessage("Invalid data: expected ArrayList, got " + obj.getClass().getSimpleName(), true);
                return false;
            }

            // Safe cast after instanceof validation
            @SuppressWarnings("unchecked")
            ArrayList<?> rawList = (ArrayList<?>) obj;
            ArrayList<Task> validatedList = new ArrayList<>();
            
            // Validate each item: null check and type check
            for (Object item : rawList) {
                if (item == null) {
                    Messages.showMessage("Invalid data: null item in list", true);
                    return false;
                }
                if (!(item instanceof Task)) {
                    Messages.showMessage("Invalid data: non-Task object found", true);
                    return false;
                }
                validatedList.add((Task) item);
            }
            
            this.taskList = validatedList;
            return true;
        } catch (ClassNotFoundException e) {
            Messages.showMessage("Error: class not found - " + e.getMessage(), true);
            return false;
        } catch (java.io.FileNotFoundException e) {
            Messages.showMessage("File not found: " + FILENAME, true);
            return false;
        } catch (java.io.EOFException e) {
            Messages.showMessage("Invalid data: unexpected end of file", true);
            return false;
        } catch (java.io.StreamCorruptedException e) {
            Messages.showMessage("Invalid data: corrupted file stream", true);
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
