package cn.ac.caict.iiiiot.idisc.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import cn.ac.caict.iiiiot.idisc.convertor.ObjBytesConvertor;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.core.SiteInfo;
import cn.ac.caict.iiiiot.idisc.data.AdminInfo;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.data.SignatureInfo;
import cn.ac.caict.iiiiot.idisc.data.ValueReference;
import cn.ac.caict.iiiiot.idisc.security.Claims;
import cn.ac.caict.iiiiot.idisc.security.SignatureStruct;
import cn.ac.caict.iiiiot.idisc.security.SignerCenter;

public class IdentifierValueUtil {

	public static void makeIdentifierValueOfAdminInfo(IdentifierValue iv, AdminInfo admin, int index)
			throws IdentifierException {
		makeValueByType("HS_ADMIN", index, iv, admin);
	}

	public static void makeIdentifierValueOfSiteInfo(IdentifierValue iv, SiteInfo site, int index)
			throws IdentifierException {
		makeValueByType("HS_SITE", index, iv, site);
	}

	public static void makeIdentifierValueOfSiteInfoPrefix(IdentifierValue iv, SiteInfo site, int index)
			throws IdentifierException {
		makeValueByType("HS_SITE.PREFIX", index, iv, site);
	}

	public static void makeIdentifierValueOfVList(IdentifierValue iv, ValueReference[] vList, int index)
			throws IdentifierException {
		makeValueByType("HS_VLIST", index, iv, vList);
	}

	public static void makeIdentifierValueOfPublicKey(IdentifierValue iv, String pubKeyPath, int index)
			throws IdentifierException {
		makeValueByType("HS_PUBKEY", index, iv, pubKeyPath);
	}

	public static void makeIdentifierValueOfSignature(IdentifierValue iv, int index, SignatureInfo signInfo)
			throws IdentifierException {
		makeValueByType("HS_SIGNATURE", index, iv, signInfo);
	}

	public static void makeIdentifierValueOfCertification(IdentifierValue iv, int index, SignatureInfo certInfo)
			throws IdentifierException {
		makeValueByType("HS_CERT", index, iv, certInfo);
	}

	////////////////////////////////////////////////////// private
	////////////////////////////////////////////////////// function////////////////////////////////////////////////////////////////////////
	private static void makeValueByType(String type, int index, IdentifierValue iv, Object originData)
			throws IdentifierException {
		if (iv == null)
			iv = new IdentifierValue();
		if (iv.index < 0)
			iv.setIndex(index);
		byte[] data_buf = new byte[0];
		switch (type) {
		case "HS_PUBKEY":
			if (iv.type.length == 0)
				iv.setType(Common.TYPE_PUBLIC_KEY);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte buf[] = new byte[1024];
			FileInputStream input = null;
			try {
				if (originData instanceof String)
					input = new FileInputStream(new File((String) originData));
				int len;
				while ((len = input.read(buf)) >= 0)
					bout.write(buf, 0, len);
				data_buf = bout.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			break;
		case "HS_ADMIN":
			if (iv.type.length == 0)
				iv.setType(Common.TYPE_ADMIN);
			if (originData instanceof AdminInfo)
				data_buf = ObjBytesConvertor.admInfoConvertToBytes((AdminInfo) originData);
			break;
		case "HS_VLIST":
			if (iv.type.length == 0)
				iv.setType(Common.TYPE_ADMIN_GROUP);
			if (originData instanceof ValueReference[])
				data_buf = ObjBytesConvertor.vListCovertToBytes((ValueReference[]) originData);
			break;
		case "HS_SITE":
			if (iv.type.length == 0)
				iv.setType(Common.TYPE_SITE);
		case "HS_SITE.PREFIX":
			if (iv.type.length == 0)
				iv.setType(Common.TYPE_PREFIX_SITE);
			if (originData instanceof SiteInfo)
				data_buf = ObjBytesConvertor.siteInfoCovertToBytes((SiteInfo) originData);
			break;
		case "HS_CERT":
			if (iv.type.length == 0)
				iv.setType(Common.TYPE_CERT);
		case "HS_SIGNATURE":
			if (iv.type.length == 0)
				iv.setType(Common.TYPE_SIGNATURE);
			if (originData instanceof SignatureInfo) {
				try {
					SignatureInfo signInfo = (SignatureInfo) originData;
					Claims claims = signInfo.getClaims();
					SignatureStruct signClaims = SignerCenter.getInstance().signClaims(claims, signInfo.prvKey);
					data_buf = signClaims.getSignatureBytes();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
		iv.setData(data_buf);
	}
}
