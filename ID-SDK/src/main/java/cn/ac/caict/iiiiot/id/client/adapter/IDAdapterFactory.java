package cn.ac.caict.iiiiot.id.client.adapter;

public class IDAdapterFactory {

    public static IDAdapter newInstance() {
        return new DefaultIDAdapter();
    }

    public static IDAdapter newInstance(final String adminIdentifier, final int keyIndex, final byte[] privateKey, int cipher)  {
        return new DefaultIDAdapter();
    }

    public static IDAdapter newInstance(final String adminIdentifier, final int keyIndex, final byte[] secretKey) {
        return new DefaultIDAdapter();
    }

    public static IDAdapter newInstance(final String serverPrefix,final String adminIdentifier, final int keyIndex, final byte[] privateKey, int cipher) {
        return new DefaultIDAdapter();
    }

    public static IDAdapter newInstance(final String serverPrefix,final String adminIdentifier, final int keyIndex, final byte[] secretKey) {
        return new DefaultIDAdapter();
    }

}
