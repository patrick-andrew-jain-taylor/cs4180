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
decode the key into private and public components:

	(1) openssl rsa -in client1.pem -noout -text -out 
		client1privexpmod.pem
	(3) openssl rsa -in client2.pem -noout -text -out 
		client2privexpmod.pem

Since the private key contains both private and public elements, we must run a
special command to generate an output file that contains only the public key:

	openssl rsa -in client1.pem -pubout -out client1pub.pem
	openssl rsa -in client2.pem -pubout -out client2pub.pem

Once the public key is generated, we can run additional openssl commands to
decode the publuic key into public components:

	(2) openssl rsa -in client1pub.pem -pubin -noout -text -out
		client1pubexpmod.pem
	(4) openssl rsa -in client2pub.pem -pubin -noout -text -out
		client2pubexpmod.pem

With these commands, we have thus generated all 4 necessary RSA files.
