package http.util.httpRequest;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    private Map<String, String> headerMap;

    public Headers() {
        this.headerMap = new HashMap<>();
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void putHeader(String key, String value){
        headerMap.put(key, value);
    }
}
