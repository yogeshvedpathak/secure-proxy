This project has a secure proxy to the GIPHY API. This proxy only supports the HTTP GET method. 
- If it receives a successful HTTP response (which may have HTTP response code > 200) it will be returned to the client as is. 
- If there are errors in the parsing the client requests then HTTP 400 will be returned.
- For any other errors HTTP 500 will be returned. 

This server endpoint has a mandatory request parameter 'q'. This is the search query that will be proxied to the GIPHY service. 

###Generate a keystore:
For successfully establishing a secure connection between client and server we will use a security key. The key will be stored locally in a keystore. To create a keystore and key run following commands.

```
mkdir securekeystore
cd securekeystore
keytool -genkey -alias sp -keystore secureproxy  -keyalg RSA -sigalg SHA1withRSA -keysize 2048 validity 10000 
```
Note the password you used while creating a key store. You will need it for running server and client. 

```
pwd
```
 
Note the path returned by the pwd command. 

###Build the project

To build a jar for client and server simply run `mvn clean install` from the project's root directory. The jar file will be generated in the 'target' directory.  

###Run server 

In order to run the server we need to pass the build jar and location and password of the keystore. Run following command:
```
java -cp target/secure-proxy-1.0-SNAPSHOT.jar -Djavax.net.ssl.keyStore=<PATH_TO_KEYSTORE>/ecureproxy -Djavax.net.ssl.keyStorePassword=<PASSWORD> org.signal.secureproxy.server.SecureProxyServer
```

The server will start and listen on port 8080`

###Run client

In order to run the client we need to pass the build jar and location and password of the trust store. Run following command:
```
java -cp target/secure-proxy-1.0-SNAPSHOT.jar -Djavax.net.ssl.trustStore=<PATH_TO_KETSTORE>/secureproxy -Djavax.net.ssl.trustStorePassword=<PASSWORD> org.signal.secureproxy.server.SecureProxyClient
```

I was not able to complete the client part. Currently, the client can't fully send the data to the server which results in getting no response from the server. 
In order to test the server you can use curl command. However, you will need to disable ssl verification in curl. Following are some examples of the curl command. 
```
curl -k -v "https://localhost:8080?q=congratulations"
curl -k -v "https://localhost:8080?q=i am excited"
```

Ideally I wanted to set up certificates for the server so that any HTTPClient can use the same certificates to open a secure connection with the server. However, in interest of time I just implemented the key store. 
I was also not able to add unit tests and extensive data validation. 


