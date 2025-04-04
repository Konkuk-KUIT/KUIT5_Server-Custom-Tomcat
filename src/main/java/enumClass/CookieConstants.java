package enumClass;

import java.nio.file.Path;

public enum CookieConstants {

    LOGIN_COOKIE("Set-Cookie: logined=true; Path=/; HttpOnly\r\n");

    private final String value;

    CookieConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
