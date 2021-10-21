package org.silnith.threading.sync;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class TaskWaiter implements Runnable {

    private static final String SOURCE_CLASS = TaskWaiter.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    private final BlockingQueue<Future<?>> futures;

    public TaskWaiter(final BlockingQueue<Future<?>> futures) {
        this.futures = futures;
    }

    @Override
    public void run() {
        final String sourceMethod = "run";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);
        while (!futures.isEmpty()) {
            try {
                final Future<?> future = futures.take();
                future.get();
            } catch (final InterruptedException e) {
                LOGGER.logp(Level.INFO, SOURCE_CLASS, sourceMethod, "exception", e);
            } catch (final ExecutionException e) {
                LOGGER.logp(Level.INFO, SOURCE_CLASS, sourceMethod, "exception", e);
            }
        }
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

}
