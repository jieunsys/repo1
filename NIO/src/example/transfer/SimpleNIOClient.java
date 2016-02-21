package example.transfer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import example.util.Util;

public class SimpleNIOClient {

	private static final String HOST = "localhost";
	private static final int PORT = 9090;

	private SocketChannel sc = null;
	private Selector selector = null;

	public SimpleNIOClient() throws Exception {
		initClient();
	}

	public void initClient() throws Exception {
		selector = Selector.open();
		sc = SocketChannel.open(new InetSocketAddress(HOST, PORT));
		sc.configureBlocking(false);
		sc.register(selector, SelectionKey.OP_READ);
	}

	public void startClient() {
		try {
			sc.write(ByteBuffer.wrap("Hi! Server~!".getBytes()));

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
			if (sc.read(buffer) > 0) {
				buffer.flip();
				Util.log("[Server Message]", buffer);
				shutdown(sc);
			} else {
				shutdown(sc);
			}
		} catch (IOException e) {
			shutdown(sc);
		}
	}

	private void shutdown(SocketChannel sc) {
		try {
			sc.close();
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SimpleNIOClient client = new SimpleNIOClient();
		client.startClient();
	}

}
