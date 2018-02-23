package http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HttpSrv {
	

	public static void start(int port,String context, HttpHandler httpHandler){
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		
			server.createContext(context, httpHandler);
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
