import java.io.*;

public class client2{
	private static void argLength(String[] args){//checks for insufficient input size
		if (args.length != 4){ 
			System.out.println("Input: <server ip address> <port number client2> <file containing client1's RSA	public exponent and modulus> <file containing client2's RSA private exponent and modulus>");
			System.out.println("client2 requires 4 input parameters.");
			System.exit(-1);
		}
	}
	private static String serverIpTest(String ipAddress){//checks for an valid server IP
		if (ipAddress.matches("^.[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}\\..[0-9]{1,3}") != true){
			System.out.println("Input: <server ip address> <port number client2> <file containing client1's RSA	public exponent and modulus> <file containing client2's RSA private exponent and modulus>");
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
			System.out.println("Input: <server ip address> <port number client2> <file containing client1's RSA	public exponent and modulus> <file containing client2's RSA private exponent and modulus>");
			System.out.println("<port number client2>: Please input a valid integer.");
			System.exit(-1);
		}
		return 0;
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
	public static void main(String[] args){
	/*	Input: 	<server ip address> <port number client2> 
	 *		<file containing client1's RSA private exponent and modulus>
	 *		<file containing client2's RSA public exponent and modulus> 
	 *		-- 4 inputs*/

		//Check for invalid/garbage/missing input
		argLength(args);//checks for insufficient input size
		//<server ip address>
		String serverIP = serverIPTest(args[0]);//checks for a valid server IP
		//<port number client2>
		int serverPort = portTest(args[1]);//checks for a valid integer for client 2 port number
		//< file containing client1's RSA public exponent and modulus>
		//make sure the client1's RSA file exists
		File RSA1 = fileTest(args[2], "<file containing client1's RSA public exponent and modulus>");
		//<file containing client2's RSA private exponent and modulus>
		//make sure the client2's RSA file exists
		File RSA2 = fileTest(args[3], "<file containing client2's RSA private exponent and modulus>");
		//Receive encrypted file and signature from server
		Socket socket = new Socket(serverIP, serverPort);//connect to the server
		//decrypt password
		BufferedInputStream inpass = new BufferedInputStream(socket.getInputStream());
		byte[] encodedKeyPriv = new byte[(int)RSA2.length()];//the encoded version of the private key will be read into this array
		new FileInputStream(RSA2).read(encodedKeyPriv); //the key contained in this file is read to the array
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey); //create a private key specification from the encoded key
		KeyFactory kf2 = null;
		PrivateKey pk2 = null;
		Cipher RSA = null;
		try{
			kf2 = KeyFactory.getInstance("RSA"); //instantiate RSA for public key
			pk2 = kf2.generatePrivate(privateKeySpec); //generate the public key
			RSA = Cipher.getInstance("RSA"); //instantiate RSA
			RSA.init(Cipher.DECRYPT_MODE, pk2); //initialize RSA encryption
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
		CipherInputStream cisRSA = new CipherInputStream(new FileInputStream(inpass), RSA);
		byte[] passDecr = new byte[256]; //password array
		byte[] passDecr = cisRSA.read(passDecr, 0, passDecr.length); //decrypt the password
		cisRSA.close();
		inpass.close();
		//prepare signature for verification
		BufferedInputStream insig = new BufferedInputStream(socket.getInputStream());
		byte[] signEncr = new byte[256]; //signature array
		insig.read(signEncr, 0, signEncr.length); //read in from socket
		insig.close(); //close the signature buffer
				Signature sig = null;
		try{
			SHA256 = Signature.getInstance("SHA256withRSA"); //instantiate SHA-256 with RSA
		} catch(NoSuchAlgorithmException e){
			System.out.println("Please include a valid algorithm.");
			System.exit(-1);
		}
		byte[] encodedKeyPub = new byte[(int)RSA1.length()];//the encoded version of the public key will be read into this array
		new FileInputStream(RSA1).read(encodedKeyPub); //the key contained in this file is read to the array
		X509EncodedKeySpec publicKeySpec =  new X509EncodedKeySpec(encodedKeyPub); //create a public key specification from the encoded key
		KeyFactory kf = null;
		PublicKey pk = null;
		try{
			kf = KeyFactory.getInstance("RSA"); //instantiate RSA for public key
			pk = kf.generatePublic(publicKeySpec); //generate the public key
			SHA256.initVerify(pk);
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
		//decrypt file
		byte[] file = new byte[1048576]; // encrypted file array
		Cipher aesCBC = null;
		try{
			aesCBC = Cipher.getInstance("AES/CBC/NoPadding"); //instantiate AES with CBC
			aesCBC.init(Cipher.DECRYPT_MODE, new SecretKeySpec(passDecr, "AES"), new IvParameterSpec(new byte[16])); //initialize
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
		//Decrypt file and Verify Signature
		BufferedInputStream infile = new BufferedInputStream(socket.getInputStream());
		CipherInputStream cis = new CipherInputStream(infile, aesCBC); //decrypt data from file
		int count = 0;
		while((count = cis.read(file, 0, file.length)) > 0){
			SHA256.update(file, 0, count);
		}
		//Disconnect from server after receiving password, file, and signature
		socket.close();
		boolean verifies = SHA256.verify(signEncr);
		if (!verifies){
			//Write result to stdout
			System.out.println("Verification Failed");
			System.exit(-1);
		}
		else{
			//Write result to stdout
			System.out.println("Verification Passed");
			//Name file "data" (no extension)
			FileOutputStream fos = new FileOutputStream("data");
			//Write unencrypted file received from server to disk in same directory as client 2 executable
			fos.write(file);
			fos.close(); //close buffer
		}
	}
}