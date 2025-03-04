package com.example.sharedjavagemini;

public class RecordProcessingSpeed {
import java.time.Duration;
import java.time.Instant;

public class RecordProcessingSpeed {

    /**
     * Calculates the record processing speed in records per second.
     *
     * @param startTime       The start time of the processing.
     * @param endTime         The end time of the processing.
     * @param numberOfRecords The number of records processed.
     * @return The processing speed in records per second, or -1 if an error occurred.
     */
    public static double calculateProcessingSpeed(Instant startTime, Instant endTime, long numberOfRecords) {
        if (startTime == null || endTime == null || numberOfRecords <= 0) {
            return -1; // Indicate an error or invalid input
        }

        Duration duration = Duration.between(startTime, endTime);
        long milliseconds = duration.toMillis();

        if (milliseconds <= 0) {
            return -1; // Handle cases where duration is zero or negative.
        }

        return (double) numberOfRecords / (milliseconds / 1000.0); // Convert milliseconds to seconds
    }

    public static void main(String[] args) {
        // Example usage:
        Instant startTime = Instant.now();
        // Simulate some processing time
        try {
            Thread.sleep(2500); // Simulate 2.5 seconds of processing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Instant endTime = Instant.now();
        long recordsProcessed = 10000;

        double speed = calculateProcessingSpeed(startTime, endTime, recordsProcessed);

        if (speed != -1) {
            System.out.println("Processing speed: " + speed + " records/second");
        } else {
            System.out.println("Error: Invalid input or zero/negative duration.");
        }

        //Example bad input
        speed = calculateProcessingSpeed(startTime, endTime, -10);
        if (speed != -1){
            System.out.println("This should not print");
        }
        else{
            System.out.println("Correctly handled bad input");
        }

        speed = calculateProcessingSpeed(null, endTime, 100);
        if (speed != -1){
            System.out.println("This should not print");
        }
        else{
            System.out.println("Correctly handled bad input");
        }
    }
}}
