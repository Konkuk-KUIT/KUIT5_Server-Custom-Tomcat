package data;

public enum Header {
    COOKIE("Cookie"),
    LOGIN("logined=true"),
    Content("Content-Length");
    final String data;
    Header(String data){
        this.data = data;
    }
    public String get(){
        return data;
    }
}
