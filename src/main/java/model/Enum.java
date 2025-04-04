package model;

public enum Enum {
    WEBAPP_PATH("./webapp"),
    INDEX("/index.html"),
    USER_LIST_PAGE("/user_list.html"),
    LOGIN_PAGE("/login.html"),
    LOGIN_FAILED_PAGE("/login_failed.html");


    private final String string;

    Enum(String path) {
        this.string = path;
    }

    public String getString() {
        return string;
    }
}
