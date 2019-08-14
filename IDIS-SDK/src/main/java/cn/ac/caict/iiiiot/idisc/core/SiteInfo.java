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
import java.io.*;
import java.net.*;
import java.util.Arrays;

import cn.ac.caict.iiiiot.idisc.convertor.BaseConvertor;
import cn.ac.caict.iiiiot.idisc.utils.Common;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;
import cn.ac.caict.iiiiot.idisc.utils.Util;
/**
 *HS_SITE类型数据 
 */
public class SiteInfo {
	public static final byte HASH_PREFIX = 0;
	public static final byte HASH_SUFFIX = 1;
	public static final byte HASH_ALL = 2;
    public static final short PRIMARY_SITE = 0x80;
    public static final short MULTI_PRIMARY = 0x40;
	public ServerInfo[] servers;
	public Attribute[] attributes;
	public long responseTime;
	public int dataFormatVersion = Common.SITE_RECORD_FORMAT_VERSION;
	public int serialNumber;
	public byte majorProtocolVersion = MessageCommon.MAJOR_VERSION;
	public byte minorProtocolVersion = MessageCommon.MINOR_VERSION;
	public byte[] hashFilter;
	public byte hashOption = HASH_ALL;
	public boolean isPrimarySite;
	public boolean isMultiPrimarySite;

	public SiteInfo() {}
	
	public SiteInfo(SiteInfo site) {
		init(site);
	}

	public SiteInfo(int siteVer,boolean isMultiPrimarySite, boolean isPrimarySite,byte hashOption, String siteDesc, InetAddress addr,
			int port, int httpPort, boolean disableUDP)
			throws IOException {
		init(siteVer, siteDesc, addr,disableUDP, port);
	}

	public SiteInfo(int siteVer, boolean isMultiPrimarySite, boolean isPrimarySite, 
			byte hashOption, String siteDesc, InetAddress addr,
			int port, int httpPort, File pubKeyFile, boolean disableUDP)
			throws IOException {
		init(siteVer, siteDesc,addr,disableUDP,port);
		byte pkbuf[] = new byte[(int) pubKeyFile.length()];
		
		FileInputStream pubKeyIn = new FileInputStream(pubKeyFile);
		int n = 0;
		int offset = 0;
		while ((n < pkbuf.length) && ((offset = pubKeyIn.read(pkbuf, n, pkbuf.length - n)) >= 0))
			n += offset;
		pubKeyIn.close();

		servers[0].publicKey = pkbuf;
	}

	public SiteInfo(int siteVer, boolean isMultiPrimarySite, boolean isPrimarySite, 
			byte hashOption, String siteDesc, InetAddress listenAddr,
			InetAddress altAddr, int port, int httpPort, File pubKeyFile,
			boolean disableUDP) throws IOException {
		this(siteVer,isMultiPrimarySite, isPrimarySite, hashOption,
				siteDesc, listenAddr, port, httpPort, pubKeyFile,
		     disableUDP);
		attributes = new Attribute[2];
		attributes[0] = attributes[0];

		Attribute altAttribute = new Attribute();
		altAttribute.name = Util.encodeString("alt_attr");
		altAttribute.value = Util.encodeString(Util.rfcIpRepresentation(altAddr));

		attributes[1] = altAttribute;
	}

	public byte[] getAttribute(byte[] attribute) {
		if (attributes != null){
			for (Attribute attr : attributes) {
				if (Util.equalsBytes(attribute, attr.name))
					return attr.value;
			}
		}
		return null;
	}

	public static final int getIdentifierHash(byte[] identifier) throws IdentifierException {
		byte[] hashPart = Util.upperCase(identifier);
		byte[] digest = Util.doMD5Digest(hashPart);
		return Math.abs(BaseConvertor.read4Bytes(digest, digest.length - 4));
	}

	public String toString() {
		String servList = "";
		if (servers != null) {
			servList = servList + servers[0];
			for (int i = 1; i < servers.length; i++)
				servList += ", " + servers[i];
		}

		return "version: " + majorProtocolVersion + '.' + minorProtocolVersion + "; serial:" + serialNumber
				+ "servers=[" + servList + "]";
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(attributes);
		result = Common.SUITABLE_PRIME_NUMBER * result + dataFormatVersion;
		result = Common.SUITABLE_PRIME_NUMBER * result + majorProtocolVersion;
		result = Common.SUITABLE_PRIME_NUMBER * result + minorProtocolVersion;
		result = Common.SUITABLE_PRIME_NUMBER * result + serialNumber;
		result = Common.SUITABLE_PRIME_NUMBER * result + Arrays.hashCode(servers);
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
		SiteInfo other = (SiteInfo) obj;
		if (!Arrays.equals(attributes, other.attributes))
			return false;
		if (dataFormatVersion != other.dataFormatVersion)
			return false;
		if (majorProtocolVersion != other.majorProtocolVersion)
			return false;
		if (minorProtocolVersion != other.minorProtocolVersion)
			return false;
		if (serialNumber != other.serialNumber)
			return false;
		if (!Arrays.equals(servers, other.servers))
			return false;
		return true;
	}

	private void init(int siteVer, String siteDesc,InetAddress addr,boolean disableUDP,int port){
		serialNumber = siteVer;
		if (siteDesc != null)
			attributes = new Attribute[] {
					new Attribute(Util.encodeString("desc"), Util.encodeString(siteDesc)) };
		servers = new ServerInfo[] { new ServerInfo() };
		servers[0].serverId = 1;

		byte[] addrBytes = addr.getAddress(); 
		byte[] addrStandardBytes = new byte[Common.IP_ADDRESS_SIZE_SIXTEEN];

		System.arraycopy(addrBytes, 0, addrStandardBytes, Common.IP_ADDRESS_SIZE_SIXTEEN - addrBytes.length, addrBytes.length);

		servers[0].ipBytes = addrStandardBytes;
		if (disableUDP) {
			servers[0].communicationItems = new IdisCommunicationItems[] {
					new IdisCommunicationItems(IdisCommunicationItems.ST_ADMIN_AND_QUERY,
							IdisCommunicationItems.TS_IDF_TCP, port)};
		} else {
			servers[0].communicationItems = new IdisCommunicationItems[] {
					new IdisCommunicationItems(IdisCommunicationItems.ST_ADMIN_AND_QUERY,
							IdisCommunicationItems.TS_IDF_TCP, port),
					new IdisCommunicationItems(IdisCommunicationItems.ST_QUERY, IdisCommunicationItems.TS_IDF_UDP,
							port)};
		}
	}
	
	private void init(SiteInfo site){
		if (site.attributes != null) {
			this.attributes = new Attribute[site.attributes.length];
			for (int i = 0; i < site.attributes.length; i++) {
				this.attributes[i] = new Attribute(site.attributes[i].name, site.attributes[i].value);
			}
		}
		if (site.servers != null) {
			this.servers = new ServerInfo[site.servers.length];
			for (int i = 0; i < site.servers.length; i++) {
				this.servers[i] = site.servers[i].copyServerInfo();
			}
		}
		this.serialNumber = site.serialNumber;
		this.dataFormatVersion = site.dataFormatVersion;
		this.responseTime = site.responseTime;
		this.majorProtocolVersion = site.majorProtocolVersion;
		this.minorProtocolVersion = site.minorProtocolVersion;
	}
}
