package cn.ac.caict.iiiiot.id.client.utils;

import cn.hutool.cache.CacheUtil;

import static org.junit.Assert.*;

public class CommonTest {
    public void test() {
        CacheUtil.newLRUCache(100,30000);
    }
}