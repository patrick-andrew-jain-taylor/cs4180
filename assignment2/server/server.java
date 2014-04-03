import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;

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
			SSLServerSocket sslServerSocket = serverSocket(serverPort);
			SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept(); //accept incoming connection on port
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
