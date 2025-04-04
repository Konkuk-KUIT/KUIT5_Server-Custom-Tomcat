package http.util;

import http.util.httpRequest.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
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


    @Test
    @DisplayName("POST 요청 테스트")
    public void testPostRequest() throws IOException {
        // postTest.txt 파일 읽기
        BufferedReader br = new BufferedReader(new FileReader("src/test/resources/testMessage/postTest.txt"));
        HttpRequest httpRequest = HttpRequest.from(br);

        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/user/signup", httpRequest.getPath());

        Map<String, String> bodyMap = httpRequest.getBodyMap();

        assertEquals(bodyMap.get("userId"), "123");
        assertEquals(bodyMap.get("password"), "123");
        assertEquals(bodyMap.get("name"), "123");

    }

}