package http.httpEnum;

public enum UrlPath {
    ROOT("/"),
    INDEX("/index.html"),
    LOGIN("/user/login"),
    LOGIN_HTML("/user/login.html"),
    SIGNUP("/user/signup"),
    SIGNUP_HTML("user/form.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    USER_LIST("/user/userList"),
    USER_LIST_HTML("/user/list.html"),
    UNKNOWN(""); // fallback

    private final String path;

    UrlPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static UrlPath from(String path) {
        for (UrlPath urlPath : values()) {
            if (urlPath.getPath().equals(path)) {
                return urlPath;
            }
        }
        return UNKNOWN;
    }
}
