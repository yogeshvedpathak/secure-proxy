package org.signal.secureproxy.giphy;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GIPHYForwarder {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        HttpResponse<String> excited = forward("excited").get();
        HttpHeaders headers = excited.headers();

        System.out.println(excited.headers());
        System.out.println(excited.body());
        
    }
    private static final String GIPHY_URL =
            "https://api.giphy.com/v1/gifs/search?api_key=X1Xn26OrWK1w65j3Vjd3rQpbZtREnOTS&q=%s&limit=25&offset=0&rating=g&lang=en";

    public static CompletableFuture<HttpResponse<String>> forward(String search) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
        String url = String.format(GIPHY_URL, search).replaceAll(" ", "%20");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

    }
}
