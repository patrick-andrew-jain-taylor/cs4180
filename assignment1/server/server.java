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
		//Pass password from client 1 to client 2
		//If trusted mode
		//	send file unmodified and signature received from client 1 to 2
		//If untrusted mode
		//	replace encrypted file with file called "s-data" located in same directory as server and send the signature to client 2.
	}
}
