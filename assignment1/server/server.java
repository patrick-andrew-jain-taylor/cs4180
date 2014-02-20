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
	private static boolean modeTest(String mode){//checks for a valid mode
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
		else if (mode.matches("t") == true) return true;
		return false;
	}
	private static File fileTest(String fileName){
		File file = new File(fileName);
		if(!file.exists()){
			System.out.println("Input: server <port number for client1> <port number for client2> <mode>");
			System.out.println("<mode>: Make sure a file entitled \"s-data\" is included in this directory for untrusted file swapping.");
			System.exit(-1);
		}
		return file;
	}
	public static void main(String[] args) throws IOException{
		/*	Input:	<port number for client1> <port number for client2> <mode> */
		//Check for invalid/garbage/missing input
		argLength(args); //checks for insufficient input size
		int client1Port = portTest(args[0], "<port number for client1>"); //checks for valid client1 port
		int client2Port = portTest(args[1], "<port number for client2>"); 
		boolean mode = modeTest(args[2]);
		//Establish a socket
		ServerSocket Socket1 = new ServerSocket(client1Port); //establish socket for client1
		Socket client1Socket = Socket1.accept(); //accept client1 connection
		BufferedInputStream in = new BufferedInputStream(client1Socket.getInputStream()); //determine client1's socket's input stream (receiving information from client1)
		ServerSocket Socket2 = new ServerSocket(client2Port); //establish socket for client 2
		Socket client2Socket = Socket2.accept();
		BufferedOutputStream out = new BufferedOutputStream(client2Socket.getOutputStream()); //determine client2's socket's output stream (sending information to client2
		//pass password for client1 to client2
		byte[] password = new byte[256]; //password array
		in.read(password, 0, password.length); //read in from input
		out.write(password, 0, password.length); //write out to output
		//pass signature unmodified
		byte[] signature = new byte[256]; //signature array
		in.read(signature, 0, signature.length); //read in from input
		out.write(signature, 0, signature.length); //write out to output
		//test for trust or untrust
		byte[] file = new byte[1048576]; // encrypted file array
		if (mode){ //trust
			int count = 0;
			while((count = in.read(file, 0, file.length)) > 0){
				out.write(file, 0, count); //send file unmodified
			}
		}
		else{//untrust
			File sdata = fileTest("s-data"); //new file to be sent
			FileInputStream is = null;
			try{
				is = new FileInputStream(sdata);
			} catch(FileNotFoundException e){
				System.out.println("Include a valid file.");
				System.exit(-1);
			}
			BufferedInputStream bis = new BufferedInputStream(is); 
			int count = 0;
			while((count = bis.read(file, 0, file.length)) > 0){
				out.write(file, 0, count); //send sdata
			}
		}
	}
}
