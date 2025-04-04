package webserver;

import Enums.HttpMethod;
import Enums.StatusCode;
import Enums.StatusText;

import java.io.BufferedReader;
import java.io.IOException;

import static http.util.IOUtils.readData;

public class HttpRequest {
    private static HttpStartLine httpStartLine;
    private static String httpHeader;
    private static String body;
    public static HttpRequest from(BufferedReader br) throws IOException {
        String userRequest = br.readLine();
        if (userRequest==null){
            httpStartLine = null;
        }else {
            httpStartLine = new HttpStartLine(userRequest.split(" "));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        int requsetContentLength=0;
        while(!(line=br.readLine()).equals("")){
            if(line.startsWith("Content-Length:")){
                requsetContentLength = Integer.parseInt(line.split(":")[1].trim());
            }
            sb.append(line+"/r");
        }
        httpHeader = sb.toString();
        body = readData(br,requsetContentLength);
        return new HttpRequest(httpStartLine,httpHeader,body);
    }

    public HttpRequest(HttpStartLine httpStartLine, String httpHeader, String body){
        this.httpStartLine = httpStartLine;
        this.httpHeader = httpHeader;
        this.body = body;
    }

    public boolean startLineIsNull(){
        if(httpStartLine==null){
            return true;
        }
        return false;
    }

    public String getMethod(){
        return httpStartLine.getMethod();
    }
    public String getUrl(){
        return httpStartLine.getPath();
    }

    public static HttpStartLine getHttpStartLine() {
        return httpStartLine;
    }

    public static String getHttpHeader() {
        return httpHeader;
    }
    public String getBody(){
        return httpStartLine.getBody();
    }

    public static void setUrl(String url) {
        HttpRequest.httpStartLine.setPath(url);
    }
}
