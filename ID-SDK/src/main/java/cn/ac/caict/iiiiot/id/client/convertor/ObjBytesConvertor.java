package cn.ac.caict.iiiiot.id.client.convertor;
import cn.ac.caict.iiiiot.id.client.core.Attribute;
import cn.ac.caict.iiiiot.id.client.core.IDCommunicationItems;
import cn.ac.caict.iiiiot.id.client.core.ServerInfo;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;
import cn.ac.caict.iiiiot.id.client.data.AdminInfo;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.utils.Common;

public class ObjBytesConvertor extends BaseConvertor {
	public static final byte[] admInfoConvertToBytes(AdminInfo admInfo) {
		int infoLen = Common.TWO_SIZE + Common.FOUR_SIZE + Common.FOUR_SIZE + admInfo.admId.length;
		byte[] result_bytes = new byte[infoLen];
		int offset = 0;
		offset += write2Bytes(result_bytes, offset, admInfo.permissions);
		offset += writeByteArray(result_bytes, offset, admInfo.admId);
		offset += write4Bytes(result_bytes, offset, admInfo.admIdIndex);
		return result_bytes;
	}

	public static final byte[] siteInfoCovertToBytes(SiteInfo site) {
		int infoLen = 0;
		// dataVersion+protocolVersion+serialNumber+primary+hashOption
		infoLen += 2 + 2 + 2 + 1 + 1; 
		// hashFilter
		infoLen += Common.FOUR_SIZE + (site.hashFilter == null ? 0 : site.hashFilter.length);
		infoLen += Common.FOUR_SIZE;
		if (site.attributes != null) {
			for (Attribute attribute : site.attributes) {
				if (attribute != null && attribute.name != null && attribute.value != null) {
					infoLen += Common.FOUR_SIZE + attribute.name.length;
					infoLen += Common.FOUR_SIZE + attribute.value.length;
				}
			}
		}
		// servers个数
		infoLen += Common.FOUR_SIZE; 
		if (site.servers != null) {
			for (ServerInfo server : site.servers) {
				infoLen += Common.FOUR_SIZE;
				infoLen += Common.IP_ADDRESS_SIZE_SIXTEEN; 
				infoLen += Common.FOUR_SIZE + (server.publicKey == null ? 0 : server.publicKey.length); 
				// communicateItems个数
				infoLen += Common.FOUR_SIZE;
				if (server.communicationItems != null) {
					infoLen += (Common.FOUR_SIZE + 1 + 1) * server.communicationItems.length;
				}
			}
		}

		byte[] result_bytes = new byte[infoLen];
		int offset = 0;
		offset += write2Bytes(result_bytes, offset, site.dataFormatVersion);
		result_bytes[offset++] = site.majorProtocolVersion;
		result_bytes[offset++] = site.minorProtocolVersion;
		offset += write2Bytes(result_bytes, offset, site.serialNumber);
		result_bytes[offset++] = (byte) ((site.isPrimarySite ? SiteInfo.PRIMARY_SITE : 0)
				| (site.isMultiPrimarySite ? SiteInfo.MULTI_PRIMARY : 0));
		result_bytes[offset++] = site.hashOption;
		offset += writeByteArray(result_bytes, offset, site.hashFilter);

		if (site.attributes == null) {
			offset += write4Bytes(result_bytes, offset, 0);
		} else {
			offset += write4Bytes(result_bytes, offset, site.attributes.length);
			for (Attribute attribute : site.attributes) {
				offset += writeByteArray(result_bytes, offset, attribute.name);
				offset += writeByteArray(result_bytes, offset, attribute.value);
			}
		}

		if (site.servers == null) {
			offset += write4Bytes(result_bytes, offset, 0);
		} else {
			offset += write4Bytes(result_bytes, offset, site.servers.length);
			for (ServerInfo server : site.servers) {
				offset += write4Bytes(result_bytes, offset, server.serverId);

				System.arraycopy(server.ipBytes, 0, result_bytes,
						offset + Common.IP_ADDRESS_SIZE_SIXTEEN - server.ipBytes.length, server.ipBytes.length);
				offset += Common.IP_ADDRESS_SIZE_SIXTEEN;

				offset += writeByteArray(result_bytes, offset, server.publicKey);

				if (server.communicationItems == null) {
					offset += write4Bytes(result_bytes, offset, 0);
				} else {
					offset += write4Bytes(result_bytes, offset, server.communicationItems.length);
					for (IDCommunicationItems item : server.communicationItems) {
						result_bytes[offset++] = item.type;
						result_bytes[offset++] = item.protocol;
						offset += write4Bytes(result_bytes, offset, item.port);
					}
				}
			}
		}
		return result_bytes;
	}

	public static byte[] vListCovertToBytes(ValueReference[] vList) {
		int size = Common.FOUR_SIZE;
		if(vList != null){
			for(int i=0; i<vList.length; i++){
				size += Common.FOUR_SIZE;
				size += vList[i].identifier.length;
				size += Common.FOUR_SIZE;
			}
		}
		byte[] result_bytes = new byte[size];
		int offset = 0;
		offset += write4Bytes(result_bytes, offset, vList == null ? 0:vList.length);
		
		if(vList != null) {
			for(int i=0; i<vList.length; i++){
				offset += writeByteArray(result_bytes, offset, vList[i].identifier);
				offset += write4Bytes(result_bytes,offset, vList[i].index);
			}
		}
		return result_bytes;
	}
	
}
