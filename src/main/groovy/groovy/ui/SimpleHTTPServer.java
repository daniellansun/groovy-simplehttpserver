package groovy.ui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.codehaus.groovy.runtime.IOGroovyMethods;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * SimpleHTTPServer for Groovy, inspired by Python's SimpleHTTPServer
 */
public class SimpleHTTPServer {
    private HttpServer server;
    private int port;
    private String contextRoot;
    private String docBase;

    public SimpleHTTPServer(final int port) throws IOException {
        this(port, "/", ".");
    }

    public SimpleHTTPServer(final int port, final String contextRoot, final String docBase) throws IOException {
        this.port = port;
        this.contextRoot = contextRoot.startsWith("/") ? contextRoot : ("/" + contextRoot);
        this.docBase = docBase;

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext(this.contextRoot, new HttpHandler() {
            @Override
            public void handle(HttpExchange exchg) throws IOException {
                BufferedOutputStream bos = new BufferedOutputStream(exchg.getResponseBody());
                byte[] content = null;

                try {
                    String uri = exchg.getRequestURI().getPath();
                    String path =
                            !"/".equals(SimpleHTTPServer.this.contextRoot) && uri.startsWith(SimpleHTTPServer.this.contextRoot) ? uri.substring(SimpleHTTPServer.this.contextRoot.length()) : uri;

                    content = readContent(path, docBase);
                    exchg.sendResponseHeaders(HttpURLConnection.HTTP_OK, content.length);
                    bos.write(content);
                } catch (Exception e) {
                    content = e.getMessage().getBytes();
                    exchg.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, content.length);
                    bos.write(content);
                } finally {
                    bos.close();
                    exchg.close();
                }
            }
        });
    }

    private byte[] readContent(String path, String docBase) throws IOException {
        if ("/".equals(path)) {
            return "Groovy SimpleHTTPServer is running".getBytes();
        } else {
            return IOGroovyMethods.getBytes(
                            new BufferedInputStream(
                                    new FileInputStream(
                                            new File((docBase + path).trim()))));
        }
    }

    public void start() {
        server.start();
        System.out.println("HTTP Server started up, visit: http://localhost:" + this.port + this.contextRoot);
    }

    public static void main(String[] args) throws IOException {
        new SimpleHTTPServer(8000).start();
    }
}
