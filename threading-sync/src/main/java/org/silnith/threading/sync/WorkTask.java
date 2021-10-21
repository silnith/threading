package org.silnith.threading.sync;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class WorkTask implements Runnable {

    private static final String SOURCE_CLASS = WorkTask.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    private final Duration duration;

    public WorkTask(final Duration duration) {
        this.duration = duration;
    }

    @Override
    public void run() {
        final String sourceMethod = "run";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);
        try {
            Thread.sleep(duration.toMillis());
        } catch (final InterruptedException e) {
            LOGGER.logp(Level.INFO, SOURCE_CLASS, sourceMethod, "Interrupted", e);
        }
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

}
