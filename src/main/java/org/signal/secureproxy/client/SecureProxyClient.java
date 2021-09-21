package org.signal.secureproxy.client;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecureProxyClient {

    static final int port = 8080;

    public static void main(String[] args) throws NoSuchAlgorithmException, ExecutionException, InterruptedException, KeyManagementException {

        if(args.length <= 0) {
            System.out.println("Please provide search query");
            System.exit(1);
        }
        SSLSocketFactory sslSocketFactory =
                (SSLSocketFactory)SSLSocketFactory.getDefault();

        try {
            Socket socket = sslSocketFactory.createSocket("0.0.0.0", port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            try (BufferedReader bufferedReader =
                         new BufferedReader(
                                 new InputStreamReader(socket.getInputStream()))) {

                out.println(String.format("GET /?q=%s HTTP/1.1", args[0]));

                System.out.println(bufferedReader.readLine());
            }

        } catch (IOException ex) {
            Logger.getLogger(SecureProxyClient.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }
}
