package websocket.views;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import websocket.util.ByteUtil;

public class PacketTest {
	public static void main(String[] args) throws NoSuchAlgorithmException {
		new PacketTest().test();
	}
	static String sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	void test(){
		System.out.println(Short.MIN_VALUE);
		String a="008c"; // ===> 140
		byte[] b = ByteUtil.hexToByteArray(a);
		
		
		
	}

}
