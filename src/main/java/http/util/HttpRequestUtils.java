package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    public static Map<String, String> parseQueryParameter(String queryString) {
        try {
            String[] queryStrings = queryString.split("&");

            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static Map<String, String> parseCookies(String cookieLine) {
        return Arrays.stream(cookieLine.split(";")) // 쿠키 단위 나누기
                .map(String::trim) // 공백 제거
                .map(s -> s.split("=", 2)) // = 여러개가 있어도 처음 한 번만 나눔
                .filter(pair -> pair.length == 2) // a=b 형태가 아닌 이상한 쿠키 필터링
                .collect(Collectors.toMap(pair -> pair[0], pair -> pair[1])); // map으로 수집
    }
}
