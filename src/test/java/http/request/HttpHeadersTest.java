package http.request;

import http.constants.HttpHeader;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpHeadersTest {
    private final String testDirectory = "./src/test/resources/";
    private final String headerPath = "HttpHeader.txt";

    @Test
    void header() throws IOException {
        HttpHeaders httpHeaders = HttpHeaders.from(bufferedReaderFromFile(testDirectory + headerPath));

        assertThat(httpHeaders.get(HttpHeader.CONNECTION)).isEqualTo("keep-alive");
        assertThat(httpHeaders.get(HttpHeader.HOST)).isEqualTo("localhost");
        assertThat(httpHeaders.get(HttpHeader.CONTENT_LENGTH)).isEqualTo("36");
    }

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }
}
