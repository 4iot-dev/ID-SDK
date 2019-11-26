package identifiermanageDemo;

import cn.ac.caict.iiiiot.idisc.core.BaseResponse;
import cn.ac.caict.iiiiot.idisc.core.ErrorResponse;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.data.MsgSettings;
import cn.ac.caict.iiiiot.idisc.service.IChannelManageService;
import cn.ac.caict.iiiiot.idisc.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.idisc.service.impl.ChannelManageServiceImpl;

/**
 * 标识操作：
 * 管理操作--需要先登录成功，才能执行管理操作，管理操作包含创建标识、删除标识、添加标识值、编辑标识值、移除标识值
 * 查询操作--查询标识
 */
public class ManageIdentifierDemo {
	
	public static void main(String[] args) {
		IChannelManageService chnnlManage = new ChannelManageServiceImpl();
		IIDManageServiceChannel channel = null;
		try {
			//若运行请将示例中的IP和端口替换为OTE环境的IP和端口，联系邮箱：fengyuan@caict.ac.cn
			channel = chnnlManage.generateChannel("192.168.150.13", 1304, "TCP");
			if(channel != null){
				demo_lookupIdentifier(channel);
				demo_login(channel);
				boolean bLogin = demo_isLogin(channel);
				if(bLogin){
					// TODO 可以进行标识的管理操作
					
					demo_createIdentifier(channel);
					// 创建标识后查询
					demo_lookupIdentifier(channel);
					// 添加各种类型标识值 其中自定义类型可以参照IdentifierValueDemo.java中创建方法
					demo_addIdentifierValues(channel);
					// 添加标识值后查询
					demo_lookupIdentifier(channel);
					
					demo_modifyIdentifierValues(channel);
					// 编辑标识值后查询
					demo_lookupIdentifier(channel);
					
					demo_removeIdentifierValues(channel);
					// 移除标识值后查询
					demo_lookupIdentifier(channel);
					
					demo_deleteIdentifier(channel);
					// 删除标识后查询
					demo_lookupIdentifier(channel);
				}
			}
			//关闭channel
			chnnlManage.closeChannel(channel);
		} catch (IdentifierException e) {
			e.printStackTrace();
			System.out.println("创建通道失败！");
		}
	}

	private static void demo_deleteIdentifier(IIDManageServiceChannel channel) {
		// TODO 删除标识
		if(channel != null){
			// 目标标识
			String identifier = "88.1000.2/mm";
			try {
				BaseResponse deleteResp = channel.deleteIdentifier(identifier, new MsgSettings());
				if(deleteResp != null && deleteResp.responseCode == 1){
					System.out.println("标识值移除成功!");
				}  else if(deleteResp instanceof ErrorResponse){
					System.out.println(((ErrorResponse)deleteResp).toString());
				} else {
					System.out.println("错误的响应：" + deleteResp);
				}

			} catch (IdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void demo_removeIdentifierValues(IIDManageServiceChannel channel) {
		// TODO 移除标识值
		if(channel != null){
			// 目标标识
			String identifier = "88.1000.2/mm";
			// 目标识下待移除的的标识值索引数组
			int[] arr = {6,7};
			try {
				BaseResponse removeResp = channel.removeIdentifierValues(identifier, arr, new MsgSettings());
				if(removeResp != null && removeResp.responseCode == 1){
					System.out.println("标识值移除成功!");
				}  else if(removeResp instanceof ErrorResponse){
					System.out.println(((ErrorResponse)removeResp).toString());
				} else {
					System.out.println("错误的响应：" + removeResp);
				}
			} catch (IdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void demo_modifyIdentifierValues(IIDManageServiceChannel channel) {
		// TODO 编辑标识值
		if(channel != null){
			// 目标标识
			String identifier = "88.1000.2/mm";
			// 目标识下待修改的标识值
			IdentifierValue[] modifyVals = new IdentifierValue[2];
			modifyVals[0] = new IdentifierValue(6, "EMAIL", "modify666@sample.com"); 
			modifyVals[1] = new IdentifierValue(7, "EMAIL", "modify777@sample.com");
			try {
				BaseResponse modifyResp = channel.modifyIdentifierValues(identifier, modifyVals, new MsgSettings());
				if(modifyResp != null && modifyResp.responseCode == 1){
					System.out.println("标识值编辑成功!");
				}  else if(modifyResp instanceof ErrorResponse){
					System.out.println(((ErrorResponse)modifyResp).toString());
				} else {
					System.out.println("错误的响应：" + modifyResp);
				}
			} catch (IdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void demo_addIdentifierValues(IIDManageServiceChannel channel) {
		// TODO 添加标识值
		if(channel != null){
			String identifier = "88.1000.2/mm";
			IdentifierValue[] addVals = new IdentifierValue[2];
			addVals[0] = new IdentifierValue(6, "URL", "www.666.com"); 
			addVals[1] = new IdentifierValue(7, "URL", "www.777.com");
			try {
				BaseResponse addResp = channel.addIdentifierValues(identifier, addVals, new MsgSettings());
				if(addResp != null && addResp.responseCode == 1){
					System.out.println("添加标识值成功！");
				} else if(addResp instanceof ErrorResponse){
					System.out.println(((ErrorResponse)addResp).toString());
				} else {
					System.out.println("错误的响应：" + addResp);
				}
			} catch (IdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void demo_createIdentifier(IIDManageServiceChannel channel) {
		// TODO 创建标识
		if(channel != null){
			// 待创建标识
			String identifier = "88.1000.2/mm";
			// 创建标识时一起添加的标识值
			IdentifierValue[] values = new IdentifierValue[3];
			values[0] = new IdentifierValue(1, "URL", "www.aaa.com");
			values[1] = new IdentifierValue(2, "email", "www.163.com");
			values[2] = new IdentifierValue(3, "url", "www.ccc.com");
			try {
				BaseResponse createResp = channel.createIdentifier(identifier, values, new MsgSettings());
				if(createResp != null && createResp.responseCode == 1){
					System.out.println(identifier + "标识创建成功!");
				} else if (createResp instanceof ErrorResponse){
					System.out.println(((ErrorResponse)createResp).toString());
				} else {
					System.out.println("错误的响应：" + createResp);
				}
			} catch (IdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static boolean demo_isLogin(IIDManageServiceChannel channel) {
		// TODO 判断是否登录
		boolean bLogin = false;
		if(channel != null){
			bLogin = channel.isLogin();
		}
		return bLogin;
	}

	private static void demo_login(IIDManageServiceChannel channel) {
		// TODO 登录
		// 若运行请将与我们联系，我们将提供OTE环境及用户信息，联系邮箱：fengyuan@caict.ac.cn
		if(channel != null){
			// 用户标识名称
			String userId = "88.1000.1/fy";
			// 用户标识下公钥对应的索引
			int index = 1;
			// 私钥文件的路径
			String privatekeyPath = "D:\\rsa_ori.pem";
			// 若私钥文件无密码则该值设置为null，若私钥文件有密码则将密码赋予password
			String password = null;
			// 生成摘要的Hash类型
			int hashType = 1;
			try {
				BaseResponse loginResp = channel.login(userId,index,privatekeyPath,password,hashType);
				if(loginResp != null && loginResp.responseCode == 1){
					System.out.println("登录成功！");
				} else if(loginResp instanceof ErrorResponse){
					System.out.println(((ErrorResponse)loginResp).toString());
				} else {
					System.out.println("错误的响应:" + loginResp);
				}
			} catch (IdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void demo_lookupIdentifier(IIDManageServiceChannel channel) {
		// TODO 查询标识
		if( channel != null){
			// 查询的目标标识
			String identifier = "88.1000.2/mm";
			// 查询条件：索引为1和2(该参数可为空)
			int[] arr = {1,2};
			// 查询条件：标识值类型为"URL"(该参数可为空)
			String[] types = {"URL"};
			MsgSettings msgSettings = new MsgSettings();
			// 当MsgSettings的truestyQuery设置为true时，查询结果是经过国家标识体系验证的，具有可信性
			msgSettings.setTruestyQuery(true);
			try {
				BaseResponse lookupResp = channel.lookupIdentifier(identifier, arr, types, msgSettings);
				if(lookupResp != null && lookupResp.responseCode == 1){
					System.out.println("查询成功，结果：" + lookupResp.toString());
				} else if (lookupResp instanceof ErrorResponse){
					System.out.println(((ErrorResponse)lookupResp).toString());
				} else {
					System.out.println("错误的响应:" + lookupResp);
				}
			} catch (IdentifierException e) {
				e.printStackTrace();
			}			
		}
	}
}
