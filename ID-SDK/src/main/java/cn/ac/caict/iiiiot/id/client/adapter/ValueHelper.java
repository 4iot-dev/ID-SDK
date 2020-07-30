package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;
import cn.ac.caict.iiiiot.id.client.data.AdminInfo;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.SignatureInfo;
import cn.ac.caict.iiiiot.id.client.security.Permission;
import cn.ac.caict.iiiiot.id.client.utils.IdentifierValueUtil;
import cn.ac.caict.iiiiot.id.client.utils.Util;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class ValueHelper {

    private static ValueHelper valueHelper;

    private ValueHelper() {

    }

    public static ValueHelper getInstance() {
        if (valueHelper == null) {
            synchronized (ValueHelper.class) {
                if (valueHelper == null) {
                    valueHelper = new ValueHelper();
                }
            }
        }
        return valueHelper;
    }

    public String extraPrefix(String identifier) {
        String prefix;
        int separator = identifier.indexOf("/");
        if (separator != -1) {
            if (Util.startsWithCaseInsensitive(identifier, "0.NA/")) {
                //前缀
                prefix = identifier;
            } else {
                prefix = identifier.substring(0, separator);
            }
        } else {
            //前缀
            prefix = identifier;
        }
        return prefix;
    }

    public List<IdentifierValue> filterOnlyPublicValues(List<IdentifierValue> values) {
        List<IdentifierValue> res = null;
        for (int i = values.size() - 1; i >= 0; i--) {
            IdentifierValue value = values.get(i);
            if (!value.bPublicRead) {
                if (res == null) res = new ArrayList<>(values);
                res.remove(i);
            }
        }
        if (res == null) return values;
        return res;
    }
    public static SiteInfo getPrimarySite(SiteInfo[] sites) {
        for (SiteInfo site : sites) {
            if (site.isPrimarySite) {
                return site;
            }
        }
        return null;
    }

    public IdentifierValue newPublicKeyValue(int index, PublicKey publicKey) throws IdentifierException {
        IdentifierValue iv = new IdentifierValue();
        IdentifierValueUtil.makeIdentifierValueOfPublicKey(iv, publicKey, index);
        return iv;
    }

    public IdentifierValue newAdminValue(int valueIndex, String admId, int admIdIndex,
                                                boolean perm_createId, boolean perm_deleteId,
                                                boolean perm_addNA, boolean perm_deleteNA,
                                                boolean perm_modifyValue, boolean perm_removeValue, boolean perm_addValue,
                                                boolean perm_modifyAdmin, boolean perm_removeAdmin, boolean perm_addAdmin,
                                                boolean perm_readValue, boolean perm_showAll) throws IdentifierException {
        IdentifierValue value = new IdentifierValue();
        AdminInfo admin = new AdminInfo();
        admin.admId = Util.encodeString(admId);
        admin.admIdIndex = admIdIndex;
        admin.initPermissions(perm_createId, perm_deleteId,
                perm_addNA, perm_deleteNA,
                perm_modifyValue, perm_removeValue, perm_addValue,
                perm_modifyAdmin, perm_removeAdmin, perm_addAdmin,
                perm_readValue, perm_showAll);
        IdentifierValueUtil.makeIdentifierValueOfAdminInfo(value, admin, valueIndex);
        return value;
    }

    public IdentifierValue newAdminValue(int valueIndex, String admId, int admIdIndex) throws IdentifierException {
        return newAdminValue(valueIndex,admId,admIdIndex,true, true, true, true, true, true, true, true, true, true, true, true);
    }

    /**
     *
     * @param index
     * @param pubKey
     * @param issue 300:88.111/test
     * @param subject
     * @param admPrvKey
     * @param expirationTime "2020-12-12 23:59:59"
     * @param notBefore "2019-11-25 00:00:00"
     * @param issedAfterTime "2019-11-24 15:44:00"
     * @return
     * @throws Exception
     */
    public IdentifierValue newCertValue(int index,PublicKey pubKey,String issue,String subject,PrivateKey admPrvKey,String expirationTime,String notBefore,String issedAfterTime) throws Exception {

        List<Permission> perms = new ArrayList<>();
        perms.add(new Permission(null, Permission.EVERYTHING));

        return newCertValue(index,pubKey,perms,issue,subject,admPrvKey,expirationTime,notBefore,issedAfterTime);

    }

    public IdentifierValue newCertValue(int index,PublicKey pubKey,List<Permission> perms,String issue,String subject,PrivateKey admPrvKey,String expirationTime,String notBefore,String issedAfterTime) throws Exception {

        IdentifierValue value = new IdentifierValue();
        SignatureInfo signInfo = SignatureInfo.newCertificationInstance(admPrvKey, pubKey, perms, issue, subject, expirationTime, notBefore, issedAfterTime);
        IdentifierValueUtil.makeIdentifierValueOfCertification(value, index, signInfo);
        return value;

    }

    /**
     *
     * @param index
     * @param values
     * @param issue 300:88.111/test
     * @param subject
     * @param admPrvKey
     * @param expirationTime
     * @param notBefore
     * @param issedAfterTime
     * @param digestAlg SHA-256
     * @return
     * @throws Exception
     */
    public IdentifierValue newSignatureValue(int index,IdentifierValue[] values,String issue,String subject,PrivateKey admPrvKey,String expirationTime,String notBefore,String issedAfterTime, String digestAlg) throws Exception {

        IdentifierValue value = new IdentifierValue();
        SignatureInfo signInfo = SignatureInfo.newSignatureInstance(admPrvKey, values, issue, subject,
                expirationTime, notBefore, issedAfterTime, digestAlg);
        IdentifierValueUtil.makeIdentifierValueOfSignature(value, index, signInfo);
        return value;

    }

}
