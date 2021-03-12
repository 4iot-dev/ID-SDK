package cn.ac.caict.iiiiot.id.client.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import cn.ac.caict.iiiiot.id.client.convertor.ObjBytesConvertor;
import cn.ac.caict.iiiiot.id.client.core.Attribute;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;
import cn.ac.caict.iiiiot.id.client.data.AdminInfo;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.SignatureInfo;
import cn.ac.caict.iiiiot.id.client.data.ValueReference;
import cn.ac.caict.iiiiot.id.client.security.Claims;
import cn.ac.caict.iiiiot.id.client.security.SignatureStruct;
import cn.ac.caict.iiiiot.id.client.security.SignerCenter;
import cn.hutool.core.io.IoUtil;

public class IdentifierValueUtil {

    public static void makeIdentifierValueOfAdminInfo(IdentifierValue iv, AdminInfo admin, int index)
            throws IdentifierException {
        makeValueByType(Common.HS_ADMIN, index, iv, admin);
    }

    public static void makeIdentifierValueOfSiteInfo(IdentifierValue iv, SiteInfo site, int index)
            throws IdentifierException {
        if (site != null) {
            if (site.attributes != null) {
                for (int i = 0; i < site.attributes.length; i++) {
                    if (site.attributes[i] != null) {
                        if (site.attributes[i].name == null) {
                            site.attributes[i].name = Util.encodeString("");
                        }
                        if (site.attributes[i].value == null) {
                            site.attributes[i].value = Util.encodeString("");
                        }
                    } else {
                        site.attributes[i] = new Attribute("", "");
                    }
                }
            }
        }
        makeValueByType(Common.HS_SITE, index, iv, site);
    }

    public static void makeIdentifierValueOfSiteInfoPrefix(IdentifierValue iv, SiteInfo site, int index)
            throws IdentifierException {
        makeValueByType(Common.HS_SITE_PREFIX, index, iv, site);
    }

    public static void makeIdentifierValueOfVList(IdentifierValue iv, ValueReference[] vList, int index)
            throws IdentifierException {
        makeValueByType(Common.HS_VLIST, index, iv, vList);
    }

    public static void makeIdentifierValueOfPublicKey(IdentifierValue iv, String pubKeyPath, int index)
            throws IdentifierException {
        makeValueByType(Common.HS_PUBKEY, index, iv, pubKeyPath);
    }

	public static void makeIdentifierValueOfPublicKey(IdentifierValue iv, PublicKey publicKey, int index)
			throws IdentifierException {
		makeValueByType(Common.HS_PUBKEY, index, iv, publicKey);
	}

    public static void makeIdentifierValueOfSignature(IdentifierValue iv, int index, SignatureInfo signInfo)
            throws IdentifierException {
        makeValueByType(Common.HS_SIGNATURE, index, iv, signInfo);
    }

    public static void makeIdentifierValueOfCertification(IdentifierValue iv, int index, SignatureInfo certInfo)
            throws IdentifierException {
        makeValueByType(Common.HS_CERT, index, iv, certInfo);
    }

    public static void makeIdentifierValueOfGeneralType(IdentifierValue iv, String generalType, int index, String strData) throws IdentifierException {
        if (iv == null)
            iv = new IdentifierValue();
        if (!Common.URL.equalsIgnoreCase(generalType) && !Common.EMAIL.equalsIgnoreCase(generalType) && !Common.HS_SERV.equalsIgnoreCase(generalType) && !Common.HS_ALIAS.equalsIgnoreCase(generalType))
            System.out.println("注意：该类型\"" + generalType + "\"可能为非预定义类型！");
        iv.setType(Util.encodeString(generalType));
        iv.setIndex(index);
        iv.setData(Util.encodeString(strData));
    }

    //////////////////////////////////////////////////////private-function////////////////////////////////////////////////////////////////////////
    private static void makeValueByType(String type, int index, IdentifierValue iv, Object originData)
            throws IdentifierException {
        if (iv == null){
            iv = new IdentifierValue();
        }
        if (iv.index < 0){
            iv.setIndex(index);
        }
        byte[] data_buf = new byte[0];
        switch (type) {
            case Common.HS_PUBKEY:
                if (iv.type.length == 0)
                    iv.setType(Common.TYPE_PUBLIC_KEY);
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                byte buf[] = new byte[1024];
                FileInputStream input = null;
                try {
                    if (originData instanceof String) {
                        input = new FileInputStream(new File((String) originData));
                        int len;
                        while ((len = input.read(buf)) >= 0) {
                            bout.write(buf, 0, len);
                        }
                        data_buf = bout.toByteArray();
                    } else if (originData instanceof PublicKey) {
                        String publicKeyPem = KeyConverter.toX509Pem((PublicKey) originData);
						data_buf = publicKeyPem.getBytes();
                    }
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
            case Common.HS_ADMIN:
                if (iv.type.length == 0)
                    iv.setType(Common.TYPE_ADMIN);
                if (originData instanceof AdminInfo)
                    data_buf = ObjBytesConvertor.admInfoConvertToBytes((AdminInfo) originData);
                break;
            case Common.HS_VLIST:
                if (iv.type.length == 0)
                    iv.setType(Common.TYPE_ADMIN_GROUP);
                if (originData instanceof ValueReference[])
                    data_buf = ObjBytesConvertor.vListCovertToBytes((ValueReference[]) originData);
                break;
            case Common.HS_SITE:
                if (iv.type.length == 0){
                    iv.setType(Common.TYPE_SITE);
                }
            case Common.HS_SITE_PREFIX:
                if (iv.type.length == 0){
                    iv.setType(Common.TYPE_PREFIX_SITE);
                }
                if (originData instanceof SiteInfo){
                    data_buf = ObjBytesConvertor.siteInfoCovertToBytes((SiteInfo) originData);
                }
                break;
            case Common.HS_CERT:
                if (iv.type.length == 0){
                    iv.setType(Common.TYPE_CERT);
                }
            case Common.HS_SIGNATURE:
                if (iv.type.length == 0) {
                    iv.setType(Common.TYPE_SIGNATURE);
                }
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
                System.out.println("暂不支持该类型：" + type);
                break;
        }
        iv.setData(data_buf);
    }
}
