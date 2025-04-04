package http.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpResponseTest {

    static String testDirectory;
    @BeforeAll
    static void setUp() {
        testDirectory = "C:\\Users\\bwgre\\JavaProject\\KUIT5_Server-Custom-Tomcat\\webapp\\";
    }

    private DataOutputStream outputStreamToFile(String path) throws IOException {
        return new DataOutputStream(Files.newOutputStream(Paths.get(path)));
    }
    @Test
    void request() throws IOException {
        String forwardPath = "resource.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(testDirectory+forwardPath));
        httpResponse.forward("/index.html");
    }
}
