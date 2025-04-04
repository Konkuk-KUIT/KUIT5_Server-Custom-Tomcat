package constant;

public enum URL {
    HOME_URL("/index.html"),
    LOGIN("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    USER_LIST("/user/list.html"),
    ROOT("./webapp");
    private final String url;

    URL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
