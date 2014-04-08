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
		if (inputSplit[0].equals("get")) return 1; //GET
		else if (inputSplit[0].equals("put")) return 0; //PUT
		else return -1; //not get or put (or exit by extension)
	}
	//get: Sends file to client
	public static int get(BufferedOutputStream out, String command, String file){
		try{
			FileInputStream get = new FileInputStream(file); //create file input stream
			BufferedInputStream getBuf = new BufferedInputStream(get); //buffer file input
			byte[] data = new byte[512]; //where file is read in
			int count = 0; //incremented by file reading
			try{
				while((count = getBuf.read(data, 0, data.length)) > 0)
					out.write(data, 0, count); //write to client
				out.flush(); //flush output
				getBuf.close(); //close file buffer
				return 0; //successful write
			} catch (IOException e){//failed write
				return serverError.WriteError();
			}
		} catch (FileNotFoundException e){//file not found
			return serverError.FileError();
		}
	}
	//put: Places file in server directory
	public static int put(BufferedOutputStream out, BufferedInputStream in, String command, String file){
		try{
			FileOutputStream put = new FileOutputStream(file); //create file output stream
			BufferedOutputStream putBuf = new BufferedOutputStream(put); //buffer file output
			byte[] data = new byte[512]; //file contents
			int count = 0; //to be incremented by data read
			try{
				while((count = in.read(data, 0, data.length)) > 0) //read in buffered file from client
					putBuf.write(data, 0, count); //write out to file
				putBuf.flush(); //flush buffer
				putBuf.close(); //close buffer
				return 0; //successful write
			} catch (IOException e){//failed write
				return serverError.WriteError();
			}
		} catch (FileNotFoundException e){//file not found
			return serverError.FileError();
		}
	}

	public static void main(String[] args){
		serverError.argLength(args);
		int serverPort = serverError.portTest(args[0]);; //server Port
		try{
			String keyStoreType = "JKS"; //required key store type
			String keyStorePath = "server.keystore"; //path of key store
			String keyStorePassword = "server"; //key store password
			//generate keystore
			KeyStore keyStore = socket.keyStoreTLS(keyStoreType, keyStorePath, keyStorePassword);
			//generate key manager factory
			KeyManagerFactory keyManagerFactory = socket.keyManagerTLS(keyStore, keyStorePassword);
			String trustStorePath = "servertrust.keystore"; //path to trust store
			String trustStorePassword = keyStorePassword; //trust store password
			//generate trust store
			KeyStore trustStore = socket.keyStoreTLS(keyStoreType, trustStorePath, trustStorePassword);
			//generate trust manager factory
			TrustManagerFactory trustManagerFactory = socket.trustManagerTLS(trustStore);
			//create SSL Context
			SSLContext sslContext =	
				socket.SSLContextTLS(keyManagerFactory, trustManagerFactory, keyStorePassword);
			//create server socket factory
			SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
			//create server socket
			SSLServerSocket serverSocket = 
				(SSLServerSocket) serverSocketFactory.createServerSocket(serverPort);
			//set the need for client authentication (required for two-way auth)
			serverSocket.setNeedClientAuth(true);
			//set TLS v1 as socket protocol
			serverSocket.setEnabledProtocols(new String[]{"TLSv1"});
			//accept socket connection
			SSLSocket sslSocket = (SSLSocket) serverSocket.accept();
			//open I/O stream on the socket
			OutputStream out = sslSocket.getOutputStream(); //output stream
			InputStream in = sslSocket.getInputStream(); //input stream
			BufferedOutputStream outBuf = new BufferedOutputStream(out); //buffered output stream
			BufferedInputStream inBuf = new BufferedInputStream(in); //buffered input stream
			while(!sslSocket.isOutputShutdown()){//close if connection closed
				byte[] command = new byte[512]; //where command is read in
				int count = in.read(command, 0, command.length); //keeps track of command size
				if (count == -1) break; //time to close the socket -- exit sent
				String commandParse = new String(command, 0, count); //convert input to string
				String[] commandSplit = commandParse.split("[ ]+"); //parse input
				int getPut = getPut(commandSplit);//check for GET or PUT (or error)
				if (getPut < 0); //invalid input -- should not happen
				else{ //valid input
					if (getPut > 0) { //GET
						//check for valid result
						int result = get (outBuf, commandParse, commandSplit[1]);
						if (result < 0) {//file not found
							String fail = "File not found";
							out.write(fail.getBytes()); //write error to client
							out.flush(); //flush output
						}
					}
					else { //PUT
						//check for valid result
						int result = put(outBuf, inBuf, commandParse, commandSplit[1]);
						if (result < 0) {//invalid R/W
							String fail = "Unable to write to directory";
							out.write(fail.getBytes()); //write error to client
							out.flush(); //flush output
						}
					}
				}
			}
			inBuf.close(); //close input stream
			outBuf.close(); //close output stream
			sslSocket.close(); //close socket
		} catch (Exception e){
			e.printStackTrace(); //print any exceptions
		}
	}
	private static class serverError{
		public static int WriteError(){ //output if writing not allowed
			System.out.println("Writing not allowed");
			return -1;
		}
		public static int FileError(){ //output if file not found
			System.out.println("File not found");
			return -1;
		}
				private static void argLength(String[] args){//checks for insufficient input size
			if (args.length != 2){ 
				System.out.println("Input: <port number>");
				System.out.println("client requires 1 input parameters.");
				System.exit(-1);
			}
		}
		private static int portTest(String port) throws NumberFormatException{ //checks for a valid integer for client 1 port number
			try {
				int serverPort = Integer.parseInt(port);
				return serverPort;
			} catch (NumberFormatException e){
				System.out.println("Input: <server's ip or hostname> <port number>");
				System.out.println("<port number>: Please input a valid integer.");
				System.exit(-1);
			}
			return 0;
		}

	}

}
