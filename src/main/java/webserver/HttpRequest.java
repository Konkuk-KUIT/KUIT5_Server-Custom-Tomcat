package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpRequest {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    BufferedReader br;

    public HttpRequest(BufferedReader bufferedReader) {
        this.br = bufferedReader;
    }

    public static HttpRequest from(BufferedReader bufferedReader) {
        return new HttpRequest(bufferedReader);
    }

    private String getRequestFirstLine(BufferedReader br) throws IOException {
        try {
            return br.readLine();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return null;
    }
}
