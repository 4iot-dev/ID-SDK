package cn.ac.caict.iiiiot.id.client.convertor;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.id.client.utils.Util;

public class BaseConvertor {

	public static final int read2Bytes(byte[] buf, int offset) {
		return (buf[offset]) << 8 | buf[offset + 1] & 0x00ff;
	}

	public static final int write2Bytes(byte[] buf, int offset, int value) {
		buf[offset++] = (byte) ((value & 0xff00) >>> 8);
		buf[offset++] = (byte) (value & 0xff);
		return Common.TWO_SIZE;
	}

	public static final int read4Bytes(byte[] buf, int offset) {
		return buf[offset] << 24 | (0x00ff & buf[offset + 1]) << 16 | (0x00ff & buf[offset + 2]) << 8
				| (0x00ff & buf[offset + 3]);
	}

	public static final int write4Bytes(byte[] buf, int offset, int value) {
		buf[offset++] = (byte) (255 & value >>> 24);
		buf[offset++] = (byte) (255 & value >> 16);
		buf[offset++] = (byte) (255 & value >> 8);
		buf[offset++] = (byte) (255 & value);
		return Common.FOUR_SIZE;
	}

	public static final byte[] readByteArray(byte[] buf, int offset) throws IdentifierException {
		int lenArr = read4Bytes(buf, offset);
		if (lenArr < 0 || lenArr > Common.MAX_ARRAY_SIZE)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
					Common.MSG_INVALID_ARRAY_SIZE);
		byte[] array = new byte[lenArr];
		System.arraycopy(buf, offset + Common.FOUR_SIZE, array, 0, lenArr);
		return array;
	}

	public static final int writeByteArray(byte[] dest, int offset, byte[] src) {
		if (src != null)
			return writeByteArray(dest, offset, src, 0, src.length);
		else
			return write4Bytes(dest, offset, 0);
	}

	public static final int writeByteArray(byte[] dest, int offset, byte[] src, int destPos, int length) {
		offset += write4Bytes(dest, offset, length);
		System.arraycopy(src, destPos, dest, offset, length);
		return Common.FOUR_SIZE + length;
	}

	public static final int writeByteArrayArray(byte[] buf, int offset, byte[][] bufToWrite) {
		if (bufToWrite == null)
			return write4Bytes(buf, offset, 0);
		int origOffset = offset;
		int alen = bufToWrite.length;
		offset += write4Bytes(buf, offset, alen);
		for (int i = 0; i < alen; i++) {
			offset += writeByteArray(buf, offset, bufToWrite[i], 0, bufToWrite[i].length);
		}
		return offset - origOffset;
	}

	public static final int writeIntArray(byte[] buf, int offset, int[] bufToWrite) {
		if (bufToWrite == null)
			return write4Bytes(buf, offset, 0);
		int alen = bufToWrite.length;
		offset += write4Bytes(buf, offset, alen);
		for (int i = 0; i < alen; i++)
			offset += write4Bytes(buf, offset, bufToWrite[i]);
		return Common.FOUR_SIZE + Common.FOUR_SIZE * alen;
	}

	public static final int[] readIntArray(byte[] buf, int offset) throws IdentifierException {
		int len = read4Bytes(buf, offset);
		if (len < 0 || len > Common.MAX_ARRAY_SIZE)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MESSAGE_FORMAT_ERROR,
					Common.MSG_INVALID_ARRAY_SIZE);
		offset += Common.FOUR_SIZE;
		int[] a = new int[len];
		for (int i = 0; i < len; i++) {
			a[i] = read4Bytes(buf, offset);
			offset += Common.FOUR_SIZE;
		}
		return a;
	}

	public static final int readByteArrayArray(byte[][] arr, byte[] buf, int offset) throws IdentifierException {
		int origOffset = offset;
		for (int i = 0; i < arr.length; i++) {
			arr[i] = readByteArray(buf, offset);
			offset += arr[i].length + Common.FOUR_SIZE;
		}
		return offset - origOffset;
	}

	public static int readOpCode(byte[] msg, int offset) {
		return read4Bytes(msg, offset);
	}

	public static final int calcStorageSize(IdentifierValue value) {
		if (value == null)
			return 0;
		int sz = Common.FOUR_SIZE + Common.FOUR_SIZE + 1 + Common.FOUR_SIZE + 1 + Common.FOUR_SIZE + value.type.length
				+ Common.FOUR_SIZE + value.data.length;

		sz += Common.FOUR_SIZE;
		if (value.references != null) {
			for (int i = 0; i < value.references.length; i++) {
				sz += Common.FOUR_SIZE + value.references[i].identifier.length + Common.FOUR_SIZE;
			}
		}
		return sz;
	}

	public static final int calcIdentifierValueSize(byte values[], int offset) {
		int origOffset = offset;
		offset += Common.FOUR_SIZE + Common.FOUR_SIZE + 1 + Common.FOUR_SIZE + 1;

		int fieldLen = read4Bytes(values, offset);
		offset += Common.FOUR_SIZE + fieldLen;

		fieldLen = read4Bytes(values, offset);
		offset += Common.FOUR_SIZE + fieldLen;

		fieldLen = read4Bytes(values, offset);
		offset += Common.FOUR_SIZE;

		for (int i = 0; i < fieldLen; i++) {
			int refLen = read4Bytes(values, offset);
			offset += Common.FOUR_SIZE + refLen + Common.FOUR_SIZE;
		}
		int sumSize = offset - origOffset;
		return sumSize;
	}
	
	public static byte[] signatureFormat(byte[] rArr, byte[] sArr) {
		int sum_len = 0;
		sum_len += 1;// 30
		sum_len += 1;// 除30外总长度
		sum_len += 1;// r类型
		System.out.println("r:" + Util.bytesToHexString(rArr));
		System.out.println("r-len:" + rArr.length);
		System.out.println("s:" + Util.bytesToHexString(sArr));
		sum_len += 1;// r长度
		sum_len += rArr.length;
		sum_len += 1;// s类型
		sum_len += 1;// s长度
		sum_len += sArr.length;
		int pos = 0;
		byte[] message = new byte[sum_len];
		byte[] first = Util.toBytes("30");
		System.arraycopy(first, 0, message, pos, 1);
		pos += 1;
		System.arraycopy(new byte[] { (byte) (255 & (sum_len - 2)) }, 0, message, pos, 1);
		pos += 1;
		byte[] type = Util.toBytes("02");
		System.arraycopy(type, 0, message, pos, 1);
		pos += 1;
		int rLen = rArr.length;
		System.arraycopy(new byte[] { (byte) (255 & rLen) }, 0, message, pos, 1);
		pos += 1;
		System.arraycopy(rArr, 0, message, pos, rArr.length);
		pos += rArr.length;
		System.arraycopy(type, 0, message, pos, 1);
		pos += 1;
		int sLen = sArr.length;
		System.arraycopy(new byte[] { (byte) (255 & sLen) }, 0, message, pos, 1);
		pos += 1;
		System.arraycopy(sArr, 0, message, pos, sArr.length);
		System.out.println(Util.bytesToHexString(message));
		return message;
	}

	public static final int INT2_SIZE = 2;
	public static final int writeInt2(byte buf[], int offset, int value) {
		buf[offset++] = (byte) ((value & 0xff00) >>> 8);
		buf[offset++] = (byte) (value & 0xff);
		return INT2_SIZE;
	}
}
