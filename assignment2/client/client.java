import java.security.KeyStore;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import javax.net.ssl.*;
import javax.net.ssl.SSLContext;

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
				return clientError.WriteError();
			}
		} catch (FileNotFoundException e){
			return clientError.FileError();
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
				return clientError.WriteError();
			}
		} catch (FileNotFoundException e){
			return clientError.FileError();
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
			if (inputSplit[0].equals("exit")){ //time to close the socket
				outBuf.write(input.getBytes());
				break;
			}
			//if beyond this point, function is either GET or PUT and string needs to be parsed
			int getPut = getPut(inputSplit);
			if(getPut < 0) acceptIn(); //invalid input
			else {
				outBuf.write(input.getBytes()); //send command to server
				if (getPut > 0) get(inBuf, input, inputSplit[1]); //get
				else put(outBuf, input, inputSplit[1]); //put
			}
		}
		br.close();
		in.close();
		out.close();
	}
	public static void main(String[] args){
		String serverIP = "128.59.15.39"; //server IP
		int serverPort = 9955; //server Port
		//create socket to server
		try{
			//create SSLcontext
			String keyStoreType = "JKS";
			String keyStorePath = "client.keystore";
			String keyStorePassword = "client";
			KeyStore keyStore = socket.keyStoreTLS(keyStoreType, keyStorePath, keyStorePassword);
			KeyManagerFactory keyManagerFactory = socket.keyManagerTLS(keyStore, keyStorePassword);
			//truststore
			String trustStorePath = "clienttrust.keystore";
			String trustStorePassword = keyStorePassword;
			KeyStore trustStore = socket.keyStoreTLS(keyStoreType, trustStorePath, trustStorePassword);
			TrustManagerFactory trustManagerFactory = socket.trustManagerTLS(trustStore);
			//bring it all together
			SSLContext sslContext =	
				socket.SSLContextTLS(keyManagerFactory, trustManagerFactory, keyStorePassword);
			SSLSocketFactory socketFactory = sslContext.getSocketFactory();
			SSLSocket sslSocket = (SSLSocket) socketFactory.createSocket(serverIP, serverPort);
			sslSocket.setEnabledProtocols(new String[]{"TLSv1"});
			sslSocket.startHandshake();
			//take input from user
			userInput(sslSocket);
			sslSocket.close(); //close the socket
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	private static class clientError{
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
