package cn.ac.caict.iiiiot.idisc.core;
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
import java.security.*;
import java.util.Arrays;

import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.Util;

import java.net.*;

public class ServerInfo {
	private final char SEPRATOR_IPV4 = '.';
	private final char SEPRATOR_IPV6 = ':';
	public int serverId;
	public byte[] ipBytes;
	public byte[] publicKey;
	public IDCommunicationItems[] communicationItems;

	private String addressStr = null;

	public IDCommunicationItems findIDCommunicationItemsByProtocol(int desiredProtocol, BaseRequest req) {
		for (int i = 0; i < this.communicationItems.length; i++) {
			if (this.communicationItems[i].protocol == desiredProtocol
					&& this.communicationItems[i].canDoRequest(req)) {
				return this.communicationItems[i];
			}
		}
		return null;
	}

	public boolean isIPv4() {
		if(ipBytes != null && ipBytes.length == 4)
			return true;
		for (int i = 0; i < Common.IP_ADDRESS_SIZE_SIXTEEN - 4; i++) {
			if (ipBytes[i] != 0) {
				return false;
			}
		}
		return true;
	}

	public byte[] getIpBytes() {
		return ipBytes;
	}

	public void setIpBytes(byte[] ipBytes) {
		this.ipBytes = ipBytes;
	}

	public InetAddress getInetAddress() {
		if (ipBytes == null || ipBytes.length != Common.IP_ADDRESS_SIZE_SIXTEEN)
			throw new IllegalStateException("ServerInfo中的地址信息不是16个字节");
		try {
			if (isIPv4())
				return InetAddress.getByAddress(Arrays.copyOfRange(ipBytes, Common.IP_ADDRESS_SIZE_SIXTEEN - 4, Common.IP_ADDRESS_SIZE_SIXTEEN));
			else
				return InetAddress.getByAddress(ipBytes);
		} catch (UnknownHostException e) {
			throw new AssertionError(e);
		}
	}

	public String getAddressStr() {
		if (addressStr != null)
			return addressStr;

		StringBuffer sb = new StringBuffer();
		if (ipBytes == null)
			return "";

		if (isIPv4()) {
			for (int i = Common.IP_ADDRESS_SIZE_SIXTEEN - 4; i < ipBytes.length; i++) {
				if (sb.length() > 0)
					sb.append(SEPRATOR_IPV4);
				sb.append(0x00ff & ipBytes[i]);
			}
		} else if (ipBytes.length != Common.IP_ADDRESS_SIZE_SIXTEEN) {
			for (int i = 0; i < ipBytes.length; i += 2) {
				if (sb.length() > 0)
					sb.append(SEPRATOR_IPV6);
				sb.append(Util.decodeHexString(ipBytes, i, 2, false));
			}
		} else {
			return Util.rfcIpRepresentation(ipBytes);
		}

		addressStr = sb.toString();
		return addressStr;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(serverId).append(' ').append(getAddressStr()).append(' ');
		IDCommunicationItems ifcs[] = communicationItems;
		boolean hasItems = false;
		for (int i = 0; ifcs != null && i < ifcs.length; i++) {
			IDCommunicationItems ifc = ifcs[i];
			if (ifc == null)
				continue;
			if (!hasItems) {
				sb.append("[");
				hasItems = true;
			} else {
				sb.append(",");
			}
			sb.append(IDCommunicationItems.getProtocolName(ifc.protocol)).append('-').append(ifc.port).append('-').append(IDCommunicationItems.getTypeName(ifc.type));
		}
		if (hasItems)
			sb.append("]");
		return sb.toString();
	}

	public ServerInfo copyServerInfo() {
		ServerInfo serCopy = new ServerInfo();
		serCopy.serverId = serverId;
		byte[] buf = publicKey;
		if (buf == null) {
			serCopy.publicKey = null;
		} else {
			serCopy.publicKey = new byte[buf.length];
			System.arraycopy(buf, 0, serCopy.publicKey, 0, buf.length);
		}
		buf = ipBytes;
		if (buf == null) {
			serCopy.ipBytes = null;
		} else {
			serCopy.ipBytes = new byte[buf.length];
			System.arraycopy(buf, 0, serCopy.ipBytes, 0, buf.length);
		}
		
		IDCommunicationItems tmpIA[] = communicationItems;
		if(tmpIA == null)
			serCopy.communicationItems = null;
		else if(tmpIA != null && tmpIA.length >=0)
			serCopy.communicationItems = new IDCommunicationItems[tmpIA.length];
		for (int i = 0; i < tmpIA.length; i++) {
			IDCommunicationItems tmpItem = tmpIA[i];
			if(tmpItem == null)
				serCopy.communicationItems[i] = null;
			else
				serCopy.communicationItems[i] = tmpItem.cloneIDCommunicationItems();
		}
		return serCopy;
	}

	public PublicKey getPublicKey() throws Exception {
		return Util.getPublicKeyFromBytes(publicKey, 0);
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(communicationItems);
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(ipBytes);
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(publicKey);
		result = Common.SUITABLE_PRIME_NUMBER * result + serverId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerInfo other = (ServerInfo) obj;
		if (serverId != other.serverId)
			return false;
		if (!Arrays.equals(ipBytes, other.ipBytes))
			return false;
		if (!Arrays.equals(publicKey, other.publicKey))
			return false;
		if (!Arrays.equals(communicationItems, other.communicationItems))
			return false;
		return true;
	}
}
