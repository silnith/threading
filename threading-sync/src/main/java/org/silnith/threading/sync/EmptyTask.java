package org.silnith.threading.sync;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

public final class EmptyTask implements Runnable, Callable<Object> {

    private static final String SOURCE_CLASS = EmptyTask.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    @Override
    public void run() {
        final String sourceMethod = "run";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

    @Override
    public Object call() throws Exception {
        final String sourceMethod = "call";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);
        final Object returnValue = null;
        LOGGER.exiting(SOURCE_CLASS, sourceMethod, returnValue);
        return returnValue;
    }

}