package Enums;

public enum StatusText {
    OK("OK"),
    BADREQUEST("Bad Request"),
    FILENOTFOUND("Not Found"),
    REDIRECT("Found");

    public final String Text;

    StatusText(String statusText){
        Text = statusText;
    }

    public String getText() {
        return Text;
    }
}
