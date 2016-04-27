package org.mukuri.base;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.SSLException;
import org.mukuri.common.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLEngineResult.HandshakeStatus;

/**
 * 
 * @author cougar
 * Make KeyStore
 * Keytool -genkey -alias mukuri -keystore mukuri.jks -keyalg RSA -keysize 2048 -sigalg SHA256withRSA  -storepass mukuri -storetype JKS -dname "cn=CN1, ou=OU1, o=Seoul, c=KR" -keypass mukuri -validity 7 -v
 * browser test: https://localhost:9090/
 *
 */

public class BaseServer {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private static final int PORT = 9090;
	private static final String keyStore = "keystore/mukuri.jks";
	private static final String storepass = "mukuri";

	private Selector selector = null;
	private ServerSocketChannel serverSocketChannel = null;
	private ServerSocket serverSocket = null;
	private ConcurrentHashMap<SocketChannel, SslHandshaker> socketTable = null;

	public void startUp() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocket = serverSocketChannel.socket();
			InetSocketAddress isa = new InetSocketAddress(PORT);
			serverSocket.bind(isa);

			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			socketTable = new ConcurrentHashMap<SocketChannel, SslHandshaker>();
		} catch (IOException e) {
			logger.error(e.getMessage());
			System.exit(-1);
		}
		while (true) {
			try {
				if (selector.select() <= 0) continue;
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey key = (SelectionKey) it.next();
				if (key.isAcceptable()) {
					accept(key);
				} else if (key.isReadable()) {
					read(key);
				}
				it.remove();
			}
		}
	}

	private void accept(SelectionKey key) {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel sc = null;

		try {
			sc = server.accept();
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
			SslHandshaker sslHandshaker = new SslHandshaker(keyStore, storepass.toCharArray(), sc);
			socketTable.put(sc, sslHandshaker);
			sslHandshaker.beginHandShake();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	

	private void read(SelectionKey key) {
		SocketChannel sc = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		SslHandshaker sslHandshaker = socketTable.get(sc);
		HandshakeStatus stat = sslHandshaker.getHandShakeStatus();
		
		try{
			if (stat != HandshakeStatus.NOT_HANDSHAKING && stat != HandshakeStatus.FINISHED) {
				sslHandshaker.handshake(sc);
			} else {
				int len = sc.read(buffer);
				if(len > 0) {
					buffer.flip();
					ByteBuffer b1 = sslHandshaker.decrypt(buffer);
					String str = ByteUtil.bufferToString(b1);
					
					if(sslHandshaker.isWssHandshakeFinished()){
						sendWssData(sc, sslHandshaker);
					} else {
						webSocketHandshake(sc, sslHandshaker, str);
					}
				}else{
					sslHandshaker.closeInbound();
					socketChannelShutdown(sc);
				}
			}
		}catch(IOException e){
			socketChannelShutdown(sc);
			logger.error(e.getMessage());
		}
	}

	private void socketChannelShutdown(SocketChannel sc) {
		try {
			SslHandshaker sslHandshaker = socketTable.get(sc);
			sslHandshaker.closeOutbound();
			socketTable.remove(sc);

			if (sc != null)
				sc.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	private void webSocketHandshake(SocketChannel sc, SslHandshaker sslHandshaker, String str) {
		if(str.startsWith("GET / HTTP/1.1")){
			String ok = "HTTP/1.1 200 OK\n\n<html><body>HelloWorld@@</body></html>\n";
			ok= HttpsDocument.getHttpsString();
			ByteBuffer bb=null;
			try {
				bb = sslHandshaker.encrypt(ByteBuffer.wrap(ok.getBytes()));
			} catch (SSLException e) {
				logger.error(e.getMessage());
			}
			try {
				sc.write(bb);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			socketChannelShutdown(sc);
			return;
		}
		
		String[] va = str.split("\n");
		String secAccept=null;
		for(String line:va){
			if(line.startsWith("Sec-WebSocket-Key:")){
				String[] vva = line.split("Sec-WebSocket-Key:");
				String src = vva[1].trim();
				logger.debug("Sec-WebSocket-Key=" + src);
				secAccept = ByteUtil.sha1base64(src + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
				logger.debug("secAccept=" + secAccept);
				break;
			}
		}
							
		StringBuffer sb = new StringBuffer();
		sb.append("HTTP/1.1 101 Switching Protocols\n");
		sb.append("Server: Apache-Coyote/1.1\n");
		sb.append("Upgrade: websocket\n");
		sb.append("Connection: upgrade\n");
		sb.append("Sec-WebSocket-Accept: " + secAccept + "\n");
		sb.append("Date: Fri, 19 Feb 2016 05:31:47 GMT\n");
		sb.append("\n");
		
		try {
			sc.write(sslHandshaker.encrypt(ByteBuffer.wrap(sb.toString().getBytes())));
		} catch (SSLException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		sslHandshaker.setWssHandshakeFinished(true);
	}
	
	private void sendWssData(SocketChannel sc, SslHandshaker sslHandshaker){
		String ok = "813731313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131";
		
		byte[] bb=ByteUtil.hexToByteArray(ok);
		ByteBuffer b2=null;
		try {
			b2 = sslHandshaker.encrypt(ByteBuffer.wrap(bb));
		} catch (SSLException e) {
			socketChannelShutdown(sc);
			logger.error(e.getMessage());
		}
		try {
			sc.write(b2);
		} catch (IOException e) {
			socketChannelShutdown(sc);
			logger.error(e.getMessage());
		}
	}
	public static void main(String[] args) {
		new BaseServer().startUp();
	}
}
