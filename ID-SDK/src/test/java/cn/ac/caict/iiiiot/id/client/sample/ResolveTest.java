package cn.ac.caict.iiiiot.id.client.sample;

import cn.ac.caict.iiiiot.id.client.convertor.BytesObjConvertor;
import cn.ac.caict.iiiiot.id.client.core.*;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.MsgSettings;
import cn.ac.caict.iiiiot.id.client.service.IChannelManageService;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.id.client.service.impl.ChannelManageServiceImpl;
import cn.ac.caict.iiiiot.id.client.utils.Util;
import org.junit.Test;

public class ResolveTest {

    @Test
    public void resolve(){
        IChannelManageService chnnlManage = new ChannelManageServiceImpl();
        try {
            IIDManageServiceChannel channel = chnnlManage.generateChannel("45.120.243.40", 3641, "TCP");
            if(channel != null){
                // 查询的目标标识
                String identifier = "0.NA/88.300.15907541011";
                MsgSettings msgSettings = new MsgSettings();
                // 当MsgSettings的truestyQuery设置为true时，查询结果是经过国家标识体系验证的，具有可信性
                msgSettings.setTruestyQuery(true);
                try {
                    BaseResponse lookupResp = channel.lookupIdentifier(identifier, null, null, msgSettings);
                    if(lookupResp != null && lookupResp.responseCode == 1){
                        System.out.println(lookupResp.trustedResult);
                        System.out.println("查询成功，结果：" + lookupResp.toString());
                        ResolutionResponse resolutionResponse = (ResolutionResponse) lookupResp;
                        IdentifierValue[] values = resolutionResponse.getAllIDValues();

                    } else if (lookupResp instanceof ErrorResponse){
                        System.out.println(((ErrorResponse)lookupResp).toString());
                    } else {
                        System.out.println("错误的响应:" + lookupResp);
                    }

                } catch (IdentifierException e) {
                    e.printStackTrace();
                }
            }

        } catch (IdentifierException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void resolveSite(){
        IChannelManageService chnnlManage = new ChannelManageServiceImpl();
        try {
            IIDManageServiceChannel channel = chnnlManage.generateChannel("45.120.243.40", 3641, "TCP");
            if(channel != null){
                // 查询的目标标识
                String identifier = "0.NA/88.300.15907541011";
                // 查询条件：索引为1和2(该参数可为空)
                String[] types = {"HS_SITE"};
                // 查询条件：标识值类型为"URL"(该参数可为空)
                MsgSettings msgSettings = new MsgSettings();
                // 当MsgSettings的truestyQuery设置为true时，查询结果是经过国家标识体系验证的，具有可信性
                msgSettings.setTruestyQuery(true);
                try {
                    BaseResponse lookupResp = channel.lookupIdentifier(identifier, null,types , msgSettings);
                    if(lookupResp != null && lookupResp.responseCode == 1){
                        System.out.println(lookupResp.trustedResult);
                        System.out.println("查询成功，结果：" + lookupResp.toString());
                        ResolutionResponse resolutionResponse = (ResolutionResponse) lookupResp;
                        IdentifierValue[] values = resolutionResponse.getAllIDValues();

                        if(values.length>0){
                            IdentifierValue iv = values[0];
                            SiteInfo siteInfo = BytesObjConvertor.bytesCovertToSiteInfo(iv.getData());
                            System.out.println("site info is: " + siteInfo.toString());
                            ServerInfo[] servers = siteInfo.servers;

                            if(servers.length>0){
                                ServerInfo serverInfo = servers[0];

                                IDCommunicationItems[] itemArray = serverInfo.communicationItems;
                                if(itemArray.length>0){
                                    IDCommunicationItems item = itemArray[0];

                                    String protocolName = IDCommunicationItems.getProtocolName(item.getProtocol());

                                    if(protocolName.equals("TCP")){
                                        System.out.println("site info is: " + serverInfo.getAddressStr()+":"+ item.getPort());
                                    }

                                }

                            }
                        }

                    } else if (lookupResp instanceof ErrorResponse){
                        System.out.println(((ErrorResponse)lookupResp).toString());
                    } else {
                        System.out.println("错误的响应:" + lookupResp);
                    }

                } catch (IdentifierException e) {
                    e.printStackTrace();
                }
            }

        } catch (IdentifierException e) {
            e.printStackTrace();
        }
    }


}
