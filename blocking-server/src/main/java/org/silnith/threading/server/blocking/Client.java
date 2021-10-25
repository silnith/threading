package org.silnith.threading.server.blocking;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.SocketFactory;

public final class Client implements Runnable {

    private static final String SOURCE_CLASS = Client.class.getName();

    private static final Logger LOGGER = Logger.getLogger(SOURCE_CLASS);

    private final SocketAddress socketAddress;

    private final byte[] buffer;

    public Client(SocketAddress socketAddress) {
        super();
        this.socketAddress = socketAddress;
        buffer = new byte[Server.MESSAGE_SIZE];
    }

    @Override
    public void run() {
        final String sourceMethod = "run";
        LOGGER.entering(SOURCE_CLASS, sourceMethod);

        long totalBytes = 0;
        final List<Socket> sockets = new ArrayList<>();
        try {
            final SocketFactory socketFactory = SocketFactory.getDefault();
            for (int i = 0; i < Server.CONCURRENCY; i++) {
                final Socket socket = socketFactory.createSocket();
                socket.connect(socketAddress);
                sockets.add(socket);
            }
            
            for (int message = 0; message < Server.MESSAGES; message++) {
                LOGGER.logp(Level.INFO, SOURCE_CLASS, sourceMethod, "Sending message {0} on all sockets.", message);
                for (final Socket socket : sockets) {
                    final OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(buffer);
                    totalBytes += buffer.length;
                }
            }
        } catch (IOException e) {
            LOGGER.logp(Level.WARNING, SOURCE_CLASS, sourceMethod, "IOException", e);
        } finally {
            LOGGER.logp(Level.INFO, SOURCE_CLASS, sourceMethod, "Shutting down client.");
            for (final Socket socket : sockets) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.logp(Level.WARNING, SOURCE_CLASS, sourceMethod, "IOException", e);
                }
            }
            LOGGER.logp(Level.INFO, SOURCE_CLASS, sourceMethod, "Sockets closed.");
        }
        System.out.println(MessageFormat.format("Total bytes written: {0}", totalBytes));
        
        LOGGER.exiting(SOURCE_CLASS, sourceMethod);
    }

}