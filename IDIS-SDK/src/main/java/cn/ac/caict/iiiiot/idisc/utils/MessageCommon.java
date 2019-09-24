package cn.ac.caict.iiiiot.idisc.utils;
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
/***********************************************************************
 * MessageCommon列写的是请求响应消息中调用常量
 ***********************************************************************/

public abstract class MessageCommon {
	// 主版本号
	public static final byte MAJOR_VERSION = 2;
	// 次版本号
	public static final byte MINOR_VERSION = 10;
	// 如果不指定站点信息的情况下，主版本号和次版本号
	public static final byte COMPATIBILITY_MAJOR_VERSION = 2;
	public static final byte COMPATIBILITY_MINOR_VERSION = 3;
	// 消息过期时间
	public static final int MSG_EXPIRATION = 12 * 60 * 60;
	// ********消息操作码*****************************
	public static final int OC_RESERVED = 0;
	public static final int OC_RESOLUTION = 1;
	public static final int OC_GET_SITE = 2;
	public static final int OC_CREATE_IDENTIFIER = 100;
	public static final int OC_DELETE_IDENTIFIER = 101;
	public static final int OC_ADD_VALUE = 102;
	public static final int OC_REMOVE_VALUE = 103;
	public static final int OC_MODIFY_VALUE = 104;
	public static final int OC_RESPONSE_TO_CHALLENGE = 200;
	public static final int OC_LOGIN = 2000;
	public static final int OC_LOGIN_IDIS = 2001;
	// ********消息响应码*****************************
	public static final int RC_RESERVED = 0;
	public static final int RC_SUCCESS = 1;
	public static final int RC_ERROR = 2;
	public static final int RC_SERVER_TOO_BUSY = 3;
	public static final int RC_PROTOCOL_ERROR = 4;
	public static final int RC_IDENTIFIER_NOT_FOUND = 100;
	public static final int RC_IDENTIFIER_ALREADY_EXISTS = 101;
	public static final int RC_INVALID_IDENTIFIER = 102;
	public static final int RC_VALUES_NOT_FOUND = 200;
	public static final int RC_VALUE_ALREADY_EXISTS = 201;
	public static final int RC_INVALID_VALUE = 202;
	public static final int RC_SERVICE_REFERRAL = 302;
	public static final int RC_PREFIX_REFERRAL = 303;
	public static final int RC_INVALID_ADMIN = 400; // 无效管理员
	public static final int RC_INSUFFICIENT_PERMISSIONS = 401; // 没有权限
	public static final int RC_AUTHENTICATION_NEEDED = 402; // 需要认证
	public static final int RC_AUTHENTICATION_FAILED = 403; // 身份认证失败
	public static final int RC_INVALID_CREDENTIAL = 404; // 请求认证信息无效
	public static final int RC_AUTHEN_TIMEOUT = 405; // 获取认证响应超时
	public static final int RC_RELOGIN = 407; // 重复登录
	public static final int RC_LOGIN_FIRST = 408; // 未登录
}
