import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import javax.net.ssl.*;

public class server{
	//getPut: checks for valid GET/PUT command
	public static int getPut(String inputSplit[]){
		//check for proper length
		if (inputSplit.length != 2) return -1; //improper input size	
		//check for get or put
		if (inputSplit[0].equals("get")) return 1;
		else if (inputSplit[0].equals("put")) return 0;
		else return -1; //not get or put (or exit by extension)
	}
	//get: Sends file to client
	public static int get(BufferedOutputStream out, String command, String file){
		try{
			FileInputStream get = new FileInputStream(file);
			BufferedInputStream getBuf = new BufferedInputStream(get);
			byte[] data = new byte[512];
			int count = 0;
			try{
				while((count = getBuf.read(data, 0, data.length)) > 0)
					out.write(data, 0, count);
				getBuf.close();
				return 0;
			} catch (IOException e){
				return serverError.WriteError();
			}
		} catch (FileNotFoundException e){
			return serverError.FileError();
		}
	}
	//put: Places file in server directory
	public static int put(BufferedInputStream in, String command, String file){
		try{
			FileOutputStream put = new FileOutputStream(file);
			BufferedOutputStream putBuf = new BufferedOutputStream(put);
			byte[] data = new byte[512];
			int count = 0;
			try{
				while((count = in.read(data, 0, data.length)) > 0) //read in buffered file from client
					putBuf.write(data, 0, count);
				putBuf.flush();
				putBuf.close();
				return 0;
			} catch (IOException e){
				return serverError.WriteError();
			}
		} catch (FileNotFoundException e){
			return serverError.FileError();
		}
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
			BufferedOutputStream outBuf = new BufferedOutputStream(sslSocket.getOutputStream());
			BufferedInputStream inBuf = new BufferedInputStream(sslSocket.getInputStream());
			while(true){//loop indefinitely
				byte[] command = new byte[512];
				inBuf.read(command, 0, command.length);
				String commandParse = new String(command, "UTF-8");
				String[] commandSplit = commandParse.split("[ ]+");
				if (commandSplit[0].equals("exit")) break; //time to close the socket
				int getPut = getPut(commandSplit);
				if (getPut < 0);
				else{
					if (getPut > 0) get (outBuf, commandParse, commandSplit[1]);
					else put(inBuf, commandParse, commandSplit[1]);
				}
			}
			inBuf.close(); //close input stream
			outBuf.close(); //close output stream
			sslSocket.close(); //close socket
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	private static class serverError{
		public static int WriteError(){
		System.out.println("Writing not allowed");
		return -1;
		}
		public static int FileError(){
		System.out.println("File not found");
		return -1;
		}
	}

}
