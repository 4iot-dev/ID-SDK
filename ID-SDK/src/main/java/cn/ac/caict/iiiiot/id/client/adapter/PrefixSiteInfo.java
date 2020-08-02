package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.IDCommunicationItems;
import cn.ac.caict.iiiiot.id.client.core.ServerInfo;
import cn.ac.caict.iiiiot.id.client.core.SiteInfo;

public class PrefixSiteInfo {
    private SiteInfo siteInfo;

    private ServerInfo serverInfo;
    private IDCommunicationItems communication;

    public PrefixSiteInfo(SiteInfo siteInfo, ServerInfo serverInfo, IDCommunicationItems communication) {
        this.siteInfo = siteInfo;
        this.serverInfo = serverInfo;
        this.communication = communication;
    }

    public SiteInfo getSiteInfo() {
        return siteInfo;
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