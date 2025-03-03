package com.example.sharedjavagemini;


import org.springframework.batch.core.*;
        import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ActiveJobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobLauncher jobLauncher;

    public void runJobWithPID(Job job, JobParameters jobParameters) throws JobExecutionException {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        JobParametersBuilder builder = new JobParametersBuilder(jobParameters);
        builder.addString("pid", pid);
        jobLauncher.run(job, builder.toJobParameters());
    }

    public boolean isJobActive(String jobName, String tableName) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("tableName", tableName)
                .toJobParameters();

        List<JobExecution> jobExecutions = jobRepository.findJobExecutions(jobName, jobParameters);

        if (jobExecutions.isEmpty()) {
            return false;
        }

        List<JobExecution> sortedExecutions = jobExecutions.stream()
                .sorted((j1, j2) -> j2.getStartTime().compareTo(j1.getStartTime()))
                .limit(1)
                .collect(Collectors.toList());

        JobExecution latestExecution = sortedExecutions.get(0);

        if (!latestExecution.getStatus().isRunning()) {
            return false;
        }

        String pid = latestExecution.getJobParameters().getString("pid");
        if (pid == null) {
            return false; // No PID, cannot check process status
        }

        if (!isProcessRunning(pid)) {
            return false; // Process not running, job is "dead"
        }

        // Time-based check (optional, but recommended)
        LocalDateTime startTime = latestExecution.getStartTime();
        if (startTime == null) {
            return false;
        }

        long elapsedTime = new Date().getTime() - startTime.getTime();
        long maxRunningTime = TimeUnit.MINUTES.toMillis(10); // Adjust as needed

        return elapsedTime < maxRunningTime;
    }

    private boolean isProcessRunning(String pid) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            Process process;

            if (os.contains("win")) {
                process = Runtime.getRuntime().exec("tasklist /FI \"PID eq " + pid + "\"");
            } else { // Linux, macOS
                process = Runtime.getRuntime().exec("ps -p " + pid);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(pid)) {
                    return true; // Process is running
                }
            }
            return false; // Process not found
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Error checking process status
        }
    }
}
