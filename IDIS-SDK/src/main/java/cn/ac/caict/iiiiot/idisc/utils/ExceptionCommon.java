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
public class ExceptionCommon {
	// 不支持的编码
	public static final int EXCEPTIONCODE_UNSUPPORTENCODING = -1;
	// 断开连接失败
	public static final int EXCEPTIONCODE_DISCONN_FAILED = -2;
	// 创建长连接失败
	public static final int EXCEPTIONCODE_LONGCONNSOCKET_CREATE_FAILED = -4;
	// 无效IP
	public static final int EXCEPTIONCODE_ILLEGAL_IP = -5;
	// 无效端口
	public static final int EXCEPTIONCODE_ILLEGAL_PORT = -6;
	// 无效的参数
	public static final int INVALID_PARM = -7;
	// 用于解析的引擎异常
	public static final int IDENTIFIER_ENGINE_ERROR = -8;
	// JWT的header解析异常
	public static final int JWT_PARSE_ERROR = -9;
	// 时间解析错误
	public static final int TIME_PARSE_ERROR = -10;
	// 无效value
	public static final int EXCEPTIONCODE_INVALID_VALUE = 0; 
	// 内部错误
	public static final int EXCEPTIONCODE_INTERNAL_ERROR = 1; 
	// idis返回的错误，服务未找到
	public static final int EXCEPTIONCODE_FOUND_NO_SERVICE = 2; 
	// idis服务不接受该连接项
	public static final int EXCEPTIONCODE_NO_ACCEPTABLE_IDISCOMMUNICATIONITEMS = 3; 
	// 未知协议
	public static final int EXCEPTIONCODE_UNKNOWN_PROTOCOL = 4; 
	// 标识已存在
	public static final int EXCEPTIONCODE_IDENTIFIER_ALREADY_EXISTS = 5;
	// 消息格式错误
	public static final int EXCEPTIONCODE_MESSAGE_FORMAT_ERROR = 6; 
	// 无法连接idis服务
	public static final int EXCEPTIONCODE_CANNOT_CONNECT_TO_IDIS_SERVER = 7; 
	// 不能认证
	public static final int EXCEPTIONCODE_UNABLE_TO_AUTHENTICATE = 8; 
	// 标识不存在
	public static final int EXCEPTIONCODE_IDENTIFIER_DOES_NOT_EXIST = 9;
	// 安全警告
	public static final int EXCEPTIONCODE_SECURITY_ALERT = 10; 
	// 无效的签名
	public static final int EXCEPTIONCODE_MISSING_OR_INVALID_SIGNATURE = 13; 
	// 没有找到密码算法
	public static final int EXCEPTIONCODE_MISSING_CRYPTO_PROVIDER = 14; 
	// idis服务错误
	public static final int EXCEPTIONCODE_IDIS_SERVER_ERROR = 15; 
	// 未知算法
	public static final int EXCEPTIONCODE_UNKNOWN_ALGORITHM_ID = 16; 
	// 获取到过期消息
	public static final int EXCEPTIONCODE_GOT_EXPIRED_MESSAGE = 17; 
	// 加密错误
	public static final int EXCEPTIONCODE_ENCRYPTION_ERROR = 26; 
	// 服务引用错误
	public static final int EXCEPTIONCODE_SERVICE_REFERRAL_ERROR = 29; 
}
