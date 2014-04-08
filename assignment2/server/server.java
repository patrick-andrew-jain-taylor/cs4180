import java.security.KeyStore;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.*;
import java.io.FileInputStream;

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
			if (args.length != 1){ 
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

