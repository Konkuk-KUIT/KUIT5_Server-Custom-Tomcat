package Enums;

public enum StatusCode {
    OK_CODE(200),
    REDIRECT_CODE(302),
    BADREQUEST_CODE(400),
    FILENOTFOUND_CODE(404);

    public final int Code;

    StatusCode(int statusCode){
        Code = statusCode;
    }

    public int getCode() {
        return Code;
    }
}
