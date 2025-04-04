package enums;

public enum UserKey {
    ID("userId"),
    PW("password"),
    NAME("name"),
    EMAIL("email");

    private String key;

    UserKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
