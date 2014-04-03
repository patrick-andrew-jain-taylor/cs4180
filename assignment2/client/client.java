import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class client{

	//clientSocket: establishes SSL socket from client to server
	public static SSLSocket clientSocket(String serverIP, int serverPort) throws Exception{ 
		//create SSL Socket Factory
		SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		//create SSL Socket to server
		//set system properties
		System.setProperty("javax.net.ssl.keyStore", "client.keystore");
		System.setProperty("javax.net.ssl.keyStorePassword", "client");
		//initate connection to the server
		SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverIP, serverPort);
		return sslSocket;
	}
	//acceptIn: to be printed out for all invalid user input
	public static int acceptIn(){
		System.out.println("Acceptable inputs:");
		System.out.println("get [<path>/filename]");
		System.out.println("put [<path>/filename]");
		System.out.println("exit");
		return 0;
	}
	//get: gets file from server
	public static int get(String file){
		return 0;
	}
	//put: puts file on server
	public static int put(String file){
		return 0;
	}
	//getPut: checks for valid GET/PUT command
	public static int getPut(String input){
		String[] inputSplit = input.split("[ ]+"); //splits string into array of space-delimited strings
		//check for proper length
		if (inputSplit.length != 2) return acceptIn(); //improper input size
		//check for get or put
		if (inputSplit[0].equals("get")) return get(inputSplit[1]);
		else if (inputSplit[0].equals("put")) return put(inputSplit[1]);
		else return acceptIn();//not get or put (or exit by extension)
	}
	public static void userInput(SSLSocket sslSocket) throws IOException{
		String input = "start";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true){ //loop indefinitely
			System.out.print("> "); //to be displayed on the terminal
			//take input from the terminal
			input = br.readLine();
			if (input.equals("exit")) break; //time to close the socket
			//if beyond this point, function is either GET or PUT and string needs to be parsed
			getPut(input);
		}
		br.close();
	}
	public static void main(String[] args){
		String serverIP = "128.59.15.38"; //server IP
		int serverPort = 9955; //server Port
		//create socket to server
		try{
			SSLSocket sslSocket = clientSocket(serverIP, serverPort);
			//take input from user
			userInput(sslSocket);
			sslSocket.close(); //close the socket
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
