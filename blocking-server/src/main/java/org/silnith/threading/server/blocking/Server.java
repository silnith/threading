package org.silnith.threading.server.blocking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;


public class Server implements Runnable {

    public static final int CONCURRENCY = 1024;

    public static final int MESSAGE_SIZE = 1024;

    public static final int MESSAGES = 1_000;
    
    public static final int READ_SIZE = 1024 * 1024;

    private static final String SOURCE_CLASS = Server.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    private final ServerSocket serverSocket;

    private final Executor executor;
    
    private final AtomicLong atomicLong;

    public Server(final ServerSocket serverSocket, final Executor executor, final AtomicLong atomicLong) {
        super();
        this.serverSocket = serverSocket;
        this.executor = executor;
        this.atomicLong = atomicLong;
    }

    @Override
    public void run() {
        final String sourceMethod = "run";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);
        
        final Duration timeout = Duration.ofSeconds(10);
        try (final ServerSocket serverSocket = this.serverSocket) {
            serverSocket.setSoTimeout((int) timeout.toMillis());
            while (!Thread.interrupted()) {
                try {
                    final Socket socket = serverSocket.accept();
                    executor.execute(new Worker(socket, atomicLong));
                } catch (final SocketTimeoutException e) {
                    LOGGER.logp(Level.FINE, SOURCE_CLASS, sourceMethod, "SocketTimeoutException", e);
                }
            }
        } catch (final IOException e) {
            LOGGER.logp(Level.WARNING, SOURCE_CLASS, sourceMethod, "IOException", e);
        } finally {
            LOGGER.logp(Level.FINE, SOURCE_CLASS, sourceMethod, "Shutting down server.");
        }
        
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

    public static void main(final String[] args) throws IOException, InterruptedException {
        final AtomicLong byteCounter = new AtomicLong();
        
        final ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENCY);
        executorService.invokeAll(Collections.nCopies(CONCURRENCY, () -> null));
        
        final ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();
        final ServerSocket serverSocket = serverSocketFactory.createServerSocket();
        serverSocket.bind(null);
        
        System.out.println("Starting test.");

        final Thread serverThread = new Thread(new Server(serverSocket, executorService, byteCounter));
        final Thread clientThread = new Thread(new Client(serverSocket.getLocalSocketAddress()));
        
        final Instant start = Instant.now();

        serverThread.start();
        clientThread.start();

        System.out.println("Waiting for test to end.");

        clientThread.join();
        System.out.println("Client thread terminated.");
        final Instant end = Instant.now();
        
        final Duration elapsed = Duration.between(start, end);
        System.out.println(MessageFormat.format("Client completed in {0}", elapsed));
        
        serverThread.interrupt();
        serverThread.join();
        executorService.shutdown();
        System.out.println("Executor service shut down.");
        
        System.out.println(MessageFormat.format("Total bytes read: {0}", byteCounter.get()));
        System.out.println(MessageFormat.format("Bytes per second: {0}", byteCounter.get() / elapsed.getSeconds()));
    }

}
