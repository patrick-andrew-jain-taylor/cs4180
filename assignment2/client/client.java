import java.security.KeyStore;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import javax.net.ssl.*;
import javax.net.ssl.SSLContext;
import java.security.cert.Certificate;
import java.net.Socket;
import java.security.*;
import java.security.cert.*;
import java.io.FileInputStream;

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

/* the socket class is a series of functions implementing the procedure outlined in the Palomino Labs Blog entitled
 * "Java 2-way TLS/SSL (Client Certificates) and PKCS12 vs JKS KeyStores). The Client and Server will both
 * use these functions, but will do so for their respective implementations.
 *
 * Link: http://blog.palominolabs.com/2011/10/18/java-2-way-tlsssl-client-certificates-and-pkcs12-vs-jks-keystores/
 * */

private static class socket{
	//KeyStore: return keyStore to be used for sending/receiving certs (keyStore/trustStore)
	private static KeyStore keyStoreTLS(String keyStoreType, String keyStorePath, String password){
		KeyStore keyStore = null; //instantiate keystore
		try{
			FileInputStream inputStream = new FileInputStream(keyStorePath); //create keystore file input
			keyStore = KeyStore.getInstance(keyStoreType); //get instance of keystore of defined type
			keyStore.load(inputStream, password.toCharArray()); //load the keystore with given PW
			inputStream.close(); //close keystore file input
			return keyStore; //return the keystore
		} catch (FileNotFoundException e){//file not found
			storeError.invalidKeyStoreFile(keyStorePath);
		} catch (KeyStoreException e){//issue with keystore
			storeError.invalidKeyStoreType(keyStoreType);
		} catch (IOException e){//issue with keystore data
			storeError.invalidKeyStoreData(keyStorePath);
		} catch (NoSuchAlgorithmException e){//keystore algorithm error
			storeError.invalidAlgorithm(keyStorePath);
		} catch (CertificateException e){//cert error
			storeError.invalidCertificate(keyStorePath);
		}
		return keyStore; //return the keystore
	}
	//KeyManagerFactory: used to manage keyStore (sending certs)
	private static KeyManagerFactory keyManagerTLS(KeyStore keyStore, String password){
		KeyManagerFactory keyManagerFactory = null; //instantiate key manager factory
		try{
			keyManagerFactory = 
				KeyManagerFactory.getInstance("SunX509", "SunJSSE"); //get instance of this type of key manager factory
			keyManagerFactory.init(keyStore, password.toCharArray()); //initalize the key manager
			return keyManagerFactory; //return the key manager
		} catch (NoSuchAlgorithmException e){//algorithm not found
			managerError.invalidAlgorithm();
		} catch (NoSuchProviderException e){//provider not found
			managerError.invalidProvider();
		} catch (IllegalArgumentException e){//not enough arguments
			managerError.illegalArgument();
		} catch (NullPointerException e){//null pointer found
			managerError.nullPointer();
		} catch (KeyStoreException e){//issue with keystore
			storeError.invalidKeyStore();
		} catch (UnrecoverableKeyException e){//issue with key
			storeError.invalidKeyStore();
		} 
		return keyManagerFactory; //return trust manager
	}

	//TrustManagerFactory: used to manage trustStore (receiving certs)
	private static TrustManagerFactory trustManagerTLS(KeyStore trustStore){
		TrustManagerFactory trustManagerFactory = null; //instantiate trust manager
		try{
			trustManagerFactory = 
				TrustManagerFactory.getInstance("PKIX", "SunJSSE"); //get instance of this type of trust manager factory
			trustManagerFactory.init(trustStore); //initalize the trust manager
			return trustManagerFactory; //return trust manager
		} catch (NoSuchAlgorithmException e){ //algorithm not found
			managerError.invalidAlgorithm();
		} catch (NoSuchProviderException e){//provider not found
			managerError.invalidProvider();
		} catch (IllegalArgumentException e){//not enough arguments
			managerError.illegalArgument();
		} catch (NullPointerException e){//null pointer found
			managerError.nullPointer();
		} catch (KeyStoreException e){//issue with keystore
			managerError.trustStoreFail();
		}
		return trustManagerFactory; //return trust manager
	}
	//SSLContext: used for socketing
	private static SSLContext SSLContextTLS(KeyManagerFactory keyManagerFactory, TrustManagerFactory 
			trustManagerFactory, String password){
		SSLContext sslContext = null; //instantiate SSL Context
		try{
			//x509keymanager
			X509KeyManager x509KeyManager = null; //instantiate X509 Key Manager
			for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {//check for available key manager factories for each unique key manager
				if (keyManager instanceof X509KeyManager) {//check for a unique instance of X509 key manager in the key manager
					x509KeyManager = (X509KeyManager) keyManager; //set X509 Key Manager
					break;
				}
			}
			if (x509KeyManager == null) {//no X509 Key Managers found
				throw new NullPointerException();
			}
			//x509trustmanager
			X509TrustManager x509TrustManager = null; //instantiate X509 Trust Manager
			for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) { //check for available trust manager factories for each unique trust manager
				if (trustManager instanceof X509TrustManager) {//check for a unique instance of X509 trust manager in the trust manager
					x509TrustManager = (X509TrustManager) trustManager; //set X509 trust manager
					break;
				}
			}
	       		if (x509TrustManager == null) {//no X509 Trust Managers found
    				throw new NullPointerException();
			}
			sslContext = SSLContext.getInstance("TLS"); //generate instance of TLS
			//initalize the SSL Context with our X509 key and trust managers
			sslContext.init(new X509KeyManager[]{x509KeyManager}, new X509TrustManager[]{x509TrustManager}, null);
			return sslContext; //return the SSL Context
		} catch (NoSuchAlgorithmException e){//algorithm not found
			managerError.invalidAlgorithm();
		} catch (NullPointerException e){//null pointer found
			managerError.invalidProtocol();
		} catch (KeyManagementException e){//issue with key manager
			managerError.invalidManager();
		} catch(IllegalStateException e){//illegal state (nearly impossible)
			managerError.illegalState();
		}
		return sslContext; //return SSL Context
	}
/* the storeError class contains all functions used to generate unique store error messages for the socket
 * class.
 */
	private static class storeError{
		//invalidKeyStore: prints error message for invalid keyStore file.
		private static void invalidKeyStore(){
			System.out.println("Keystore not found.");
			System.exit(1);
		}
		//invalid KeyStoreFile: key store not found at this path
		private static void invalidKeyStoreFile(String keyStorePath){
			System.out.println("Given keyStorePath: " + keyStorePath);
			System.out.println("Please use a valid path for the keystore.");
			System.exit(1);
		}
		//invalidKeyStoreType: prints error message for invalid keyStore type
		private static void invalidKeyStoreType(String keyStoreType){
			System.out.println("Given keyStoreType: " + keyStoreType);
			System.out.println("keyStoreType is either \"JKS\" or \"PKCS12\"");
			System.exit(1);
		}
		//invalidKeyStoreData: issue with the keystore at the given path
		private static void invalidKeyStoreData(String keyStorePath){
			System.out.println("Problem with keystore found at " + keyStorePath);
			System.exit(1);
		}
		//invalidAlgorithm: issue with keystore algorithm
		private static void invalidAlgorithm(String keyStorePath){
				System.out.println("The algorithm used to check the integrity of the keystore found at " + keyStorePath + " cannot be found.");
			System.exit(1);
		}
		//invalidCertificate: issue with cert found at the keystore
		private static void invalidCertificate(String keyStorePath){
			System.out.println("One or more of the certificates in the keystore found at "
					+ keyStorePath + "could not be loaded.");
			System.exit(1);
		}
	}


/* the managerError class contains all functions used to generate unique manager error messages for the 
 * socket class.
 */ 
	private static class managerError{
		//invalid algorithm: issue with key/trust manager algorithm specified
		private static void invalidAlgorithm(){ //since directly specified, this should not ever occur.
			System.out.println("Unknown trust manager algorithm issue.");
			System.exit(-1);
		}
		//invalid provider: issue with key/trust manager provider specified
		private static void invalidProvider(){ //since directly specified, this should not ever occur.
			System.out.println("Unknown trust manager provider issue.");
			System.exit(-1);
		}
		//illegal argument: incorrect number of arguments for algorithm/provider
		private static void illegalArgument(){ //since directly specified, this should not ever occur.
			System.out.println("Provider name null/empty.");
			System.exit(-1);
		}
		//null pointer: null pointer found
		private static void nullPointer(){//since directly specified, this should not ever occur.
			System.out.println("Algorithm is null.");
			System.exit(-1);
		}
		//trustStoreFailed: trust initialization failed
		private static void trustStoreFail(){
			System.out.println("Trust store init failed.");
			System.exit(1);
		}
		//illegalState: issue with factory
		private static void illegalState(){
			System.out.println("Factory not initialized.");
			System.exit(1);
		}
		//invalidProtocol: issue with protocol specified
		private static void invalidProtocol(){ //since directly specified, this should not ever occur.
			System.out.println("Protocol is null");
			System.exit(-1);
		}
		//invalidManager: incorrect manager used
		private static void invalidManager(){
			System.out.println("Failed context initialization.");
			System.exit(1);
		}
	}
}
}

