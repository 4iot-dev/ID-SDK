package cn.ac.caict.iiiiot.id.client.adapter;

public class IDAdapterFactory {

    private static IDAdapter cachedPrefixIDAdapter;

    /**
     * 创建IDAdapter实例,调用递归节点,缓存前缀与证书与ttl无关
     *
     * @return
     */
    public static IDAdapter cachedInstance() {
        if (cachedPrefixIDAdapter == null) {
            synchronized (IDAdapterFactory.class) {
                if (cachedPrefixIDAdapter == null) {
                    cachedPrefixIDAdapter = new CachedPrefixIDAdapter();
                }
            }
        }
        return cachedPrefixIDAdapter;
    }

    /**
     * 创建默认IDAdapter实例,调用递归节点,不会进行缓存
     *
     * @return
     */
    public static IDAdapter newInstance() {
        return new DefaultIDAdapter();
    }

    /**
     * 通过adminIdentifier的前缀定位服务地址,并登录到相关服务上,构建认证的IDAdapter,如果认证不通过,将会抛出异常
     *
     * @param adminIdentifier
     * @param keyIndex
     * @param privateKeyPem
     * @param cipher
     * @return
     */
    public static IDAdapter newInstance(final String adminIdentifier, final int keyIndex, final String privateKeyPem, int cipher) {
        return new DefaultIDAdapter(adminIdentifier, keyIndex, privateKeyPem, cipher);
    }

    /**
     * 通过serverPrefix的前缀定位服务地址,并登录到相关服务上,构建认证的IDAdapter,如果认证不通过,将会抛出异常
     *
     * @param serverPrefix
     * @param adminIdentifier
     * @param keyIndex
     * @param privateKeyPem
     * @param cipher
     * @return
     */
    public static IDAdapter newInstance(final String serverPrefix, final String adminIdentifier, final int keyIndex, final String privateKeyPem, int cipher) {
        return new DefaultIDAdapter(serverPrefix, adminIdentifier, keyIndex, privateKeyPem, cipher);
    }

    /**
     * 通过指定服务的ip地址与端口,登录到相关服务上,构建认证的IDAdapter,如果认证不通过,将会抛出异常
     *
     * @param serverIp
     * @param port
     * @param adminIdentifier
     * @param keyIndex
     * @param privateKeyPem
     * @param cipher          生成摘要hash算法(MD5算法：rdType=1,SH1算法：rdType=2,SH256算法：rdType=3)
     * @return
     * @throws IdentifierAdapterRuntimeException 连接失败或认证失败
     */
    public static IDAdapter newInstance(String serverIp, int port, final String adminIdentifier, final int keyIndex, final String privateKeyPem, int cipher) {
        return new DefaultIDAdapter(serverIp, port, adminIdentifier, keyIndex, privateKeyPem, cipher);
    }

    /**
     * 通过前缀构建IDAdapter,根据前缀定位服务地址
     *
     * @param serverPrefix
     * @return
     */
    public static IDAdapter newInstance(final String serverPrefix) {
        return new DefaultIDAdapter(serverPrefix);
    }

    /**
     * 通过定服务的ip地址与端口构建IDAdapter
     *
     * @param serverIp
     * @param port
     * @return
     */
    public static IDAdapter newInstance(String serverIp, int port) {
        return new DefaultIDAdapter(serverIp, port);
    }


}
