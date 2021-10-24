package org.silnith.threading.server.blocking;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Worker implements Runnable {

    private static final String SOURCE_CLASS = Worker.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    private final Socket socket;

    private final byte[] buffer;

    private final AtomicLong atomicLong;

    public Worker(final Socket socket, final AtomicLong atomicLong) {
        super();
        this.socket = socket;
        buffer = new byte[Server.MESSAGE_SIZE];
        this.atomicLong = atomicLong;
    }

    @Override
    public void run() {
        final String sourceMethod = "run";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);
        
        long totalBytes = 0;
        try (final Socket socket = this.socket) {
            final InputStream inputStream = socket.getInputStream();
            int bytesRead = inputStream.read(buffer);
            while (bytesRead != -1) {
                totalBytes += bytesRead;
                LOGGER.logp(Level.FINE, SOURCE_CLASS, sourceMethod, "Read {0} bytes.", bytesRead);
                bytesRead = inputStream.read(buffer);
            }
        } catch (final IOException e) {
            LOGGER.logp(Level.WARNING, SOURCE_CLASS, sourceMethod, "IOException", e);
        } finally {
            atomicLong.addAndGet(totalBytes);
            LOGGER.logp(Level.FINE, SOURCE_CLASS, sourceMethod, "Total bytes read: {0}", totalBytes);
        }
        
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

}
