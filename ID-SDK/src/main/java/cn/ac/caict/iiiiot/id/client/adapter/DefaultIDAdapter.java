package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.convertor.BytesObjConvertor;
import cn.ac.caict.iiiiot.id.client.core.*;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.MsgSettings;
import cn.ac.caict.iiiiot.id.client.log.IDLog;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.id.client.utils.KeyConverter;
import org.apache.commons.logging.Log;
import org.apache.log4j.Logger;

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

    private Log logger = IDLog.getLogger(DefaultIDAdapter.class);
    private IIDManageServiceChannel channel;

    private ChannelFactory factory;

    private MsgSettings msgSettings;

    private ValueHelper valueHelper = ValueHelper.getInstance();

    // private int tcpTimeout = 60 * 1000;

    public DefaultIDAdapter() {
        this.factory = ChannelFactory.getChannelFactory();
        msgSettings = new MsgSettings();
        msgSettings.setTruestyQuery(false);
        this.channel = factory.proxyChannel();
    }

    public DefaultIDAdapter(String adminIdentifier, int keyIndex, String privateKeyPem, int cipher) {
        this.factory = ChannelFactory.getChannelFactory();
        msgSettings = new MsgSettings();
        msgSettings.setTruestyQuery(false);
        String prefix = valueHelper.extraPrefix(adminIdentifier);
        PrefixSiteInfo prefixSiteInfo;
        try {
            prefixSiteInfo = resolveSiteByProxy(prefix);
        } catch (Exception e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed", e);
        }
        logger.info("connect to server,ip: " + prefixSiteInfo.getIp() + ",port: " + prefixSiteInfo.getPort());
        this.channel = factory.newChannel(prefixSiteInfo.getIp(), prefixSiteInfo.getPort(), prefixSiteInfo.getProtocolName());
        PrivateKey privateKey;
        try {
            privateKey = KeyConverter.fromPkcs8Pem(privateKeyPem, null);
        } catch (Exception e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, load key failed ", e);
        }
        try {
            this.channel.login(adminIdentifier, keyIndex, privateKey, cipher, msgSettings);
        } catch (IdentifierException e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, login failed ,server is " + prefixSiteInfo.getIp() + ";" + prefixSiteInfo.getPort(), e);
        }
        if (!this.channel.isLogin()) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, login failed ,server is " + prefixSiteInfo.getIp() + ";" + prefixSiteInfo.getPort());
        }
    }

    public DefaultIDAdapter(String serverPrefix, String adminIdentifier, int keyIndex, String privateKeyPem, int cipher) {
        this.factory = ChannelFactory.getChannelFactory();
        msgSettings = new MsgSettings();
        msgSettings.setTruestyQuery(false);
        PrefixSiteInfo prefixSiteInfo;
        try {
            prefixSiteInfo = resolveSiteByProxy(serverPrefix);
        } catch (Exception e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed", e);
        }
        this.channel = factory.newChannel(prefixSiteInfo.getIp(), prefixSiteInfo.getPort(), prefixSiteInfo.getProtocolName());
        PrivateKey privateKey;
        try {
            privateKey = KeyConverter.fromPkcs8Pem(privateKeyPem, null);
        } catch (Exception e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, load key failed ", e);
        }
        try {
            this.channel.login(adminIdentifier, keyIndex, privateKey, cipher, msgSettings);
        } catch (IdentifierException e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, login failed ,server is " + prefixSiteInfo.getIp() + ";" + prefixSiteInfo.getPort(), e);
        }
        if (!this.channel.isLogin()) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, login failed, on exception, server is " + prefixSiteInfo.getIp() + ";" + prefixSiteInfo.getPort());
        }
    }

    public DefaultIDAdapter(String serverIp, int port, String adminIdentifier, int keyIndex, String privateKeyPem, int cipher) {
        this.factory = ChannelFactory.getChannelFactory();
        msgSettings = new MsgSettings();
        // msgSettings.setTruestyQuery(true);

        this.channel = factory.newChannel(serverIp, port, "TCP");
        PrivateKey privateKey;
        try {
            privateKey = KeyConverter.fromPkcs8Pem(privateKeyPem, null);
        } catch (Exception e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, load key failed ", e);
        }
        try {
            this.channel.login(adminIdentifier, keyIndex, privateKey, cipher, msgSettings);
        } catch (IdentifierException e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, login failed ,server is " + serverIp + ";" + port, e);
        }
        if (!this.channel.isLogin()) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, login failed, on exception ,server is " + serverIp + ";" + port);
        }
    }

    public DefaultIDAdapter(String serverPrefix) {
        this.factory = ChannelFactory.getChannelFactory();
        msgSettings = new MsgSettings();
        msgSettings.setTruestyQuery(false);
        PrefixSiteInfo prefixSiteInfo;
        try {
            prefixSiteInfo = resolveSiteByProxy(serverPrefix);
        } catch (Exception e) {
            throw new IdentifierAdapterRuntimeException("instance idAdapter failed, caused by: " + e.getMessage(), e);
        }
        this.channel = factory.newChannel(prefixSiteInfo.getIp(), prefixSiteInfo.getPort(), prefixSiteInfo.getProtocolName());
    }


    public DefaultIDAdapter(String serverIp, int port) {
        this.factory = ChannelFactory.getChannelFactory();
        msgSettings = new MsgSettings();
        msgSettings.setTruestyQuery(false);
        this.channel = factory.newChannel(serverIp, port, "TCP");
    }

    @Override
    public void addIdentifierValues(String identifier, IdentifierValue[] values) throws IdentifierAdapterException {
        try {
            BaseResponse response = channel.addIdentifierValues(identifier, values, msgSettings);
            if (response != null && response.responseCode == 1) {
                logger.debug("标识值添加成功:" + identifier);
            } else if (response instanceof ErrorResponse) {
                throw new IdentifierAdapterException(new StringBuilder("add value error,response:").append(response.toString()).toString());
            } else {
                throw new IdentifierAdapterException(new StringBuilder("add value error,response:").append(response.toString()).toString());
            }
        } catch (IdentifierException e) {
            throw new IdentifierAdapterException("add value error, caused by: " + e.getMessage(), e);
        }
    }

    @Override
    public void createIdentifier(String identifier, IdentifierValue[] values) throws IdentifierAdapterException {
        try {
            BaseResponse createResp = channel.createIdentifier(identifier, values, msgSettings);
            if (createResp != null && createResp.responseCode == 1) {
                logger.debug("标识创建成功:" + identifier);
            } else if (createResp instanceof ErrorResponse) {
                throw new IdentifierAdapterException(new StringBuilder("create error,response:").append(createResp.toString()).toString());
            } else {
                throw new IdentifierAdapterException(new StringBuilder("create error,response:").append(createResp.toString()).toString());
            }
        } catch (IdentifierException e) {
            throw new IdentifierAdapterException("create error, caused by: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteIdentifierValues(String identifier, IdentifierValue[] values) throws IdentifierAdapterException {
        int[] indexArray = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            indexArray[i] = values[i].getIndex();
        }
        try {
            BaseResponse response = channel.removeIdentifierValues(identifier, indexArray, msgSettings);
            if (response != null && response.responseCode == 1) {
                logger.debug("标识值删除成功:" + identifier);
            } else if (response instanceof ErrorResponse) {
                throw new IdentifierAdapterException(new StringBuilder("delete value error,response:").append(response.toString()).toString());
            } else {
                throw new IdentifierAdapterException(new StringBuilder("delete value error,response:").append(response.toString()).toString());
            }
        } catch (IdentifierException e) {
            throw new IdentifierAdapterException("delete value error, caused by: " + e.getMessage(), e);
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
            BaseResponse lookupResp = channel.lookupIdentifier(identifier, indexes, types, msgSettings);
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
            throw new IdentifierAdapterException("resolve error, caused by: " + e.getMessage(), e);

        }

    }

    @Override
    public IdentifierValue[] resolve(String identifier) throws IdentifierAdapterException {
        return resolve(identifier, null, null);
    }

    @Override
    public void updateIdentifierValues(String identifier, IdentifierValue[] values) throws IdentifierAdapterException {
        try {
            BaseResponse modifyResp = channel.modifyIdentifierValues(identifier, values, msgSettings);
            if (modifyResp != null && modifyResp.responseCode == 1) {
                logger.debug("标识值更新成功:" + identifier);
            } else if (modifyResp instanceof ErrorResponse) {
                throw new IdentifierAdapterException(new StringBuilder("update values error,response:").append(modifyResp.toString()).toString());
            } else {
                throw new IdentifierAdapterException(new StringBuilder("update values error,response:").append(modifyResp.toString()).toString());
            }

        } catch (IdentifierException e) {
            throw new IdentifierAdapterException("update values error, caused by: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteIdentifier(String identifier) throws IdentifierAdapterException {
        try {
            BaseResponse response = channel.deleteIdentifier(identifier, msgSettings);
            if (response != null && response.responseCode == 1) {
                logger.debug("标识删除成功:" + identifier);
            } else if (response instanceof ErrorResponse) {
                throw new IdentifierAdapterException(new StringBuilder("delete error,response:").append(response.toString()).toString());
            } else {
                throw new IdentifierAdapterException(new StringBuilder("delete error,response:").append(response.toString()).toString());
            }
        } catch (IdentifierException e) {
            throw new IdentifierAdapterException("delete error, caused by: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            try {
                factory.channelManage().closeChannel(channel);
            } catch (IdentifierException e) {
                throw new IdentifierAdapterRuntimeException("close channel error, caused by: " + e.getMessage(), e);
            }
        }
    }

    public PrefixSiteInfo resolveSiteByProxy(String prefixIdentifier) throws IdentifierAdapterException, IdentifierException {
        try (IDAdapter idAdapter = IDAdapterFactory.cachedInstance()) {
            String[] types = {"HS_SITE"};
            return resolveSiteByProxy(idAdapter, prefixIdentifier, types);
        } catch (IOException e) {
            throw new IdentifierAdapterException("idAdapter close error, caused by: " + e.getMessage());
        }
    }

    public PrefixSiteInfo resolveSiteByProxy(IDAdapter idAdapter, String prefixIdentifier, String[] types) throws IdentifierAdapterException, IdentifierException {
        // String[] types = {"HS_SITE","HS_SITE.PREFIX"};
        IdentifierValue[] valueArray = idAdapter.resolve(prefixIdentifier, types, null);
        if (valueArray.length > 0) {
            IdentifierValue iv = valueArray[0];
            SiteInfo siteInfo = BytesObjConvertor.bytesCovertToSiteInfo(iv.getData());
            ServerInfo[] servers = siteInfo.servers;

            if (servers.length > 0) {

                ServerInfo serverInfo = servers[0];
                IDCommunicationItems tcpItem = findFirstByProtocolName(serverInfo, "TCP");
                return new PrefixSiteInfo(siteInfo, serverInfo, tcpItem);

            } else {
                throw new IdentifierAdapterException("cannot find servers");
            }
        } else {
            throw new IdentifierAdapterException("cannot find site type value");
        }
    }

    protected IDCommunicationItems findFirstByProtocolName(ServerInfo serverInfo, String protocolName) {
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

    public IIDManageServiceChannel getChannel() {
        return channel;
    }

    public void setChannel(IIDManageServiceChannel channel) {
        this.channel = channel;
    }

    public ChannelFactory getFactory() {
        return factory;
    }


}
