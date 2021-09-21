package org.signal.secureproxy.http;

import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HttpResponseGenerator {

    private static SimpleDateFormat formatter= new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss z");
    private static Calendar calendar = Calendar.getInstance();
    private static String CL = "content-length";
    private static String CT = "content-type";

    public static Map<String, String> generate400Response() {
        Map<String, String> response = new HashMap<>();
        response.put("HTTP/1.1 ", "400 Bad Request");
        response.put("Date: ", formatter.format(calendar.getTime()));
        response.put(CL, "0");
        return response;
    }

    public static Map<String, String> generate500Response() {
        Map<String, String> response = new HashMap<>();
        response.put("HTTP/1.1 ", "500 Internal Server Error");
        response.put("Date: ", formatter.format(calendar.getTime()));
        response.put(CL, "0");
        return response;
    }

    public static Map<String, String> generateSuccessResponse(HttpResponse<String> giphyResponse) {
        Map<String, String> response = new HashMap<>();
        response.put("HTTP/1.1 ", String.format("%d", giphyResponse.statusCode()));
        response.put("Date: ", formatter.format(calendar.getTime()));
        response.put(CL + ": ", giphyResponse.headers().firstValue(CL).get());
        response.put(CT +  ": ", giphyResponse.headers().firstValue(CT).get());
        response.put("Connection: ", "Closed");
        return response;
    }
}
