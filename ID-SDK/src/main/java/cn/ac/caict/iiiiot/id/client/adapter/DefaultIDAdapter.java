package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.convertor.BytesObjConvertor;
import cn.ac.caict.iiiiot.id.client.core.*;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.MsgSettings;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;

import java.io.IOException;
import java.security.PrivateKey;

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *© COPYRIGHT 2019 Corporation for Institute of Industrial Internet & Internet of Things (IIIIT);
 *                      All rights reserved.
 * http://www.caict.ac.cn
 * https://www.citln.cn/
 */
public class DefaultIDAdapter implements IDAdapter {

    private IIDManageServiceChannel channel;

    private BeanFactory factory;

    private MsgSettings msgSettings;

    private ValueHelper valueHelper = ValueHelper.getInstance();

    private int tcpTimeout = 60 * 1000;

    public DefaultIDAdapter() {
        this.factory = BeanFactory.getBeanFactory();
        msgSettings = new MsgSettings();
        msgSettings.setTruestyQuery(false);
        this.channel = factory.proxyChannel();
    }

    public DefaultIDAdapter(String adminIdentifier, int keyIndex, String privateKeyPem,String password, int cipher) {
        this.factory = BeanFactory.getBeanFactory();
        msgSettings = new MsgSettings();
        msgSettings.setTruestyQuery(false);
        String prefix = valueHelper.extraPrefix(adminIdentifier);
        PrefixSite prefixSite;
        try {
            prefixSite = resolveSiteByProxy(prefix);
        } catch (Exception e) {
           throw new IdentifierAdapterRuntimeException("instance idAdapter failed",e);
        }
        this.channel = factory.newChannel(prefixSite.getIp(),prefixSite.getPort(),prefixSite.getProtocolName());
        PrivateKey privateKey;
        try {
            privateKey = KeyConverter.fromPkcs8Pem(privateKeyPem,password);
        } catch (Exception e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, load key failed ",e);
        }
        try {
            this.channel.login(adminIdentifier,keyIndex,privateKey,cipher,msgSettings);
        } catch (IdentifierException e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, login failed ",e);
        }
    }

    @Override
    public IdentifierValue[] resolve(String identifier, String[] types, int[] indexes, boolean auth) throws IdentifierAdapterException {

        if (auth) {
            if (!channel.isLogin()) {
                throw new IdentifierAdapterException("not login,please login first");
            }
        }

        return resolve(identifier, types, indexes);
    }

    @Override
    public IdentifierValue[] resolve(String identifier, String[] types, int[] indexes) throws IdentifierAdapterException {

        try {
            BaseResponse lookupResp = channel.lookupIdentifier(identifier, null, null, msgSettings);
            if (lookupResp != null && lookupResp.responseCode == 1) {
                ResolutionResponse resolutionResponse = (ResolutionResponse) lookupResp;
                IdentifierValue[] values = resolutionResponse.getAllIDValues();
                return values;
            } else if (lookupResp instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) lookupResp;
                throw new IdentifierAdapterException(new StringBuilder("resolve error,response:").append(errorResponse.toString()).toString());
            } else {
                throw new IdentifierAdapterException(new StringBuilder("resolve error,response:").append(lookupResp.toString()).toString());
            }
        } catch (IdentifierException e) {
            throw new IdentifierAdapterException("resolve error", e);
        }

    }

    @Override
    public void setTcpTimeout(int timeout) {
        this.tcpTimeout = timeout;
    }

    @Override
    public int getTcpTimeout() {
        return tcpTimeout;
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            try {
                factory.channelManage().closeChannel(channel);
            } catch (IdentifierException e) {
                throw new IdentifierAdapterRuntimeException("close channel error", e);
            }
        }
    }

    protected PrefixSite resolveSiteByProxy(String prefixIdentifier) throws IdentifierAdapterException, IdentifierException {

        try(IDAdapter idAdapter = IDAdapterFactory.newInstance()){
            String[] types = {"HS_SITE"};
            IdentifierValue[] valueArray = idAdapter.resolve(prefixIdentifier, types, null);

            if (valueArray.length > 0) {
                IdentifierValue iv = valueArray[0];
                SiteInfo siteInfo = BytesObjConvertor.bytesCovertToSiteInfo(iv.getData());
                ServerInfo[] servers = siteInfo.servers;

                if (servers.length > 0) {

                    ServerInfo serverInfo = servers[0];
                    IDCommunicationItems tcpItem = findFirstByProtocolName(serverInfo, "TCP");
                    return new PrefixSite(serverInfo, tcpItem);

                } else {
                    throw new IdentifierAdapterException("cannot find servers");
                }
            } else {
                throw new IdentifierAdapterException("cannot find site type value");
            }
        } catch (IOException e) {
            throw new IdentifierAdapterException("idAdapter close error");
        }
    }

    private IDCommunicationItems findFirstByProtocolName(ServerInfo serverInfo, String protocolName) {
        IDCommunicationItems[] itemArray = serverInfo.communicationItems;

        String itemProtocolName;
        IDCommunicationItems item;
        IDCommunicationItems matchItem = null;
        for (int i = 0; i < itemArray.length; i++) {
            item = itemArray[i];
            itemProtocolName = IDCommunicationItems.getProtocolName(item.getProtocol());
            if (protocolName.equals(itemProtocolName)) {
                matchItem = item;
                break;
            }
        }
        return matchItem;
    }


}
