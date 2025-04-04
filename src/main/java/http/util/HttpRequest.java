package http.util;

import data.Header;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpRequest {
    private static final Logger log = Logger.getLogger(HttpRequest.class.getName());
    private String method;
    private String url;
    private String body;
    public Map<String, String> headers = new HashMap<>();

    private HttpRequest(BufferedReader br) {
        try{
            String requestLine=br.readLine();
            System.out.println(requestLine);
            if (requestLine == null || requestLine.isEmpty()) return;
            String[] tokens = requestLine.split(" ");
            method = tokens[0];
            url = tokens[1];

            while (true) {
                String line = br.readLine();
                if (line == null || line.isEmpty()) break;
                String[] headerParts = line.split(": ", 2);
                headers.put(headerParts[0], headerParts[1]);
            }
            body= IOUtils.readData(br,Integer.parseInt(headers.getOrDefault(Header.Content.get(), "0")));
            System.out.println(body);
        }
        catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    public static HttpRequest from(BufferedReader br) {
        return new HttpRequest(br);
    }

    public String getMethod() {
        return method;
    }


    public String getUrl() {
        return url;
    }
    public String getBody() {
        return body;
    }

}
