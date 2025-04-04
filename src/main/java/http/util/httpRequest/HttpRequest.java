package http.util.httpRequest;

import http.util.HttpRequestUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private StartLine startLine;
    private Headers headers = new Headers();
    private String body = "";

    public static HttpRequest from(BufferedReader br) throws IOException {
        return new HttpRequest(br);
    }

    private HttpRequest(BufferedReader br) throws IOException {

        String line = br.readLine();
        System.out.println(line);
        if (line != null) {
            String[] parts = line.split(" ");
            startLine = new StartLine(parts[0], parts[1], parts[2]);
        }

        while ((line = br.readLine()) != null && !line.isEmpty()) {
            System.out.println(line);
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.putHeader(headerParts[0], headerParts[1]);
            }
        }

        if (headers.getHeaderMap().containsKey("Content-Length")) {

            int contentLength = Integer.parseInt(headers.getHeaderMap().get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);

        }



    }


    public Map<String, String> getHeaders() {
        return headers.getHeaderMap();
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getBodyMap() {
        return HttpRequestUtils.parseQueryParameter(body);
    }

}