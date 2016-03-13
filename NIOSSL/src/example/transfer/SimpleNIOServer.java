package example.transfer;

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
import javax.net.ssl.SSLException;

import example.ssl.SSLServer;
import example.util.ByteUtil;
import example.util.Util;

public class SimpleNIOServer {
	private static final int PORT = 9090;
	
//	Make KeyStore
//	Keytool -genkey -alias mukuri -keystore mukuri.jks -keyalg RSA -keysize 2048 -sigalg SHA256withRSA  -storepass mukuri -storetype JKS -dname "cn=CN1, ou=OU1, o=Seoul, c=KR" -keypass mukuri -validity 7 -v
	
	private static final String keyStore = "keystore/server/mukuri.jks";
	private static final String storepass = "mukuri";

	private Selector selector = null;
	private ServerSocketChannel serverSocketChannel = null;
	private ServerSocket serverSocket = null;

	private ConcurrentHashMap<SocketChannel, SSLServer> sslServerMap = null;

	public SimpleNIOServer() {
		initServer();
	}

	public void initServer() {
		try {
			selector = Selector.open();
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocket = serverSocketChannel.socket();
			InetSocketAddress isa = new InetSocketAddress(PORT);
			serverSocket.bind(isa);

			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			sslServerMap = new ConcurrentHashMap<SocketChannel, SSLServer>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startServer() {
		try {
			while (true) {
				if (selector.select() <= 0) {
					continue;
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
		} catch (ClosedSelectorException e) {

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	private void accept(SelectionKey key) {
		ServerSocketChannel server = (ServerSocketChannel) key.channel();
		SocketChannel sc;

		try {
			sc = server.accept();
			sc.configureBlocking(false);
			sc.register(selector, SelectionKey.OP_READ);
			SSLServer sslServer = new SSLServer(keyStore, storepass.toCharArray(), sc);
			sslServerMap.put(sc, sslServer);
			sslServer.beginHandShake();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getResponseString(SocketChannel sc, SSLServer sslServer, String str) throws SSLException, IOException {
		System.out.println("======  Request ======================================");
		System.out.println("[[[" + str + "]]]");
		
		if(str.startsWith("GET / HTTP/1.1")){
			System.out.println("##########################################");
			String ok = "HTTP/1.1 200 OK\n\n<html><body>HelloWorld@@</body></html>\n";
//			ok = ByteUtil.byteArrayToHex( ok.getBytes() );
			
			ok= HelloWorlds.getHelloWorlds();
			System.out.println("ok=[[[" + ok + "]]]");
			
			sc.write(sslServer.encrypt(ByteBuffer.wrap(ok.getBytes())));
			shutdown(sc);
			return;
		}
		
		String[] va = str.split("\n");
		String secAccept=null;
		for(String line:va){
			if(line.startsWith("Sec-WebSocket-Key:")){
				String[] vva = line.split("Sec-WebSocket-Key:");
				String src = vva[1].trim();
				System.out.println("@@@@@@@@@@@@@@@@@@sec  Key=" + src);
				secAccept = Util.sha1base64(src + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11");
				System.out.println("@@@@@@@@@@@@@@@@@@secAccept=" + secAccept);
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
		System.out.println("======= Response Send Start ====================================================");					
		System.out.println(sb.toString());					
		System.out.println("======= Response Send End ====================================================");	
		
		try {
			sc.write(sslServer.encrypt(ByteBuffer.wrap(sb.toString().getBytes())));
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sslServer.setHandshake(true);

		return;
	}

	private void read(SelectionKey key) {
		SocketChannel sc = (SocketChannel) key.channel();

		ByteBuffer buffer = ByteBuffer.allocate(1024);

		try {
			SSLServer sslServer = sslServerMap.get(sc);
			HandshakeStatus hs = sslServer.getHandShakeStatus();
			if (hs != HandshakeStatus.NOT_HANDSHAKING && hs != HandshakeStatus.FINISHED) {
				System.out.println("############## SERVER: handshake() start");
				sslServer.handshake(sc);
				System.out.println("############## SERVER: handshake() end");
			} else {
				/**** start *****/
				System.out.println("############## SERVER: read() start");
				int len = sc.read(buffer);
				if (len > 0) {
					buffer.flip();
				
					ByteBuffer b1 = sslServer.decrypt(buffer);
					String str = Util.bufferToString(b1);
					if(sslServer.isHandshake()){
						System.out.println( "REQUEST = [[[" + ByteUtil.byteArrayToHex(str.getBytes()) + "]]]");
						String ok = "813731313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131313131";
						
						byte[] bb=ByteUtil.hexToByteArray(ok);
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> Data Response 1st=[" + new String(bb) + "]");
						ByteBuffer b2 = sslServer.encrypt(ByteBuffer.wrap(bb));
						sc.write(b2);
						return;
					}

					getResponseString(sc, sslServer, str);
				}else{
					sslServer.closeInbound();
					shutdown(sc);
				}
				int a=5; if(a==5)return;
				/**** end *******/
				if (sc.read(buffer) > 0) {
					buffer.flip();
					Util.log("[Client Message]", sslServer.decrypt(buffer));
					sc.write(sslServer.encrypt(ByteBuffer.wrap("Hi! Client~!".getBytes())));
				} else {
					sslServer.closeInbound();
					shutdown(sc);
				}
			}
		} catch (IOException e) {
			shutdown(sc);
		}
	}

	private void shutdown(SocketChannel sc) {
		try {
			SSLServer sslServer = sslServerMap.get(sc);
			sslServer.closeOutbound();
			sslServerMap.remove(sc);

			if (sc != null)
				sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SimpleNIOServer server = new SimpleNIOServer();
		server.startServer();
	}
}
