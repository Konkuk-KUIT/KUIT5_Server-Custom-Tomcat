package webserver;

import constant.RequestPath;
import db.MemoryUserRepository;
import db.Repository;
import model.User;
import webserver.controller.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;
    private Controller controller = new ForwardController();

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest  = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            if(httpRequest.getMethod().equals("GET") && httpRequest.getUrl().endsWith(".html")){
                controller = new ForwardController();
            }

            if(httpRequest.getUrl().equals(RequestPath.ROOT.getPath())){
                controller = new HomeController();
            }

            if(httpRequest.getUrl().equals(RequestPath.SIGNUP.getPath())){
                controller = new SignUpController();
            }

            if(httpRequest.getUrl().equals(RequestPath.LOGIN.getPath())){
                controller = new LoginController();
            }

            if(httpRequest.getUrl().equals(RequestPath.USER_LIST.getPath())){
                controller = new ListController();
            }
            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }
}
