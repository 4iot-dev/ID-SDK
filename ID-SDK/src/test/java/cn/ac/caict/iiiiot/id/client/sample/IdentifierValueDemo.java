package cn.ac.caict.iiiiot.id.client.sample;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.IDCommunicationItems;
import cn.ac.caict.iiiiot.id.client.core.ServerInfo;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;
import cn.ac.caict.iiiiot.id.client.data.AdminInfo;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.SignatureInfo;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.security.Permission;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.id.client.utils.IdentifierValueUtil;
import cn.ac.caict.iiiiot.id.client.utils.Util;
/**
 * 说明：该demo主要演示预定义标识值的创建，即IdentifierValueUtil工具类的使用方法。
 * 当用户在进行标识需要为Identifier添加预定义类型标识值时，参考如下示例创建相应的标识值。
 * 
 * 注：创建标识值结束后通过addIdentifierValues接口添加至目标标识。
 */
public class IdentifierValueDemo {

	// type:HS_ADMIN
	public static IdentifierValue demo_makeValueOfADMIN() throws IdentifierException {
		IdentifierValue value = new IdentifierValue();
		AdminInfo admin = new AdminInfo();
		admin.admId = Util.encodeString("88.1000.2/cupA");
		admin.admIdIndex = 300;
		admin.initPermissions(true, true, true, true, true, true, true, true, true, true, true, true);
		IdentifierValueUtil.makeIdentifierValueOfAdminInfo(value, admin, 301);
		return value;
	}

	// type:HS_SITE
	public static IdentifierValue demo_makeValueOfSITE() throws IOException, IdentifierException {
		boolean bSitePrefix = false;
		return makeSites(bSitePrefix);
	}

	// type:HS_SITE.PREFIX
	public static IdentifierValue demo_makeValueOfSITEPREFIX() throws IdentifierException, IOException {
		boolean bSitePrefix = true;
		return makeSites(bSitePrefix);
	}

	// tyep:HS_CERT
	public static IdentifierValue demo_makeValueOfCERT() throws Exception {
		String CERTIFICATION_PUBKEY_PATH = "D:/sm2key/sm2_public.pem";
		String CERTIFICATION_PRVKEY_PATH = "D:/sm2key/sm2_private.pem";
		PublicKey pubKey = Util.getPublicKeyFromFile(CERTIFICATION_PUBKEY_PATH);
		PrivateKey prvKey = Util.getPrivateKeyFromFile(CERTIFICATION_PRVKEY_PATH, null);

		List<Permission> perms = new ArrayList<>();
		perms.add(new Permission(null, "everything"));

		IdentifierValue value = new IdentifierValue();
		int index = 401;

		SignatureInfo signInfo = SignatureInfo.newCertificationInstance(prvKey, pubKey, perms, "100:88", "300:88.996",
				"2020-12-12 23:59:59", "2019-11-25 00:00:00", "2019-11-24 15:44:00");
		IdentifierValueUtil.makeIdentifierValueOfCertification(value, index, signInfo);
		return value;
	}

	// type:HS_SIGNATURE
	public static IdentifierValue demo_makeValueOfSIGNATURE(IdentifierValue[] values)
			throws IdentifierException {
		IdentifierValue value = new IdentifierValue();
		int index = 400;
		String SIGNATURE_PRVKEY_PATH = "D:/rsakeys/rsa_pri.pem";
		PrivateKey prvKey = Util.getPrivateKeyFromFile(SIGNATURE_PRVKEY_PATH, null);
		SignatureInfo signInfo = SignatureInfo.newSignatureInstance(prvKey, values, "300:88.996", "88.996.438",
				"2020-12-12 23:59:59", "2019-11-25 00:00:00", "2019-11-24 15:44:00", "SHA-256");
		IdentifierValueUtil.makeIdentifierValueOfSignature(value, index, signInfo);
		return value;
	}

	// type:HS_PUBKEY
	public static IdentifierValue demo_makeValueOfPUBKEY() throws IdentifierException {
		IdentifierValue value = new IdentifierValue();
		int index = 300;
		String PUBLICKEY_PATH = "D:/rsakeys/rsa_pub.pem";
		IdentifierValueUtil.makeIdentifierValueOfPublicKey(value, PUBLICKEY_PATH, index);
		return value;
	}

	// type:HS_VLIST
	public static IdentifierValue demo_makeValueOfVLIST() throws IdentifierException {
		IdentifierValue value = new IdentifierValue();
		int index = 30;
		ValueReference[] vr = new ValueReference[2];
		vr[0] = new ValueReference("88.1000.2/mm", 1);
		vr[1] = new ValueReference("88.1000.2/cup", 2);
		IdentifierValueUtil.makeIdentifierValueOfVList(value, vr, index);
		return value;
	}

	// type:HS_SERV
	public static IdentifierValue demo_makeValueOfSERV() {
		IdentifierValue value = new IdentifierValue(500, "HS_SERV", "88.8000");
		return value;

		/* 另外一种创建方法
		 * IdentifierValue iv = new IdentifierValue();
		 * IdentifierValueUtil.makeIdentifierValueOfGeneralType(iv, "HS_SERV",
		 * 500, "88.8000"); return iv;
		 */
	}

	// type:HS_ALIAS
	public static IdentifierValue demo_makeValueOfALIAS() {
		IdentifierValue value = new IdentifierValue(200, "HS_ALIAS", "88.99");
		return value;
		/* 另外一种创建方法
		 * IdentifierValue iv = new IdentifierValue();
		 * IdentifierValueUtil.makeIdentifierValueOfGeneralType(iv, "HS_ALIAS",
		 * 200, "88.99"); return iv;
		 */
	}

	// type:EMAIL
	public static IdentifierValue demo_makeValueOfEMAIL(IIDManageServiceChannel channel) {
		IdentifierValue value = new IdentifierValue(2, "EMAIL", "zhangsan@sample.com");
		return value;
		/* 另外一种创建方法
		 * IdentifierValue iv = new IdentifierValue();
		 * IdentifierValueUtil.makeIdentifierValueOfGeneralType(iv, "EMAIL",
		 * 2, "zhangsan@sample.com"); return iv;
		 */
	}

	// type:URL
	public static IdentifierValue demo_makeValueOfURL() {
		IdentifierValue value = new IdentifierValue(1, "URL", "http://www.caict.ac.cn");
		return value;
		/* 另外一种创建方法
		 * IdentifierValue iv = new IdentifierValue();
		 * IdentifierValueUtil.makeIdentifierValueOfGeneralType(iv, "URL",
		 * 1, "http://www.caict.ac.cn"); return iv;
		 */
	}

	/******************************************************
	 * private-function
	 ******************************************************/
	private static IdentifierValue makeSites(boolean bPrefix) throws IdentifierException, IOException {
		IdentifierValue value = new IdentifierValue();
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
		ser1.publicKey = Util.getBytesFromFile("D:/temp/svr_1/admpub.pem");
		ser1.serverId = 1;
		// servers
		ServerInfo[] servArr = new ServerInfo[] { ser1 };
		// siteinfo
		SiteInfo si = new SiteInfo();
		si.servers = servArr;
		si.attributes = null;
		if (bPrefix)
			IdentifierValueUtil.makeIdentifierValueOfSiteInfoPrefix(value, si, index);
		else
			IdentifierValueUtil.makeIdentifierValueOfSiteInfo(value, si, index);
		return value;
	}
}