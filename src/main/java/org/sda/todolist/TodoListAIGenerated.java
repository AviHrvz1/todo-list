package org.sda.todolist;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * A class that manages a collection of tasks in a todo list application.
 * This class provides methods to add tasks, retrieve the task list, and count completed and incomplete tasks.
 */
public class TodoListAIGenerated {
    private ArrayList<Task> taskList;

    /**
     * Default constructor that initializes an empty ArrayList to store tasks.
     */
    public TodoListAIGenerated() {
        this.taskList = new ArrayList<Task>();
    }

    /**
     * Adds a new task to the todo list with the specified parameters.
     * 
     * @param title A string representing the title of the task
     * @param project A string representing the project name associated with the task
     * @param dueDate A LocalDate object representing the due date for the task
     */
    public void addTask(String title, String project, LocalDate dueDate) {
        Task task = new Task(title, project, dueDate);
        this.taskList.add(task);
    }

    /**
     * Returns the current list of tasks stored in this todo list.
     * 
     * @return An ArrayList containing all Task objects in the todo list
     */
    public ArrayList<Task> getTaskList() {
        return this.taskList;
    }

    /**
     * Calculates and returns the number of tasks that have been marked as completed.
     * Uses Java Streams API to filter and count completed tasks.
     * 
     * @return An integer representing the count of completed tasks
     */
    public int completedCount() {
        long result = this.taskList.stream()
            .filter(item -> item.isComplete())
            .count();
        return (int) result;
    }

    /**
     * Calculates and returns the number of tasks that have not been completed yet.
     * This is computed by subtracting the completed count from the total number of tasks.
     * 
     * @return An integer representing the count of incomplete tasks
     */
    public int notCompletedCount() {
        int total = this.taskList.size();
        int completed = completedCount();
        int result = total - completed;
        return result;
    }
}
