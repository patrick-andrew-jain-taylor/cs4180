Language Used: Java
Certificate Generation: keytool utility

The Assignment 2 specification listed the following requirements of
certificates:

--Two-way authentication: client and server certificates
--Self-signed certificates allowed & imported into appropriate keystores
--Signature and Hash: RSA and SHA256

To complete this using keytool, we follow the following steps:

1) Generate client & server keystore

Given our outlined specification, we use the -genkeypair option, allowing for
public and private key generation, with the public key wrapped in a X.509 v3
self-signed certificate stored in a single element cert chain. The chain and
private key are stored in a keystore entry identified by the -alias option
(client for client, server for server). We specify RSA as our key algorithm
with the -keyalg option, and with RSA specified, RSA with SHA256 is
automatically specified for the -sigalg option (signature algorithm).

Additionally, X.500 certificates require a X.500 Distinguished Name, produced
by the -dname option. A Dname requires the following attributes: 
	CN=commonName
        OU=organizationUnit
        O=organizationName
        L=localityName
        S=stateName
        C=country
My selection for these options is included in the keystore entries.

-genkeypair also requires a key password and keystore password of 6+
characters (specified in -keypass and -storepass options), -genkeypair also
allows for a -validity option (length of time in days of a valid cert), a
keystore location (specified with -keystore option, and will be placed in the
respective client and server folders), and a storetype (specified by
-storetype, and set to JKS for the SSLSocket class).

The keystore entries, therefore, are generated as follows (entered as one
line, split up here for easy reading):

Client Keystore: 
	keytool -genkeypair 
		-alias client 
		-keyalg RSA 
		-dname "CN=Client, OU=COMS 4180, O=Columbia University, L=New York City, S=New York, C=US" 
		-keypass client
		-storepass client 
		-validity 90 
		-keystore client/client.keystore 	
		-storetype jks

Server Keystore:
	keytool -genkeypair 
		-alias server 
		-keyalg RSA 
		-dname "CN=Server, OU=COMS 4180, O=Columbia University, L=New York City, S=New York, C=US" 
		-keypass server 
		-storepass server 
		-validity 90 
		-keystore server/server.keystore 
		-storetype jks

2) Import server into client and client into server

Now that the keystores are generated, each respective keystore requires an
entry from the other for mutual authentication. This requires a certificate
import, provided by the -importcert option, after the respective -exportcert
option on the certificate generated in step 1) -exportcert will use the same
respective options for -alias, -storetype, -keystore, and -storepass, and will
now have an additional, -file, for the certificate .pfx (client/client.pfx for
client, server/server.pfx for server).

Export Client Cert:
	keytool -exportcert
		-alias client
		-file client/client.pfx
		-storetype jks
		-keystore client/client.keystore
		-storepass client

Export Server Cert:
	keytool -exportcert
		-alias server
		-file server/server.pfx
		-storetype jks
		-keystore server/server.keystore
		-storepass server

Once these certificates are exported, they will be imported into the opposite
keystores. Using -importcert, we will use import the -alias, -file, and
-keypass of the client, but the -keystore and -storepass of the server. Vice
versa applies for the server.

Import Client Cert:
	keytool -importcert
		-alias client
		-file client/client.pfx
		-keypass client
		-storetype jks
		-keystore server/server.keystore
		-storepass server

Import Server Cert:
	keytool -importcert
		-alias client
		-file server/server.pfx
		-keypass server
		-storetype jks
		-keystore client/client.keystore
		-storepass client

With the certs generated and imported into the proper keystores, we are ready
to begin socketing.
