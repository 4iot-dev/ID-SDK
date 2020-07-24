package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.service.IChannelManageService;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.id.client.service.impl.ChannelManageServiceImpl;

public class BeanFactory {

    private static BeanFactory beanFactory;

    private IChannelManageService channelManageService;

    private IDAdapter proxyIdAdapter;

    private BeanFactory() {
        channelManageService = new ChannelManageServiceImpl();
    }

    public static BeanFactory getBeanFactory() {
        if (beanFactory == null) {
            synchronized (BeanFactory.class) {
                if (beanFactory == null) {
                    beanFactory = new BeanFactory();
                }
            }
        }
        return beanFactory;
    }

    public IChannelManageService channelManage() {
        return channelManageService;
    }

    public IIDManageServiceChannel proxyChannel() {
        IIDManageServiceChannel channel = null;
        try {
            channel = channelManageService.generateChannel("45.120.243.40", 3641, "TCP");
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
