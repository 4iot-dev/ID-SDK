package cn.ac.caict.iiiiot.idisc.convertor;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *© COPYRIGHT 2019 Corporation for Institute of Industrial Internet & Internet of Things (IIIIT);
 *                      All rights reserved. 
 * http://www.caict.ac.cn  
 * https://www.citln.cn/
 */
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.ExceptionCommon;

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
}
