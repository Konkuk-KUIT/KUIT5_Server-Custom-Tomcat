package webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers = new HashMap<>();

    public void addHeader(String headerLine) {
        String[] parts = headerLine.split(": ", 2);
        headers.put(parts[0], parts[1]);
    }

    public boolean containHeader(String key) {
        return headers.containsKey(key);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}
