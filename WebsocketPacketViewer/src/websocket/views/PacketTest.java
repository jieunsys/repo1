package websocket.views;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
		String src="O+e1WnJ0/kR9tmjU+8ilUA==";
//		String b64 = Base64.getEncoder().encodeToString(src.getBytes());
		byte[] b64 = Base64.getDecoder().decode(src.getBytes());
		System.out.println(new String(b64));
		
		
		
	}

}
