package org.silnith.threading.server.blocking;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Worker implements Runnable {

    public static final int BUFFER_SIZE = 1024;

    private static final String SOURCE_CLASS = Worker.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    private final Socket socket;

    private final byte[] buffer;

    public Worker(final Socket socket) {
        super();
        this.socket = socket;
        buffer = new byte[BUFFER_SIZE];
    }

    @Override
    public void run() {
        final String sourceMethod = "run";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);
        try (final Socket socket = this.socket) {
            final InputStream inputStream = socket.getInputStream();
            int bytesRead = inputStream.read(buffer);
            while (bytesRead != -1) {
                LOGGER.logp(Level.FINE, SOURCE_CLASS, sourceMethod, "Read {0} bytes.", bytesRead);
                bytesRead = inputStream.read(buffer);
            }
        } catch (final IOException e) {
            LOGGER.logp(Level.WARNING, SOURCE_CLASS, sourceMethod, "IOException", e);
        } finally {
            LOGGER.logp(Level.FINE, SOURCE_CLASS, sourceMethod, "Shutting down worker.");
        }
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

}
