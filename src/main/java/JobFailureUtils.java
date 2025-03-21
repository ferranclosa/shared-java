
public class JobFailureUtils {

    public static String getReadableFailureExceptions(JobExecution jobExecution) {
        // Get the list of failure exceptions
        if (jobExecution.getFailureExceptions() == null || jobExecution.getFailureExceptions().isEmpty()) {
            return "No failure exceptions found.";
        }

        // Use StringBuilder to build a readable string
        StringBuilder failureDetails = new StringBuilder("Job failed with the following exceptions:\n");
        int i = 1;

        for (Throwable exception : jobExecution.getFailureExceptions()) {
            failureDetails.append(i++)
                    .append(". ")
                    .append(exception.getClass().getSimpleName()) // Include the exception class
                    .append(": ")
                    .append(exception.getMessage() != null ? exception.getMessage() : "No specific message")
                    .append("\n");

            // Optional: Add stack trace details if required
            for (StackTraceElement element : exception.getStackTrace()) {
                failureDetails.append("\tat ").append(element.toString()).append("\n");
            }

            failureDetails.append("\n");
        }

        return failureDetails.toString();
    }
}{
}
