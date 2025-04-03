package http.util;

public enum HttpMethod {

    GET, POST;

    public static boolean isEqual(String inMethod, HttpMethod target) {
        return target.name().equalsIgnoreCase(inMethod);

    }
}
