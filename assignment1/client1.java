import java.io.*;

public static void main(String[] args){
/*	Input: 	<server ip address> <port number client1> <client1 password>
 *		<file containing client1's RSA private exponent and modulus>
 *		<file containing client2's RSA public exponent and modulus> 
 *		<file name>*/

	//Check for invalid/garbage/missing input
	//Encrypt file with AES in CBC
	//Hash plaintext with SHA-256
	//Encrypt hash with RSA (private key)
	//Send Encrypted data and signature to the server
	//Encrypt password with client2 RSA key
	//Send encrypted password to client 2 via server
	//Disconnect from server after sending password, file, and signature 
}
