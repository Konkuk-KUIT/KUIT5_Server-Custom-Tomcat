package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static enums.HttpMethod.POST;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {
    @Test
    @DisplayName("HttpRequest 생성 확인")
    void testHttpRequest() throws IOException, URISyntaxException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("src/test/java/resource/msg1.txt"));

        assertEquals(POST.getMethod(), httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getURI().toString());
        assertEquals("userId=jw&password=password&name=jungwoo",httpRequest.getBody());
    }

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }
}
