package groovy.ui;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * SimpleHTTPServer for Groovy, inspired by Python's SimpleHTTPServer
 */
public class SimpleHttpServer {
    private HttpServer server;
    private int port;
    private String contextRoot;
    private String docBase;

    public SimpleHttpServer(final int port) throws IOException {
        this(port, "/", ".");
    }

    public SimpleHttpServer(final int port, final String contextRoot, final String docBase) throws IOException {
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
                            !"/".equals(SimpleHttpServer.this.contextRoot) && uri.startsWith(SimpleHttpServer.this.contextRoot) ? uri.substring(SimpleHttpServer.this.contextRoot.length()) : uri;

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
            try (BufferedInputStream bis =
                         new BufferedInputStream(
                                 new FileInputStream(
                                         new File((docBase + path).trim())));
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                for (int byteCnt; (byteCnt = bis.read(buffer)) != -1; ) {
                    baos.write(buffer, 0, byteCnt);
                }

                return baos.toByteArray();
            }
        }
    }

    public void start() {
        server.start();
        System.out.println("HTTP Server started up, visit http://localhost:" + this.port + this.contextRoot + "  to access the files in the " + this.docBase);
    }

    public static void main(String[] args) throws IOException {
        new SimpleHttpServer(8000).start();
    }
}
