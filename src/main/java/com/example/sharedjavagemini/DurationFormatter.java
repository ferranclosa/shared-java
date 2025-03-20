package com.example.sharedjavagemini;

import java.time.Duration;
import java.time.LocalDateTime;

public class DurationFormatter {

    public static String formatDurationReadable(LocalDateTime start, LocalDateTime end) {
        // Calculate the duration between the two LocalDateTime objects
        Duration duration = Duration.between(start, end);

        // Extract components from Duration
        long days = duration.toDays(); // Number of days
        long hours = duration.toHours() % 24; // Hours remaining after removing days
        long minutes = duration.toMinutes() % 60; // Minutes remaining
        long seconds = duration.getSeconds() % 60; // Seconds remaining
        long millis = duration.toMillis() % 1000; // Milliseconds (if needed)

        // Build a human-readable string
        StringBuilder humanReadable = new StringBuilder();

        // Append days, if any
        if (days > 0) {
            humanReadable.append(days).append(" day").append(days > 1 ? "s" : "").append(" ");
        }
        // Append hours, if any
        if (hours > 0) {
            humanReadable.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(" ");
        }
        // Append minutes, if any
        if (minutes > 0) {
            humanReadable.append(minutes).append(" minute").append(minutes > 1 ? "s" : "").append(" ");
        }
        // Append seconds and fractional seconds
        if (seconds > 0 || millis > 0) {
            humanReadable.append(seconds);
            if (millis > 0) {
                humanReadable.append(".").append(String.format("%03d", millis)); // Include milliseconds
            }
            humanReadable.append(" second").append((seconds > 1 || millis > 0) ? "s" : "");
        }

        // If the duration is 0, return "0 seconds"
        if (humanReadable.length() == 0) {
            humanReadable.append("0 seconds");
        }

        return humanReadable.toString().trim();
    }

    // Example usage
    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 8, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 12, 10, 15, 30);

        String result = formatDurationReadable(start, end);
        System.out.println(result); // Example Output: "2 days 2 hours 15 minutes 30 seconds"
    }
}