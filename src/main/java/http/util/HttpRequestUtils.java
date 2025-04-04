package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    public static Map<String, String> parseQueryParameter(String queryString) {
        // 
        try {
            // 쿼리 문자열 구분자
            String[] queryStrings = queryString.split("&");

            // 각 쿼리 항목을 '='로 분리하여 키-값 쌍을 만들어 Map에 저장
            return Arrays.stream(queryStrings)
                    .map(q -> q.split("="))
                    // map으로 변환
                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
        } catch (Exception e) {
            // 예외 발생시 빈 HashMap 반환
            return new HashMap<>();
        }
    }
}
