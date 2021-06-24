package cn.ac.caict.iiiiot.id.client.service.impl;
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

import cn.ac.caict.iiiiot.id.client.core.AddValueRequest;
import cn.ac.caict.iiiiot.id.client.core.BaseRequest;
import cn.ac.caict.iiiiot.id.client.core.BaseResponse;
import cn.ac.caict.iiiiot.id.client.core.CreateIdentifierRequest;
import cn.ac.caict.iiiiot.id.client.core.DeleteIdentifierRequest;
import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.core.IdentifierResolveEngine;
import cn.ac.caict.iiiiot.id.client.core.LoginIDSystemRequest;
import cn.ac.caict.iiiiot.id.client.core.LoginRequest;
import cn.ac.caict.iiiiot.id.client.core.ModifyValueRequest;
import cn.ac.caict.iiiiot.id.client.core.RemoveValueRequest;
import cn.ac.caict.iiiiot.id.client.core.ResolutionRequest;
import cn.ac.caict.iiiiot.id.client.core.SiteRequest;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;
import cn.ac.caict.iiiiot.id.client.data.MsgSettings;
import cn.ac.caict.iiiiot.id.client.log.IDLog;
import cn.ac.caict.iiiiot.id.client.security.AbstractAuthentication;
import cn.ac.caict.iiiiot.id.client.security.PubKeyAuthentication;
import cn.ac.caict.iiiiot.id.client.service.IIDManageServiceChannel;
import cn.ac.caict.iiiiot.id.client.utils.ExceptionCommon;
import cn.ac.caict.iiiiot.id.client.utils.MessageCommon;
import cn.ac.caict.iiiiot.id.client.utils.Util;

public class IDManageServiceChannelImpl implements IIDManageServiceChannel{
	private IdentifierResolveEngine resolverEngine = null;
	private Log log = IDLog.getLogger(IDManageServiceChannelImpl.class);
	private String printServInfo = "--";
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
		if(resolverEngine != null) {
			printServInfo += resolverEngine.getSiteInfo().servers[0];
		}
	}
	
	@Override
	public BaseResponse login(String identifier, int index, String privakeyFilePath, String password, int rdType,
			MsgSettings settings) throws IdentifierException {
		return innerLogin(identifier,index,privakeyFilePath,password,rdType,settings);
	}

	@Override
	public BaseResponse login(String identifier, int index, PrivateKey privateKey, int rdType, MsgSettings settings) throws IdentifierException {
		validateForLogin(identifier,index,rdType);
		return loginInternal(identifier,index,privateKey,rdType,settings);
	}


	@Override
	public BaseResponse login(String identifier, int index, String privakeyFilePath, String password, int rdType)
			throws IdentifierException {
		return innerLogin(identifier,index,privakeyFilePath,password,rdType,new MsgSettings());
	}

	public void logout() throws IdentifierException {
		resolverEngine = null;
		setLogin(false);
		setUserIdentifier(null);
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
//		log.debug("before processRequest req:" + req);
		BaseResponse response = resolverEngine.processRequest(req,null);
//		log.debug("after processRequest response:" + response);
		long afterTime = new Date().getTime();
		long time = afterTime - beforeTime;
		log.debug("查询耗时(毫秒):" + time);
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
		log.debug("创建标识耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info(identifier + "标识创建成功!" + printServInfo);
		}else{
			log.error(identifier + "标识创建失败!" + printServInfo);				
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
		log.debug("删除标识耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info(identifier +"删除标识成功！" + printServInfo);
		}else {
			log.error(identifier +"删除标识失败！" + printServInfo);
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
		log.debug("添加标识值耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info(identifier + "的标识值添加成功!" + printServInfo);
		}else{
			log.error(identifier + "的标识值添加失败!"+ printServInfo);
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
		setMessageSettings(req, settings);
		long beforeTime = new Date().getTime();
		BaseResponse response = resolverEngine.processRequest(req,null);
		long afterTime = new Date().getTime();
		long time = afterTime - beforeTime;
		log.debug("修改标识值耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info(identifier + "的标识值修改成功！" + printServInfo);
		}else{
			log.error(identifier + "的标识值修改失败！" + printServInfo);
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
		setMessageSettings(req, settings);
		long beforeTime = new Date().getTime();
		response = resolverEngine.processRequest(req,null);
		long afterTime = new Date().getTime();
		long time = afterTime - beforeTime;
		log.debug("移除标识值耗时(毫秒):" + time);
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info(identifier + "的移除标识值成功!"+ printServInfo);
		}else{
			log.error(identifier + "的移除标识值失败!"+ printServInfo);
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
		setMessageSettings(req, settings);
		long beforeTime = new Date().getTime();
		response = resolverEngine.processRequest(req,null);
		long afterTime = new Date().getTime();
		log.debug("移除标识值耗时(毫秒):" + (afterTime - beforeTime));
		if (response != null && (response.responseCode == MessageCommon.RC_SUCCESS)){
			log.info("成功获取站点信息!");
		}else{
			log.error("获取站点信息失败!");
		}
		log.info("getServerSiteInfo---method---end");
		return response;
	}
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
	
	private BaseResponse innerLogin(String identifier, int index, String privakeyFilePath, String password, int rdType,
			MsgSettings settings) throws IdentifierException{

		validateForLogin(identifier, index, privakeyFilePath, rdType);

		PrivateKey privKey = Util.getPrivateKeyFromFile(privakeyFilePath, null);
		if (privKey == null) {
			IDLog.getLogger(IDManageServiceChannelImpl.class).info("private key is null,maybe file is not exit,please check it!");
			throw new IdentifierException(ExceptionCommon.INVALID_PARM,"privakeyFilePath文件不存在");
		}

		return loginInternal(identifier, index, privKey, rdType, settings);
	}

	private void validateForLogin(String identifier, int index, int rdType) throws IdentifierException {
		if(identifier == null || index<1 ){
			throw new IdentifierException(ExceptionCommon.INVALID_PARM,"identifier=" + identifier + ",identifier不能为空");
		}
		if(index < 1) {
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "index=" + index + ",index必须为正整数");

		}
		if(rdType != 1 && rdType != 2 && rdType != 3) {
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "不支持该类型摘要rdType=" + rdType);
		}
	}

	private void validateForLogin(String identifier, int index, String privakeyFilePath, int rdType) throws IdentifierException {
		if(identifier == null || index<1 || privakeyFilePath == null){
			throw new IdentifierException(ExceptionCommon.INVALID_PARM,"identifier=" + identifier + ",identifier不能为空");
		}
		if(index < 1) {
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "index=" + index + ",index必须为正整数");

		}
		if(rdType != 1 && rdType != 2 && rdType != 3) {
			throw new IdentifierException(ExceptionCommon.INVALID_PARM, "不支持该类型摘要rdType=" + rdType);
		}
	}

	private BaseResponse loginInternal(String identifier, int index, PrivateKey privKey, int rdType, MsgSettings settings) throws IdentifierException {

		log.info("login---method---begin");
		byte[] userIdentifier = Util.encodeString(identifier);
		AbstractAuthentication authInfo = new PubKeyAuthentication(userIdentifier, index, privKey);
		BaseResponse response = null;
		LoginIDSystemRequest loginReq = new LoginIDSystemRequest(userIdentifier, index, authInfo);
		setMessageSettings(loginReq, settings);
		loginReq.returnRequestDigest = true;//该位不能改
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

	@Override
	public BaseResponse login(String username, String password, MsgSettings settings) throws IdentifierException {
		byte[][] reqTypes = new byte[][] { Util.encodeString(password) };
		LoginRequest req = new LoginRequest(Util.encodeString(username), reqTypes, null, null);
		setMessageSettings(req,settings);
		BaseResponse response = resolverEngine.processRequest(req, null);
		return response;
	}
}
