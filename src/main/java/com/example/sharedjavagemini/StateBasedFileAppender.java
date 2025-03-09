package com.example.sharedjavagemini;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class StateBasedFileAppender {

    private Path filePath;
    private ProcessState currentState;

    public StateBasedFileAppender(String filePath, ProcessState initialState) {
        this.filePath = Paths.get(filePath);
        this.currentState = initialState;
    }

    public void appendLine(String line) throws IOException {
        if (currentState == ProcessState.INIT) {
            clearFile(); // Clear the file if in INIT state
            currentState = ProcessState.IN_PROGRESS; // Transition to IN_PROGRESS
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        }
    }

    public void setState(ProcessState newState) {
        this.currentState = newState;
    }

    private void clearFile() throws IOException {
        Files.write(filePath, "".getBytes()); // Overwrite with an empty string
    }

    public static void main(String[] args) {
        try {
            StateBasedFileAppender appender = new StateBasedFileAppender("process_log.txt", ProcessState.INIT);

            appender.appendLine("Starting the process...");
            appender.appendLine("Step 1 completed.");

            appender.setState(ProcessState.COMPLETED);
            appender.appendLine("Process completed successfully.");

            //Simulating an error on a second run.
            StateBasedFileAppender appender2 = new StateBasedFileAppender("process_log.txt", ProcessState.INIT);
            appender2.setState(ProcessState.ERROR);
            appender2.appendLine("Error occured, restarting");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
