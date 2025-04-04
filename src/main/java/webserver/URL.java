package webserver;

import java.nio.file.Paths;

public enum URL {

    ROOT("/"),
    CURRENT_DIR(Paths.get("").toAbsolutePath().toString()),
    WEBAPP(CURRENT_DIR.getPath()+"/webapp"),
    INDEX_HTML(WEBAPP.getPath()+"/index.html"),

    USER(WEBAPP.getPath()+"/user"),
    CSS(WEBAPP.getPath()+"/css"),
    JS(WEBAPP.getPath()+"/js"),

    LOGIN_HTML(USER.getPath()+"/login.html"),
    LIST_HTML(USER.getPath()+"/list.html"),
    LOGIN_FAILED_HTML(USER.getPath()+"/login_failed.html"),

    STYLE_CSS(CSS.getPath()+"/styles.css"),
    SCRIPT_JS(JS.getPath()+"/script.js");
    private final String path;

    URL(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
