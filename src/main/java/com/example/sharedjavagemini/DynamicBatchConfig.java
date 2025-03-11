package com.example.sharedjavagemini;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamicBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DynamicTargetDatabaseTasklet dynamicTasklet;

    public DynamicBatchConfig(JobBuilderFactory jobBuilderFactory,
                              StepBuilderFactory stepBuilderFactory,
                              DynamicTargetDatabaseTasklet dynamicTasklet) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dynamicTasklet = dynamicTasklet;
    }

    @Bean
    public Job dynamicJob() {
        return jobBuilderFactory.get("dynamicJob")
                .start(dynamicStep())
                .build();
    }

    @Bean
    public Step dynamicStep() {
        return stepBuilderFactory.get("dynamicStep")
                .tasklet(dynamicTasklet)
                .build();
    }
}