import java.io.*;
import java.lang.*;
	
public class client1{
	private static void argLength(String[] args){//checks for insufficient input size
		if (args.length != 6){ 
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA	private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file	name>");
			System.out.println("client1 requires 6 input parameters.");
			System.exit(0);
		}
	}
	private static String serverIpTest(String ipAddress){//checks for an valid server IP
		if (ipAddress.matches("^.[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}") != true){
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("<server ip address>: Please input a valid IPv4 server address ([0-255].[0-255].[0-255].[0-255]).");
			System.exit(0);
		}
		return ipAddress;
	}
	private static int portTest(String port) throws NumberFormatException{ //checks for a valid integer for client 1 port number
		try {
			int serverPort = Integer.parseInt(port);
			return serverPort;
		} catch (NumberFormatException e){
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("<port number client1>: Please input a valid integer.");
			System.exit(0);
		}
		return 0;
	}
	private static String passwordTest(String password){
		if (password.length() != 16){//checks for a 16 character password
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("<client1 password>: Please input a password of exactly 16 characters.");
			System.exit(0);
		}
		if (password.matches("^[a-zA-Z0-9,./<>?;:.\"[]{}\\|!@#$%.&*()-_=+]*]$")){ // checks for valid characters
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("<client1 password>: Please only use alphanumeric characters as well as the following symbols:");
			System.out.println(" , . / < > ? ; : . \" [ ] { } \\ | ! @ # $ % . & * ( ) - _ = +");
			System.exit(0);
		}
		return password;
	}
	private static File fileTest(String fileName, String input){
		File file = new File(fileName);
		if (!file.exists()){
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1.s RSA private exponent and modulus> <file containing client2.s RSA public exponent and modulus> <file name>");
			System.out.println(input + ": Input a valid file");
			System.exit(0);
		}
		return file;
	}
	public static void main(String[] args){
	/*	Input: 	<server ip address> <port number client1> <client1 password>
	 *		<file containing client1's RSA private exponent and modulus>
	 *		<file containing client2's RSA public exponent and modulus> 
	 *		<file name> -- 6 inputs*/

		//Check for invalid/garbage/missing input
		argLength(args);//checks for insufficient input size
		//<server ip address>
		String serverIP = serverIpTest(args[0]); //checks for an valid server IP
		//<port number client1>
		int serverPort = portTest(args[1]);//checks for a valid integer for client 1 port number
		//<client1 password>
		String password = passwordTest(args[2]); //checks for a valid, 16 character password
		//< file containing client1.s RSA public exponent and modulus>
		//make sure the client1's RSA file exists
		File RSA1 = fileTest(args[3], "<file containing client1.s RSA public exponent and modulus>");
		//<file containing client2.s RSA public exponent and modulus>
		//make sure the client2's RSA file exists
		File RSA2 = fileTest(args[4], "<file containing client2.s RSA public exponent and modulus>");
		//<file name>
		//make sure the file to be encrypted exists
		File Data = fileTest(args[5], "<file name>");
		//Encrypt file with AES in CBC
		//Hash plaintext with SHA-256
		//Encrypt hash with RSA (private key)
		//Send Encrypted data and signature to the server
		//Encrypt password with client2 RSA key
		//Send encrypted password to client 2 via server
		//Disconnect from server after sending password, file, and signature
	}
}

