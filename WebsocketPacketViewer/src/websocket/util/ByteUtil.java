package websocket.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class ByteUtil {
	/**
	 * 8���� ��Ʈ�� ����Ѵ�. 
	 */
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

	/**
	 * 1��Ʈ�� �̾Ƴ���
	 * 0 �Ǵ� 1 ����
	 */
	public static int extractBit(int src, int pos) {
		if (!((pos >= 0) && (pos < 16)))
			return 0;

		return ((src & (1 << pos)) >> pos);
	}
	
	/**
	 * 1����Ʈ (8��Ʈ) �߿��� pos���� �����ϴ� len���̸�ŭ�� ��Ʈ���� ������ int�� ��ȯ�Ͽ� �����Ѵ�
	 * Pos�� 76543210 ���� ������. (�迭÷�� �����̴�)
	 * len �� �������� �ϴ� ��Ʈ���� �����̴�.
	 */
	public static int getBitRange(byte b, int pos, int len){
		int x = 8 - (pos +1);
		int y = 1 + pos - len; // y = 8 - (x + len) = 8 - (7-pos + len) = 8-7+pos-len = 1+pos-len
		int right = x + y;
		int ret = Byte.toUnsignedInt((byte)(b<<x)) >>> right;
		return ret;
	}
	public static String getBitRangeString(byte b, int pos, int len){
		int x = 8 - (pos +1);
		int y = 1 + pos - len; // y = 8 - (x + len) = 8 - (7-pos + len) = 8-7+pos-len = 1+pos-len
		int right = x + y;
		
		String ret="";
		for(int i=x; i<x+len; i++){
			ret += extractBit(b, i);
		}
		return ret;
	}
	
	public static void main(String[] args) throws DecoderException {
		byte b=127;
		System.out.println(getBitString(b));
		String a=getBitRangeString(b,2,3);
		System.out.println("a=" + a);
	}
	
	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.put(bytes);
		buffer.flip();// need flip
		return buffer.getLong();
	}

	public static byte[] intToBytes(int integer, ByteOrder order) {
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
		buff.order(order);
		buff.putInt(integer);
		return buff.array();
	}

	public static int bytesToInt(byte[] bytes, ByteOrder order) {
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
		buff.order(order);
		buff.put(bytes);
		buff.flip();
		return buff.getInt();
	}

	public static byte[] shortToBytes(short shorter, ByteOrder order) {
		ByteBuffer buff = ByteBuffer.allocate(Short.SIZE / 8);
		buff.order(order);
		buff.putShort(shorter);
		return buff.array();
	}

	public static short bytesToShort(byte[] bytes, ByteOrder order) {
		ByteBuffer buff = ByteBuffer.allocate(Short.SIZE / 8);
		buff.order(order);
		buff.put(bytes);
		buff.flip();
		return buff.getShort();
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
