package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.convertor.BytesObjConvertor;
import cn.ac.caict.iiiiot.id.client.core.IDCommunicationItems;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.ServerInfo;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;

import java.io.IOException;
import java.util.concurrent.*;

public class CachedPrefixIDAdapter extends DefaultIDAdapter {
    private LRUCache<String, PrefixSiteInfo> prefixSiteCache;
    private ExecutorService cleanExecutor;

    private int capacity = 1000;
    private int timeout = 30000;

    public CachedPrefixIDAdapter() {
        prefixSiteCache = CacheUtil.newLRUCache(capacity, timeout);
        cleanExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());

    }

    @Override
    protected PrefixSiteInfo resolveSiteByProxy(String prefixIdentifier) throws IdentifierAdapterException, IdentifierException {

        PrefixSiteInfo prefixSiteInfo = prefixSiteCache.get(prefixIdentifier);
        if (prefixSiteInfo != null) {
            return prefixSiteInfo;
        }
        if (prefixSiteCache.isFull()) {
            cleanExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    prefixSiteCache.prune();
                }
            });
        }
        try (IDAdapter idAdapter = IDAdapterFactory.cachedInstance()) {
            String[] types = {"HS_SITE"};
            IdentifierValue[] valueArray = idAdapter.resolve(prefixIdentifier, types, null);

            if (valueArray.length > 0) {
                IdentifierValue iv = valueArray[0];
                SiteInfo siteInfo = BytesObjConvertor.bytesCovertToSiteInfo(iv.getData());
                ServerInfo[] servers = siteInfo.servers;

                if (servers.length > 0) {

                    ServerInfo serverInfo = servers[0];
                    IDCommunicationItems tcpItem = findFirstByProtocolName(serverInfo, "TCP");
                    prefixSiteInfo = new PrefixSiteInfo(serverInfo, tcpItem);
                    prefixSiteCache.put(prefixIdentifier, prefixSiteInfo);
                    return prefixSiteInfo;

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
