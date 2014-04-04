import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class client{
	public static int WriteError(){
		System.out.println("Writing not allowed");
		return -1;
	}
	public static int FileError(){
		System.out.println("File not found");
		return -1;
	}
	//clientSocket: establishes SSL socket from client to server
	public static SSLSocket clientSocket(String serverIP, int serverPort){ 
		//create SSL Socket Factory
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		//create SSL Socket to server
		SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverIP, serverPort);
		//generate KeyStore
		return sslSocket;
	}
	//keyStore: generates keystore instance for use with the SSL Socket
	public static KeyStore keyStore(SSLSocket clientSocket, String password){
		KeyStore keyStore = KeyStore.getInstance("JKS"); //using JKS keystore
		//load keystore
		try{
			FileInputStream keyStoreFile = new FileInputStream("client.keystore");
			keyStore.load(keyStoreFile, password.toCharArray());
			keyStoreFile.close();
		} finally {
			if (keyStoreFile != null){
				fis.close(); //close 
			}
		}
		return keyStore;
	}
	//trustManager: generates trustStore instance
	public static
	//keyManager: creates a key manager for two-way authentication
	public static KeyManagerFactory keyManager(Keystore keyStore, String password){
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
		keyManagerFactory.init(keyStore, password.toCharArray());
		return keyManagerFactory;
	}
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
	 	File fileOut = new File(file);
		try{
			FileOutputStream get = new FileOutputStream(fileOut);
			BufferedOutputStream getBuf = new BufferedOutputStream(get);
			byte[] data = new byte[512];
			int count = 0;
			try{
				while((count = in.read(data, 0, data.length)) > 0) //read in buffered file from server
					getBuf.write(data, 0, count);
				getBuf.flush();
				getBuf.close();
				return 0;
			} catch (IOException e){
				return WriteError();
			}
		} catch (FileNotFoundException e){
			return FileError();
		}
	}
	//put: Places file on server
	public static int put(BufferedOutputStream out, String command, String file){
		try{
			FileInputStream put = new FileInputStream(file);
			BufferedInputStream putBuf = new BufferedInputStream(put);
			byte[] data = new byte[512];
			int count = 0;
			try{
				while((count = putBuf.read(data, 0, data.length)) > 0) //read in buffered file from client
					out.write(data, 0, count);
				putBuf.close();
				return 0;
			} catch (IOException e){
				return WriteError();
			}
		} catch (FileNotFoundException e){
			return FileError();
		}
	}
	//acceptIn: to be printed out for all invalid user input
	public static void acceptIn(){
		System.out.println("Acceptable inputs:");
		System.out.println("get [<path>/filename]");
		System.out.println("put [<path>/filename]");
		System.out.println("exit");
	}
	public static void userInput(SSLSocket sslSocket) throws IOException{
		String input = "start";
		OutputStream out = sslSocket.getOutputStream();
		BufferedOutputStream outBuf = new BufferedOutputStream(out);
		InputStream in = sslSocket.getInputStream();
		BufferedInputStream inBuf = new BufferedInputStream(in);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true){ //loop indefinitely
			System.out.print("> "); //to be displayed on the terminal
			//take input from the terminal
			input = br.readLine();
			String[] inputSplit = input.split("[ ]+"); //splits string into array of space-delimited strings
			if (inputSplit[0].equals("exit")) break; //time to close the socket
			//if beyond this point, function is either GET or PUT and string needs to be parsed
			int getPut = getPut(inputSplit);
			if(getPut < 0) acceptIn(); //invalid input
			else {
				out.write(input.getBytes()); //send command to server
				if (getPut > 0) get(inBuf, input, inputSplit[1]); //get
				else put(outBuf, input, inputSplit[1]); //put
			}
		}
		br.close();
		in.close();
		out.close();
	}
	public static void main(String[] args){
		String serverIP = "128.59.15.30"; //server IP
		int serverPort = 9955; //server Port
		//create socket to server
		try{
			SSLSocket sslSocket = clientSocket(serverIP, serverPort);
			KeyStore keyStore = keyStore(sslSocket);
			KeyManagerFactory keyManagerFactory = keyManager(keyStore);
			X509Keymanager x509KeyManager 
			//take input from user
			userInput(sslSocket);
			sslSocket.close(); //close the socket
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
