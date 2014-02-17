import java.io.*;
import java.lang.*;

public class client1{
	public static void main(String[] args){
	/*	Input: 	<server ip address> <port number client1> <client1 password>
	 *		<file containing client1's RSA private exponent and modulus>
	 *		<file containing client2's RSA public exponent and modulus> 
	 *		<file name> -- 6 inputs*/

		//Check for invalid/garbage/missing input
		//<server ip address>
		if (args.length != 6){ //checks for insufficient input size
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA	private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file	name>");
			System.out.println("client1 requires 6 input parameters.");
			System.exit(0);
		}
		if (args[0].matches("^.[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}") != true){//checks for an valid server IP
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("args[0]: Please input a valid IPv4 server address ([0-255].[0-255].[0-255].[0-255]).");
			System.exit(0);
		}
		String serverIP = args[0];

		//<port number client1>
		try { //checks for a valid integer for client 1 port number
			int serverPort = Integer.parseInt(args[1]);
		} catch (NumberFormatException e){
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("args[1]: Please input a valid integer.");
			System.exit(0);
		}

		//<client1 password>
		if (args[2].length() != 16){//checks for a 16 character password
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("args[2]: Please input a password of exactly 16 characters.");
			System.exit(0);
		}
		if (args[2].matches("^[a-zA-Z0-9,./<>?;:.\"[]{}\\|!@#$%.&*()-_=+]*]$")){ // checks for valid characters
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("args[2]: Please only use alphanumeric characters as well as the following symbols:");
			System.out.println(" , . / < > ? ; : . \" [ ] { } \\ | ! @ # $ % . & * ( ) - _ = +");
			System.exit(0);
		}
		String password = args[2];

		//< file containing client1.s RSA public exponent and modulus>
		//make sure the client1's RSA file exists
		File RSA1 = new File(args[3]);
		if (!RSA1.exists()){
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1.s RSA private exponent and modulus> <file containing client2.s RSA public exponent and modulus> <file name>");
			System.out.println("args[3]: Input a valid file containing client1's private RSA exponent and modulus.");
			System.exit(0);
		}

		//<file containing client2.s RSA public exponent and modulus>
		//make sure the client2's RSA file exists
		File RSA2 = new File(args[4]);
		if (!RSA2.exists()){
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1.s RSA private exponent and modulus> <file containing client2.s RSA public exponent and modulus> <file name>");
			System.out.println("args[4]: Input a valid file containing client2's public RSA exponent and modulus.");
			System.exit(0);
		}
		
		//<file name>
		//make sure the file to be encrypted exists
		File Data = new File(args[5]);
		if(!Data.exists()){
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1.s RSA private exponent and modulus> <file containing client2.s RSA public exponent and modulus> <file name>");
			System.out.println("args[5]: Input a valid file to be encrypted.");
			System.exit(0);
		}
		//Encrypt file with AES in CBC
		//Hash plaintext with SHA-256
		//Encrypt hash with RSA (private key)
		//Send Encrypted data and signature to the server
		//Encrypt password with client2 RSA key
		//Send encrypted password to client 2 via server
		//Disconnect from server after sending password, file, and signature
	}
}

