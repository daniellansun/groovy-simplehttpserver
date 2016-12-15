# groovy-simplehttpserver
SimpleHTTPServer for Groovy, inspired by Python's SimpleHTTPServer

**Usage**

Launch a SimpleHttpServer instance
```
groovy SimpleHttpServer <port> <base dir|zip file> <context root>
```
Visit the following URL to access files
```
 http://localhost:<port>/<context root>/<relative path>
```

**For example**
1) listen on port 8000 and serve files under current directory
```
groovy SimpleHttpServer
```
2) listen on port 8080 and serve files under current directory
```
groovy SimpleHttpServer 8080
```
3) listen on port 8080 and serve files under the directory D:\temp
```
groovy SimpleHttpServer 8080 D:\temp
```
4) listen on port 8080 and serve files under the directory D:\temp, in addition its context root is gshs 
```
groovy SimpleHttpServer 8080 D:\temp gshs
```
5) listen on port 8080 and serve files in the zip file D:\temp\apidoc.zip
```
groovy SimpleHttpServer 8080 D:\temp\apidoc.zip
```
