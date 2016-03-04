package example.transfer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import example.ssl.SSLServer;
import example.util.ByteUtil;
import example.util.Util;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import example.ssl.SSLServer;
import example.util.ByteUtil;
import example.util.Util;


public class Test2 {

	public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
		new Test2().test();
	}
	void print(ByteBuffer b){
		System.out.println("position()=" + b.position() + ", limit()=" + b.limit() + ",  capacity=" + b.capacity());
	}
	void test(){
	}
	void test3() throws NoSuchAlgorithmException, KeyManagementException{
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, null, null);
        String[] protocols = sslContext.getSupportedSSLParameters().getProtocols();
        for (String protocol : protocols) {
            System.out.println("Context supported protocol: " + protocol);
        }
System.out.println("-----------------------");
        SSLEngine engine = sslContext.createSSLEngine();
        String[] supportedProtocols = engine.getSupportedProtocols();
        for (String protocol : supportedProtocols) {
        	System.out.println("Engine supported protocol: " + protocol);
        }
		// flip, get
	}
	void test2() throws IOException{
		ReadableByteChannel src = Channels.newChannel(System.in);
		WritableByteChannel des = Channels.newChannel(System.out);
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		
		while(src.read(buffer) != -1){
			des.write(buffer);
		}
		buffer.clear();
	}
}
