package webserver;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpRequest {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final BufferedReader br;

    public HttpRequest(BufferedReader bufferedReader) {
        this.br = bufferedReader;
    }

    public static HttpRequest from(BufferedReader bufferedReader) {
        return new HttpRequest(bufferedReader);
    }

    public String getRequestFirstLine() throws IOException {
        try {
            return br.readLine();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public String getRequestBody() throws IOException {
        try {
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
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    public String getCookieInfo() throws IOException {
        try {
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                if (line.startsWith("Cookie")) {
                    return line.split(": ")[1];
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }
}
