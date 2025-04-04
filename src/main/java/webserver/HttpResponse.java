package webserver;

import Enums.ContentType;
import Enums.StatusCode;
import Enums.StatusText;
import Enums.UserQuery;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static StartLine startLine;
    private static String httpHeader;
    private static String body;
    DataOutputStream dos;
    public HttpResponse(DataOutputStream dos, StartLine startLine, String httpHeader, String body){
        this.dos = dos;
        this.startLine = startLine;
        this.httpHeader = httpHeader;
        this.body = body;
    }

    public static StartLine getStartLine() {
        return startLine;
    }

    public static String getHttpHeader() {
        return httpHeader;
    }

    public static String getBody() {
        return body;
    }

    public void forwardFile(File file,ContentType contentType){
        sendFileResponse(dos,file,contentType);
    }

    public void forwardBody(){
        sendErrorResponse(dos, startLine.getStatusCode(), startLine.getMessage(), body);
    }

    public void redirect(){
        response302Header(dos);
    }

    private void response302Header(DataOutputStream dos){
        try {
            dos.writeBytes(startLine.getVersion()+" "+ StatusCode.REDIRECT_CODE.getCode()+" "+ StatusText.REDIRECT.getText()+"\r\n");  // 302 리다이렉트 상태 코드
            dos.writeBytes(httpHeader); // 헤더 종료
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, UserQuery path, String cookie){
        try {
            dos.writeBytes(startLine.getVersion()+" "+StatusCode.REDIRECT_CODE.getCode()+" "+StatusText.REDIRECT.getText()+"\r\n");  // 302 리다이렉트 상태 코드
            dos.writeBytes("Location: " + path.getQuery() + "\r\n");// 이동할 URL
            dos.writeBytes("Set-Cookie: "+cookie+"; Path=/; HttpOnly;\r\n");
            dos.writeBytes("Content-Length: 0\r\n");  // 바디가 없음을 명시
            dos.writeBytes("\r\n");  // 헤더 종료
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void sendErrorResponse(DataOutputStream dos, StatusCode statusCode, StatusText statusText, String body) {
        responseHeader(dos,statusCode,statusText,body.length(), ContentType.CONTENT_TYPE_HTML);
        responseErrorBody(dos,body);
    }

    private void sendFileResponse(DataOutputStream dos, File file, ContentType contentType){
        StatusCode statusCode = StatusCode.OK_CODE;
        StatusText statusText = StatusText.OK;
        responseHeader(dos,statusCode,statusText,file.length(),contentType);
        responseBody(dos,file);
    }

    private void responseHeader(DataOutputStream dos, StatusCode statusCode, StatusText statusText, long lengthOfBodyContent, ContentType contentType){
        try {
            dos.writeBytes(startLine.getVersion()+" "+statusCode.getCode()+" "+statusText.getText()+"\r\n");
            dos.writeBytes(httpHeader);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseErrorBody(DataOutputStream dos, String body) {
        try {
            dos.writeBytes(body);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void responseBody(DataOutputStream dos, File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while((bytesRead = fileInputStream.read(buffer))!= -1){
                dos.write(buffer,0,bytesRead);
            }
            dos.flush();
            fileInputStream.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
