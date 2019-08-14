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
	public byte[] admId;
    public int admIdIndex;
    public int permissions = 0;
    
    public AdminInfo() {}

	public AdminInfo(String admId, int admIdIndex,

			boolean createIdentifier, boolean deleteIdentifier,

			boolean addNA, boolean deleteNA,

			boolean modifyValue, boolean removeValue, boolean addValue,

			boolean modifyAdmin, boolean removeAdmin, boolean addAdmin,

			boolean readValue,boolean showAll) {
		this.admId = Util.encodeString(admId);
		this.admIdIndex = admIdIndex;
		permissions |= createIdentifier ? PERM_CREATE_IDENTIFIER : 0;
		permissions |= deleteIdentifier ? PERM_DELETE_IDENTIFIER : 0;
		permissions |= addNA ? PERM_ADD_NA : 0;
		permissions |= deleteNA ? PERM_DELETE_NA : 0;
		permissions |= modifyValue ? PERM_MODIFY_VALUE : 0;
		permissions |= removeValue ? PERM_REMOVE_VALUE : 0;
		permissions |= addValue ? PERM_ADD_VALUE : 0;
		permissions |= modifyAdmin ? PERM_MODIFY_ADMIN : 0;
		permissions |= removeAdmin ? PERM_REMOVE_ADMIN : 0;
		permissions |= addAdmin ? PERM_ADD_ADMIN : 0;
		permissions |= readValue ? PERM_READ_VALUE : 0;
		permissions |= addAdmin ? PERM_SHOW_ALL_IDENTIFIERS : 0;
	}


}
