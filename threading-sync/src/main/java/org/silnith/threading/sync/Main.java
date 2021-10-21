package org.silnith.threading.sync;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final int THREAD_COUNT = 50_000;

    private static final int TASK_COUNT = 10_000_000;

    private static final String sourceClass = Main.class.getName();

    private static final Logger logger = Logger.getLogger(sourceClass);

    private static final String sourceMethod = "main";

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        logger.entering(sourceClass, sourceMethod, args);
        final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

        final BlockingQueue<Future<?>> futures = new ArrayBlockingQueue<>(TASK_COUNT);
        final Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                final Future<?> future = futures.remove();
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        System.out.println("Priming thread pool.");
        final Instant primingStart = Instant.now();
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Future<?> future = threadPool.submit(() -> {});
            futures.add(future);
            logger.logp(Level.FINE, sourceClass, sourceMethod, "Priming thread: {0}", i);
        }
        while (!futures.isEmpty()) {
            final Future<?> future = futures.remove();
            future.get();
            final int i = futures.size();
            logger.logp(Level.FINE, sourceClass, sourceMethod, "Threads still priming: {0}", i);
        }
        System.out.println("Thread pool primed in " + Duration.between(primingStart, Instant.now()));
        
        System.out.println("Starting workload.");
        final Instant workloadStart = Instant.now();
        for (int i = 0; i < TASK_COUNT; i++) {
            final Future<?> future = threadPool.submit(() -> {});
            futures.add(future);
            logger.logp(Level.FINE, sourceClass, sourceMethod, "Submitted task: {0}", i);
        }
        System.out.println("Workload submitted in " + Duration.between(workloadStart, Instant.now()));
        while ( !futures.isEmpty()) {
            final Future<?> future = futures.remove();
            future.get();
            final int i = futures.size();
            logger.logp(Level.FINE, sourceClass, sourceMethod, "Remaining tasks: {0}", i);
        }
        System.out.println("Workload completed in " + Duration.between(workloadStart, Instant.now()));
        
        threadPool.shutdown();
        thread.interrupt();
        thread.join();
        logger.exiting(sourceClass, sourceMethod);
    }

}
