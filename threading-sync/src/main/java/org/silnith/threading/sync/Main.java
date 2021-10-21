package org.silnith.threading.sync;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

    public static final int THREAD_COUNT = 1_000;

    public static final int TASK_COUNT = 10_000_000;

    private static final String SOURCE_CLASS = Main.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    private static final Duration SLEEP_DURATION = Duration.ofMillis(1);

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        final String sourceMethod = "main";
        LOGGER.entering(SOURCE_CLASS, sourceMethod, args);
        final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

        System.out.println("Priming thread pool.");
        final Instant primingStart = Instant.now();
        threadPool.invokeAll(Collections.nCopies(THREAD_COUNT, new EmptyTask()));
        System.out.println(
                MessageFormat.format("Thread pool primed in {0}", Duration.between(primingStart, Instant.now())));

        final BlockingQueue<Future<?>> futures = new ArrayBlockingQueue<>(TASK_COUNT);
        final Thread thread = new Thread(new TaskWaiter(futures));
        final WorkTask task = new WorkTask(SLEEP_DURATION);
        System.out.println("Starting workload.");
        final Instant workloadStart = Instant.now();
        for (int i = 0; i < TASK_COUNT; i++) {
            final Future<?> future = threadPool.submit(task);
            futures.put(future);
            LOGGER.logp(Level.FINE, SOURCE_CLASS, sourceMethod, "Submitted task: {0}", i);
        }
        System.out.println(
                MessageFormat.format("Workload submitted in {0}", Duration.between(workloadStart, Instant.now())));
        thread.start();
        while (!futures.isEmpty()) {
            Thread.sleep(1000);
            System.out.println(MessageFormat.format("Tasks remaining: {0}", futures.size()));
        }
        thread.join();
        System.out.println(
                MessageFormat.format("Workload completed in {0}", Duration.between(workloadStart, Instant.now())));

        threadPool.shutdown();
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

}
