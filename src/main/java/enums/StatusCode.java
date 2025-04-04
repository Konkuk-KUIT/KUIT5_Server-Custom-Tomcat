package enums;

public enum StatusCode {
    OK(200), FOUND(302), NOT_FOUND(404);

    private int code;

    StatusCode(int i) {
        code = i;
    }

    public int getCode() {
        return code;
    }

}
