package webserver;

import http.model.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class RequestHandlerTest {

    private final String testOutputPath = "build/test_output.html";

    private OutputStream outputStreamToFile(String path) throws IOException {
        Files.createDirectories(Paths.get("build"));
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    void testHttpResponseForward() throws IOException {
        // given
        OutputStream out = outputStreamToFile(testOutputPath);
        HttpResponse response = new HttpResponse(out);

        // when
        response.forward("/index.html");

        // then
        String content = Files.readString(Paths.get(testOutputPath));

        assertTrue(content.contains("HTTP/1.1 200 OK"));
        assertTrue(content.contains("Content-Type: text/html"));
        assertTrue(content.contains("<html")); // 실제 index.html에 맞게 수정 가능
    }

    @Test
    void testHttpResponseRedirect() throws IOException {
        OutputStream out = outputStreamToFile(testOutputPath);
        HttpResponse response = new HttpResponse(out);

        response.sendRedirect("/login.html");

        String content = Files.readString(Paths.get(testOutputPath));

        assertTrue(content.contains("HTTP/1.1 302 Found"));
        assertTrue(content.contains("Location: /login.html"));
    }

    @Test
    void testHttpResponseRedirectWithCookie() throws IOException {
        OutputStream out = outputStreamToFile(testOutputPath);
        HttpResponse response = new HttpResponse(out);

        response.sendRedirectWithCookie("/index.html", "logined=true");

        String content = Files.readString(Paths.get(testOutputPath));

        assertTrue(content.contains("HTTP/1.1 302 Found"));
        assertTrue(content.contains("Location: /index.html"));
        assertTrue(content.contains("Set-Cookie: logined=true"));
    }
}
