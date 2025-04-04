package Enums;

public enum UserQuery {
    INDEX("/index.html"),
    SHOW_USER_LIST("/user/userList.html"),
    LOGIN("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    HTML(".html"),
    CSS(".css"),
    JS(".js"),
    SIGNUP("signup");

    public final String Query;

    UserQuery(String userQuery) {
        Query = userQuery;
    }

    public String getQuery() {
        return Query;
    }
}
