package cn.ac.caict.iiiiot.id.client.test;

import java.io.IOException;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.service.IChannelManageService;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.id.client.service.impl.ChannelManageServiceImpl;

public class ManageChannelDemo {
	public static final int CHANNEL_CLOSED = 0;
	public static final int CHANNEL_LOGIN = 1;
	public static final int CHANNEL_LOGOUT = 2;
	
	public static void main(String[] args) {
		// 实例化通道管理服务
		IChannelManageService chnnlManage = new ChannelManageServiceImpl();
		// 通过参数创建连接通道
		IIDManageServiceChannel channel1 = demo_generateChannel(chnnlManage);
		// 通过配置文件创建连接通道
		IIDManageServiceChannel channel2 = demo_generateChannelByConfig(chnnlManage);
		// 获取当前通道个数
		int count = demo_getIDManageServiceChannelCount(chnnlManage);
		System.out.println("当前存在" + count + "个通道.");
		// 获取某通道状态
		demo_getIDManageServiceChannelState(chnnlManage,channel2);
		// 获取某通道的登录用户的标识
		demo_getChannelUserIdentifier(chnnlManage,channel2);
		// 关闭通道
		demo_closeChannel(chnnlManage,channel2);
	
	}

	private static void demo_getChannelUserIdentifier(IChannelManageService chnnlManage,
			IIDManageServiceChannel channel2) {
		if(chnnlManage != null) {
			String identifier = chnnlManage.getChannelUserIdentifier(channel2);
			System.out.println("登录用户的标识为：" + identifier);
		}
	}

	private static void demo_getIDManageServiceChannelState(IChannelManageService chnnlManage,IIDManageServiceChannel channel) {
		if(chnnlManage != null) {
			int state = chnnlManage.getIDManageServiceChannelState(channel);
			if(state == CHANNEL_CLOSED){
				System.out.println("通道已关闭");
			} else if (state == CHANNEL_LOGIN){
				System.out.println("通道已登录");
			} else if (state == CHANNEL_LOGOUT){
				System.out.println("通道已创建但未登录");
			}
		}
	}

	private static int demo_getIDManageServiceChannelCount(IChannelManageService chnnlManage) {
		if(chnnlManage != null)
			return chnnlManage.getIDManageServiceChannelCount();
		return 0;
	}

	private static void demo_closeChannel(IChannelManageService chnnlManage,IIDManageServiceChannel channel) {
		try {
			if(chnnlManage != null){
				chnnlManage.closeChannel(channel);
				System.out.println("通道已关闭");
			}
		} catch (IdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static IIDManageServiceChannel demo_generateChannel(IChannelManageService chnnlManage){
		IIDManageServiceChannel channel = null;
		try {
			// 若运行请将示例中的IP和端口替换为OTE环境的IP和端口，联系邮箱：fengyuan@caict.ac.cn
			channel = chnnlManage.generateChannel("192.168.150.13", 1304, "TCP");
			if(channel != null)
				System.out.println("创建通道成功,在当前状态下可以进行查询操作！");
			return channel;
		} catch (IdentifierException e) {
			e.printStackTrace();
			System.out.println("创建通道失败！");
		}
		return channel;
	}
	
	private static IIDManageServiceChannel demo_generateChannelByConfig(IChannelManageService chnnlManage){
		boolean bSuccess = true;
		IIDManageServiceChannel channel = null;
		try {
			// 读取当前工程src/config.json配置文件来创建通道
			channel = chnnlManage.generateChannelByConfig();
			if(channel != null)
				System.out.println("使用配置文件创建通道成功,在当前状态下可以进行查询操作！");
		} catch (IdentifierException e) {
			e.printStackTrace();
			bSuccess = false;
		} catch (IOException e) {
			e.printStackTrace();
			bSuccess = false;
		}
		if(!bSuccess)
			System.out.println("创建通道失败！");
		return channel;
	}
	
}