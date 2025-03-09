package com.example.sharedjavagemini;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileWritter {

    private BufferedWriter writer;
    private Path filePath;
    private ProcessState currentState;

    public FileWritter(String filePath) throws IOException {
        this.filePath = Paths.get(filePath);
        this.writer = Files.newBufferedWriter(this.filePath, StandardOpenOption.APPEND);
    }

    public void setState(ProcessState newState) {
        this.currentState = newState;
    }

    private void clearFile() throws IOException {
        Files.write(filePath, "".getBytes()); // Overwrite with an empty string
    }
    public void appendLine(String line) throws IOException {

        if (currentState == ProcessState.INIT) {
            clearFile(); // Clear the file if in INIT state
            currentState = ProcessState.IN_PROGRESS; // Transition to IN_PROGRESS
        }

        writer.write(line);
        writer.newLine();
    }

    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }

    public static void main(String[] args) {
        try {
            FileWritter appender = new FileWritter("output.txt");
            appender.appendLine("Line 1");
            appender.appendLine("Line 2");
            appender.appendLine("Line 3");
            appender.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
