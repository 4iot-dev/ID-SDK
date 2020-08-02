package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.adapter.cache.IdentifierRecordCache;
import cn.ac.caict.iiiiot.id.client.convertor.BytesObjConvertor;
import cn.ac.caict.iiiiot.id.client.core.IDCommunicationItems;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.ServerInfo;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.Util;

import java.io.IOException;

public class CachedPrefixIDAdapter extends DefaultIDAdapter {

    public CachedPrefixIDAdapter() {
        super();
    }

    @Override
    protected PrefixSiteInfo resolveSiteByProxy(String prefixIdentifier) throws IdentifierAdapterException, IdentifierException {
        IdentifierRecord identifierRecord = IdentifierRecordCache.getInstance().get(Util.upperCasePrefix(prefixIdentifier));
        IdentifierValue[] valueArray = null;
        if (identifierRecord != null) {
            valueArray = new IdentifierValue[identifierRecord.getValues().size()];
            identifierRecord.getValues().toArray(valueArray);
        }
        try (IDAdapter idAdapter = IDAdapterFactory.cachedInstance()) {
            valueArray = idAdapter.resolve(prefixIdentifier, null, null);

            valueArray = ValueHelper.getInstance().filter(valueArray, Common.HS_SITE);
            if (valueArray.length > 0) {
                IdentifierValue iv = valueArray[0];
                SiteInfo siteInfo = BytesObjConvertor.bytesCovertToSiteInfo(iv.getData());
                ServerInfo[] servers = siteInfo.servers;

                if (servers.length > 0) {

                    ServerInfo serverInfo = servers[0];
                    IDCommunicationItems tcpItem = findFirstByProtocolName(serverInfo, "TCP");
                    return new PrefixSiteInfo(serverInfo, tcpItem);

                } else {
                    throw new IdentifierAdapterException("cannot find servers");
                }
            } else {
                throw new IdentifierAdapterException("cannot find site type value");
            }
        } catch (IOException e) {
            throw new IdentifierAdapterException("idAdapter close error");
        }
    }

}
