package cn.ac.caict.iiiiot.idisc.service.impl;
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
import java.security.PrivateKey;
import java.util.Date;
import org.apache.commons.logging.Log;

import cn.ac.caict.iiiiot.idisc.core.AddValueRequest;
import cn.ac.caict.iiiiot.idisc.core.BaseRequest;
import cn.ac.caict.iiiiot.idisc.core.BaseResponse;
import cn.ac.caict.iiiiot.idisc.core.CreateIdentifierRequest;
import cn.ac.caict.iiiiot.idisc.core.DeleteIdentifierRequest;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.core.IdentifierResolveEngine;
import cn.ac.caict.iiiiot.idisc.core.LoginIdisRequest;
import cn.ac.caict.iiiiot.idisc.core.ModifyValueRequest;
import cn.ac.caict.iiiiot.idisc.core.RemoveValueRequest;
import cn.ac.caict.iiiiot.idisc.core.ResolutionRequest;
import cn.ac.caict.iiiiot.idisc.core.SiteRequest;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.data.MsgSettings;
import cn.ac.caict.iiiiot.idisc.log.IdisLog;
import cn.ac.caict.iiiiot.idisc.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.idisc.security.PubKeyAuthentication;
import cn.ac.caict.iiiiot.idisc.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.idisc.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.idisc.utils.MessageCommon;
import cn.ac.caict.iiiiot.idisc.utils.Util;

public class IDManageServiceChannelImpl implements IIDManageServiceChannel{
	private IdentifierResolveEngine resolverEngine = null;
	private Log log = IdisLog.getLogger(IDManageServiceChannelImpl.class);
	private boolean login = false;
	private String userIdentifier;

	public String getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public IdentifierResolveEngine getResolverEngine() {
		return resolverEngine;
	}

	public void setResolverEngine(IdentifierResolveEngine resolverEngine) {
		this.resolverEngine = resolverEngine;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public IDManageServiceChannelImpl(IdentifierResolveEngine resolverEngine) {
		this.resolverEngine = resolverEngine;
	}
	
	@Override
	public BaseResponse login(String identifier, int index, String privakeyFilePath, String password, int rdType)
			throws IdentifierException {
		log.info("login---method---begin");
		if(identifier == null || index<1 || privakeyFilePath == null){
			throw new IdentifierException(ExceptionCommon.INVALID_PARM,"identifier=" + identifier + ",identifier不能为空");
		}
		if(index < 1)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM,"index=" + index + ",index必须为正整数");
		if(rdType != 1 && rdType != 2 && rdType != 3)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM,"不支持该类型摘要rdType=" + rdType);
		PrivateKey privKey = Util.getPrivateKeyFromFile(privakeyFilePath, null);
		if (privKey == null) {
			IdisLog.getLogger(IDManageServiceChannelImpl.class).info("private key is null,maybe file is not exit,please check it!");
			throw new IdentifierException(ExceptionCommon.INVALID_PARM,"privakeyFilePath文件可能不存在");
		}
		byte[] userIdentifier = Util.encodeString(identifier);
		AbstractAuthentication authInfo = new PubKeyAuthentication(userIdentifier, index, privKey);
		BaseResponse response = null;
		LoginIdisRequest loginReq = new LoginIdisRequest(userIdentifier, index, authInfo);
		loginReq.rdHashType = (byte) rdType;
		try {
			response = resolverEngine.processRequest(loginReq, null);
			if(response != null && response.responseCode == 1){
				setUserIdentifier(identifier);
				setLogin(true);
				log.info(identifier + ":" + index + "登录成功！");
			} else {
				log.error(identifier + ":" + index + "登录失败！");
			}
			log.info("login---method---end");
			return response;
		} catch (IdentifierException e) {
			e.printStackTrace();
			throw new IdentifierException(e.getExceptionCode(),
					IdentifierException.getCodeDescription(e.getExceptionCode()));
		}
	}

	public void logout() throws IdentifierException {
		try {
			resolverEngine.finalize();
		}catch (Throwable e){
			log.error(e.getMessage());
			int errCode = ExceptionCommon.EXCEPTIONCODE_DISCONN_FAILED;
			throw new IdentifierException(errCode,IdentifierException.getCodeDescription(errCode));
		} finally {
			resolverEngine = null;
			setLogin(false);
			setUserIdentifier(null);
		}
	}
	
	@Override
	public BaseResponse lookupIdentifier(String identifier, int[] indexes, String[] types, MsgSettings settings)
			throws IdentifierException {
		log.info("lookupIdentifier---method---begin");
		if(resolverEngine == null)
			throw new IdentifierException(ExceptionCommon.IDENTIFIER_ENGINE_ERROR, "解析引擎异常！");
		if(identifier == null || "".equals(identifier))
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "标识不能为空");
		if(settings == null)
			settings = new MsgSettings();
		byte[][] reqTypes = null;
		if(types != null){
			reqTypes = new byte[types.length][];
			for(int i=0; i<types.length; i++){
				reqTypes[i] = Util.encodeString(types[i]);
			}
		}
		byte[] id = Util.encodeString(identifier);
		ResolutionRequest req = new ResolutionRequest(id, reqTypes, indexes, null);
		setMessageSettings(req,settings);
		long beforeTime = new Date().getTime();
		System.out.println("before processRequest req:" + req);
		BaseResponse response = resolverEngine.processRequest(req,null);
		System.out.println("after processRequest response:" + response);
		long afterTime = new Date().getTime();
		long time = afterTime - beforeTime;
		System.out.println("查询耗时(毫秒):" + time);
		log.info("lookupIdentifier---method---end");
		return response;
	}

	@Override
	public BaseResponse createIdentifier(String identifier, IdentifierValue[] values, MsgSettings settings)
			throws IdentifierException {
		log.info("createIdentifierRequest---method---begin");
		if(resolverEngine == null){
			throw new IdentifierException(ExceptionCommon.IDENTIFIER_ENGINE_ERROR, "解析引擎异常!");
		}
		if(identifier == null)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "identifier不能为空");
		if(settings == null)
			settings = new MsgSettings();
		
		CreateIdentifierRequest req = new CreateIdentifierRequest(Util.encodeString(identifier), values, null);
		setMessageSettings(req, settings);
		long beforeTime = new Date().getTime();
		BaseResponse response = resolverEngine.processRequest(req,null);
		long afterTime = new Date().getTime();
		long time = afterTime - beforeTime;
		System.out.println("创建标识耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info("标识创建成功!");
		}else{
			log.error("标识创建失败!");				
		}
		log.info("createIdentifierRequest---method---end");
		return response;
	}

	@Override
	public BaseResponse deleteIdentifier(String identifier, MsgSettings settings) throws IdentifierException {
		log.info("deleteIdentifierRequest---method---begin");
		if(resolverEngine == null){
			throw new IdentifierException(ExceptionCommon.IDENTIFIER_ENGINE_ERROR, "解析引擎异常!");
		}
		if(identifier == null)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "identifier不能为空");
		if(settings == null)
			settings = new MsgSettings();
		BaseResponse response = null;
		DeleteIdentifierRequest req = new DeleteIdentifierRequest(Util.encodeString(identifier), null);
		setMessageSettings(req, settings);
		long beforeTime = new Date().getTime();
		response = resolverEngine.processRequest(req,null);
		long afterTime = new Date().getTime();
		long time = afterTime - beforeTime;
		System.out.println("删除标识耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info("删除标识成功！");
		}else {
			log.error("删除标识失败！");
		}
		log.info("deleteIdentifierRequest---method---end");
		return response;
	}
	
	@Override
	public BaseResponse addIdentifierValues(String identifier, IdentifierValue[] values, MsgSettings settings)
			throws IdentifierException {
		log.info("addIdentifierValueRequest---method---begin");
		if(resolverEngine == null){
			throw new IdentifierException(ExceptionCommon.IDENTIFIER_ENGINE_ERROR, "解析引擎异常!");
		}
		if(identifier == null)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "identifier不能为空");
		if(values == null)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "values不能为空");
		BaseResponse response = null;
		AddValueRequest req = new AddValueRequest(Util.encodeString(identifier), values, null);
		setMessageSettings(req, settings);
		long beforeTime = new Date().getTime();
		response = resolverEngine.processRequest(req,null);
		long afterTime = new Date().getTime();
		long time = afterTime - beforeTime;
		System.out.println("添加标识值耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info("添加标识值成功!");
		}else{
			log.error("添加标识值失败!");
		}
		log.info("addIdentifierValueRequest---method---end");
		return response;
	}

	@Override
	public BaseResponse modifyIdentifierValues(String identifier, IdentifierValue[] values, MsgSettings settings)
			throws IdentifierException {
		log.info("modifyIdentifierRequest---method---begin");
		if(resolverEngine == null){
			throw new IdentifierException(ExceptionCommon.IDENTIFIER_ENGINE_ERROR, "解析引擎异常!");
		}
		if(identifier == null)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "identifier不能为空");
		if(values == null)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "values不能为空");
		ModifyValueRequest req = new ModifyValueRequest(Util.encodeString(identifier), values, null);
		long beforeTime = new Date().getTime();
		BaseResponse response = resolverEngine.processRequest(req,null);
		long afterTime = new Date().getTime();
		long time = afterTime - beforeTime;
		System.out.println("修改标识值耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info("标识值修改成功！");
		}else{
			log.error("标识值修改失败！");
		}
		log.info("modifyIdentifierRequest---method---end");
		return response;
	}

	@Override
	public BaseResponse removeIdentifierValues(String identifier, int[] indexes, MsgSettings settings) throws IdentifierException {
		log.info("removeIdentifierValueRequest---method---begin");
		if(resolverEngine == null){
			throw new IdentifierException(ExceptionCommon.IDENTIFIER_ENGINE_ERROR, "解析引擎异常!");
		}
		if(identifier == null)
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "identifier不能为空");
		if(indexes == null || (indexes!=null && indexes.length<1))
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "indexes不能为空,需要指定待删除标识值索引");
		
		BaseResponse response = null;
		RemoveValueRequest req = new RemoveValueRequest(Util.encodeString(identifier), indexes, null);
		long beforeTime = new Date().getTime();
		response = resolverEngine.processRequest(req,null);
		long afterTime = new Date().getTime();
		long time = afterTime - beforeTime;
		System.out.println("移除标识值耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info("移除标识值成功!");
		}else{
			log.error("移除标识值失败!");
		}
		log.info("removeIdentifierValueRequest---method---end");
		return response;
	}
	
	@Override
	public BaseResponse getServerSiteInfo(MsgSettings settings) throws IdentifierException {
		log.info("getServerSiteInfo---method---begin");
		if(resolverEngine == null){
			throw new IdentifierException(ExceptionCommon.IDENTIFIER_ENGINE_ERROR, "解析引擎异常!");
		}
		BaseResponse response = null;
		SiteRequest req = new SiteRequest();
		long beforeTime = new Date().getTime();
		response = resolverEngine.processRequest(req,null);
		long afterTime = new Date().getTime();
		System.out.println("移除标识值耗时(毫秒):" + (afterTime - beforeTime));
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info("成功获取站点信息!");
		}else{
			log.error("获取站点信息失败!");
		}
		log.info("getServerSiteInfo---method---end");
		return response;
	}
	/////////////////////////////////////////////////private-functions///////////////////////////////////////////
	private void setMessageSettings(BaseRequest bm,MsgSettings settings){
		if(settings == null){
			log.error("settings=null,无法为请求消息赋予设置值!");
			return;
		}
		bm.trustedQuery = settings.isTruestyQuery();
		bm.bAuthoritative = settings.isAuthoritative();
		bm.bCertify = settings.isCertify();
		bm.bEncrypt = settings.isEncrypt();
		bm.bRecursive = settings.isRecursive();
		bm.bCacheCertify = settings.isCacheCertify();
		bm.continuous = settings.isContinuous();
		bm.bKeepAlive = settings.isKeepAlive();
		bm.ignoreRestrictedValues = settings.isPublicOnly();
		bm.returnRequestDigest = settings.isReturnRequestDigest();
	}
}
