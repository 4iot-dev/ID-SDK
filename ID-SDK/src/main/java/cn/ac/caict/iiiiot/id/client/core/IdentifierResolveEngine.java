package cn.ac.caict.iiiiot.id.client.core;

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
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

import cn.hutool.core.util.RandomUtil;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import com.google.gson.Gson;

import cn.ac.caict.iiiiot.id.client.convertor.BytesMsgConvertor;
import cn.ac.caict.iiiiot.id.client.convertor.MsgBytesConvertor;
import cn.ac.caict.iiiiot.id.client.log.IDLog;
import cn.ac.caict.iiiiot.id.client.utils.Common;
import cn.ac.caict.iiiiot.id.client.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;
import cn.ac.caict.iiiiot.id.client.utils.Util;

public class IdentifierResolveEngine {
	private Socket longConnSocket = null;
	private OutputStream longConnOut = null;
	private InputStream longConnIn = null;
	private boolean haveSiteInfo = false;
	private int maxUDPDataSize = Common.MAX_UDP_DATA_SIZE;
	private int tcpTimeout = 60 * 60;
	private SiteInfo siteInfo;
	private Random messageIDMaker;
	private static final int RETRY_TIMES = 3;
	private Map<String, Object> config = null;
	private Log logger = IDLog.getLogger(IdentifierResolveEngine.class);
	private int sessionId;

	public SiteInfo getSiteInfo(){
		return siteInfo;
	}

	private void initSiteInfo(String ip, int port, String protocol) throws IdentifierException {
		byte iPro = IDCommunicationItems.TS_IDF_TCP;
		if (protocol == null || protocol.equalsIgnoreCase("tcp"))
			iPro = IDCommunicationItems.TS_IDF_TCP;
		else if (protocol.equalsIgnoreCase("udp"))
			iPro = IDCommunicationItems.TS_IDF_UDP;
		byte[] b = Util.convertIPStr2Bytes(ip);

		IDCommunicationItems i = new IDCommunicationItems(IDCommunicationItems.ST_ADMIN_AND_QUERY, iPro, port);
		ServerInfo s = new ServerInfo();
		s.setIpBytes(b);
		s.communicationItems = new IDCommunicationItems[] { i };

		SiteInfo si = new SiteInfo();
		si.servers = new ServerInfo[] { s };
		siteInfo = si;

	}

	public void updateSiteInfo() {
		SiteRequest request = new SiteRequest();
		SiteInfo si = new SiteInfo();
		try {
			BaseResponse response = processRequest(request, null);
			if (response != null && response instanceof SiteResponse) {
				si = ((SiteResponse) response).getSiteInfo();
				siteInfo = si;
				haveSiteInfo = true;
			} else if(response != null && response instanceof ErrorResponse){
				ErrorResponse errorResp = (ErrorResponse)response;
				logger.error(errorResp.toString());
			}
		} catch (IdentifierException e) {
			e.printStackTrace();
			logger.error("SiteInfo获取失败，将使用默认站点信息配置");
		}
	}

	int preferredProtocols[] = { IDCommunicationItems.TS_IDF_TCP, IDCommunicationItems.TS_IDF_UDP };

	private void loadConfig() throws IOException {
		logger.info("begin----loadConfig()---");
		String path = System.getProperty("user.dir");
		File fConfig = new File(path, "src/config.json");
		logger.debug("配置文件路径：" + fConfig.getAbsolutePath());
		InputStream is = null;
		BufferedReader br = null;
		Reader input = null;
		try {
			if (fConfig.exists()) {
				is = new BufferedInputStream(new FileInputStream(fConfig));
			} else {
				is = IdentifierResolveEngine.class
						.getResourceAsStream("/conf/config.json");
				if (is == null) {
					logger.error("读取ID-SDK的配置文件失败");
				}
			}
			if (is == null) {
				logger.error("资源获取失败");
				return;
			}
			input = new InputStreamReader(is);
			br = new BufferedReader(input);
			String s = "";
			StringBuilder sb = new StringBuilder();
			while ((s = br.readLine()) != null) {
				sb.append(s.trim());
			}
			Gson gson = new Gson();
			config = gson.fromJson(sb.toString(), Map.class);
			logger.debug("配置信息：" + config.toString());
			logger.info("end----loadConfig()---");
		} catch (IOException e) {
			throw new IOException();
		} finally {
			if (input != null)
				input.close();
			if (br != null)
				br.close();
			if (is != null)
				is.close();
		}
	}

	private void createConnection(String ip, int port, String protocol) throws IdentifierException {
		if (!Common.IPV4_REGEX.matcher(ip).matches() && !Common.IPV6_COMPRESS_REGEX.matcher(ip).matches()
				&& !Common.IPV6_STD_REGEX.matcher(ip).matches())
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ILLEGAL_IP, "ip非法");
		if (!Common.PORT_REGEX.matcher(Integer.toString(port)).matches())
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ILLEGAL_PORT, "端口非法");
		if (protocol == null || !"tcp".equalsIgnoreCase(protocol) && !"udp".equalsIgnoreCase(protocol))
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_UNKNOWN_PROTOCOL, "暂不支持该协议");
		try {
			messageIDMaker = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException nse) {
			messageIDMaker = new SecureRandom();
		}
		messageIDMaker.setSeed(System.currentTimeMillis());
		this.sessionId = RandomUtil.randomInt(10000, 1000000000);
		initSiteInfo(ip, port, protocol);
		if (!"TCP".equalsIgnoreCase(protocol))
			return;
		SocketChannel sockChannel = null;
		try {
			sockChannel = SocketChannel.open();
			// 打开socket通道
			longConnSocket = sockChannel.socket();
			longConnSocket.setSoLinger(false, 0);
			// 直接指向配置文件中的服务器地址
			longConnSocket.connect(new InetSocketAddress(ip, port), 0);
			// 输出流
			longConnOut = longConnSocket.getOutputStream();
			// 输入流
			longConnIn = new BufferedInputStream(longConnSocket.getInputStream());
		} catch (IOException e) {
			try {
				longConnIn.close();
			} catch (Exception e1) {
			}
			try {
				longConnOut.close();
			} catch (Exception e3) {
			}
			try {
				longConnSocket.close();
			} catch (Exception e2) {
			}
			try {
				sockChannel.close();
			} catch (IOException e3) {
			}
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_LONGCONNSOCKET_CREATE_FAILED,
					IdentifierException.getCodeDescription(ExceptionCommon.EXCEPTIONCODE_LONGCONNSOCKET_CREATE_FAILED));
		}
		updateSiteInfo();
	}

	public IdentifierResolveEngine(String ip, int port, String protocol) throws IdentifierException {
		createConnection(ip, port, protocol);
	}

	public IdentifierResolveEngine() throws IOException, IdentifierException {
		loadConfig();
		String ip = (String) config.get("ip");
		logger.debug("ip:" + ip);
		int port = Integer.parseInt((String) config.get("port"));
		logger.debug("port:" + port);
		String protocol = (String) config.get("protocol");
		logger.debug("protocol:" + protocol);
		createConnection(ip, port, protocol);
	}

	@Override
	public void finalize() throws Throwable {
		try {
			if (longConnIn != null)
				longConnIn.close();
		} catch (Exception e1) {
		}
		try {
			longConnOut.close();
		} catch (Exception e3) {
		}
		try {
			if (longConnSocket != null)
				longConnSocket.close();
		} catch (Exception e2) {
		}
		super.finalize();
	}

	public void setTcpTimeout(int newTcpTimeout) {
		this.tcpTimeout = newTcpTimeout;
	}

	public int getTcpTimeout() {
		return this.tcpTimeout;
	}

	public BaseResponse processRequest(BaseRequest req, InetAddress caller) throws IdentifierException {
		req.sessionId = this.sessionId;
		if (req.opCode == 2 && haveSiteInfo && siteInfo!=null){
			return new SiteResponse(siteInfo);
		}
		return sendResquestToIDService(req, siteInfo);
	}

	public BaseResponse sendResquestToIDService(BaseRequest req, SiteInfo sites) throws IdentifierException {
		req.sessionId = this.sessionId;
		if (sites == null)
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_FOUND_NO_SERVICE, "未找到站点信息");
		BaseResponse response = sendResquestToIDSites(req, sites);
		return response;
	}

	public BaseResponse sendResquestToIDSites(BaseRequest req, SiteInfo sites) throws IdentifierException {
		req.sessionId = this.sessionId;
		BaseResponse response = null;
		for (int p = 0; p < preferredProtocols.length; p++) {
			response = sendResquestToIDSiteViaProtocol(req, sites, p);
			if (response != null)
				return response;
		}
		return response;
	}

	public BaseResponse sendResquestToIDSiteViaProtocol(BaseRequest req, SiteInfo siteInfo, int protocol)
			throws IdentifierException {
		req.sessionId = this.sessionId;
		// 1.获取一个服务器server信息 2.调用服务器方法
		ServerInfo[] servInfos = siteInfo.servers;
		if (servInfos.length < 1) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ID_SYS_SERVER_ERROR, "server number is 0");
		}
		int theProtocol = siteInfo.servers[0].communicationItems[0].protocol;
		if (protocol != theProtocol) {
			logger.debug("期望使用" + (theProtocol == IDCommunicationItems.TS_IDF_UDP ? "UDP" : "TCP") + "类型处理");
			return null;
		}

		BaseResponse response = null;

		for (int i = 0; i < siteInfo.servers.length; i++) {
			response = sendResquestToIDServerInfo(req, servInfos[i], protocol);
			if (response != null)
				return response;
		}
		return response;
	}

	public BaseResponse sendResquestToIDServerInfo(BaseRequest req, ServerInfo server, int protocol)
			throws IdentifierException {
		req.sessionId = this.sessionId;
		IDCommunicationItems itemsWithProtocol = server.findIDCommunicationItemsByProtocol(protocol, req);
		if (itemsWithProtocol == null) {
			String strPro = "";
			if (protocol == 0) {
				strPro = "TCP";
			} else if (protocol == 1) {
				strPro = "UDP";
			} else {
				strPro = "未知协议";
				logger.info("服务中没有该协议：" + strPro);
			}
			return null;
		}
		BaseResponse response = sendRequestToIDCommunicationItems(req, server, itemsWithProtocol);

		if (response != null && response.getClass() == ChallengeResponse.class
				&& response.opCode == MessageCommon.OC_LOGIN_ID_SYSTEM
				&& response.responseCode == MessageCommon.RC_AUTHENTICATION_NEEDED) {
			logger.info("登录标识系统发起质询---begin");
			if (req.authInfo == null) {
				logger.error("response为挑战响应，身份认证信息不能为空。req.authInfo=" + req.authInfo);
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_UNABLE_TO_AUTHENTICATE,
						"No authentication info provided");
			}
			logger.info("---------------登录操作的挑战响应:" + response);
			ChallengeResponse challResponse = (ChallengeResponse) response;
			byte sig[] = req.authInfo.authenticateAction(challResponse, req);
			int challengeSessionID = response.sessionId;

			response = null;
			ChallengeAnswerRequest answer = new ChallengeAnswerRequest(req.authInfo.getTypeAuth(),
					req.authInfo.getUserIdentifier(), req.authInfo.getUserIndex(), sig, req.authInfo);
			answer.populateMsgSettings(req);
			answer.originalRequest = req;
			answer.majorProtocolVersion = challResponse.majorProtocolVersion;
			answer.minorProtocolVersion = challResponse.minorProtocolVersion;
			answer.setSupportedProtocolVersion();
			answer.sessionId = challengeSessionID;
			//opcode=200时不再进行消息凭据验证，因为2001登录请求过程中已有验证
			answer.bCertify = false;
			logger.debug("ChallengeAnswerRequest请求信息:" + answer + " requestid:" + answer.requestId);
			response = sendRequestToIDCommunicationItems(answer, server, itemsWithProtocol);
			logger.debug("发送ChallengeAnswerRequest请求的响应结果:response=" + response);
			logger.info("登录标识系统质询---end");
		}
		return response;
	}

	public BaseResponse sendRequestToIDCommunicationItems(BaseRequest req, ServerInfo server,
														  IDCommunicationItems items) throws IdentifierException {
		req.sessionId = this.sessionId;
		if(siteInfo != null && siteInfo.servers != null && siteInfo.servers.length > 0){
			req.serverPubKeyBytes = siteInfo.servers[0].publicKey;
		}
		InetAddress addr = server.getInetAddress();
		int port = items.port;
		BaseResponse response = null;
		switch (items.protocol) {
			case IDCommunicationItems.TS_IDF_TCP:
				response = sendRequestWithTCP(req, addr, port);
				break;
			case IDCommunicationItems.TS_IDF_UDP:
				response = sendRequestWithUDP(req, addr, port);
				break;
			default:
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_UNKNOWN_PROTOCOL,
						"unknown protocol: " + items.protocol);
		}

		if (response != null) {
			if (response.responseCode == MessageCommon.RC_ERROR) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_ID_SYS_SERVER_ERROR,
						Util.decodeString(((ErrorResponse) response).message));
			} else if (response.expiration < System.currentTimeMillis() / 1000) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_GOT_EXPIRED_MESSAGE);
			}
			return response;
		} else {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INTERNAL_ERROR);
		}
	}

	private BaseResponse sendRequestWithUDP(BaseRequest req, InetAddress addr, int port) throws IdentifierException {
		logger.info("sendRequestWithUDP--method--begin");
		req.sessionId = this.sessionId;
		DatagramSocket udpSocket = null;
		try {
			udpSocket = new DatagramSocket(port);

			for (int t = 1; t <= RETRY_TIMES; t++) {
				logger.debug("重试次数：" + t + "次。");
				long timeout = t * 1000L;
				sendDataByUdp(req, addr, port, udpSocket);
				timeout = +System.currentTimeMillis();
				BaseResponse response = null;
				try {
					response = receiveDataByUdp(req, addr, port, timeout, udpSocket);
				} catch (IdentifierException e) {
					logger.error(e);
					if (t != RETRY_TIMES)
						continue;
					else
						throw new IdentifierException(e.getExceptionCode(),
								IdentifierException.getCodeDescription(e.getExceptionCode()));
				}
				if (response == null)
					continue;
				return response;
			}
		} catch (SocketException e) {
			e.printStackTrace();
			logger.info("sendRequestWithUDP--method--" + e.getMessage());
		} finally {
			if (udpSocket != null) {
				try {
					udpSocket.close();
				} catch (Exception e) {
					logger.error("DatagramSocket关闭失败");
				}
			}
			logger.info("sendRequestWithUDP--method--end");
		}
		return null;
	}

	// 1.构建要发送的数据 2.发送数据
	private void sendDataByUdp(BaseRequest req, InetAddress addr, int port, DatagramSocket udpSocket)
			throws IdentifierException {
		logger.info("sendDataByUdp--method--begin");
		req.sessionId = this.sessionId;
		DatagramPacket[] dpArray = getPacketArray(req, addr, port);
		try {
			for (int n = 0; n < dpArray.length; n++) {
				udpSocket.send(dpArray[n]);
			}
			logger.info("sendDataByUdp--method--end");
		} catch (IOException e) {
			logger.error("sendDataByUdp--method--error:" + e.getMessage());
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INTERNAL_ERROR,
					String.valueOf(e) + " sending UDP request to " + Util.rfcIpRepresentation(addr));
		}
	}

	/**
	 * 说明：接收UDP传输的消息 1.解析信息，获取到消息长度信息 2.根据长度信息构建容器 3.在未超时的时间范围内获取每一个分包，存入指定容器
	 * 4.容器内消息接收完整以后，转码为BaseResponse
	 *
	 * @param req
	 *            请求消息
	 * @param addr
	 *            服务IP地址
	 * @param port
	 *            服务端口号
	 * @param timeout
	 *            超时时间
	 * @param udpSocket
	 * @return 返回接收到响应消息
	 * @throws IdentifierException
	 */
	private BaseResponse receiveDataByUdp(BaseRequest req, InetAddress addr, int port, long timeout,
										  DatagramSocket udpSocket) throws IdentifierException {
		logger.info("receiveDataByUdp--method--begin");
		req.sessionId = this.sessionId;
		byte respMsg[] = null;
		boolean bAllPackets = false;
		int packetSum = 0;
		MsgEnvelope rcvEnvelope = new MsgEnvelope();
		BaseResponse response = null;
		int packageNum = 0;
		while (!bAllPackets && System.currentTimeMillis() <= timeout) {
			byte[] receiveBuf = new byte[maxUDPDataSize + Common.MESSAGE_ENVELOPE_SIZE];// 接收到的每个包size
			DatagramPacket dpReceive = new DatagramPacket(receiveBuf, receiveBuf.length);
			try {
				udpSocket.receive(dpReceive);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (dpReceive.getLength() <= 0)
				continue;
			// 有数据，解包,先解信封
			byte[] dataReceived = dpReceive.getData();
			int dataReveicedLength = dpReceive.getLength();
			BytesMsgConvertor.bytesConvertIntoEnvelope(dataReceived, rcvEnvelope);
			// 如果不是当前要解的包，丢掉
			if (rcvEnvelope.requestId != req.requestId)
				continue;
			// 计算需要接收数据包数量
			if (packetSum == 0) {
				logger.debug("当前信息中记录的消息长度：" + rcvEnvelope.messageLength);
				packetSum = rcvEnvelope.messageLength / maxUDPDataSize;
				if (rcvEnvelope.messageLength % maxUDPDataSize != 0) {
					packetSum++;
				}
				respMsg = new byte[rcvEnvelope.messageLength];
			}
			// 把数据包中数据放到指定位置，比如messageSequenceNum为3，就放到第4个包的起始位置
			System.arraycopy(dataReceived, Common.MESSAGE_ENVELOPE_SIZE, respMsg,
					rcvEnvelope.messageSequenceNum * maxUDPDataSize, dataReveicedLength - Common.MESSAGE_ENVELOPE_SIZE);

			bAllPackets = (packetSum == ++packageNum);
			if (bAllPackets) {
				logger.info("receiveDataByUdp--method--end");
				response =(BaseResponse) BytesMsgConvertor.bytesConvertInfoMessage(respMsg, 0, rcvEnvelope);
				if (req.bCertify)
					checkMessageCredential(req, response);
				return response;
			}
		}
		logger.info("receiveDataByUdp--method--end");
		return response;
	}

	/**
	 * 说明：将消息体分成若干数据包
	 *
	 * @param req
	 *            形成数据包的消息体
	 * @param addr
	 *            ip地址信息
	 * @param port
	 *            服务端口号
	 */
	private DatagramPacket[] getPacketArray(BaseRequest req, InetAddress addr, int port) throws IdentifierException {
		logger.info("getPacketArray--method--begin");
		req.sessionId = this.sessionId;
		MsgEnvelope mEnvlp = createEnvelope(req);
		// 获取编码消息（包括头文件和签名，但不包括信封），为创建一组udp数据包做准备
		byte[] requestBuf = req.getEncodedMessage();
		if (requestBuf.length == 0) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INTERNAL_ERROR, "Cannot send empty request");
		}
		int num = requestBuf.length / maxUDPDataSize;
		logger.info("requestBuf.length=" + requestBuf.length);
		if (requestBuf.length % maxUDPDataSize != 0) {
			num += 1;
		}
		logger.debug("数据包组个数num:" + num);
		mEnvlp.truncated = (num > 1) ? true : false;
		DatagramPacket[] pakets = new DatagramPacket[num];
		int offset = 0;
		for (int n = 0; n < num; n++) {
			mEnvlp.messageSequenceNum = n;
			int curPacketSize = Math.min(maxUDPDataSize, mEnvlp.messageLength);
			byte[] buf = new byte[Common.MESSAGE_ENVELOPE_SIZE + curPacketSize];
			MsgBytesConvertor.envelopeConvertInfoBytes(mEnvlp, buf);
			offset = n * maxUDPDataSize;
			System.arraycopy(requestBuf, offset, buf, Common.MESSAGE_ENVELOPE_SIZE, requestBuf.length);
			DatagramPacket dp = new DatagramPacket(buf, buf.length, addr, port);
			pakets[n] = dp;
		}
		logger.info("getPacketArray--method--end");
		return pakets;
	}

	/**
	 * 说明：为请求消息创建信封对象（定义信封的各标志位信息）
	 *
	 * @param req 即将发送的请求消息
	 * @return 返回信封对象
	 * @throws IdentifierException
	 */
	private MsgEnvelope createEnvelope(BaseRequest req) throws IdentifierException {
		logger.info("createEnvelope--method--begin");
		req.sessionId = this.sessionId;
		MsgEnvelope mEnvlp = new MsgEnvelope();
		mEnvlp.protocolMajorVersion = req.majorProtocolVersion;
		mEnvlp.protocolMinorVersion = req.minorProtocolVersion;
		mEnvlp.suggestMajorProtocolVersion = req.suggestMajorProtocolVersion;
		mEnvlp.suggestMinorProtocolVersion = req.suggestMinorProtocolVersion;
		mEnvlp.sessionId = req.sessionId;
		if (req.requestId <= 0) {
			int nInt = messageIDMaker.nextInt();
			if (nInt != Integer.MIN_VALUE)
				mEnvlp.requestId = Math.abs(nInt);
			req.requestId = mEnvlp.requestId;
		} else {
			mEnvlp.requestId = req.requestId;
		}
		mEnvlp.messageLength = req.getEncodedMessage().length;
		logger.info("createEnvelope--method--end");
		return mEnvlp;
	}

	/**
	 * 说明：tcp使用长连接收发数据 1.发送数据:信封+请求消息 2.接收数据:接收信息，接收消息体
	 *
	 * @param req
	 *            请求消息
	 * @param addr
	 *            请求ip对象
	 * @param port
	 *            请求端口
	 * @return 发送TCP消息后的响应数据
	 */
	private BaseResponse sendRequestWithTCP(BaseRequest req, InetAddress addr, int port) throws IdentifierException {
		logger.info("sendRequestWithTCP--method--begin ["+req+"],requestId:"+req.requestId);

		req.sessionId = this.sessionId;

		MsgEnvelope sndEnv = createEnvelope(req);
		req.encodedMessage = null;
		byte[] requestMsgBuf = req.getEncodedMessage();
		sndEnv.messageLength = requestMsgBuf.length;
		byte[] evelopeBuf = new byte[Common.MESSAGE_ENVELOPE_SIZE];
		MsgBytesConvertor.envelopeConvertInfoBytes(sndEnv, evelopeBuf);
		byte[] sendMsg = Util.concat(evelopeBuf, requestMsgBuf);
		try {
			longConnOut.write(sendMsg);
		} catch (IOException e) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_CANNOT_CONNECT_TO_ID_SYS_SERVER,
					" 向 " + Util.rfcIpPortRepresentation(addr, port) + "发送TCP请求");
		}
		logger.info("消息发送完成！开始接收响应数据...");
		BaseResponse response = null;
		while (true) {
			int offsize = 0;
			int n = 0;

			try {
				while (n < Common.MESSAGE_ENVELOPE_SIZE
						&& (offsize = longConnIn.read(evelopeBuf, 0, Common.MESSAGE_ENVELOPE_SIZE)) > 0) {
					n += offsize;
				}
				MsgEnvelope rvcEnv = new MsgEnvelope();
				BytesMsgConvertor.bytesConvertIntoEnvelope(evelopeBuf, rvcEnv);
				byte[] receiveMsg = new byte[rvcEnv.messageLength];
				n = 0;
				while ((n < rvcEnv.messageLength)
						&& (offsize = longConnIn.read(receiveMsg, n, rvcEnv.messageLength - n)) > 0) {
					n += offsize;
				}
				response = (BaseResponse) BytesMsgConvertor.bytesConvertInfoMessage(receiveMsg, 0, rvcEnv);
				if(response instanceof SiteResponse){
					SiteResponse siteRes = (SiteResponse)response;
					if(siteRes.getSiteInfo() != null && siteRes.getSiteInfo().servers.length > 0)
						req.serverPubKeyBytes = siteRes.getSiteInfo().servers[0].publicKey;
				}
				if (req.bCertify)
					checkMessageCredential(req, response);
				if (!response.continuous) {
					logger.debug("接收响应数据结束：response--\n" + response);
					return response;
				}
			} catch (IOException e) {
//				e.printStackTrace();
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_CANNOT_CONNECT_TO_ID_SYS_SERVER,
						"Error talking to " + Util.rfcIpRepresentation(addr), e);
			} finally {
				req.socketRef.set(null);
				logger.info("sendRequestWithTCP--method--end");
			}
		}
	}

	private void checkMessageCredential(BaseRequest request, BaseResponse response) throws IdentifierException {
		logger.info("checkMessageCredential--method--begin");
		request.sessionId = this.sessionId;
		if (request.returnRequestDigest) {
			System.err.println("rdHashType---------:"+response.rdHashType);
			byte[] requestDigest = Util.doDigest(response.rdHashType, request.getEncodedMessageBody());
			if (!Util.equalsBytes(requestDigest, response.requestDigest)) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_SECURITY_ALERT, "请求消息与响应消息摘要不同，发出安全警告！！");
			}
		}
		if (request.serverPubKeyBytes == null) {
			throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_SECURITY_ALERT,"没有用于验证凭据的公钥！！");
		}

		PublicKey pubKey;
		boolean sucessGetPubkey = false;
		try {
			pubKey = Util.getPublicKeyFromBytes(request.serverPubKeyBytes, 0);
			sucessGetPubkey = true;
			if (response.signature == null || response.signature.length <= 0) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MISSING_OR_INVALID_SIGNATURE,"签名数据不存在无法验证！！");
			}
			if (!response.checkCredential(pubKey)) {
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MISSING_OR_INVALID_SIGNATURE, "消息凭据验证失败！！");
			}
		} catch (Exception e) {
			if(!sucessGetPubkey)
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_INVALID_VALUE, "无法获取公钥对象！！", e);
			else
				throw new IdentifierException(ExceptionCommon.EXCEPTIONCODE_MISSING_OR_INVALID_SIGNATURE,"无法验证消息凭据异常如下: " + response, e);
		}
		logger.info("checkMessageCredential--method--end");
	}
}
