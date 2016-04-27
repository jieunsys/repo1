package org.mukuri.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SslHandshaker {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@SuppressWarnings("unused")
	private String keyStorePath;
	@SuppressWarnings("unused")
	private char[] passwd;
	private SSLEngine sslEngine;
	private SSLContext sslContext;
	private SSLSession sslSession;
	private SSLEngineResult sslEngineResult;
	private SocketChannel sc;
	private ByteBuffer outBuffer;
	private ByteBuffer inBuffer;
	private ByteBuffer dummy;
	
	private boolean wssHandshakeFinished = false;

	public boolean isWssHandshakeFinished() {
		return wssHandshakeFinished;
	}

	public void setWssHandshakeFinished(boolean wssHandshakeFinished) {
		this.wssHandshakeFinished = wssHandshakeFinished;
	}

	public SslHandshaker(String keyStorePath, char[] passwd, SocketChannel sc) throws Exception {
		this.keyStorePath = keyStorePath;
		this.passwd = passwd;
		this.sc = sc;
		
		KeyStore store = KeyStore.getInstance("JKS");
		store.load(new FileInputStream(keyStorePath), passwd);

		KeyManagerFactory factory = KeyManagerFactory.getInstance("SunX509");
		factory.init(store, passwd);

		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(factory.getKeyManagers(), null, null);

		sslEngine = sslContext.createSSLEngine();
		sslEngine.setUseClientMode(false);
		sslSession = sslEngine.getSession();

		dummy = ByteBuffer.allocate(0);
		outBuffer = ByteBuffer.allocate(this.getNetBufferSize());
		inBuffer = ByteBuffer.allocate(this.getAppBufferSize());
	}

	public void beginHandShake() throws SSLException {
		sslEngine.beginHandshake();
	}

	public void handshake(SocketChannel sc) throws SSLException, IOException {

		ByteBuffer buff = ByteBuffer.allocate(this.getNetBufferSize());
		while (sc.read(buff) > 0);
		buff.flip();

		while (buff.hasRemaining()) {
			inBuffer = this.decrypt(buff);
			if (this.getHandShakeStatus() == HandshakeStatus.NEED_TASK) {
				doTask();
			}
			while (this.getHandShakeStatus() == HandshakeStatus.NEED_WRAP) {
				dummy.rewind();
				outBuffer = this.encrypt(dummy);
				writeToSocket(outBuffer);
			}
		}
	}

	public HandshakeStatus getHandShakeStatus() {
		return sslEngine.getHandshakeStatus();
	}

	public synchronized ByteBuffer encrypt(ByteBuffer buffer) throws SSLException {
		outBuffer.clear();
		sslEngineResult = sslEngine.wrap(buffer, outBuffer);
		outBuffer.flip();
		return outBuffer;
	}

	public synchronized ByteBuffer decrypt(ByteBuffer buffer) throws SSLException {
		inBuffer.clear();
		sslEngineResult = sslEngine.unwrap(buffer, inBuffer);
		inBuffer.flip();
		return inBuffer;
	}

	public int getNetBufferSize() {
		return sslSession.getPacketBufferSize();
	}

	public int getAppBufferSize() {
		return sslSession.getApplicationBufferSize();
	}

	private void doTask() {
		Runnable task;

		while ((task = sslEngine.getDelegatedTask()) != null) {
			task.run();
		}
	}

	public void closeInbound() throws SSLException {
		sslEngine.closeInbound();
	}

	public void closeOutbound() {
		sslEngine.closeOutbound();
	}

	private void writeToSocket(ByteBuffer buff) throws IOException {
		while (buff.hasRemaining()) {
			sc.write(buff);
		}
	}
	
	public void print(String str) {
		logger.info("status=" + sslEngineResult.getStatus());
		logger.info("bytesConsumed=" + sslEngineResult.bytesConsumed());
		logger.info("bytesProduced=" + sslEngineResult.bytesProduced());
		logger.info("result=" + sslEngineResult.toString()); 
	}

}
