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
import cn.ac.caict.iiiiot.idisc.utils.Util;

public class AdminInfo {
    public static final int CREATE_IDENTIFIER = 0;
    public static final int DELETE_IDENTIFIER = 1;
    public static final int ADD_DERIVED_PREFIX = 2;
    public static final int DELETE_DERIVED_PREFIX = 3;
    public static final int MODIFY_VALUE = 4;
    public static final int REMOVE_VALUE = 5;
    public static final int ADD_VALUE = 6;
    public static final int MODIFY_ADMIN = 7;
    public static final int REMOVE_ADMIN = 8;
    public static final int ADD_ADMIN = 9;
    public static final int READ_VALUE = 10;
    public static final int SHOW_ALL_IDENTIFIERS = 11;
    
	public static final int PERM_CREATE_IDENTIFIER = 0x0001;
	public static final int PERM_DELETE_IDENTIFIER = 0x0002;
	public static final int PERM_ADD_NA = 0x0004;
	public static final int PERM_DELETE_NA = 0x0008;
	public static final int PERM_MODIFY_VALUE = 0x0010;
	public static final int PERM_REMOVE_VALUE = 0x0020;
	public static final int PERM_ADD_VALUE = 0x0040;
	public static final int PERM_MODIFY_ADMIN = 0x0080;
	public static final int PERM_REMOVE_ADMIN = 0x0100;
	public static final int PERM_ADD_ADMIN = 0x0200;
	public static final int PERM_READ_VALUE = 0x0400;
    public static final int PERM_SHOW_ALL_IDENTIFIERS = 0x0800;	
    // 管理员标识
    public byte[] admId;
    // 索引-对应标识下公钥类型的标识值
    public int admIdIndex;
    // 管理员权限，用4字节整数表示，每个权限由不同比特位诠释
    public int permissions = 0;
    
    public AdminInfo() {}
    
    public AdminInfo(byte[] admId,int admIndex,int permissions){
    	this.admId = admId;
    	this.admIdIndex = admIndex;
    	this.permissions = permissions;
    }

	public AdminInfo(String admId, int admIdIndex,

			boolean perm_createId, boolean perm_deleteId,

			boolean perm_addNA, boolean perm_deleteNA,

			boolean perm_modifyValue, boolean perm_removeValue, boolean perm_addValue,

			boolean perm_modifyAdmin, boolean perm_removeAdmin, boolean perm_addAdmin,

			boolean perm_readValue,boolean perm_showAll) {
		
		this.admId = Util.encodeString(admId);
		
		this.admIdIndex = admIdIndex;
		
		initPermissions(perm_createId, perm_deleteId, perm_addNA, perm_deleteNA, perm_modifyValue, perm_removeValue, perm_addValue, perm_modifyAdmin, perm_removeAdmin, perm_addAdmin, perm_readValue, perm_showAll);
	}

	public void initPermissions(boolean perm_createId, boolean perm_deleteId,

			boolean perm_addNA, boolean perm_deleteNA,

			boolean perm_modifyValue, boolean perm_removeValue, boolean perm_addValue,

			boolean perm_modifyAdmin, boolean perm_removeAdmin, boolean perm_addAdmin,

			boolean perm_readValue,boolean perm_showAll){
		
		permissions |= perm_createId ? PERM_CREATE_IDENTIFIER : 0;
		
		permissions |= perm_deleteId ? PERM_DELETE_IDENTIFIER : 0;
		
		permissions |= perm_addNA ? PERM_ADD_NA : 0;
		
		permissions |= perm_deleteNA ? PERM_DELETE_NA : 0;
		
		permissions |= perm_modifyValue ? PERM_MODIFY_VALUE : 0;
		
		permissions |= perm_removeValue ? PERM_REMOVE_VALUE : 0;
		
		permissions |= perm_addValue ? PERM_ADD_VALUE : 0;
		
		permissions |= perm_modifyAdmin ? PERM_MODIFY_ADMIN : 0;
		
		permissions |= perm_removeAdmin ? PERM_REMOVE_ADMIN : 0;
		
		permissions |= perm_addAdmin ? PERM_ADD_ADMIN : 0;
		
		permissions |= perm_readValue ? PERM_READ_VALUE : 0;
		
		permissions |= perm_addAdmin ? PERM_SHOW_ALL_IDENTIFIERS : 0;
	}
}
