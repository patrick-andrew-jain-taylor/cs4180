import java.io.*;

public class server{
	private static void argLength(String[] args){//checks for insufficient input size
		if (args.length != 3){
			System.out.println("Input: server <port number for client1> <port number for client2> <mode>");
		      	System.out.println("server requires 3 input parameters.");
		        System.exit(0);
		}
	}
	private static int portTest(String port, String message) throws NumberFormatException{ //checks for a valid integer for client 1 port number
		try {
			int serverPort = Integer.parseInt(port);
			return serverPort;
		} catch (NumberFormatException e){
			System.out.println("Input: server <port number for client1> <port number for client2> <mode>");
			System.out.println(message + ": Please input a valid integer.");
			System.exit(0);
		}
		return 0;
	}
	private static String modeTest(String mode){//checks for a valid mode
		if(mode.matches("^.[tu]{1}*$") != true){
			System.out.println("Input: server <port number for client1> <port number for client2> <mode>");
			System.out.println("<mode>: Please input a valid mode: t for trust, u for untrust");
			System.exit(0);
		}
		return mode;
	}
	public static void main(String[] args){
		/*	Input:	<port number for client1> <port number for client2> <mode> */
		//Check for invalid/garbage/missing input
		argLength(args); //checks for insufficient input size
		int client1Port = portTest(args[0], "<port number for client1>"); //checks for valid client1 port
		int client2Port = portTest(args[1], "<port number for client2>"); 
		String mode = modeTest(args[2]);
		//Establish a socket
		ServerSocket Socket1 = new ServerSocket(client1port); //establish socket for client1
		ServerSocket Socket2 = new ServerSocket(client2port); //establish socket for client2
		Socket client1Socket = serverSocket.accept(); //accept client1 connection
		BufferedReader in = new BufferedReader(new InputStreamReader(client1Socket.getInputStream())); //determine client1's socket's input stream (receiving information from client1)
		Socket client2Socket = serverSocket.accept();
		BufferedReader out = new BufferedReader(new OutputStreamReader(client2Socket.getOutputStream())); //determine client2's socket's output stream (sending information to client2
		//Pass password from client 1 to client 2
		//If trusted mode
		//	send file unmodified and signature received from client 1 to 2
		//If untrusted mode
		//	replace encrypted file with file called "s-data" located in same directory as server and send the signature to client 2.
	}
}
