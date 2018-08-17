# Wisp websocket server

Wisp is a lightweight, embeddable Websocket protocol server based on [Netty](http://www.netty.io) and Java 9+ 
modularization. Every service implements ```wisp.websocket.api.WebSocketService``` which in turn extends the JDK's
[jdk.incubator.http.WebSocket.Listener](https://docs.oracle.com/javase/9/docs/api/jdk/incubator/http/WebSocket.Listener.html) 
interface; it does not implement the heavier [J2EE Websocket](http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/HomeWebsocket/WebsocketHome.html) server interfaces,
which are more oriented toward application container-type environments.

## Requirements

Wisp requires a minimum of Java 10 plus the jdk.incubator.http module (via ```--add-module```)

As soon as JDK 11 goes GA and the jdk.incubator.http module is officially available, Wisp will be upgraded to match.

## Running

Run ```gradlew install``` to create build/image, which will give you a structure like this:

```
build/
  image/
    boot/
      *.jar
    modules/
      wisp.logger/
        *.jar
      wisp.websocket/
        *.jar
      wisp.websocket.echo/
        *.jar
```

then run Java 10 as follows, where ```wisp.conf``` points to an (optional) configuration file in HOCON format:

```bash
$ java -p build/image/boot --add-modules jdk.incubator.httpclient \
    -m wisp.boot/wisp.boot.WispBoot \
    -b build/image/ -c wisp.conf
```

e.g. if you want to run the example with SSL on (using a self-signed certificate at this time) you would provide
a configuration file as follows:

```hocon
wisp {
  websocket {
     port: 8043
     ssl: true
  }
}

```

Note that Wisp natively supports properties format as well, and will auto-recognize either HOCON (*.conf) or properties
(*.properties) based on the file extension. The equivalent to the above in properties syntax would be:

```properties
wisp.websocket.port=8043
wisp.websocket.ssl=true

```

## Extending

Take a look at ```wisp-websocket-echo``` for a sample of how to build a ```wisp.websocket.api.WebSocketService```. There
are three basic requirements:

* add a class implementing ```WebSocketService```
* add a ```module-info.java``` that declares this module ```provides wisp.websocket.api.WebSocketService with your.class.name```
* build a directory with the same name as your module containing all the JAR files you need *minus* any already provided in Wisp;
due to the strictness of the Jigsaw module mechanism every JAR must be provided once and only once

## Learning more

See my blog posting [Modular Microservices in Java 10](http://www.cloudwall.io/modular-microservices). In addition to covering the
internals of Wisp it has a number of good learning references for modular development in general. 

