package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.service.IChannelManageService;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.id.client.service.impl.ChannelManageServiceImpl;

public class ChannelFactory {

    private static ChannelFactory channelFactory;

    private IChannelManageService channelManageService;

    private IDAdapter proxyIdAdapter;

//    private String recursionServerIp = "45.120.243.40";
//    private int recursionServerPort = 3641;

    private String recursionServerIp = "192.168.150.37";
    private int recursionServerPort = 5643;


    private ChannelFactory() {
        channelManageService = new ChannelManageServiceImpl();
    }

    public static ChannelFactory getChannelFactory() {
        if (channelFactory == null) {
            synchronized (ChannelFactory.class) {
                if (channelFactory == null) {
                    channelFactory = new ChannelFactory();
                }
            }
        }
        return channelFactory;
    }

    public IChannelManageService channelManage() {
        return channelManageService;
    }

    public IIDManageServiceChannel proxyChannel() {
        IIDManageServiceChannel channel = null;
        try {
            channel = channelManageService.generateChannel(recursionServerIp, recursionServerPort, "TCP");
        } catch (IdentifierException e) {
            throw new IdentifierAdapterRuntimeException("build proxy channel error",e);
        }
        return channel;
    }


    public IIDManageServiceChannel newChannel(String ip, int port, String protocol) {
        IIDManageServiceChannel channel = null;
        try {
            channel = channelManageService.generateChannel(ip, port, protocol);
        } catch (IdentifierException e) {
            throw new IdentifierAdapterRuntimeException("build channel error",e);
        }
        return channel;
    }


}
