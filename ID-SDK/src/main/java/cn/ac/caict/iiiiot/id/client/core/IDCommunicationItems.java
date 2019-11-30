package cn.ac.caict.iiiiot.id.client.core;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;

public class IDCommunicationItems {
	public static final byte ST_OUT_OF_SERVICE = 0;
	public static final byte ST_ADMIN = 1;
	public static final byte ST_QUERY = 2;
	public static final byte ST_ADMIN_AND_QUERY = 3;

	public static final byte TS_IDF_UDP = 0;
	public static final byte TS_IDF_TCP = 1;
	public static final byte TS_IDF_HTTP = 2;
	public static final byte TS_IDF_HTTPS = 3;

	public byte type; // 停止运行,管理员, 查询, 管理+查询
	public int port; // 服务端口
	public byte protocol; // 传输协议

	public IDCommunicationItems(byte type, byte protocol, int port) {
		this.type = type;
		this.port = port;
		this.protocol = protocol;
	}

	public IDCommunicationItems cloneIDCommunicationItems() {
		IDCommunicationItems icis = new IDCommunicationItems();
		icis.type = type;
		icis.port = port;
		icis.protocol = protocol;
		return icis;
	}

	public IDCommunicationItems() {
	}

	public String toString() {
		return getTypeName(type) + '/' + getProtocolName(protocol) + '/' + port;
	}

	public static final String getTypeName(byte type) {
		if(type == ST_OUT_OF_SERVICE)
			return "stop-service";
		else if (type == ST_ADMIN_AND_QUERY)
			return "admin&query";
		else if (type == ST_QUERY)
			return "query";
		else if (type == ST_ADMIN)
			return "admin";
		else
			return "unknown";
	}

	public static final String getProtocolName(byte protocol) {
		if(protocol == TS_IDF_TCP)
			return "TCP";
		else if (protocol == TS_IDF_UDP)
			return "UDP";
		else if (protocol == TS_IDF_HTTP)
			return "HTTP";
		else
			return "UNKNOWN";
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = Common.SUITABLE_PRIME_NUMBER * result + port;
		result = Common.SUITABLE_PRIME_NUMBER  * result + protocol;
		result = Common.SUITABLE_PRIME_NUMBER  * result + type;
		return result;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public byte getProtocol() {
		return protocol;
	}

	public void setProtocol(byte protocol) {
		this.protocol = protocol;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IDCommunicationItems other = (IDCommunicationItems) obj;
		if (type != other.type)
			return false;
		if (port != other.port)
			return false;
		if (protocol != other.protocol)
			return false;
		return true;
	}

	public boolean canDoRequest(BaseRequest req) {
		if ((req.requiresConnection) && protocol != TS_IDF_TCP ) {
			return false;
		}
		boolean canDo = true;
		if (!req.isAdminRequest) {
			canDo = (type == ST_QUERY || type == ST_ADMIN_AND_QUERY);
		} else {
			canDo = (type == ST_ADMIN || type == ST_ADMIN_AND_QUERY);
		}
		return canDo;
	}
}
