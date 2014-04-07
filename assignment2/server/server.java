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
			System.out.println("test");
			BufferedInputStream getBuf = new BufferedInputStream(get);
			byte[] data = new byte[512];
			int count = 0;
			try{
				while((count = getBuf.read(data, 0, data.length)) > 0)
					out.write(data, 0, count);
				out.flush();
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
	public static int put(BufferedOutputStream out, BufferedInputStream in, String command, String file){
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
			String keyStorePath = "server.keystore";
			String keyStorePassword = "server";
			KeyStore keyStore = socket.keyStoreTLS(keyStoreType, keyStorePath, keyStorePassword);
			KeyManagerFactory keyManagerFactory = socket.keyManagerTLS(keyStore, keyStorePassword);
			//truststore
			String trustStorePath = "servertrust.keystore";
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
			OutputStream out = sslSocket.getOutputStream();
			InputStream in = sslSocket.getInputStream();
			BufferedOutputStream outBuf = new BufferedOutputStream(out);
			BufferedInputStream inBuf = new BufferedInputStream(in);
			while(true){//loop indefinitely
				byte[] command = new byte[512];
				int count = in.read(command, 0, command.length);
				if (count == -1) break; //time to close the socket -- exit sent
				String commandParse = new String(command, 0, count);
				System.out.println(commandParse);
				String[] commandSplit = commandParse.split("[ ]+");
				int getPut = getPut(commandSplit);
				System.out.println(commandSplit[1].length());
				if (getPut < 0);
				else{
					if (getPut > 0) {
						int result = get (outBuf, commandParse, commandSplit[1]);
						if (result < 0) {
							String fail = "File not found";
							out.write(fail.getBytes());
							out.flush();
						}
					}
					else {
						int result = put(outBuf, inBuf, commandParse, commandSplit[1]);
						if (result < 0) {
							String fail = "Unable to write to directory";
							out.write(fail.getBytes());
							out.flush();
						}
					}
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
