package http;

import http.util.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpHeadersTest {

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    @DisplayName("요청 헤더")
    void parseHeaders() throws IOException {
        // given
        HttpRequest request = HttpRequest.from(bufferedReaderFromFile("src/test/resources/HttpHeader.txt"));

        // when & then
        assertEquals("localhost:8080", request.getHeader("Host"));
        assertEquals("keep-alive", request.getHeader("Connection"));
        assertEquals("55", request.getHeader("Content-Length"));
    }
}
