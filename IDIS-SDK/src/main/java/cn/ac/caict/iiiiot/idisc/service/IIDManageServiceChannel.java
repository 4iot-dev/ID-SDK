package cn.ac.caict.iiiiot.idisc.service;
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
import cn.ac.caict.iiiiot.idisc.core.BaseResponse;
import cn.ac.caict.iiiiot.idisc.core.IdentifierException;
import cn.ac.caict.iiiiot.idisc.data.IdentifierValue;
import cn.ac.caict.iiiiot.idisc.data.MsgSettings;

public interface IIDManageServiceChannel {
	/**
	 * @param identifier 管理员标识
	 * @param index 索引
	 * @param privakeyFilePath 私钥文件路径
	 * @param password 如果私钥有密码，将密码赋值给password,如果私钥没有密码则password为null
	 * @param rdType 生成摘要hash算法
	 * 		   MD5算法：rdType=1
	 * 		   SH1算法：rdType=2
	 *         SH256算法：rdType=3
	 * @return 消息响应
	 * @throws IdentifierException
	 */
	public BaseResponse login(String identifier, int index, String privakeyFilePath, String password, int rdType) throws IdentifierException;
	/**
	 * @param identifier 标识名称
	 * @param indexes 待查询标识值索引数组
	 * @param types 待查询标识值类型数组
	 * @param settings 查询操作的消息设置,如该消息是否为可信解析、是否需要服务端签名等
	 * @return response 消息响应，返回符合查询条件的所有的标识值
	 * @throws IdentifierException
	 */
	public BaseResponse lookupIdentifier(String identifier, int[] indexes, String[] types, MsgSettings settings) throws IdentifierException;
	/**
	 * @param identifier 待创建标识名称
	 * @param values 创建标识可以添加标识值，若不添加标识值则该值为null
	 * @param settings 创建标识操作的消息设置
	 * @return 创建标识消息响应结果
	 * @throws IdentifierException
	 */
	public BaseResponse createIdentifier(String identifier,IdentifierValue[] values, MsgSettings settings) throws IdentifierException;
	/**
	 * @param identifier 待删除的标识名称
	 * @param settings 删除标识操作的消息设置
	 * @return 删除标识消息响应结果
	 * @throws IdentifierException
	 */
	public BaseResponse deleteIdentifier(String identifier, MsgSettings settings) throws IdentifierException;
	/**
	 * @param identifier 目标标识名称
	 * @param values 待添加的一组标识值
	 * @param 添加标识值操作的消息设置
	 * @return 添加标识值消息响应结果
	 * @throws IdentifierException
	 */
	public BaseResponse addIdentifierValues(String identifier,IdentifierValue[] values, MsgSettings settings) throws IdentifierException;
	/**
	 * @param identifier 目标标识名称
	 * @param values 已修改的标识值
	 * @param 修改标识值操作的消息设置
	 * @return 修改标识值消息响应结果
	 * @throws IdentifierException
	 */
	public BaseResponse modifyIdentifierValues(String identifier,IdentifierValue[] values, MsgSettings settings) throws IdentifierException;
	/**
	 * @param identifier 目标标识名称
	 * @param indexes 待移除标识值索引
	 * @param settings 移除标识值操作的消息设置
	 * @return 移除标识消息响应结果
	 * @throws IdentifierException
	 */
	public BaseResponse removeIdentifierValues(String identifier,int[] indexes, MsgSettings settings) throws IdentifierException;
	/**
	 * @return 判断当前连接是否登录
	 */
	public boolean isLogin();
	/**
	 * @param settings 获取站点信息操作的消息设置
	 * @return 返回站点信息的响应结果
	 */
	public BaseResponse getServerSiteInfo(MsgSettings settings) throws IdentifierException;

}
