import java.security.KeyStore;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import javax.net.ssl.*;
import javax.net.ssl.SSLContext;
import java.security.cert.Certificate;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class client{
	//getPut: checks for valid GET/PUT command
	public static int getPut(String inputSplit[]){
				//check for proper length
		if (inputSplit.length != 2) return -1; //improper input size	
		//check for get or put
		if (inputSplit[0].equals("get")) return 1;
		else if (inputSplit[0].equals("put")) return 0;
		else return -1; //not get or put (or exit by extension)
	}
	//get: Retrieves file from server
	public static int get(BufferedInputStream in, String command, String file){
	 	File fileOut = new File(file); //create new file to be output to
		try{
			FileOutputStream get = new FileOutputStream(fileOut); //open file output stream
			BufferedOutputStream getBuf = new BufferedOutputStream(get); //buffer file output
			byte[] data = new byte[512]; //will read in file data
			int count = 0;
			try{
				while((count = in.read(data, 0, data.length)) > 0){ //read in buffered file from server
					String dataParse = new String(data, 0, count); //create string from data
					String fail = "File not found"; //to be compared to
					if (dataParse.equals(fail)) { //make comparison
						System.out.println(fail); //file not found
						break;
					}
					getBuf.write(data, 0, count); //write data to file
					if (count != data.length) break; //break loop if EOF
				}
				getBuf.flush(); //flush file buffer
				getBuf.close(); //close file buffer
				return 0;
			} catch (IOException e){
				return clientError.WriteError(); //writing failed
			}
		} catch (FileNotFoundException e){
			return clientError.FileError(); //file not found
		} 
	}
	//put: Places file on server
	public static int put(BufferedOutputStream out, String command, String file){
		try{
			out.write(command.getBytes()); //send command to server
			out.flush(); //flush output stream
			FileInputStream put = new FileInputStream(file); //generate new file input stream
			BufferedInputStream putBuf = new BufferedInputStream(put); //buffer the input stream
			byte[] data = new byte[512]; //will store file data
			int count = 0; //increments when data is read
			try{
				while((count = putBuf.read(data, 0, data.length)) > 0){ //read in buffered file from client
					out.write(data, 0, count); //write file to server
				}
				out.flush(); //flush output buffer
				putBuf.close(); //close file buffer
				return 0; //successful write
			} catch (IOException e){
				return clientError.WriteError(); //unsuccessful write
			}
		} catch (FileNotFoundException e){
			return clientError.FileError(); //file not found
		}  catch (IOException e){
			return clientError.WriteError(); //error with writing to server
		}
	}
	//acceptIn: to be printed out for all invalid user input
	public static void acceptIn(){
		System.out.println("Acceptable inputs:");
		System.out.println("get [<path>/filename]");
		System.out.println("put [<path>/filename]");
		System.out.println("exit");
	}
	//hostAvailabilityCheck: instantiates socket to connect to server if still available
	public static boolean hostAvailabilityCheck(String serverIP, int serverPort){
		try {
			Socket s = new Socket(serverIP, serverPort); //instantiate socket
			return true;
		} catch (IOException e){
			System.out.println("Server closed.");
		}
		return false;
	}
	public static void userInput(SSLSocket sslSocket, String serverIP, int serverPort) throws IOException{
		//input: used for terminal input
		String input = "start";
		//create buffered streams for the socket
		OutputStream out = sslSocket.getOutputStream(); //the socket output stream
		BufferedOutputStream outBuf = new BufferedOutputStream(out); //buffered socket output stream
		InputStream in = sslSocket.getInputStream(); //socket input stream
		BufferedInputStream inBuf = new BufferedInputStream(in); //buffered socket input stream
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); //terminal reader
		System.out.print("> "); //to be displayed in the terminal
		while (hostAvailabilityCheck(serverIP, serverPort)){ //close if socket closes
				//take input from the terminal
				input = br.readLine();
				//split input into strings
				String[] inputSplit = input.split("[ ]+"); //splits string into array of space-delimited strings
				//check if "exit" occurs
				if (inputSplit[0].equals("exit")){ //time to close the socket
					outBuf.write(input.getBytes()); //write "exit" to server
					break;
				}
				//if beyond this point, function is either GET or PUT and string needs to be parsed
				int getPut = getPut(inputSplit); //check if GET or PUT (or error)
				if(getPut < 0) acceptIn(); //invalid input
				else {
					if (getPut > 0) { //GET
						outBuf.write(input.getBytes()); //send command to server
						outBuf.flush(); //flush output buffer
						get(inBuf, input, inputSplit[1]); //get file from server
					}
					else put(outBuf, input, inputSplit[1]); //put file on server
				}
				//next terminal line
				System.out.println();
				System.out.print("> ");
		}
		//close buffers
		br.close();
		in.close();
		out.close();
	}
	public static void main(String[] args){
		clientError.argLength(args); //args length test
		String serverIP = clientError.serverIpTest(args[0]); //server IP test
		int serverPort = clientError.portTest(args[1]); //server Port test
		//create socket to server
		try{
			//create SSLcontext
			String keyStoreType = "JKS"; //required type for instantiated keystore
			String keyStorePath = "client.keystore"; //directory of keystore
			String keyStorePassword = "client"; //password of keystore
			//generate keystore
			KeyStore keyStore = socket.keyStoreTLS(keyStoreType, keyStorePath, keyStorePassword);
			//generate key manager factory
			KeyManagerFactory keyManagerFactory = socket.keyManagerTLS(keyStore, keyStorePassword);
			//generate truststore
			String trustStorePath = "clienttrust.keystore"; //path of trust store
			String trustStorePassword = keyStorePassword; //password of trust store
			KeyStore trustStore = socket.keyStoreTLS(keyStoreType, trustStorePath, trustStorePassword);
			//generate trust manager factory
			TrustManagerFactory trustManagerFactory = socket.trustManagerTLS(trustStore);
			//bring it all together
			//create SSLContext (required for TLS Socketing)
			SSLContext sslContext =	
				socket.SSLContextTLS(keyManagerFactory, trustManagerFactory, keyStorePassword);
			//create SSL Socket Factory
			SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			//create SSL Socket
			SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(serverIP, serverPort);
			//set TLS v1 as protocol
			sslSocket.setEnabledProtocols(new String[]{"TLSv1"});
			//initiate handshaking
			sslSocket.startHandshake();
			//take input from user
			userInput(sslSocket, serverIP, serverPort);
			//close the socket
			sslSocket.close();
		} catch (Exception e){ //check for any exception
			e.printStackTrace();
		}
	}
	private static class clientError{
		public static int WriteError(){ //print if writing not allowed
			System.out.println("Writing not allowed");
			return -1;
		}
		public static int FileError(){ //print if file not found
			System.out.println("File not found");
			return -1;
		}
		private static void argLength(String[] args){//checks for insufficient input size
			if (args.length != 2){ 
				System.out.println("Input: <server's ip or hostname> <port number>");
				System.out.println("client requires 2 input parameters.");
				System.exit(-1);
			}
		}
		private static String serverIpTest(String ipAddress){//checks for an valid server IP
			if (ipAddress.matches("^.[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}") != true){
				System.out.println("Input: <server's ip or hostname> <port number>");
				System.out.println("<server ip address>: Please input a valid IPv4 server address ([0-255].[0-255].[0-255].[0-255]).");
				System.exit(-1);
			}
			return ipAddress;
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
