package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.IDCommunicationItems;
import cn.ac.caict.iiiiot.id.client.core.ServerInfo;

public class PrefixSiteInfo {

    private ServerInfo serverInfo;
    private IDCommunicationItems communication;

    public PrefixSiteInfo(ServerInfo serverInfo, IDCommunicationItems communication) {
        this.serverInfo = serverInfo;
        this.communication = communication;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public IDCommunicationItems getCommunication() {
        return communication;
    }

    public String getIp() {
        return serverInfo.getAddressStr();
    }

    public int getPort(){
        return communication.getPort();
    }

    public String getProtocolName(){
        return IDCommunicationItems.getProtocolName(communication.getProtocol());
    }
}