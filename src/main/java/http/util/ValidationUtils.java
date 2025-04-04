package http.util;

import java.util.Arrays;

public class ValidationUtils {
    public static boolean isAnyBlank(String... values) {
        return Arrays.stream(values).anyMatch(v -> v == null || v.trim().isEmpty());
    }
}