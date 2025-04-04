package Enums;

public enum HttpMethod {

    GET("GET"),
    POST("POST");

    public final String Method;

    HttpMethod(String httpMethod){
        Method = httpMethod;
    }

    public String getMethod() {
        return Method;
    }
}
