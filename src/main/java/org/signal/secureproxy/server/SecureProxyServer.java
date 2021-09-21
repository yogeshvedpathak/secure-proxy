package org.signal.secureproxy.server;

import javax.net.ssl.SSLServerSocketFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecureProxyServer {
    private static final ExecutorService scheduler = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static int port = 8080;
    private volatile AtomicBoolean stopped = new AtomicBoolean(false);
    public static void main(String[] args) {
        SecureProxyServer secureProxyServer = new SecureProxyServer();
        secureProxyServer.runServer();
    }

    private boolean isStopped() {
        return stopped.get();
    };
    public void runServer() {
        SSLServerSocketFactory sslServerSocketFactory =
                (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

        try {
            ServerSocket sslServerSocket =
                    sslServerSocketFactory.createServerSocket(port);

            System.out.println("Secure Proxy Server Started");
            System.out.println(sslServerSocket.toString());

            while(!isStopped()){
                Socket client = sslServerSocket.accept();
                HandleClient handleClient = new HandleClient(client);
                scheduler.submit(handleClient);
            }
            System.out.println("Secure Proxy Server Shutting Down");
            scheduler.shutdownNow();

        } catch (IOException ex) {
            Logger.getLogger(SecureProxyServer.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
