package cn.ac.caict.iiiiot.id.client.adapter.cache;

import cn.ac.caict.iiiiot.id.client.adapter.IdentifierRecord;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class IdentifierRecordCache {
    private LRUCache<String, IdentifierRecord> cache;
    private ExecutorService cleanExecutor;

    private static IdentifierRecordCache identifierRecordCache;

    private IdentifierRecordCache(int capacity, int timeout) {
        cache = CacheUtil.newLRUCache(capacity, timeout);
        cleanExecutor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), new ThreadPoolExecutor.DiscardPolicy());
    }

    public static IdentifierRecordCache getInstance() {
        if (identifierRecordCache == null) {
            synchronized (IdentifierRecordCache.class) {
                if (identifierRecordCache == null) {
                    identifierRecordCache = new IdentifierRecordCache(1000, 30000);
                }
            }
        }
        return identifierRecordCache;
    }

    public void cacheIdentifierRecord(IdentifierRecord identifierRecord) {
        if (cache.isFull()) {
            cleanExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    cache.prune();
                }
            });
        }
        cache.put(identifierRecord.getIdentifier(), identifierRecord);
    }

    public IdentifierRecord get(String identifier) {

        return cache.get(identifier);
    }

    public void clear(){
        cache.clear();
    }
}
