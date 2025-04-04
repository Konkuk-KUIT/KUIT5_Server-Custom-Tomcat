package webserver;

import http.*;
import http.controller.*;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private Controller controller;

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpResponse response = new HttpResponse(out);
            HttpRequest request = HttpRequest.from(br);

            HttpMethod method = HttpMethod.from(request.getMethod());
            String url = request.getPath();

            UrlPath matchedPath = UrlPath.from(url);

            switch (matchedPath) {
                case ROOT:
                    controller = new HomeController();
                    break;

                case SIGNUP:
                    if (method == HttpMethod.POST) {
                        controller = new SignUpController();
                    }
                    break;

                case LOGIN:
                    if (method == HttpMethod.POST) {
                        controller = new LoginController();
                    }
                    break;

                case USER_LIST:
                    controller = new UserListController();
                    break;

                default:
                    controller = new StaticFileController(url);
                    break;
            }

            if (controller != null) {
                controller.execute(request, response);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
