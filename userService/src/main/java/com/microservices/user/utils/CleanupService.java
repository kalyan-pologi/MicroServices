package com.microservices.user.utils;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "jobs.cleanup.enabled", havingValue = "true", matchIfMissing = true)
public class CleanupService {

// right now all scheduling is paused with config in yml file

   // ScheduleThreadPoolConfig is used to configure the thread pool for scheduling tasks.
  // so that multiple scheduled tasks can run concurrently without waiting for each other to complete.

    @Scheduled(fixedRate = 5000)
    // task running conditionally based on property value
    @ConditionalOnProperty(name = "jobs.cleanup.enabled", havingValue = "true", matchIfMissing = false)
    // task running conditionally based on active profile
    @Profile("prod")
    public void task1() {
        System.out.println(Thread.currentThread().getName() + " - Running Task 1");
    }

    @Scheduled(fixedRate = 7000)
    public void task2() {
        System.out.println(Thread.currentThread().getName() + " - Running Task 2");
    }

    @Scheduled(fixedRate = 10000) // Runs every 10 seconds
    public void cleanTempFiles(){
        try{
            System.out.println("Cleaning up temporary files...");
            // Simulate cleanup work
            Thread.sleep(2000);
            System.out.println("Temporary files cleaned up successfully.");
        } catch (InterruptedException e) {
            System.err.println("Cleanup interrupted: " + e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 15000, initialDelay = 5000) // Runs 15 seconds after the last completion, with an initial delay of 5 seconds
    public void archiveOldLogs(){
        try{
            System.out.println("Archiving old logs...");
            // Simulate archiving work
            Thread.sleep(3000);
            System.out.println("Old logs archived successfully.");
        } catch (InterruptedException e) {
            System.err.println("Archiving interrupted: " + e.getMessage());
        }
    }
    @Scheduled(cron = "0/20 * * * * ?") // Runs every 20 seconds
    public void cleanTempFilesCorn(){
        try{
            System.out.println("Cleaning up temporary files using cron...");
            // Simulate cleanup work
            Thread.sleep(1000);
            System.out.println("Temporary files cleaned up successfully using cron.");
        } catch (InterruptedException e) {
            System.err.println("Cleanup interrupted: " + e.getMessage());
        }
    }

//    Thread Pool Scheduling (TaskScheduler) → allows multiple scheduled jobs to run in parallel.
//    Async Scheduling (@Async) → allows a single scheduled job to execute asynchronously so the scheduler isn’t blocked.
//
//    Without @Async → the scheduler waits for the 5s sleep before it schedules again.
//    With @Async → the scheduler triggers the job every 10s, regardless of how long the task runs, because execution happens in a separate thread.
    @Async
    @Scheduled(cron = "0/20 * * * * ?") // Runs every 20 seconds
    public void asyncCleanupJob() {
        try {
            System.out.println("Starting async cleanup job...");
            // Simulate a long-running cleanup task
            Thread.sleep(1000);
            System.out.println("Async cleanup job completed.");
        } catch (InterruptedException e) {
            System.err.println("Async cleanup job interrupted: " + e.getMessage());
        }
    }

   //If you deploy multiple service instances, use ShedLock to avoid duplicate execution:

    @Scheduled(fixedRate = 60000) // Runs every minute
    @SchedulerLock(name = "scheduledTaskWithShedLock", lockAtLeastFor = "PT30S", lockAtMostFor = "PT55S")
    public void scheduledTaskWithShedLock() {
        // Task implementation
    }

}
