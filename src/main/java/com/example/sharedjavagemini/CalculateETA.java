package com.example.sharedjavagemini;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalculateETA {

    private static Long initialEstimatedTotalTimeMillis = null; // Store initial estimate

    public static String calculateEstimatedFinishTime(LocalDateTime startTime, long totalRecords, long recordsProcessed) {
        if (startTime == null || totalRecords <= 0 || recordsProcessed <= 0 || recordsProcessed > totalRecords) {
            return "Invalid input parameters.";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration elapsedTime = Duration.between(startTime, now);

        if (recordsProcessed == totalRecords) {
            return "Job already completed.";
        }

        double progress = (double) recordsProcessed / totalRecords;
        if (progress >= 1.0) {
            return "Job already completed.";
        }

        if (initialEstimatedTotalTimeMillis == null) {
            // Calculate initial estimate only once
            initialEstimatedTotalTimeMillis = (long) (elapsedTime.toMillis() / progress);
        }

        long estimatedRemainingTimeMillis = (long) (initialEstimatedTotalTimeMillis * (1.0 - progress));

        LocalDateTime estimatedFinishTime = now.plus(Duration.ofMillis(estimatedRemainingTimeMillis));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Estimated finish time: " + estimatedFinishTime.format(formatter);
    }

    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        long totalRecords = 100000;
        long recordsProcessed = 1000;

        String estimatedFinishTime = calculateEstimatedFinishTime(startTime, totalRecords, recordsProcessed);
        System.out.println("Initial ETA: " + estimatedFinishTime);

        // Simulate progress
        recordsProcessed = 10000;
        estimatedFinishTime = calculateEstimatedFinishTime(startTime, totalRecords, recordsProcessed);
        System.out.println("ETA after 10% progress: " + estimatedFinishTime);

        recordsProcessed = 50000;
        estimatedFinishTime = calculateEstimatedFinishTime(startTime, totalRecords, recordsProcessed);
        System.out.println("ETA after 50% progress: " + estimatedFinishTime);

        recordsProcessed = 90000;
        estimatedFinishTime = calculateEstimatedFinishTime(startTime, totalRecords, recordsProcessed);
        System.out.println("ETA after 90% progress: " + estimatedFinishTime);

        recordsProcessed = 100000;
        estimatedFinishTime = calculateEstimatedFinishTime(startTime, totalRecords, recordsProcessed);
        System.out.println("ETA after 100% progress: " + estimatedFinishTime);

    }
}

}