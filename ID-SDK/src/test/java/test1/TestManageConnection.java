package test1;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.ac.caict.iiiiot.id.client.core.BaseResponse;
import cn.ac.caict.iiiiot.id.client.core.IDCommunicationItems;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.ResolutionResponse;
import cn.ac.caict.iiiiot.id.client.core.ServerInfo;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;
import cn.ac.caict.iiiiot.id.client.core.SiteResponse;
import cn.ac.caict.iiiiot.id.client.data.AdminInfo;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.MsgSettings;
import cn.ac.caict.iiiiot.id.client.data.SignatureInfo;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.security.Permission;
import cn.ac.caict.iiiiot.id.client.service.IChannelManageService;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.id.client.service.impl.ChannelManageServiceImpl;
import cn.ac.caict.iiiiot.id.client.utils.IdentifierValueUtil;
import cn.ac.caict.iiiiot.id.client.utils.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestManageConnection {
	public static final int CHANNEL_CLOSED = 0;
	public static final int CHANNEL_LOGIN = 1;
	public static final int CHANNEL_LOGOUT = 2;
	
	public static final String PUBLICKEY_PATH = "D:/工作/客户端-无登录/hsclient_new_lhs_resolver/bin/rsa_pub.pem";
	public static final String SIGNATURE_PUBKEY_PATH = "D:/工作/客户端-无登录/hsclient_new_lhs_resolver/bin/rsa_pub.pem";
	public static final String SIGNATURE_PRVKEY_PATH = "D:/工作/客户端-无登录/hsclient_new_lhs_resolver/bin/rsa_pri.bin";
	public static final String CERTIFICATION_PUBKEY_PATH = "D:/sm2key/sm2_public - 副本.pem";
	public static final String CERTIFICATION_PRVKEY_PATH = "D:/sm2key/sm2_priv - 副本.pem";
	
	public static final String IP = "192.168.150.29";
	public static final int PORT = 2640;
	public static final String PROTOCOL = "TCP";
	public static final String OP_ID = "88.8000.1";
	

	public static void main(String[] args) throws Exception {
		//entry_1_Test();
		//entry_2_Test();
		//entry_3_Test();
		entry_4_Test();
	}
	
	private static void entry_4_Test() throws Exception {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse("2019-11-21 20:34:54");
		System.out.println(date.getTime()/1000L);
	}

	private static void entry_3_Test(){
		String str = "    SHA-256    ";
		String pattern = "\\s*[S,s][H,h][A,a][\\s,-]?256\\s*";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(str);
		System.out.println(m.matches());
	}

	private static void entry_2_Test() throws Exception {
		// 创建通道管理实例
		IChannelManageService chnnlService = new ChannelManageServiceImpl();
		IIDManageServiceChannel channel = chnnlService.generateChannel(IP, PORT, PROTOCOL);
		testCreate(channel);
		//testAdd(channel);
		//testRemove(channel);
		//testLookup(channel);
		//testGetSiteInfo(channel);
	}

	private static void entry_1_Test() throws Exception {
		// 创建通道管理实例
		IChannelManageService chnnlService = new ChannelManageServiceImpl();
		try {
			// 根据标识服务系统提供的ip和端口，创建与标识服务系统的连接通道对象
			IIDManageServiceChannel channel = chnnlService.generateChannel("192.168.150.29", 2643, "TCP");
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
				//testAdd(channel);
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
		return channel.login(OP_ID, 100, "C:\\Users\\fengyuan\\Desktop\\liyue\\hrs_pri.pem", null,3);
	}

	private static BaseResponse testDelete(IIDManageServiceChannel channel) throws IdentifierException {
		return channel.deleteIdentifier(OP_ID, new MsgSettings());
	}

	private static BaseResponse testCreate(IIDManageServiceChannel channel) throws IdentifierException {
		/*IdentifierValue value1 = new IdentifierValue();
		try {
			value1 = makeSiteInfoValue();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IdentifierValue value2 = makeVListValue();*/

		IdentifierValue value3 = new IdentifierValue(500, "HS_SERV", "88.8000");

		return channel.createIdentifier(OP_ID, new IdentifierValue[]{ value3 }, new MsgSettings());
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
		settings.setCertify(true);
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
		IDCommunicationItems[] items = new IDCommunicationItems[2];
		items[0] = new IDCommunicationItems(IDCommunicationItems.ST_ADMIN_AND_QUERY,
				IDCommunicationItems.TS_IDF_TCP, 1304);
		items[1] = new IDCommunicationItems(IDCommunicationItems.ST_ADMIN_AND_QUERY,
				IDCommunicationItems.TS_IDF_UDP, 1304);
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
		IDCommunicationItems[] items = new IDCommunicationItems[2];
		items[0] = new IDCommunicationItems(IDCommunicationItems.ST_ADMIN_AND_QUERY,
				IDCommunicationItems.TS_IDF_TCP, 2641);
		items[1] = new IDCommunicationItems(IDCommunicationItems.ST_ADMIN_AND_QUERY,
				IDCommunicationItems.TS_IDF_UDP, 2641);
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

		SignatureInfo signInfo = SignatureInfo.newSignatureInstance(prvKey, values, "300:88.996", "88.996.438",
				"2020-12-12 23:59:59", "2019-11-25 00:00:00", "2019-11-24 15:44:00", "SM3");
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

		SignatureInfo signInfo = SignatureInfo.newCertificationInstance(prvKey, pubKey, perms, "100:88", "300:88.996", "2020-12-12 23:59:59",
				"2019-11-25 00:00:00", "2019-11-24 15:44:00");
		IdentifierValueUtil.makeIdentifierValueOfCertification(iv, index, signInfo);
		return iv;
	}
}
