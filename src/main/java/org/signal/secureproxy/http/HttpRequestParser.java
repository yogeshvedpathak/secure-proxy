package org.signal.secureproxy.http;

import org.signal.secureproxy.server.HandleClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequestParser {

    private static Pattern URL = Pattern.compile("(GET) /(.*) HTTP/1.1");
    private static Pattern HEADER = Pattern.compile("(.+): (.+)");


    public static void main(String[] args) throws IOException, HttpFormatException {
        BufferedReader br = new BufferedReader(new StringReader("GET /?q=i am excited HTTP/1.1"));
        Map<String, String> map = parseRequest(br);
        map.forEach((k, v) -> System.out.println(k + ": " + v));
    }
    public static Map<String, String> parseRequest(BufferedReader reader) throws IOException, HttpFormatException {
        Map<String, String> requestDetails = new HashMap<>();
        String line;
        if((line = reader.readLine()) != null && !line.isBlank()) {
            Matcher m = URL.matcher(line);
            if(m.matches()) {
                requestDetails.put(m.group(1), m.group(2));
            }
        }

        while((line = reader.readLine()) != null && !line.isBlank()) {
            Matcher m = HEADER.matcher(line);
            if(m.matches()) {
                requestDetails.put(m.group(1), m.group(2));
            }
        }
        if(requestDetails.isEmpty()) {
            throw new HttpFormatException("Bad Request");
        }
        return requestDetails;
    }
}