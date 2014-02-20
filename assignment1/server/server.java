import java.io.*;
import java.net.*;
import java.util.*;

public class server{
	private static void argLength(String[] args){//checks for insufficient input size
		if (args.length != 3){
			System.out.println("Input: server <port number for client1> <port number for client2> <mode>");
		      	System.out.println("server requires 3 input parameters.");
		        System.exit(-1);
		}
	}
	private static int portTest(String port, String message) throws NumberFormatException{ //checks for a valid integer for client 1 port number
		try {
			int serverPort = Integer.parseInt(port);
			return serverPort;
		} catch (NumberFormatException e){
			System.out.println("Input: server <port number for client1> <port number for client2> <mode>");
			System.out.println(message + ": Please input a valid integer.");
			System.exit(-1);
		}
		return 0;
	}
	private static String modeTest(String mode){//checks for a valid mode
		if(mode.length() != 1){
			System.out.println("Input: server <port number for client1> <port number for client2> <mode>");
			System.out.println("<mode>: Please include a valid mode (\"t\" for trust, \"u\" for untrust.)");
			System.exit(-1);
		}
		if(mode.matches("[tu]") != true){
			System.out.println("Input: server <port number for client1> <port number for client2> <mode>");
			System.out.println("<mode>: Please input a valid mode: t for trust, u for untrust");
			System.exit(-1);
		}
		return mode;
	}
	public static void main(String[] args) throws IOException{
		/*	Input:	<port number for client1> <port number for client2> <mode> */
		//Check for invalid/garbage/missing input
		argLength(args); //checks for insufficient input size
		int client1Port = portTest(args[0], "<port number for client1>"); //checks for valid client1 port
		int client2Port = portTest(args[1], "<port number for client2>"); 
		String mode = modeTest(args[2]);
		//Establish a socket
		ServerSocket Socket1 = new ServerSocket(client1Port); //establish socket for client1
		System.out.println(1);
		Socket client1Socket = Socket1.accept(); //accept client1 connection
		System.out.println(2);
		BufferedInputStream in = new BufferedInputStream(client1Socket.getInputStream()); //determine client1's socket's input stream (receiving information from client1)
		
		byte[] signature = new byte[256]; //signature array
		in.read(signature, 0, signature.length);
		//System.out.println(Arrays.toString(signature));
		byte[] password = new byte[256]; //password array
		in.read(password, 0, password.length);
		//System.out.println(Arrays.toString(password));
		byte[] file = new byte[1024]; // encrypted file array
		in.read(file, 0, file.length);
		System.out.println(Arrays.toString(file));
		ServerSocket Socket2 = new ServerSocket(client2Port); //establish socket for client2
		Socket client2Socket = Socket2.accept();
		BufferedOutputStream out = new BufferedOutputStream(client2Socket.getOutputStream()); //determine client2's socket's output stream (sending information to client2
		//Pass password from client 1 to client 2
		//If trusted mode
		//	send file unmodified and signature received from client 1 to 2
		//If untrusted mode
		//	replace encrypted file with file called "s-data" located in same directory as server and send the signature to client 2.
	}
}
