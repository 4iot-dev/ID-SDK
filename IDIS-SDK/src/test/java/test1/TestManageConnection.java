package test1;

import cn.ac.caict.iiiiot.idisc.core.BaseResponse;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.data.MsgSettings;
import cn.ac.caict.iiiiot.idisc.service.IChannelManageService;
import cn.ac.caict.iiiiot.idisc.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.idisc.service.impl.ChannelManageServiceImpl;

public class TestManageConnection {
	public static final int CHANNEL_CLOSED = 0;
	public static final int CHANNEL_LOGIN = 1;
	public static final int CHANNEL_LOGOUT = 2;
	public static void main(String[] args) {
		//创建通道管理实例
		IChannelManageService chnnlService = new ChannelManageServiceImpl();
				try {
					//根据IDIS系统提供的ip和端口，创建与IDIS的连接通道对象
					IIDManageServiceChannel channel = chnnlService.generateChannel("192.168.150.13", 1304, "TCP");
					if (channel != null && chnnlService.getIDManageServiceChannelState(channel) == CHANNEL_LOGOUT) {
						String userId0 = chnnlService.getChannelUserIdentifier(channel);
						System.out.println("++++++" + userId0);	
						BaseResponse loginResp = testLogin(channel);
						if(loginResp != null && loginResp.responseCode == 1){
							System.out.println("登录成功!");
							//testDelete(channel);
							//testCreate(channel);
							//testAdd(channel);
							//testEdit(channel);
							testRemove(channel);
						}
						testLookup(channel);
					}
				} catch (IdentifierException e) {
					e.printStackTrace();
				}
	}
	private static BaseResponse testLogin(IIDManageServiceChannel channel) throws IdentifierException{
		return channel.login("88.1000.1/fy", 1, "D:\\工作\\客户端-无登录\\hsclient_new_lhs_resolver\\bin\\rsa_pri.bin", null,1);
	}
	private static BaseResponse testDelete(IIDManageServiceChannel channel) throws IdentifierException{
		return channel.deleteIdentifier("88.1000.2/mm", new MsgSettings());
	}
	
	private static BaseResponse testCreate(IIDManageServiceChannel channel) throws IdentifierException{
		IdentifierValue[] values = new IdentifierValue[5];
		values[0] = new IdentifierValue(1, "URL", "www.aaa.com");
		values[1] = new IdentifierValue(2, "email", "www.163.com");
		values[2] = new IdentifierValue(3, "url", "www.ccc.com");
		values[3] = new IdentifierValue(4, "email", "www.ddd.com");
		values[4] = new IdentifierValue(5, "email", "www.fff.com");
		return channel.createIdentifier("88.1000.2/mm", values, new MsgSettings());
	}
	
	private static BaseResponse testAdd(IIDManageServiceChannel channel) throws IdentifierException{
		IdentifierValue[] values = new IdentifierValue[2];
		values[0] = new IdentifierValue(6, "URL", "www.666.com");
		values[1] = new IdentifierValue(7, "email", "www.777.com");
		return channel.addIdentifierValues("88.1000.2/mm", values, new MsgSettings());
	}
	
	private static BaseResponse testEdit(IIDManageServiceChannel channel) throws IdentifierException{
		IdentifierValue[] values = new IdentifierValue[2];
		values[0] = new IdentifierValue(6, "email", "www.666e.com");
		values[1] = new IdentifierValue(7, "url", "www.777e.com");
		return  channel.modifyIdentifierValues("88.1000.2/mm", values, new MsgSettings());
	}
	
	private static BaseResponse testRemove(IIDManageServiceChannel channel) throws IdentifierException{
		IdentifierValue[] values = new IdentifierValue[2];
		values[0] = new IdentifierValue(6, "email", "www.666e.com");
		values[1] = new IdentifierValue(7, "url", "www.777e.com");
		int[] arr = {6,7};
		return  channel.removeIdentifierValues("88.1000.2/mm", arr, null);
	}
	
	private static BaseResponse testLookup(IIDManageServiceChannel channel) throws IdentifierException{
		String identifier = "88.1000.2/mm";
		int[] arr = null;
		String[] types = null;
		return channel.lookupIdentifier(identifier, arr, types, null);
	}

}
