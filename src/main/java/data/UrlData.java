package data;

public enum UrlData {
    DIRECTORY("webapp"),DEFAULT_URL("/index.html"),SIGNUP_URL("/user/signup"),
    LOGIN_URL("/user/login"),LIST_URL("/user/userList"), BLANK_URL("/"),
    END_HTML(".html"), GO_LIST_URL("/user/list.html"),LOGIN_FAIL_URL("/user/login_failed.html"),
    SIGN_URL("/user/form.html");
    final String data;
    UrlData(String data) {
        this.data = data;
    }

    public String getData(){
        return data;
    }
}
