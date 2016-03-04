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
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import example.util.ByteUtil;
import example.util.Util;

public class SimpleNIOServer {

	private static final int PORT = 9090;

	private Selector selector = null;
	private ServerSocketChannel serverSocketChannel = null;
	private ServerSocket serverSocket = null;

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	boolean isHandShake=false;			

	private void read(SelectionKey key) {
		SocketChannel sc = (SocketChannel) key.channel();

		ByteBuffer buffer = ByteBuffer.allocate(1024);

		try {
			if (sc.read(buffer) > 0) {
//=================================================================================================================				
				boolean boo=true;if(boo==true){
//int len=buffer.position();					
					buffer.flip();
//byte[] bb= new byte[len];					
//buffer.get(bb,0,len);
//String str = new String(bb);
					String str = Util.bufferToString(buffer);
					System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<< Data Request=[" + str + "]");
					if(isHandShake==true){
						String ok = "811c526f636b20697420776974682048544d4c3520576562536f636b6574";
						byte[] bb = ByteUtil.hexToByteArray(ok);
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> Data Response 1st=[" + new String(bb) + "]");
						sc.write(ByteBuffer.wrap(bb));
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>> Data Response hex=[" + ByteUtil.byteArrayToHex(bb) + "]");
						return;
					}
System.out.println(str);	
System.out.println("start parsing----------------");
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
System.out.println("end parsing----------------");
	
					
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
isHandShake=true;
					sc.write(ByteBuffer.wrap(sb.toString().getBytes()));
				}
				boo=true; if(boo==true) return;
//=================================================================================================================				
				buffer.flip();
				Util.log("[Client Message]", buffer);
				sc.write(ByteBuffer.wrap("Hi! Client~!".getBytes()));
			} else {
				shutdown(sc);
			}
		} catch (IOException e) {
			shutdown(sc);
		}
	}

	private void shutdown(SocketChannel sc) {
		try {
			if (sc != null)
				sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleNIOServer server = new SimpleNIOServer();
		server.startServer();
	}
}
