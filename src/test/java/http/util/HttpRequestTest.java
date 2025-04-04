package http.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {
    static String testDirectory;
    @BeforeAll
    static void setUp() {
        testDirectory = "C:\\Users\\bwgre\\JavaProject\\KUIT5_Server-Custom-Tomcat\\src\\test\\resources\\";
    }

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void request() throws IOException {
        String forwardPath = "resource.txt";
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testDirectory + forwardPath));
        assertEquals("/user/create", httpRequest.getUrl());
        assertEquals("POST", httpRequest.getMethod());

    }
}
