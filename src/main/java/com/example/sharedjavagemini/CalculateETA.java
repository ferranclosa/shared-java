package com.example.sharedjavagemini;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalculateETA {

    public static String calculateEstimatedFinishTime(LocalDateTime startTime, long totalRecords, long recordsProcessed) {
        if (startTime == null || totalRecords <= 0 || recordsProcessed <= 0 || recordsProcessed > totalRecords) {
            return "Invalid input parameters.";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration elapsedTime = Duration.between(startTime, now);

        if (recordsProcessed == totalRecords){
            return "Job already completed";
        }

        double progress = (double) recordsProcessed / totalRecords;
        if (progress >= 1.0) {
            return "Job already completed";
        }

        double remainingProgress = 1.0 - progress;
        double estimatedTotalTimeMillis = elapsedTime.toMillis() / progress;
        long estimatedRemainingTimeMillis = (long) (estimatedTotalTimeMillis * remainingProgress);

        LocalDateTime estimatedFinishTime = now.plus(Duration.ofMillis(estimatedRemainingTimeMillis));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Estimated finish time: " + estimatedFinishTime.format(formatter);
    }

    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(1).minusMinutes(30);
        long totalRecords = 10000;
        long recordsProcessed = 3500;

        String estimatedFinishTime = calculateEstimatedFinishTime(startTime, totalRecords, recordsProcessed);
        System.out.println(estimatedFinishTime);

        //Example already finished
        startTime = LocalDateTime.now().minusMinutes(10);
        totalRecords = 1000;
        recordsProcessed = 1000;
        estimatedFinishTime = calculateEstimatedFinishTime(startTime, totalRecords, recordsProcessed);
        System.out.println(estimatedFinishTime);

        //Example invalid input
        startTime = LocalDateTime.now().minusMinutes(10);
        totalRecords = 1000;
        recordsProcessed = 2000;
        estimatedFinishTime = calculateEstimatedFinishTime(startTime, totalRecords, recordsProcessed);
        System.out.println(estimatedFinishTime);

    }
}