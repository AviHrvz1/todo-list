package org.sda.todolist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AI-Generated TodoList file persistence methods
 * This test suite validates the saveToFile and readFromFile functionality
 * 
 * @author AI Assistant
 * @version 2.0
 */
class TodoListAIGeneratedTest {

    private TodoListAIGenerated todoList;
    private String testFilename;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        todoList = new TodoListAIGenerated();
        testFilename = tempDir.resolve("test_tasks.obj").toString();
    }

    @Test
    void testSaveToFileWithNullFilename() {
        boolean result = todoList.saveToFile(null);
        assertFalse(result, "Should return false for null filename");
    }

    @Test
    void testSaveToFileWithEmptyFilename() {
        boolean result = todoList.saveToFile("");
        assertFalse(result, "Should return false for empty filename");
    }

    @Test
    void testSaveToFileWithEmptyList() {
        boolean result = todoList.saveToFile(testFilename);
        assertTrue(result, "Should successfully save empty list");
        
        File file = new File(testFilename);
        assertTrue(file.exists(), "File should be created");
        assertTrue(file.length() > 0, "File should not be empty");
    }

    @Test
    void testSaveToFileWithTasks() {
        todoList.addTask("Test Task 1", "Project A", LocalDate.now().plusDays(1));
        todoList.addTask("Test Task 2", "Project B", LocalDate.now().plusDays(2));
        todoList.addTask("Test Task 3", "Project A", LocalDate.now().plusDays(3));
        
        boolean result = todoList.saveToFile(testFilename);
        assertTrue(result, "Should successfully save tasks to file");
        
        File file = new File(testFilename);
        assertTrue(file.exists(), "File should be created");
        assertTrue(file.length() > 0, "File should contain data");
    }

    @Test
    void testReadFromFileWithNullFilename() {
        boolean result = todoList.readFromFile(null);
        assertFalse(result, "Should return false for null filename");
    }

    @Test
    void testReadFromFileWithEmptyFilename() {
        boolean result = todoList.readFromFile("");
        assertFalse(result, "Should return false for empty filename");
    }

    @Test
    void testReadFromFileWhenFileDoesNotExist() {
        boolean result = todoList.readFromFile("non_existent_file.obj");
        assertFalse(result, "Should return false when file does not exist");
        assertTrue(todoList.getTaskList().isEmpty(), "Task list should remain empty");
    }

    @Test
    void testReadFromFileWithEmptyList() {
        todoList.saveToFile(testFilename);
        
        TodoListAIGenerated newTodoList = new TodoListAIGenerated();
        boolean result = newTodoList.readFromFile(testFilename);
        
        assertTrue(result, "Should successfully read empty list from file");
        assertTrue(newTodoList.getTaskList().isEmpty(), "Loaded list should be empty");
    }

    @Test
    void testSaveAndReadRoundTrip() {
        todoList.addTask("Task 1", "Project X", LocalDate.now().plusDays(5));
        todoList.addTask("Task 2", "Project Y", LocalDate.now().plusDays(10));
        todoList.addTask("Task 3", "Project X", LocalDate.now().plusDays(15));
        
        int originalCount = todoList.getTaskList().size();
        
        boolean saveResult = todoList.saveToFile(testFilename);
        assertTrue(saveResult, "Save operation should succeed");
        
        TodoListAIGenerated loadedTodoList = new TodoListAIGenerated();
        boolean readResult = loadedTodoList.readFromFile(testFilename);
        assertTrue(readResult, "Read operation should succeed");
        
        assertEquals(originalCount, loadedTodoList.getTaskList().size(), 
                "Loaded list should have same number of tasks");
        
        assertEquals("Task 1", loadedTodoList.getTaskList().get(0).getTitle());
        assertEquals("Project X", loadedTodoList.getTaskList().get(0).getProject());
        assertEquals("Task 2", loadedTodoList.getTaskList().get(1).getTitle());
        assertEquals("Project Y", loadedTodoList.getTaskList().get(1).getProject());
    }

    @Test
    void testReadFromFilePreservesTaskState() {
        todoList.addTask("Completed Task", "Project A", LocalDate.now().plusDays(1));
        todoList.addTask("Incomplete Task", "Project B", LocalDate.now().plusDays(2));
        
        todoList.getTaskList().get(0).markCompleted();
        
        todoList.saveToFile(testFilename);
        TodoListAIGenerated loadedTodoList = new TodoListAIGenerated();
        loadedTodoList.readFromFile(testFilename);
        
        assertTrue(loadedTodoList.getTaskList().get(0).isComplete(), 
                "First task should remain completed");
        assertFalse(loadedTodoList.getTaskList().get(1).isComplete(), 
                "Second task should remain incomplete");
    }

    @Test
    void testMultipleSaveOperations() {
        todoList.addTask("Initial Task", "Project 1", LocalDate.now().plusDays(1));
        todoList.saveToFile(testFilename);
        
        todoList.addTask("Additional Task", "Project 2", LocalDate.now().plusDays(2));
        boolean result = todoList.saveToFile(testFilename);
        assertTrue(result, "Should successfully overwrite file with new data");
        
        TodoListAIGenerated loadedTodoList = new TodoListAIGenerated();
        loadedTodoList.readFromFile(testFilename);
        assertEquals(2, loadedTodoList.getTaskList().size(), 
                "Should contain both tasks after second save");
    }

    @Test
    void testCompletedCountAfterLoad() {
        todoList.addTask("Task 1", "Project A", LocalDate.now().plusDays(1));
        todoList.addTask("Task 2", "Project B", LocalDate.now().plusDays(2));
        todoList.addTask("Task 3", "Project C", LocalDate.now().plusDays(3));
        
        todoList.getTaskList().get(0).markCompleted();
        todoList.getTaskList().get(2).markCompleted();
        
        todoList.saveToFile(testFilename);
        TodoListAIGenerated loadedTodoList = new TodoListAIGenerated();
        loadedTodoList.readFromFile(testFilename);
        
        assertEquals(2, loadedTodoList.completedCount(), 
                "Should have 2 completed tasks");
        assertEquals(1, loadedTodoList.notCompletedCount(), 
                "Should have 1 incomplete task");
    }

    @Test
    void testSaveAndReadWithSpecialCharacters() {
        todoList.addTask("Task with 'quotes'", "Project & More", LocalDate.now().plusDays(1));
        todoList.saveToFile(testFilename);
        
        TodoListAIGenerated loadedTodoList = new TodoListAIGenerated();
        boolean result = loadedTodoList.readFromFile(testFilename);
        assertTrue(result, "Should handle special characters in task data");
        assertEquals(1, loadedTodoList.getTaskList().size());
        assertEquals("Task with 'quotes'", loadedTodoList.getTaskList().get(0).getTitle());
    }
}
