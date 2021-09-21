package org.signal.secureproxy.server;

import org.signal.secureproxy.giphy.GIPHYForwarder;
import org.signal.secureproxy.http.HttpFormatException;
import org.signal.secureproxy.http.HttpRequestParser;
import org.signal.secureproxy.http.HttpResponseGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HandleClient implements Callable<CompletableFuture<Void>> {
    private static Pattern requestParameter = Pattern.compile("\\?q=(.*)");
    private Socket client;
    CompletableFuture<Void> future;

    public HandleClient(Socket client) {
        future = new CompletableFuture<>();
        this.client = client;
    }

    public CompletableFuture<Void> getFuture()  {
        return future;
    }
    @Override
    public CompletableFuture<Void> call()  {
        System.out.println("Connection accepted");
        PrintWriter out = null;
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                Map<String, String> requestDetails = HttpRequestParser.parseRequest(bufferedReader);
                HttpResponse<String> giphyResponse = GIPHYForwarder.forward(getSearchQuery(requestDetails)).get();
                writeResponse(out, HttpResponseGenerator.generateSuccessResponse(giphyResponse), giphyResponse.body());
            }
        }catch (HttpFormatException e) {
            writeResponse(out, HttpResponseGenerator.generate400Response(), "");
        }
        catch (IOException | InterruptedException | ExecutionException e) {
            if(out != null) {
                writeResponse(out, HttpResponseGenerator.generate500Response(), "");
            }
        }
        future.complete(null);
        return future;
    }

    private void writeResponse(PrintWriter out, Map<String, String> response, String body) {
        for(Map.Entry<String, String> entry: response.entrySet()) {
            out.println(String.format("%s %s", entry.getKey(), entry.getValue()));
        }
        out.println(body);
    }
    public static String getSearchQuery(Map<String, String> requestDetails) throws HttpFormatException {
        String req = requestDetails.get("GET");
        if(req != null) {
            Matcher m = requestParameter.matcher(req);
            if (m.matches()) {
                return m.group(1);
            }
        }
        throw new HttpFormatException("Missing query parameter q");
    }
}
