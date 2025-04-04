package webserver;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class HttpRequest {

    private final BufferedReader br;
    private String requestFirstLine;
    private String url;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        this.br = bufferedReader;
        this.requestFirstLine = this.getRequestFirstLine();
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        return new HttpRequest(bufferedReader);
    }

    private String getRequestFirstLine() throws IOException {
        requestFirstLine = br.readLine();
        return requestFirstLine;
    }

    public String getRequestBody() throws IOException {

        int requestContentLength = 0;
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        return IOUtils.readData(br, requestContentLength);
    }

    public String getCookieInfo() throws IOException {

        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            if (line.startsWith("Cookie")) {
                return line.split(": ")[1];
            }

        }
        return null;
    }


    public String getMethod(){
        String s[] = requestFirstLine.split(" ");
        return s[0];
    }

    public String getUrl(){
        String s[] = requestFirstLine.split(" ");
        return s[1];
    }


}
