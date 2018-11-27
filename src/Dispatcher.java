import java.io.IOException;
import java.net.Socket;

public class Dispatcher implements Runnable {
    private Socket client;
    private Request request;
    private Response response;
    private int code = 200;

    public Dispatcher(Socket client) {
        this.client = client;
        try {
            request = new Request(client.getInputStream());
            response = new Response(client.getOutputStream());
        } catch (IOException e) {
            code = 500;
        }
    }

    @Override
    public void run() {
        try {
            Servlet servlet = WebApp.getServlet(request.getRelativePath());
            if (servlet == null) {
                code = 404;
            } else {
                servlet.service(request, response);
            }
            response.pushToClient(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
        CloseUtil.close(client);
    }
}
