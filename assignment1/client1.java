import java.io.*;
import java.lang.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.net.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
	
public class client1{
	private static void argLength(String[] args){//checks for insufficient input size
		if (args.length != 6){ 
			System.out.println("Input: client1 <server ip address> <port number client1> <client1 password> <file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("client1 requires 6 input parameters.");
			System.exit(-1);
		}
	}
	private static String serverIpTest(String ipAddress){//checks for an valid server IP
		if (ipAddress.matches("^.[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}") != true){
			System.out.println("Input: client1 <server ip address> <port number client1> <client1 password> <file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("<server ip address>: Please input a valid IPv4 server address ([0-255].[0-255].[0-255].[0-255]).");
			System.exit(-1);
		}
		return ipAddress;
	}
	private static int portTest(String port) throws NumberFormatException{ //checks for a valid integer for client 1 port number
		try {
			int serverPort = Integer.parseInt(port);
			return serverPort;
		} catch (NumberFormatException e){
			System.out.println("Input: client1 <server ip address> <port number client1> <client1 password> <file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("<port number client1>: Please input a valid integer.");
			System.exit(-1);
		}
		return 0;
	}
	private static String passwordTest(String password){
		if (password.length() != 16){//checks for a 16 character password
			System.out.println("Input: client1 <server ip address> <port number client1> <client1 password> <file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("<client1 password>: Please input a password of exactly 16 characters.");
			System.exit(-1);
		}
		if (password.matches("^[a-zA-Z0-9,./<>?;:.\"[]{}\\|!@#$%.&*()-_=+]*]$")){ // checks for valid characters
			System.out.println("Input: client1 <server ip address> <port number client1> <client1 password> <file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("<client1 password>: Please only use alphanumeric characters as well as the following symbols:");
			System.out.println(" , . / < > ? ; : . \" [ ] { } \\ | ! @ # $ % . & * ( ) - _ = +");
			System.exit(-1);
		}
		return password;
	}
	private static File fileTest(String fileName, String input){
		File file = new File(fileName);
		if (!file.exists()){
			System.out.println("Input: <server ip address> <port number client1> <client1 password> < file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println(input + ": Input a valid file");
			System.exit(-1);
		}
		return file;
	}
	public static void main(String[] args) 
		throws SignatureException, IOException, IllegalBlockSizeException, BadPaddingException{
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
		//< file containing client1's RSA private exponent and modulus>
		//make sure the client1's RSA file exists
		File RSA1 = fileTest(args[3], "<file containing client1's RSA private exponent and modulus>");
		//<file containing client2's RSA public exponent and modulus>
		//make sure the client2's RSA file exists
		File RSA2 = fileTest(args[4], "<file containing client2's RSA public exponent and modulus>");
		//<file name>
		//make sure the file to be encrypted exists
		File Data = fileTest(args[5], "<file name>");
		//Encrypt file with AES in CBC
		Cipher aesCBC = null;
		byte[] IV = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		try{
			aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding"); //instantiate AES with CBC
			aesCBC.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(password.getBytes("UTF-8"), "AES"), new IvParameterSpec(IV)); //initialize
		} catch(NoSuchAlgorithmException e){
			System.out.println("Please include a valid cipher.");
			System.exit(-1);
		} catch(NoSuchPaddingException e){
			System.out.println("Please include a valid padding.");
			System.exit(-1);
		} catch(UnsupportedEncodingException e){
			System.out.println("Please include a valid encoding.");
			System.exit(-1);
		} catch(InvalidKeyException e){
			System.out.println("Please insert a valid key.");
			System.exit(-1);
		} catch(InvalidAlgorithmParameterException e){
			System.out.println("Please include a valid algorithm parameter.");
			System.exit(-1);
		}
		FileInputStream isplain = null;
		try{	
			isplain = new FileInputStream(Data); //to be used for plaintext hashing
		} catch(FileNotFoundException e){
			System.out.println("Please include a valid file.");
			System.exit(-1);
		}
		FileInputStream isencr = null;
		try{
			isencr = new FileInputStream(Data);//to be used for ciphertext
		} catch(FileNotFoundException e){
			System.out.println("Please include a valid file.");
			System.exit(-1);
		}
		CipherInputStream cis = new CipherInputStream(isencr, aesCBC); //encrypt data from file
		byte[] dataArrayAES = new byte[(int) Data.length()]; //the encrypted version of the file
		BufferedInputStream bis = new BufferedInputStream(cis); //buffered for use in socket transmission
		//Hash plaintext with SHA-256 & encrypt hash with RSA (private key)
		Signature SHA256 = null;
		try{
			SHA256 = Signature.getInstance("SHA256withRSA"); //instantiate SHA-256 with RSA
		} catch(NoSuchAlgorithmException e){
			System.out.println("Please include a valid algorithm.");
			System.exit(-1);
		}
		byte[] encodedKeyPriv = new byte[(int)RSA1.length()]; //the encoded version of the private key will be read into this array
		try{
			new FileInputStream(RSA1).read(encodedKeyPriv); //the key contained in this file is read to the array
		} catch(FileNotFoundException e){
			System.out.println("Please include a valid file.");
			System.exit(-1);
		}
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKeyPriv); //create a private key specification from the encoded key
		KeyFactory kf = null;
		PrivateKey pk = null;
		try{
			kf = KeyFactory.getInstance("RSA"); //instatiate RSA for private key
			pk = kf.generatePrivate(privateKeySpec); //generate the private key
			SHA256.initSign(pk); //initialize the signature process
		} catch(NoSuchAlgorithmException e){
			System.out.println("Please include a valid algorithm.");
			System.exit(-1);
		} catch(InvalidKeySpecException e){
			System.out.println("Please include a valid key specification.");
			System.exit(-1);
		} catch(InvalidKeyException e){
			System.out.println("Please include a valid key.");
			System.exit(-1);
		}
		byte[] dataArrayPlain = new byte[(int) Data.length()]; //array for plaintext to be written to
		isplain.read(dataArrayPlain, 0, dataArrayPlain.length); //read in plaintext
		SHA256.update(dataArrayPlain); //update the signature with the plaintext
		byte[] dataArraySign = SHA256.sign(); //sign the plaintext
		//Encrypt password with client2 RSA key
		byte[] encodedKeyPub = new byte[(int)RSA2.length()];//the encoded version of the public key will be read into this array
		new FileInputStream(RSA2).read(encodedKeyPub); //the key contained in this file is read to the array
		X509EncodedKeySpec publicKeySpec =  new X509EncodedKeySpec(encodedKeyPub); //create a public key specification from the encoded key
		KeyFactory kf2 = null;
		PublicKey pk2 = null;
		Cipher RSA = null;
		try{
			kf2 = KeyFactory.getInstance("RSA"); //instantiate RSA for public key
			pk2 = kf2.generatePublic(publicKeySpec); //generate the public key
			RSA = Cipher.getInstance("RSA"); //instantiate RSA
			RSA.init(Cipher.ENCRYPT_MODE, pk2); //initialize RSA encryption
		} catch(NoSuchAlgorithmException e){
			System.out.println("Please include a valid algorithm.");
			System.exit(-1);
		} catch(InvalidKeySpecException e){
			System.out.println("Please include a valid key specification.");
			System.exit(-1);
		} catch(InvalidKeyException e){
			System.out.println("Please include a valid key.");
			System.exit(-1);
		} catch(NoSuchPaddingException e){
			System.out.println("Please include a valid padding.");
			System.exit(-1);
		}
		byte[] passEncr = RSA.doFinal(password.getBytes()); //encrypt the password
		//Send Encrypted data, signature, and password to the server
		try{
			Socket socket = new Socket(serverIP, serverPort);//connect to the server
			BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
			out.write(passEncr, 0, passEncr.length);//write encrypted password to server
			out.write(dataArraySign, 0, dataArraySign.length);//write signature to server
			int count = 0;
			while((count = bis.read(dataArrayAES, 0, dataArrayAES.length)) > 0){//read in buffered ciphertext		
				out.write(dataArrayAES, 0, count); //write ciphertext to buffer
			}
			out.flush();
			out.close(); //close buffer
			socket.close(); //disconnect from server after sending password, file, and signature

		} catch (IOException e){
			System.out.println("Input: client1 <server ip address> <port number client1> <client1 password> <file containing client1's RSA private exponent and modulus> <file containing client2's RSA public exponent and modulus> <file name>");
			System.out.println("<server ip address> <port number client1>: Make sure the server is running on the entered IP and port.");
			System.exit(-1);
		}
		bis.close(); //close the buffer
		cis.close(); //close the cipher stream
		isencr.close(); //close the encrypting file stream
		isplain.close(); //close the plaintext file stream
	}
}
