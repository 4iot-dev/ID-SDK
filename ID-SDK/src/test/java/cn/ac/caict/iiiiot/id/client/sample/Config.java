package cn.ac.caict.iiiiot.id.client.sample;

import cn.ac.caict.iiiiot.id.client.adapter.ChannelFactory;
import org.junit.Test;

public class Config {

    /**
     * 运行时更新公共递归配置,channelFactory是单例的
     */
    public void updateConfigRuntime(){
        ChannelFactory channelFactory = ChannelFactory.getChannelFactory();
        channelFactory.setRecursionServerIp("127.0.0.1");
        channelFactory.setRecursionServerPort(2641);
    }
}
