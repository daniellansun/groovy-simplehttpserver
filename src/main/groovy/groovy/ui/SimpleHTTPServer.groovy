package groovy.ui

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer

import java.util.concurrent.Executors

/**
 * SimpleHTTPServer for Groovy, inspired by Python's SimpleHTTPServer
 */
class SimpleHTTPServer {
    private HttpServer server;
    private int port;
    private String contextRoot;
    private String docBase;

    public SimpleHTTPServer(final int port) {
        this(port, '/', '.');
    }

    public SimpleHTTPServer(final int port, final String contextRoot, final String docBase) {
        this.port = port;
        this.contextRoot = contextRoot;
        this.docBase = docBase;

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext(contextRoot, new HttpHandler() {
            @Override
            void handle(HttpExchange exchg) throws IOException {
                BufferedOutputStream bos = new BufferedOutputStream(exchg.getResponseBody());
                byte[] content = null;

                try {
                    content = new File("${docBase}${exchg.getRequestURI()}".trim()).bytes;
                    exchg.sendResponseHeaders(HttpURLConnection.HTTP_OK, content.length);
                    bos.write(content);
                } catch (Exception e) {
                    content = e.getMessage().bytes;
                    exchg.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, content.length);
                    bos.write(content);
                } finally {
                    bos.close();
                    exchg.close();
                }
            }
        });
    }

    public void start() {
        server.start();
        println "HTTP Server is listening on port ${this.port}"
    }

    public static void main(String[] args) {
        new SimpleHTTPServer(8000).start();
    }
}
