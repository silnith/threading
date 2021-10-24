package org.silnith.threading.server.blocking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;


public class Server implements Runnable {

    private static final String SOURCE_CLASS = Server.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    private final ServerSocket serverSocket;

    private final Executor executor;

    public Server(final ServerSocket serverSocket, final Executor executor) {
        super();
        this.serverSocket = serverSocket;
        this.executor = executor;
    }

    @Override
    public void run() {
        final String sourceMethod = "run";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);
        try (final ServerSocket serverSocket = this.serverSocket) {
            while (!Thread.interrupted()) {
                final Socket socket = serverSocket.accept();
                executor.execute(new Worker(socket));
            }
        } catch (final IOException e) {
            LOGGER.logp(Level.WARNING, SOURCE_CLASS, sourceMethod, "IOException", e);
        } finally {
            LOGGER.logp(Level.INFO, SOURCE_CLASS, sourceMethod, "Shutting down server.");
        }
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

    public static void main(final String[] args) throws IOException, InterruptedException {
        final ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();
        final ServerSocket serverSocket = serverSocketFactory.createServerSocket();
        serverSocket.bind(null);
        final InetAddress inetAddress = serverSocket.getInetAddress();
        final int localPort = serverSocket.getLocalPort();
        final ExecutorService executorService = Executors.newFixedThreadPool(Client.CONCURRENCY);
        final Server server = new Server(serverSocket, executorService);
        final Client client = new Client(inetAddress, localPort);
        
        final Thread serverThread = new Thread(server);
        final Thread clientThread = new Thread(client);
        
        serverThread.start();
        clientThread.start();
        
        clientThread.join();
    }

}