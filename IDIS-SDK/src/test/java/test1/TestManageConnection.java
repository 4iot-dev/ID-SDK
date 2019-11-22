package test1;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.ac.caict.iiiiot.idisc.core.BaseResponse;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.core.IdisCommunicationItems;
import cn.ac.caict.iiiiot.idisc.core.ResolutionResponse;
import cn.ac.caict.iiiiot.idisc.core.ServerInfo;
import cn.ac.caict.iiiiot.idisc.core.SiteInfo;
import cn.ac.caict.iiiiot.idisc.core.SiteResponse;
import cn.ac.caict.iiiiot.idisc.data.AdminInfo;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.data.MsgSettings;
import cn.ac.caict.iiiiot.idisc.data.SignatureInfo;
import cn.ac.caict.iiiiot.idisc.data.ValueReference;
import cn.ac.caict.iiiiot.idisc.security.Permission;
import cn.ac.caict.iiiiot.idisc.service.IChannelManageService;
import cn.ac.caict.iiiiot.idisc.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.idisc.service.impl.ChannelManageServiceImpl;
import cn.ac.caict.iiiiot.idisc.utils.IdentifierValueUtil;
import cn.ac.caict.iiiiot.idisc.utils.Util;

public class TestManageConnection {
	public static final int CHANNEL_CLOSED = 0;
	public static final int CHANNEL_LOGIN = 1;
	public static final int CHANNEL_LOGOUT = 2;
	
	public static final String PUBLICKEY_PATH = "D:/工作/客户端-无登录/hsclient_new_lhs_resolver/bin/rsa_pub.pem";
	public static final String SIGNATURE_PUBKEY_PATH = "D:/工作/客户端-无登录/hsclient_new_lhs_resolver/bin/rsa_pub.pem";
	public static final String SIGNATURE_PRVKEY_PATH = "D:/工作/客户端-无登录/hsclient_new_lhs_resolver/bin/rsa_pri.bin";
	public static final String CERTIFICATION_PUBKEY_PATH = "D:/sm2key/sm2_public - 副本.pem";
	public static final String CERTIFICATION_PRVKEY_PATH = "D:/sm2key/sm2_priv - 副本.pem";
	
	public static final String IP = "192.168.150.25";
	public static final int PORT = 2641;
	public static final String PROTOCOL = "TCP";
	public static final String OP_ID = "88.996.438";
	

	public static void main(String[] args) throws Exception {
		//entry_1_Test();
		entry_2_Test();
	}

	private static void entry_2_Test() throws Exception {
		// 创建通道管理实例
		IChannelManageService chnnlService = new ChannelManageServiceImpl();
		IIDManageServiceChannel channel = chnnlService.generateChannel(IP, PORT, PROTOCOL);
		//testCreate(channel);
		testAdd(channel);
		//testRemove(channel);
		testLookup(channel);
		
	}

	private static void entry_1_Test() throws Exception {
		// 创建通道管理实例
		IChannelManageService chnnlService = new ChannelManageServiceImpl();
		try {
			// 根据IDIS系统提供的ip和端口，创建与IDIS的连接通道对象
			IIDManageServiceChannel channel = chnnlService.generateChannel("192.168.150.25", 2640, "TCP");
			// testLookup(channel);
			// testGetSiteInfo(channel);
			if (channel != null && chnnlService.getIDManageServiceChannelState(channel) == CHANNEL_LOGOUT) {
				String userId0 = chnnlService.getChannelUserIdentifier(channel);
				System.out.println("++++++" + userId0);
				 BaseResponse loginResp = testLogin(channel);
				 if (loginResp != null && loginResp.responseCode == 1) {
				 System.out.println("登录成功!");
				// testDelete(channel);
				// testCreate(channel);
				testAdd(channel);
				// testEdit(channel);
				// testRemove(channel);
				}
				// testLookup(channel);
			}
		} catch (IdentifierException e) {
			e.printStackTrace();
		}
	}

	private static BaseResponse testLogin(IIDManageServiceChannel channel) throws IdentifierException {
		return channel.login("88.1000.1/fy", 1, "D:\\工作\\客户端-无登录\\hsclient_new_lhs_resolver\\bin\\rsa_pri.bin", null,
				1);
	}

	private static BaseResponse testDelete(IIDManageServiceChannel channel) throws IdentifierException {
		return channel.deleteIdentifier("88.1000.2/cupA", new MsgSettings());
	}

	private static BaseResponse testCreate(IIDManageServiceChannel channel) throws IdentifierException {
		/*IdentifierValue value1 = new IdentifierValue();
		try {
			value1 = makeSiteInfoValue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IdentifierValue value2 = makeVListValue();

		IdentifierValue value3 = new IdentifierValue(500, "HS_SERV", "88.1234");*/

		return channel.createIdentifier(OP_ID, null, new MsgSettings());
	}

	private static BaseResponse testAdd(IIDManageServiceChannel channel) throws Exception {
		IdentifierValue[] values = new IdentifierValue[1];
		// values[0] = makeSiteInfoPrefixValue();
		// values[0] = makeSignatureValue(channel);
		// values[0] = makeAdminValue();
		// values[0] = new IdentifierValue(66, "URL", "www.666.com");
		// values[1] = new IdentifierValue(7, "email", "www.777.com");
		//values[0] = makePublicKeyValue();
		// values[0] = makeCertValue(channel);
		values[0] = makeSignatureValue(channel);//这里有一个逻辑顺序，如果要对标识值做签名，就需要先把目标标识值添加上。
		return channel.addIdentifierValues(OP_ID, values, new MsgSettings());
	}

	private static BaseResponse testEdit(IIDManageServiceChannel channel) throws IdentifierException {
		IdentifierValue[] values = new IdentifierValue[2];
		values[0] = new IdentifierValue(6, "email", "www.666e.com");
		values[1] = new IdentifierValue(7, "url", "www.777e.com");
		return channel.modifyIdentifierValues("88.1000.2/mm", values, new MsgSettings());
	}

	private static BaseResponse testRemove(IIDManageServiceChannel channel) throws IdentifierException {
		int[] arr = { 400 };
		return channel.removeIdentifierValues(OP_ID, arr, null);
	}

	private static BaseResponse testLookup(IIDManageServiceChannel channel) throws IdentifierException {
		int[] arr = null;
		String[] types = null;
		return channel.lookupIdentifier(OP_ID, arr, types, null);
	}

	private static BaseResponse testGetSiteInfo(IIDManageServiceChannel channel) throws IdentifierException {
		MsgSettings settings = new MsgSettings();
		BaseResponse response = channel.getServerSiteInfo(settings);
		if (response instanceof SiteResponse) {
			SiteInfo si = ((SiteResponse) response).getSiteInfo();
			System.out.println("siteInfo:" + si);
		}
		return response;
	}

	private static IdentifierValue makeAdminValue() throws IdentifierException {
		IdentifierValue value = new IdentifierValue();
		AdminInfo admin = new AdminInfo();
		admin.admId = Util.encodeString("88.1000.2/cupA");
		admin.admIdIndex = 302;
		admin.initPermissions(true, true, true, true, true, true, true, true, true, true, true, true);
		IdentifierValueUtil.makeIdentifierValueOfAdminInfo(value, admin, 10);
		return value;
	}

	private static IdentifierValue makeSiteInfoValue() throws IdentifierException, IOException {
		IdentifierValue iv = new IdentifierValue();
		int index = 20;
		// items[]
		IdisCommunicationItems[] items = new IdisCommunicationItems[2];
		items[0] = new IdisCommunicationItems(IdisCommunicationItems.ST_ADMIN_AND_QUERY,
				IdisCommunicationItems.TS_IDF_TCP, 1304);
		items[1] = new IdisCommunicationItems(IdisCommunicationItems.ST_ADMIN_AND_QUERY,
				IdisCommunicationItems.TS_IDF_UDP, 1304);
		// server
		ServerInfo ser1 = new ServerInfo();
		ser1.communicationItems = items;
		ser1.ipBytes = Util.convertIPStr2Bytes("192.168.150.13");
		ser1.publicKey = Util.getBytesFromFile("D:/temp/svr_1/admpub.bin");
		ser1.serverId = 1;
		// servers
		ServerInfo[] servArr = new ServerInfo[] { ser1 };
		// siteinfo
		SiteInfo si = new SiteInfo();
		si.servers = servArr;
		si.attributes = null;

		IdentifierValueUtil.makeIdentifierValueOfSiteInfo(iv, si, index);
		return iv;
	}

	private static IdentifierValue makeSiteInfoPrefixValue() throws IdentifierException {
		IdentifierValue iv = new IdentifierValue();
		int index = 25;
		// items[]
		IdisCommunicationItems[] items = new IdisCommunicationItems[2];
		items[0] = new IdisCommunicationItems(IdisCommunicationItems.ST_ADMIN_AND_QUERY,
				IdisCommunicationItems.TS_IDF_TCP, 2641);
		items[1] = new IdisCommunicationItems(IdisCommunicationItems.ST_ADMIN_AND_QUERY,
				IdisCommunicationItems.TS_IDF_UDP, 2641);
		// server
		ServerInfo ser1 = new ServerInfo();
		ser1.communicationItems = items;
		ser1.ipBytes = Util.convertIPStr2Bytes("192.168.150.25");
		ser1.publicKey = null;
		ser1.serverId = 1;
		// servers
		ServerInfo[] servArr = new ServerInfo[] { ser1 };
		// siteinfo
		SiteInfo si = new SiteInfo();
		si.servers = servArr;
		si.attributes = null;
		IdentifierValueUtil.makeIdentifierValueOfSiteInfoPrefix(iv, si, index);
		return iv;
	}

	private static IdentifierValue makeVListValue() throws IdentifierException {
		IdentifierValue iv = new IdentifierValue();
		int index = 30;
		ValueReference[] vr = new ValueReference[2];
		vr[0] = new ValueReference("88.1000.2/mm", 1);
		vr[1] = new ValueReference("88.1000.2/cup", 2);
		IdentifierValueUtil.makeIdentifierValueOfVList(iv, vr, index);
		return iv;
	}

	private static IdentifierValue makePublicKeyValue() throws IdentifierException {
		IdentifierValue iv = new IdentifierValue();
		int index = 300;
		IdentifierValueUtil.makeIdentifierValueOfPublicKey(iv, PUBLICKEY_PATH, index);
		return iv;
	}

	private static IdentifierValue makeSignatureValue(IIDManageServiceChannel channel) throws Exception {
		IdentifierValue iv = new IdentifierValue();
		int index = 400;
		PrivateKey prvKey = Util.getPrivateKeyFromFile(SIGNATURE_PRVKEY_PATH, null);

		IdentifierValue[] values = new IdentifierValue[1];
		BaseResponse response = channel.lookupIdentifier(OP_ID, null, null, null);

		if (response instanceof ResolutionResponse) {
			values = ((ResolutionResponse) response).getAllIDValues();
		}
		SignatureInfo signInfo = SignatureInfo.newSignatureInstance(prvKey, values,"300:88.996", "88.996.438", "2020-11-11 11:11:11", "2019-11-22 14:01:00", "2019-11-22 14:00:00", "SM3");
		IdentifierValueUtil.makeIdentifierValueOfSignature(iv, index, signInfo);
		return iv;
	}

	private static IdentifierValue makeCertValue(IIDManageServiceChannel channel) throws Exception {
		PublicKey pubKey = Util.getPublicKeyFromFile(CERTIFICATION_PUBKEY_PATH);
		PrivateKey prvKey = Util.getPrivateKeyFromFile(CERTIFICATION_PRVKEY_PATH, null);

		List<Permission> perms = new ArrayList<>();
		perms.add(new Permission(null, "everything"));

		IdentifierValue iv = new IdentifierValue();
		int index = 401;

		SignatureInfo signInfo = SignatureInfo.newCertificationInstance(prvKey, pubKey, perms, "100:88", "300:88.996", "2020-11-11 11:11:11", "2019-11-22 14:01:00", "2019-11-22 14:00:00");
		IdentifierValueUtil.makeIdentifierValueOfCertification(iv, index, signInfo);
		return iv;
	}
}
