package example.client.test.ok;

import java.io.*;
import javax.net.ssl.*;

import example.util.ByteUtil;

public class SSLSocketClient2 {

	public static void main(String[] args) throws Exception {
		new SSLSocketClient2().test();
	}
	public void test() throws Exception {
		
//		Original factory
//		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		
        SSLContext ssl = SSLContext.getInstance("TLSv1.2");
        ssl.init(null, WebSocketSslClientTrustManagerFactory.getTrustManagers(), null);
	    SSLSocketFactory factory = ssl.getSocketFactory();
	    
		SSLSocket socket = (SSLSocket) factory.createSocket("127.0.0.1", 9090);
		socket.startHandshake();
		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
		
		StringBuffer sb=new StringBuffer();
		sb.append("GET /chat HTTP/1.1\n");
		sb.append("Host: server.example.com\n");
		sb.append("Upgrade: websocket\n");
		sb.append("Connection: Upgrade\n");
		sb.append("Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\n");
		sb.append("Origin: http://this.client.com\n");
		sb.append("Sec-WebSocket-Version: 13\n");
		sb.append("\n");
		
		out.println(sb.toString());
		out.flush();
		if (out.checkError()) System.out.println("SSLSocketClient:  java.io.PrintWriter error");

		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String inputLine;
		// Print Web Socket Handshake Response 
		while ((inputLine = in.readLine()) != null){
			System.out.println(inputLine);
			if(inputLine.equals("")) break;
		}

		// 1st send ---------------------------------------------------
		String wssStr = "efbfbdefbfbdefbfbdefbfbd1aefbfbdefbfbd";
		wssStr = new String(ByteUtil.hexToByteArray(wssStr));
		out.print(wssStr);
		out.flush();

		char[] bb = new char[128];
		in.read(bb);
		System.out.println( new String(bb));
		
		// 2nd send ---------------------------------------------------
		out.print(wssStr);
		out.flush();

		in.read(bb);
		System.out.println( new String(bb));
		// 2nd send end ---------------------------------------------------

		
		in.close();
		out.close();
		socket.close();
	}
	
	public void testok() throws Exception {
        SSLContext clientContext = SSLContext.getInstance("TLSv1.2");
        clientContext.init(null, WebSocketSslClientTrustManagerFactory.getTrustManagers(), null);
	    SSLSocketFactory factory = clientContext.getSocketFactory();
	    
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