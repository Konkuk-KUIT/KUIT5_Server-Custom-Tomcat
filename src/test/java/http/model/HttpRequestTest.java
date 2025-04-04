package http.model;

import http.enums.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.http.HttpRequest;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestTest {

//    @Test
//    void testParseHttpRequest() throws Exception {
//        // 테스트 리소스 파일 경로 불러오기
//        URL resource = getClass().getClassLoader().getResource("http_request.txt");
//        assertNotNull(resource);
//
//        File file = new File(resource.toURI());
//        BufferedReader br = new BufferedReader(new FileReader(file));
//
//        // HttpRequest 객체 생성
//        HttpRequest request = HttpRequest.from(br);
//
//        // 검증
//        assertEquals(HttpMethod.POST, request.getMethod());
//        assertEquals("/user/create", request.getPath());
//        assertEquals("HTTP/1.1", request.getVersion());
//        assertEquals("localhost:8080", request.getHeader("Host"));
//        assertEquals("keep-alive", request.getHeader("Connection"));
//        assertEquals("40", request.getHeader("Content-Length"));
//        assertEquals("*/*", request.getHeader("Accept"));
//
//        assertEquals("userId=jw&password=password&name=jungwoo", request.getBody());
//    }
}
