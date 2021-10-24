package org.silnith.threading.server.blocking;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;

public final class Client implements Runnable {

    public static final int CONCURRENCY = 50;
    
    public static final int MESSAGE_SIZE = 1024;
    
    public static final int MESSAGES = 1024;

    private static final String SOURCE_CLASS = Client.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    private final InetAddress inetAddress;

    private final int localPort;

    private final byte[] buffer;

    public Client(InetAddress inetAddress, int localPort) {
        this.inetAddress = inetAddress;
        this.localPort = localPort;
        buffer = new byte[MESSAGE_SIZE];
    }

    @Override
    public void run() {
        final String sourceMethod = "run";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);

        final List<Socket> sockets = new ArrayList<>();
        try {
            final SocketFactory socketFactory = SocketFactory.getDefault();
            for (int i = 0; i < CONCURRENCY; i++) {
                final Socket socket = socketFactory.createSocket(inetAddress, localPort);
                sockets.add(socket);
            }
            
            for (int message = 0; message < MESSAGES; message++) {
                LOGGER.logp(Level.INFO, SOURCE_CLASS, sourceMethod, "Sending message {0} on all sockets.", message);
                for (final Socket socket : sockets) {
                    final OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(buffer);
                }
            }
        } catch (IOException e) {
            LOGGER.logp(Level.WARNING, SOURCE_CLASS, sourceMethod, "IOException", e);
        } finally {
            for (final Socket socket : sockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.logp(Level.WARNING, SOURCE_CLASS, sourceMethod, "IOException", e);
                }
            }
            LOGGER.logp(Level.INFO, SOURCE_CLASS, sourceMethod, "Shutting down client.");
        }
        
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

}