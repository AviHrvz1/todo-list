#!/bin/bash

echo "========================================="
echo "Running TodoListAIGenerated Unit Tests"
echo "========================================="
echo ""

# Create test output directory
mkdir -p /tmp/test_output
cd /tmp/todo-list-test

# Compile main classes
echo "[1/4] Compiling main classes..."
javac -cp "build/libs/*" -d /tmp/test_output src/main/java/org/sda/todolist/*.java 2>&1
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile main classes"
    exit 1
fi
echo "✓ Main classes compiled successfully"
echo ""

# Download JUnit 5 if needed
JUNIT_JAR="/tmp/junit-platform-console-standalone-1.9.2.jar"
if [ ! -f "$JUNIT_JAR" ]; then
    echo "[2/4] Downloading JUnit 5..."
    curl -L -o "$JUNIT_JAR" "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.9.2/junit-platform-console-standalone-1.9.2.jar" 2>&1 | grep -E "(Download|saved|error)" || echo "Downloading..."
fi

# Compile test classes
echo "[3/4] Compiling test classes..."
javac -cp "/tmp/test_output:$JUNIT_JAR:build/libs/*" -d /tmp/test_output src/test/java/org/sda/todolist/TodoListAIGeneratedTest.java 2>&1
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile test classes"
    exit 1
fi
echo "✓ Test classes compiled successfully"
echo ""

# Run tests
echo "[4/4] Running unit tests..."
echo "----------------------------------------"
if java -cp "/tmp/test_output:$JUNIT_JAR:build/libs/*" org.junit.platform.console.ConsoleLauncher --class-path /tmp/test_output --select-class org.sda.todolist.TodoListAIGeneratedTest 2>&1; then
    echo "----------------------------------------"
    echo "✓ ALL TESTS PASSED"
    echo "========================================="
    exit 0
else
    echo "----------------------------------------"
    echo "✗ SOME TESTS FAILED"
    echo "========================================="
    exit 1
fi

