(1) the steps you used to generate the RSA keys

For this assignment, we need to generate four files:
	1) Client1's private exponent and modulus
	2) Client1's public exponent and modulus
	3) Client2's private exponent and modulus
	4) Client2's public exponent and modulus

To retrieve these files for exponents and moduli, we must first generate RSA
keys for Client1 and Client2. We may generate RSA keys using the following
openssl commands:

	openssl genrsa -out client1.pem 2048
	openssl genrsa -out client2.pem 2048

Once the keys have been generated, we can run additional openssl commands to
decode the key into private and public components in the right format:

	(1) openssl pkcs8 -topk8 -nocrypt -in client1.pem -inform PEM -out
		client1.der -outform DER
	(3) openssl pkcs8 -topk8 -nocrypt -in client2.pem -inform PEM -out
		client2.der -outform DER

Since the private key contains both private and public elements, we must run a
special command to generate an output file that contains only the public key:

	(2) openssl rsa -in client1.pem -pubout -outform DER -out client1pub.der
	(4) openssl rsa -in client2.pem -pubout -outform DER -out client2pub.der

With these commands, we have thus generated all 4 necessary RSA files.
