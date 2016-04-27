package org.mukuri.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ByteUtil {
	public static String bufferToString(ByteBuffer buff) {
		int len=buff.limit();
		byte[] bb = new byte[len];
		buff.get(bb,0,len);
		String str = new String(bb);
		return str;
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
	
	public static String getBitString(int n) {
		int[] array = new int[8];
		int iattr = (int) ((byte) n & 0xFF);
		int bitWise = 1;
		for (int i = 0; i < 8; i++) {
			if ((bitWise & iattr) > 0)
				array[i] = 1;
			else
				array[i] = 0;
			bitWise = (bitWise << 1) & 0xfe;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; ++i)
			sb.append(array[7 - i]);
		return sb.toString();
	}

	public static int extractBit(int src, int pos) {
		if (!((pos >= 0) && (pos < 16)))
			return 0;

		return ((src & (1 << pos)) >> pos);
	}
	
	public static int getBitRange(byte b, int pos, int len){
		int x = 8 - (pos +1);
		int y = 1 + pos - len; // y = 8 - (x + len) = 8 - (7-pos + len) = 8-7+pos-len = 1+pos-len
		int right = x + y;
		int ret = Byte.toUnsignedInt((byte)(b<<x)) >>> right;
		return ret;
	}
	public static String getBitRangeString(byte b, int pos, int len){
		int x = 8 - (pos +1);
//		int y = 1 + pos - len; // y = 8 - (x + len) = 8 - (7-pos + len) = 8-7+pos-len = 1+pos-len
//		int right = x + y;
		
		String ret="";
		for(int i=x; i<x+len; i++){
			ret += extractBit(b, i);
		}
		return ret;
	}
	
	public static void longToBytes(long x) {
		ByteBuffer buf = ByteBuffer.allocate(Long.SIZE / 8);
		buf.putLong(x);
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		return buf.getLong();
	}

	public static void intToBytes(int integer, ByteOrder order) {
		ByteBuffer buf = ByteBuffer.allocate(Integer.SIZE / 8);
		buf.putInt(integer);
	}

	public static int bytesToInt(byte[] bytes, ByteOrder order) {
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		return buf.getInt();
	}

	public static void shortToBytes(short shorter, ByteOrder order) {
		ByteBuffer buf = ByteBuffer.allocate(Short.SIZE / 8);
		buf.putShort(shorter);
	}

	public static short bytesToShort(byte[] bytes) {
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		return buf.getShort();
	}
	
	public static byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() == 0) {
			return null;
		}

		byte[] ba = new byte[hex.length() / 2];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return ba;
	}

	public static String byteArrayToHex(byte[] ba) {
		if (ba == null || ba.length == 0) {
			return null;
		}

		StringBuffer sb = new StringBuffer(ba.length * 2);
		String hexNumber;
		for (int x = 0; x < ba.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}
		return sb.toString();
	}
}
