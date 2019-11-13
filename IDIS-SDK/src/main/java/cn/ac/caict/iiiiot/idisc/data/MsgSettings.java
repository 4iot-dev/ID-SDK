package cn.ac.caict.iiiiot.idisc.data;
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
public class MsgSettings {
	private boolean truestyQuery = false;//查询时是否进行可信解析
	private boolean authoritative = false;//暂不使用
	private boolean certify = false;//是否认证
	private boolean encrypt = false;//暂不使用
	private boolean recursive = false;//暂不使用
	private boolean cacheCertify = false;//暂不使用
	private boolean continuous = false;//暂不使用
	private boolean keepAlive = false;//暂不使用
	private boolean publicOnly = false;//暂不使用
	private boolean returnRequestDigest = false;//暂不使用
	// getter & setter
	public boolean isTruestyQuery() {
		return truestyQuery;
	}
	public void setTruestyQuery(boolean truestyQuery) {
		this.truestyQuery = truestyQuery;
	}
	public void setCertify(boolean certify){
		this.certify = certify;
	}
	public boolean isAuthoritative() {
		return authoritative;
	}
	public boolean isCertify() {
		return certify;
	}
	public boolean isEncrypt() {
		return encrypt;
	}
	public boolean isRecursive() {
		return recursive;
	}
	public boolean isCacheCertify() {
		return cacheCertify;
	}
	public boolean isContinuous() {
		return continuous;
	}
	public boolean isKeepAlive() {
		return keepAlive;
	}
	public boolean isPublicOnly() {
		return publicOnly;
	}
	public boolean isReturnRequestDigest() {
		return returnRequestDigest;
	}
}
