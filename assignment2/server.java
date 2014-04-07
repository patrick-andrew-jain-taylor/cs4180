import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import javax.net.ssl.*;

public class server{
	public static SSLServerSocket serverSocket(int serverPort) throws Exception{
		//set system properties
		System.setProperty("javax.net.ssl.keyStore", "server.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword", "server");
		SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault(); //use default settings for server socket factory
		//create SSL Socket for client
		SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(serverPort); //create socket on serverPort
		//require socket to have client authentication
		sslServerSocket.setNeedClientAuth(true);
		return sslServerSocket;
	}
	public static void main(String[] args){
		int serverPort = 9955; //server Port
		try{
			//create SSLcontext
			String keyStoreType = "JKS";
			String keyStorePath = "server/server.keystore";
			String keyStorePassword = "server";
			KeyStore keyStore = socket.keyStoreTLS(keyStoreType, keyStorePath, keyStorePassword);
			KeyManagerFactory keyManagerFactory = socket.keyManagerTLS(keyStore, keyStorePassword);
			//truststore
			String trustStorePath = "server/servertrust.keystore";
			String trustStorePassword = keyStorePassword;
			KeyStore trustStore = socket.keyStoreTLS(keyStoreType, trustStorePath, trustStorePassword);
			TrustManagerFactory trustManagerFactory = socket.trustManagerTLS(trustStore);
			//bring it all together
			SSLContext sslContext =	
				socket.SSLContextTLS(keyManagerFactory, trustManagerFactory, keyStorePassword);
			//open socket
			SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
			SSLServerSocket serverSocket = 
				(SSLServerSocket) serverSocketFactory.createServerSocket(serverPort);
			serverSocket.setNeedClientAuth(true);
			serverSocket.setEnabledProtocols(new String[]{"TLSv1"});
			SSLSocket sslSocket = (SSLSocket) serverSocket.accept();
			//open a buffered stream on the socket
			BufferedInputStream in = new BufferedInputStream(sslSocket.getInputStream());
			in.read();
			in.close(); //close input stream
			sslSocket.close(); //close socket
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
