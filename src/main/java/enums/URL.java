package enums;

public enum URL {
    ROOT("/"),
    INDEX_HTML("/index.html"),
    LIST_HTML("/user/list.html"),
    LOGIN_HTML("/user/login.html"),
    LOGIN_FAILED_HTML("/user/login_failed.html"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login");

    private final String url;

    URL(String s) {
        this.url = s;
    }

    public String getUrl() {
        return url;
    }
}
