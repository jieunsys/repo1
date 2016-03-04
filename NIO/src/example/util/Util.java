package example.util;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Util {
	public static String bufferToString(ByteBuffer buff) {
		System.out.println("pos=" + buff.position() + ",  limit=" + buff.limit());
		int len=buff.limit();
		byte[] bb = new byte[len];
		buff.get(bb,0,len);
		String str = new String(bb);
		if(1==1)return str;
		
		Charset charset = Charset.forName("UTF-8");
//		Charset charset = StandardCharsets.UTF_8;
		CharsetDecoder decoder = charset.newDecoder();
		String message = "";
		try {
			message = decoder.decode(buff).toString();
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}
		return message;
	}

	public static String bufferToString2(ByteBuffer buff) {
		Charset charset = Charset.forName("UTF-8");
//		Charset charset = StandardCharsets.UTF_8;
		
		CharsetDecoder decoder = charset.newDecoder();
		
		String message = "";

		try {
			message = decoder.decode(buff).toString();
		} catch (Exception e) {
			e.printStackTrace();
			message = e.getMessage();
		}

		return message;
	}

	public static void log(String str) {
		System.out.println(str);
	}

	public static void log(String str, ByteBuffer buff) {
		log(str + bufferToString(buff));
	}
	private static String sha1(String input) throws NoSuchAlgorithmException {
		MessageDigest mDigest = MessageDigest.getInstance("SHA1");
		byte[] result = mDigest.digest(input.getBytes());
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < result.length; i++) {
			sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}


	public static String sha1base64(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString((md.digest(str.getBytes())));
	}
}
