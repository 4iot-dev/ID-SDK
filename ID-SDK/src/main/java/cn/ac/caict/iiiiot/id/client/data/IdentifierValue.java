package cn.ac.caict.iiiiot.id.client.data;
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

import java.security.PublicKey;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.logging.Log;

import cn.ac.caict.iiiiot.id.client.convertor.BytesObjConvertor;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;
import cn.ac.caict.iiiiot.id.client.log.IDLog;
import cn.ac.caict.iiiiot.id.client.security.Claims;
import cn.ac.caict.iiiiot.id.client.security.SignatureStructImpl;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.DateUtils;
import cn.ac.caict.iiiiot.id.client.utils.JsonWorker;
import cn.ac.caict.iiiiot.id.client.utils.Util;
/**
 * 一个标识下可以设置一个标识值集，每一个标识值即IdentifierValue包括如下数据：
 * ValueReference：值引用数组，即一个IdentifierValue可以引用一组ValueReference，例如一个IdentifierValue可以引用多个管理员
 * index:标识值的索引，在标识下的若干个标识值中的，这个index是不允许重复的
 * type:标识值类型，如URL、EMAIL、HS_SITE等，也可有子类型URL.SUBURL
 * data:标识数据
 * permission权限用四个boolean描述，如下：
 * bAdminRead：管理员可读
 * bAdminWrite：管理员可写
 * bPublicRead：公共可读
 * bPublicWrite：公共可写
 * ttl:存活时间，单位秒，默认86400秒即1天
 * ttlType：存活时间的类型，分为相对时间和绝对时间
 * timestamp:时间戳，可设置，一般数据产生或更新的时间标记
 */
public class IdentifierValue {
	public ValueReference[] references = null;
	public int index = -1;
	public byte[] type = new byte[0];
	public byte[] data = new byte[0];
	public boolean bAdminRead = true;
	public boolean bAdminWrite = true;
	public boolean bPublicRead = true;
	public boolean bPublicWrite = false;
	public int ttl = 86400;
	public byte ttlType = TTL_TYPE_RELATIVE;
	public long timestamp = new Date().getTime()/1000;//转换为秒

	public static final String BLANK_TIME_FORMAT = "--:--:-- --:--:--";
	public static final byte TTL_TYPE_RELATIVE = 0;
	public static final byte TTL_TYPE_ABSOLUTE = 1;
	public static final int MAX_RECOGNIZED_TTL = 86400 * 2;// 单位秒
	private Log logger = IDLog.getLogger(IdentifierValue.class);
	// 构造函数
	public IdentifierValue() {
	}

	public IdentifierValue(int index, byte[] type, byte[] data) {
		this.index = index;
		this.type = type;
		this.data = data;
	}

	public IdentifierValue(int index, byte[] type, String data) {
		this.index = index;
		this.type = type;
		this.data = Util.encodeString(data);
	}

	public IdentifierValue(int index, String type, byte[] data) {
		this.index = index;
		this.type = Util.encodeString(type);
		this.data = data;
	}

	public IdentifierValue(int index, String type, String data) {
		this.index = index;
		this.type = Util.encodeString(type);
		this.data = Util.encodeString(data);
	}

	public IdentifierValue(int index, byte type[], byte data[], byte ttlType, int ttl, long timestamp,
			ValueReference references[], boolean bAdminRead, boolean bAdminWrite, boolean bPublicRead,
			boolean bPublicWrite) {
		this.index = index;
		this.type = type;
		this.data = data;
		this.ttlType = ttlType;
		this.ttl = ttl;
		this.timestamp = timestamp;
		this.references = references;
		this.bAdminRead = bAdminRead;
		this.bAdminWrite = bAdminWrite;
		this.bPublicRead = bPublicRead;
		this.bPublicWrite = bPublicWrite;
	}

	// 值的获取与设置
	public ValueReference[] getReferences() {
		return references;
	}

	public void setReferences(ValueReference[] references) {
		this.references = references;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public byte[] getType() {
		return type;
	}

	public void setType(byte[] type) {
		this.type = type;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isAdminRead() {
		return bAdminRead;
	}

	public void setAdminRead(boolean adminRead) {
		this.bAdminRead = adminRead;
	}

	public boolean isAdminWrite() {
		return bAdminWrite;
	}

	public void setAdminWrite(boolean adminWrite) {
		this.bAdminWrite = adminWrite;
	}

	public boolean isPublicRead() {
		return bPublicRead;
	}

	public void setPublicRead(boolean publicRead) {
		this.bPublicRead = publicRead;
	}

	public boolean isPublicWrite() {
		return bPublicWrite;
	}

	public void setPublicWrite(boolean publicWrite) {
		this.bPublicWrite = publicWrite;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public byte getTtlType() {
		return ttlType;
	}

	public void setTtlType(byte ttlType) {
		this.ttlType = ttlType;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = Common.SUITABLE_PRIME_NUMBER * result + (bAdminRead ? 1231 : 1237);
		result = Common.SUITABLE_PRIME_NUMBER * result + (bAdminWrite ? 1231 : 1237);
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(data);
		result = Common.SUITABLE_PRIME_NUMBER * result + index;
		result = Common.SUITABLE_PRIME_NUMBER * result + (bPublicRead ? 1231 : 1237);
		result = Common.SUITABLE_PRIME_NUMBER * result + (bPublicWrite ? 1231 : 1237);
		result = Common.SUITABLE_PRIME_NUMBER * result + ttl;
		result = Common.SUITABLE_PRIME_NUMBER * result + ttlType;
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(type);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		IdentifierValue other = (IdentifierValue) obj;
		if (timestamp != other.timestamp) {
			return false;
		}
		return equalsIgnoreTimestamp(other);
	}

	public boolean equalsIgnoreTimestamp(IdentifierValue other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (bAdminWrite != other.bAdminWrite) {
			return false;
		}
		if (bAdminRead != other.bAdminRead) {
			return false;
		}
		if (!Arrays.equals(data, other.data)) {
			return false;
		}
		if (index != other.index) {
			return false;
		}
		if (bPublicRead != other.bPublicRead) {
			return false;
		}
		if (bPublicWrite != other.bPublicWrite) {
			return false;
		}
		if (!isSameReference(references, other.references)) {
			return false;
		}
		if (ttl != other.ttl) {
			return false;
		}
		if (ttlType != other.ttlType) {
			return false;
		}
		if (!Arrays.equals(type, other.type)) {
			return false;
		}
		return true;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("index=" + index + ",type=" + (type == null ? "" : Util.decodeString(type)));
		String strData = "";
		if (data != null) {
			strData = Util.looksLikeBinary(data) ? Util.decodeHexString(data, false) : Util.decodeString(data);
		}
		sb.append(",data=" + strData);
		sb.append(",permission=" + showPermissionByString());
		sb.append(",ttl=" + ttl);
		try {
			sb.append(",timestamp=" + DateUtils.parseDate2String(new Date(timestamp*1000L)));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("日期格式转换失败");
		}
		if (references != null) {
			for (int i = 0; i < references.length; i++) {
				sb.append("ref_index=").append(references[i].index).append(" ref_identifier=")
						.append(references[i].getIdentifierAsString());
			}
		}
		return sb.toString();
	}

	// 公有方法
	public final String showPermissionByString() {
		StringBuffer sb = new StringBuffer();
		sb.append(bAdminRead ? "r" : "-");
		sb.append(bAdminWrite ? "w" : "-");
		sb.append(bPublicRead ? "r" : "-");
		sb.append(bPublicWrite ? "w" : "-");
		return sb.toString();
	}

	public boolean isOutOfDate(long valueTimestamp) {
		long curTime = System.currentTimeMillis();
		if (ttlType == TTL_TYPE_RELATIVE) {
			return ttl == 0 || Math.min(ttl, MAX_RECOGNIZED_TTL) < (curTime - valueTimestamp);
		} else {
			return MAX_RECOGNIZED_TTL > (curTime - valueTimestamp) || ttl < curTime;
		}
	}

	public final String getDataStr() {
		if (data == null)
			return "";
		if (Util.looksLikeBinary(data))
			return Util.decodeHexString(data, false);
		return Util.decodeString(data);
	}

	public final String getTypeStr() {
		if (type == null)
			return "";
		return Util.decodeString(type);
	}

	public final String getTimestampStr() {
		if (timestamp <= 0)
			return BLANK_TIME_FORMAT;
		return new Date(timestamp).toString();
	}

	public final String getFormatTimestamp() {
		if (timestamp <= 0)
			return BLANK_TIME_FORMAT;
		try {
			return DateUtils.parseDate2String(new Date(timestamp));
		} catch (Exception e) {
			e.printStackTrace();
			return BLANK_TIME_FORMAT;
		}
	}

	public IdentifierValue copyIdentifierValue() {
		ValueReference newRefs[] = null;
		ValueReference myRefs[] = references;
		if (myRefs != null) {
			newRefs = new ValueReference[myRefs.length];
			for (int i = 0; i < newRefs.length; i++)
				newRefs[i] = new ValueReference(myRefs[i].identifier, myRefs[i].index);
		}
		return new IdentifierValue(index, Util.duplicateByteArray(type), Util.duplicateByteArray(data), ttlType, ttl,
				timestamp, newRefs, bAdminRead, bAdminWrite, bPublicRead, bPublicWrite);
	}

	public boolean hasType(byte[] theType) {
		return Util.equalsCaseInsensitive(this.type, theType) || (theType.length < type.length && type[theType.length] == (byte) '.'
				&& Util.startsWithCaseInsensitive(type, theType));
	}
	
	public Object convertData2Object() throws IdentifierException{
		if(type == null || type.length == 0)
			return data;
		switch (Util.decodeString(type)) {
		case Common.HS_SITE_PREFIX:
		case Common.HS_SITE:{
			SiteInfo si = new SiteInfo();
			si = BytesObjConvertor.bytesCovertToSiteInfo(data);
			return si;
		}
		case Common.HS_ADMIN:{
			AdminInfo ai = new AdminInfo();
			ai = BytesObjConvertor.bytesConvertToAdminInfo(data);
			return ai;
		}
		case Common.HS_VLIST:{
			ValueReference[] arr = BytesObjConvertor.bytesCovertToVList(data);
			return arr;
		}
		case Common.HS_PUBKEY:{
			try {
				PublicKey pubKey = Util.getPublicKeyFromBytes(data);
				return pubKey;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return data;
		}
		case Common.HS_SIGNATURE:
		case Common.HS_CERT:{
			try {
				SignatureStructImpl signatureStruct = new SignatureStructImpl(data);
				String payload = signatureStruct.getPayloadString();
				Claims claim = JsonWorker.getGson().fromJson(payload, Claims.class);
				return claim;
			} catch (IdentifierException e) {
				e.printStackTrace();
			}
		}
		default:
			break;
		}
		return data;
	}

	// 私有方法
	private boolean isSameReference(ValueReference[] v1, ValueReference[] v2) {
		if (v1 == v2)
			return true;
		if ((v1 == null && v2 != null) || (v1 != null && v2 == null))
			return false;
		if (v1.length != v2.length)
			return false;
		for (int i = 0; i < v1.length; i++) {
			if (!v1.equals(v2))
				return false;
		}
		return true;
	}


}
