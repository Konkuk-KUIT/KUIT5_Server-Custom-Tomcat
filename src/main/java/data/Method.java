package data;

public enum Method {
    GET,POST,PUT,DELETE,HEAD,OPTIONS,TRACE,CONNECT;
    public String get() {
        return GET.name();
    }
}
