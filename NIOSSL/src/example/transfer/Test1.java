package example.transfer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Test1 {

	public static void main(String[] args) throws NoSuchAlgorithmException {
		new Test1().test();
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


	private String sha1base64(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString((md.digest(str.getBytes())));
	}

	void test() throws NoSuchAlgorithmException {

		String src = "O+e1WnJ0/kR9tmjU+8ilUA==";
		
		String secAccept = sha1base64(src + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11");

		System.out.println(secAccept);
	}

}
