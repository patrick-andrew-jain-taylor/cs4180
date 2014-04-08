import java.security.KeyStore;
import java.security.*;
import java.io.*;
import java.security.cert.*;
import java.io.FileInputStream;
import javax.net.ssl.*;
import javax.net.ssl.SSLContext;

/* the socket class is a series of functions implementing the procedure outlined in the Palomino Labs Blog entitled
 * "Java 2-way TLS/SSL (Client Certificates) and PKCS12 vs JKS KeyStores). The Client and Server will both
 * use these functions, but will do so for their respective implementations.
 *
 * Link: http://blog.palominolabs.com/2011/10/18/java-2-way-tlsssl-client-certificates-and-pkcs12-vs-jks-keystores/
 * */

public class socket{
	//KeyStore: return keyStore to be used for sending/receiving certs (keyStore/trustStore)
	public static KeyStore keyStoreTLS(String keyStoreType, String keyStorePath, String password){
		KeyStore keyStore = null; //instantiate keystore
		try{
			FileInputStream inputStream = new FileInputStream(keyStorePath); //create keystore file input
			keyStore = KeyStore.getInstance(keyStoreType); //get instance of keystore of defined type
			keyStore.load(inputStream, password.toCharArray()); //load the keystore with given PW
			inputStream.close(); //close keystore file input
			return keyStore; //return the keystore
		} catch (FileNotFoundException e){//file not found
			storeError.invalidKeyStoreFile(keyStorePath);
		} catch (KeyStoreException e){//issue with keystore
			storeError.invalidKeyStoreType(keyStoreType);
		} catch (IOException e){//issue with keystore data
			storeError.invalidKeyStoreData(keyStorePath);
		} catch (NoSuchAlgorithmException e){//keystore algorithm error
			storeError.invalidAlgorithm(keyStorePath);
		} catch (CertificateException e){//cert error
			storeError.invalidCertificate(keyStorePath);
		}
		return keyStore; //return the keystore
	}
	//KeyManagerFactory: used to manage keyStore (sending certs)
	public static KeyManagerFactory keyManagerTLS(KeyStore keyStore, String password){
		KeyManagerFactory keyManagerFactory = null; //instantiate key manager factory
		try{
			keyManagerFactory = 
				KeyManagerFactory.getInstance("SunX509", "SunJSSE"); //get instance of this type of key manager factory
			keyManagerFactory.init(keyStore, password.toCharArray()); //initalize the key manager
			return keyManagerFactory; //return the key manager
		} catch (NoSuchAlgorithmException e){//algorithm not found
			managerError.invalidAlgorithm();
		} catch (NoSuchProviderException e){//provider not found
			managerError.invalidProvider();
		} catch (IllegalArgumentException e){//not enough arguments
			managerError.illegalArgument();
		} catch (NullPointerException e){//null pointer found
			managerError.nullPointer();
		} catch (KeyStoreException e){//issue with keystore
			storeError.invalidKeyStore();
		} catch (UnrecoverableKeyException e){//issue with key
			storeError.invalidKeyStore();
		} 
		return keyManagerFactory; //return trust manager
	}

	//TrustManagerFactory: used to manage trustStore (receiving certs)
	public static TrustManagerFactory trustManagerTLS(KeyStore trustStore){
		TrustManagerFactory trustManagerFactory = null; //instantiate trust manager
		try{
			trustManagerFactory = 
				TrustManagerFactory.getInstance("PKIX", "SunJSSE"); //get instance of this type of trust manager factory
			trustManagerFactory.init(trustStore); //initalize the trust manager
			return trustManagerFactory; //return trust manager
		} catch (NoSuchAlgorithmException e){ //algorithm not found
			managerError.invalidAlgorithm();
		} catch (NoSuchProviderException e){//provider not found
			managerError.invalidProvider();
		} catch (IllegalArgumentException e){//not enough arguments
			managerError.illegalArgument();
		} catch (NullPointerException e){//null pointer found
			managerError.nullPointer();
		} catch (KeyStoreException e){//issue with keystore
			managerError.trustStoreFail();
		}
		return trustManagerFactory; //return trust manager
	}
	//SSLContext: used for socketing
	public static SSLContext SSLContextTLS(KeyManagerFactory keyManagerFactory, TrustManagerFactory 
			trustManagerFactory, String password){
		SSLContext sslContext = null; //instantiate SSL Context
		try{
			//x509keymanager
			X509KeyManager x509KeyManager = null; //instantiate X509 Key Manager
			for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {//check for available key manager factories for each unique key manager
				if (keyManager instanceof X509KeyManager) {//check for a unique instance of X509 key manager in the key manager
					x509KeyManager = (X509KeyManager) keyManager; //set X509 Key Manager
					break;
				}
			}
			if (x509KeyManager == null) {//no X509 Key Managers found
				throw new NullPointerException();
			}
			//x509trustmanager
			X509TrustManager x509TrustManager = null; //instantiate X509 Trust Manager
			for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) { //check for available trust manager factories for each unique trust manager
				if (trustManager instanceof X509TrustManager) {//check for a unique instance of X509 trust manager in the trust manager
					x509TrustManager = (X509TrustManager) trustManager; //set X509 trust manager
					break;
				}
			}
	       		if (x509TrustManager == null) {//no X509 Trust Managers found
    				throw new NullPointerException();
			}
			sslContext = SSLContext.getInstance("TLS"); //generate instance of TLS
			//initalize the SSL Context with our X509 key and trust managers
			sslContext.init(new X509KeyManager[]{x509KeyManager}, new X509TrustManager[]{x509TrustManager}, null);
			return sslContext; //return the SSL Context
		} catch (NoSuchAlgorithmException e){//algorithm not found
			managerError.invalidAlgorithm();
		} catch (NullPointerException e){//null pointer found
			managerError.invalidProtocol();
		} catch (KeyManagementException e){//issue with key manager
			managerError.invalidManager();
		} catch(IllegalStateException e){//illegal state (nearly impossible)
			managerError.illegalState();
		}
		return sslContext; //return SSL Context
	}
/* the storeError class contains all functions used to generate unique store error messages for the socket
 * class.
 */
	private static class storeError{
		//invalidKeyStore: prints error message for invalid keyStore file.
		private static void invalidKeyStore(){
			System.out.println("Keystore not found.");
			System.exit(1);
		}
		//invalid KeyStoreFile: key store not found at this path
		private static void invalidKeyStoreFile(String keyStorePath){
			System.out.println("Given keyStorePath: " + keyStorePath);
			System.out.println("Please use a valid path for the keystore.");
			System.exit(1);
		}
		//invalidKeyStoreType: prints error message for invalid keyStore type
		private static void invalidKeyStoreType(String keyStoreType){
			System.out.println("Given keyStoreType: " + keyStoreType);
			System.out.println("keyStoreType is either \"JKS\" or \"PKCS12\"");
			System.exit(1);
		}
		//invalidKeyStoreData: issue with the keystore at the given path
		private static void invalidKeyStoreData(String keyStorePath){
			System.out.println("Problem with keystore found at " + keyStorePath);
			System.exit(1);
		}
		//invalidAlgorithm: issue with keystore algorithm
		private static void invalidAlgorithm(String keyStorePath){
				System.out.println("The algorithm used to check the integrity of the keystore found at " + keyStorePath + " cannot be found.");
			System.exit(1);
		}
		//invalidCertificate: issue with cert found at the keystore
		private static void invalidCertificate(String keyStorePath){
			System.out.println("One or more of the certificates in the keystore found at "
					+ keyStorePath + "could not be loaded.");
			System.exit(1);
		}
	}


/* the managerError class contains all functions used to generate unique manager error messages for the 
 * socket class.
 */ 
	private static class managerError{
		//invalid algorithm: issue with key/trust manager algorithm specified
		private static void invalidAlgorithm(){ //since directly specified, this should not ever occur.
			System.out.println("Unknown trust manager algorithm issue.");
			System.exit(-1);
		}
		//invalid provider: issue with key/trust manager provider specified
		private static void invalidProvider(){ //since directly specified, this should not ever occur.
			System.out.println("Unknown trust manager provider issue.");
			System.exit(-1);
		}
		//illegal argument: incorrect number of arguments for algorithm/provider
		private static void illegalArgument(){ //since directly specified, this should not ever occur.
			System.out.println("Provider name null/empty.");
			System.exit(-1);
		}
		//null pointer: null pointer found
		private static void nullPointer(){//since directly specified, this should not ever occur.
			System.out.println("Algorithm is null.");
			System.exit(-1);
		}
		//trustStoreFailed: trust initialization failed
		private static void trustStoreFail(){
			System.out.println("Trust store init failed.");
			System.exit(1);
		}
		//illegalState: issue with factory
		private static void illegalState(){
			System.out.println("Factory not initialized.");
			System.exit(1);
		}
		//invalidProtocol: issue with protocol specified
		private static void invalidProtocol(){ //since directly specified, this should not ever occur.
			System.out.println("Protocol is null");
			System.exit(-1);
		}
		//invalidManager: incorrect manager used
		private static void invalidManager(){
			System.out.println("Failed context initialization.");
			System.exit(1);
		}
	}

	public static void main(String[] args){
		//tested for client
		//keystore
		String keyStoreType = "JKS";
		String keyStorePath = "client/client.keystore";
		String keyStorePassword = "client";
		KeyStore keyStore = keyStoreTLS(keyStoreType, keyStorePath, keyStorePassword);
		KeyManagerFactory keyManagerFactory = keyManagerTLS(keyStore, keyStorePassword);
		//truststore
		String trustStorePath = "client/clienttrust.keystore";
		String trustStorePassword = keyStorePassword;
		KeyStore trustStore = keyStoreTLS(keyStoreType, trustStorePath, trustStorePassword);
		TrustManagerFactory trustManagerFactory = trustManagerTLS(trustStore);
		//bring it all together
		SSLContext sslContext =	
			SSLContextTLS(keyManagerFactory, trustManagerFactory, keyStorePassword);
	}
	
}
