package http.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestUtilsTest {

    @Test
    void parseQuery() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("userId=1");
        assertEquals("1",queryParameter.get("userId"));
    }

    @Test
    void parseQueryMore() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("userId=1&password=1");
        assertEquals("1",queryParameter.get("userId"));
        assertEquals("1",queryParameter.get("password"));
    }

    @Test
    void parseQueryZero() {
        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter("");
    }



}