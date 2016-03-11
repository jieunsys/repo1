package pak;

import java.net.*;
import java.io.*;

public class EchoServer {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		ServerSocket serverSocket = null;

		serverSocket = new ServerSocket(10007);

		Socket clientSocket = serverSocket.accept();
		

		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		String inputLine;
		
		while ((inputLine = in.readLine()) != null) {
			System.out.println("Server: " + inputLine);
			out.println(inputLine);

			if (inputLine.equals("Bye."))
				break;
		}

		out.close();
		in.close();
		clientSocket.close();
		serverSocket.close();
	}
}