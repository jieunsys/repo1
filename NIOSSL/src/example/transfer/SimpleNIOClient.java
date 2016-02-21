package example.transfer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import javax.net.ssl.SSLEngineResult.HandshakeStatus;

import example.ssl.SSLClient;
import example.util.Util;

public class SimpleNIOClient {

	private static final String keyStore = "keystore/client/SimpleNIOClient.keystore";
	private static final String storepass = "example";

	private static final String HOST = "localhost";
	private static final int PORT = 9090;

	private SocketChannel sc = null;
	private Selector selector = null;

	private SSLClient sslClient = null;

	public SimpleNIOClient() throws Exception {
		initClient();
	}

	public void initClient() throws Exception {
		selector = Selector.open();
		sc = SocketChannel.open(new InetSocketAddress(HOST, PORT));
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);

		sslClient = new SSLClient(keyStore, storepass.toCharArray(), sc);
		sslClient.beginHandShake();
	}

	public void startClient() {
		try {
			while (true) {
				if (selector.select() <= 0) {
					continue;
				}

				Iterator<SelectionKey> it = selector.selectedKeys().iterator();

				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();

					if (key.isReadable()) {
						read(key);
					}
					it.remove();
				}
			}
		} catch (Exception e) {

		}
	}

	private void read(SelectionKey key) {
		SocketChannel sc = (SocketChannel) key.channel();

		ByteBuffer buffer = ByteBuffer.allocate(1024);

		try {
			if (sslClient.getHandShakeStatus() != HandshakeStatus.NOT_HANDSHAKING
					&& sslClient.getHandShakeStatus() != HandshakeStatus.FINISHED) {
				sslClient.handshake(sc);

				if (sslClient.getHandShakeStatus() == HandshakeStatus.NOT_HANDSHAKING) {
					sc.write(sslClient.encrypt(ByteBuffer.wrap("Hi! Server~!".getBytes())));
				}
			} else {
				if (sc.read(buffer) > 0) {
					buffer.flip();
					Util.log("[Server Message]", sslClient.decrypt(buffer));
					shutdown(sc);
				} else {
					sslClient.closeInbound();
					shutdown(sc);
				}
			}
		} catch (IOException e) {
			shutdown(sc);
		}
	}

	private void shutdown(SocketChannel sc) {
		try {
			sslClient.closeOutbound();
			sc.close();
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		SimpleNIOClient client = new SimpleNIOClient();
		client.startClient();
	}

}
