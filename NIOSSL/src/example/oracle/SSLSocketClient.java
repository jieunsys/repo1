package example.oracle;

import java.io.*;
import java.security.Security;
import java.util.Properties;

import javax.net.ssl.*;

public class SSLSocketClient {

	public static void main(String[] args) throws Exception {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		// SSLSocket socket = (SSLSocket)
		// factory.createSocket("www.verisign.com", 443);
		SSLSocket socket = (SSLSocket) factory.createSocket("127.0.0.1", 9090);
		socket.startHandshake();
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		out.println("GET / HTTP/1.1");
		out.println();
		out.flush();

		if (out.checkError())
			System.out.println("SSLSocketClient:  java.io.PrintWriter error");

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);

		in.close();
		out.close();
		socket.close();
	}
}