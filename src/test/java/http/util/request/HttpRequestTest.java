package http.util;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpRequestTest {

    @Test
    void 정상_동작() throws IOException {
        String input = "GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(input)));

        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getPath()).isEqualTo("/index.html");
    }

    @Test
    void 쿼리_정상_동작() throws IOException {
        String input = "GET /index.html?name=query HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(input)));

        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getQueryParams().get("name")).isEqualTo("query");
    }

    @Test
    void 지원하지_않는_메소드() {
        String input = "PATCH /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        assertThrows(IllegalArgumentException.class, () ->
                HttpRequest.from(new BufferedReader(new StringReader(input))));
    }

    @Test
    void 이상한_문장() {
        String input = "안녕하세요\r\nHost: localhost\r\n\r\n";
        assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                HttpRequest.from(new BufferedReader(new StringReader(input))));
    }

    @Test
    void 쿼리_존재하지_않을_때() throws IOException {
        String input = "POST /user/signup? HTTP/1.1\r\nHost: localhost\r\n\r\n";
        HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(input)));

        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getPath()).isEqualTo("/user/signup");
    }
}
