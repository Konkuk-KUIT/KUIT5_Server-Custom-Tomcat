package webserver;

import Enums.*;
import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import jdk.jshell.Snippet;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Enums.StatusCode.*;
import static Enums.HttpMethod.*;
import static Enums.StatusText.*;
import static Enums.UserQuery.*;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEBAPPDIR = "webapp";
    private static final String HTTP_VERSION = "HTTP/1.1";
    private MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            // 서버가 클라이언트로부터 데이터를 받을 때 사용
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // 클라이언트 -> 서버로 데이터를 보내기 위해 이용
            DataOutputStream dos = new DataOutputStream(out);
            httpRequest = HttpRequest.from(br);
            //사용자 요청 url 추가

            if(httpRequest.startLineIsNull()==true){
                String body = "Invalid request format";
                StartLine startLine = new StartLine(HTTP_VERSION,StatusCode.BADREQUEST_CODE,StatusText.BADREQUEST);
                StringBuilder sb = new StringBuilder();// 이동할 URL
                sb.append("Content-Type: "+ ContentType.CONTENT_TYPE_HTML.getType()+"; charset=utf-8\r\n");
                sb.append("Content-Length: " + body.length() + "\r\n");
                sb.append("\r\n");
                httpResponse = new HttpResponse(dos,startLine,sb.toString(),body);
                httpResponse.forwardBody();
                return;
            }
            if(httpRequest.getMethod().equals(HttpMethod.GET.getMethod())){
                System.out.println("get");
                handleGetRequest(httpRequest,dos);
            }else if(httpRequest.getMethod().equals(HttpMethod.POST.getMethod())){
                System.out.println("post");
                handlePostRequest(httpRequest,dos);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void handleGetRequest(HttpRequest httpRequest,DataOutputStream dos) throws IOException {
        if(httpRequest.getUrl().equals("/")){
            httpRequest.setUrl("/index.html");
        }
        if(httpRequest.getUrl().contains(UserQuery.SHOW_USER_LIST.getQuery())){
            if(isUserLogined(httpRequest)){
                System.out.println("승낙");
                userRequsetFile(httpRequest,dos, ContentType.CONTENT_TYPE_HTML);
                return;
            }
            System.out.println("거절");
            StartLine startLine = new StartLine(HTTP_VERSION, REDIRECT_CODE, REDIRECT);
            StringBuilder sb = new StringBuilder();
            sb.append("Location: " + UserQuery.LOGIN.getQuery() + "\r\n");  // 이동할 URL
            sb.append("Content-Length: 0\r\n");  // 바디가 없음을 명시
            sb.append("\r\n");  // 헤더 종료
            httpResponse = new HttpResponse(dos,startLine,sb.toString(),null);
            httpResponse.redirect();
            return;
        }else if(httpRequest.getUrl().contains(HTML.getQuery())){
            userRequsetFile(httpRequest,dos,ContentType.CONTENT_TYPE_HTML);
        }else if(httpRequest.getUrl().contains(CSS.getQuery())){
            userRequsetFile(httpRequest,dos,ContentType.CONTENT_TYPE_CSS);
        }else if(httpRequest.getUrl().contains(JS.getQuery())){
            userRequsetFile(httpRequest,dos,ContentType.CONTENT_TYPE_JS);
        }else if(httpRequest.getUrl().contains(SIGNUP.getQuery())){
            Map<String, String> userMap = HttpRequestUtils.parseQueryParameter(httpRequest.getUrl());
            String userId=userMap.get("userId");
            String password=userMap.get("password");
            String name=userMap.get("name");
            String email=userMap.get("email");

            memoryUserRepository.addUser(new User(userId,password,name,email));
            StartLine startLine = new StartLine(HTTP_VERSION, REDIRECT_CODE, REDIRECT);
            StringBuilder sb = new StringBuilder();
            sb.append("Location: " + UserQuery.INDEX.getQuery() + "\r\n");  // 이동할 URL
            sb.append("Content-Length: 0\r\n");  // 바디가 없음을 명시
            sb.append("\r\n");  // 헤더 종료
            httpResponse = new HttpResponse(dos,startLine,sb.toString(),null);
            httpResponse.redirect();
        }
    }

    private void userRequsetFile(HttpRequest httpRequest, DataOutputStream dos, ContentType contentType) {
        File file = new File(WEBAPPDIR + httpRequest.getUrl());
        if(!file.exists() | file.isDirectory()){
            String body = "404 - File Not Found";
            StartLine startLine = new StartLine(HTTP_VERSION, FILENOTFOUND_CODE, StatusText.FILENOTFOUND);
            StringBuilder sb = new StringBuilder();// 이동할 URL
            sb.append("Content-Type: "+ contentType+"; charset=utf-8\r\n");
            sb.append("Content-Length: " + body.length() + "\r\n");
            sb.append("\r\n");
            httpResponse = new HttpResponse(dos,startLine,sb.toString(),body);
            httpResponse.forwardBody();
        }else{
            StatusCode statusCode = StatusCode.OK_CODE;
            StatusText statusText = StatusText.OK;
            StartLine startLine = new StartLine(HTTP_VERSION, StatusCode.OK_CODE, StatusText.OK);
            StringBuilder sb = new StringBuilder();// 이동할 URL
            sb.append("Content-Type: "+ contentType+"; charset=utf-8\r\n");
            sb.append("Content-Length: " + file.length() + "\r\n");
            sb.append("\r\n");
            httpResponse = new HttpResponse(dos,startLine,sb.toString(),null);
            httpResponse.forwardFile(file,contentType);
        }
    }

    private void handlePostRequest(HttpRequest httpRequest, DataOutputStream dos) throws IOException {
        Map<String,String> userMap = HttpRequestUtils.parseQueryParameter(httpRequest.getBody());
        if(httpRequest.getUrl().contains("signup")){
            String userId=userMap.get("userId");
            String password=userMap.get("password");
            String name=userMap.get("name");
            String email=userMap.get("email");

            memoryUserRepository.addUser(new User(userId,password,name,email));
            StartLine startLine = new StartLine(HTTP_VERSION, REDIRECT_CODE, REDIRECT);
            StringBuilder sb = new StringBuilder();
            sb.append("Location: " + UserQuery.INDEX.getQuery() + "\r\n");  // 이동할 URL
            sb.append("Content-Length: 0\r\n");  // 바디가 없음을 명시
            sb.append("\r\n");  // 헤더 종료
            httpResponse = new HttpResponse(dos,startLine,sb.toString(),null);
            httpResponse.redirect();
        }else if(httpRequest.getUrl().contains("login")){
            String inputId = userMap.get("userId");
            String inputPassword = userMap.get("password");
            User target;
            if((target=memoryUserRepository.findUserById(inputId))==null || (!target.equals(new User(inputId,inputPassword,target.getName(),target.getEmail())))){
                StartLine startLine = new StartLine(HTTP_VERSION, REDIRECT_CODE, REDIRECT);
                StringBuilder sb = new StringBuilder();
                sb.append("Location: " + UserQuery.LOGIN_FAILED.getQuery() + "\r\n");  // 이동할 URL
                sb.append("Content-Length: 0\r\n");  // 바디가 없음을 명시
                sb.append("\r\n");  // 헤더 종료
                httpResponse = new HttpResponse(dos,startLine,sb.toString(),null);
                httpResponse.redirect();
                return;
            }
            String cookie = "logined=true";
            StartLine startLine = new StartLine(HTTP_VERSION, REDIRECT_CODE, REDIRECT);

            StringBuilder sb = new StringBuilder();
            sb.append("Location: " + UserQuery.INDEX.getQuery() + "\r\n");// 이동할 URL
            sb.append("Set-Cookie: "+cookie+"; Path=/; HttpOnly;\r\n");
            sb.append("Content-Length: 0\r\n");  // 바디가 없음을 명시
            sb.append("\r\n");  // 헤더 종료
            httpResponse = new HttpResponse(dos,startLine,sb.toString(),null);
            httpResponse.redirect();
        }


    }
    private boolean isUserLogined(HttpRequest httpRequest) throws IOException {
        String [] header = httpRequest.getHttpHeader().split("/r");
        for (String line : header) {
            System.out.println(line);
            if (line.startsWith("Cookie:")) {
                String[] cookies = line.substring(7).trim().split("; ");
                for (String cookie : cookies) {
                    String[] keyValue = cookie.split("=");
                    if (keyValue.length == 2 && keyValue[0].trim().equals("logined") && keyValue[1].trim().equals("true")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



}
