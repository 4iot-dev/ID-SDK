package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.core.IdentifierException;
import cn.ac.caict.iiiiot.id.client.data.IdentifierValue;

import java.io.Closeable;

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
 *
 */

/**
 * 标识操作适配器,重新封装了标识相关操作,包括所有发送到服务端的操作
 * @author bluepoint
 * @since 2.0.4 2020-07-23
 */
public interface IDAdapter extends Closeable {

    /**
     * 添加一个标识值
     * @param identifier 标识
     * @param values 标识值
     * @throws IdentifierException
     */
    public void addIdentifierValues(String identifier, IdentifierValue[] values) throws IdentifierAdapterException;

    /**
     * 创建一个标识
     * @param identifier 标识
     * @param values 标识值
     * @throws IdentifierAdapterException
     */
    public void createIdentifier(String identifier, IdentifierValue[] values) throws IdentifierAdapterException;

    /**
     * 删除标识值
     * @param identifier
     * @param values
     * @throws IdentifierAdapterException
     */
    public void deleteIdentifierValues(String identifier, IdentifierValue[] values) throws IdentifierAdapterException;

    /**
     * 解析,支持递归解析
     * @param identifier
     * @param types
     * @param indexes
     * @param auth
     * @return
     * @throws IdentifierAdapterException
     */
    public IdentifierValue[] resolve(String identifier, String[] types, int[] indexes, boolean auth) throws IdentifierAdapterException;

    /**
     * 解析,支持递归解析
     * @param identifier
     * @param types
     * @param indexes
     * @return
     * @throws IdentifierAdapterException
     */
    public IdentifierValue[] resolve(String identifier, String[] types, int[] indexes) throws IdentifierAdapterException;

    /**
     * 解析,支持递归解析
     * @param identifier
     * @return
     * @throws IdentifierAdapterException
     */
    public IdentifierValue[] resolve(String identifier) throws IdentifierAdapterException;


    /**
     * 更新标识
     * @param identifier
     * @param values
     * @throws IdentifierAdapterException
     */
    public void updateIdentifierValues(String identifier, IdentifierValue[] values) throws IdentifierAdapterException;

    /**
     * 删除标识
     * @param identifier
     * @throws IdentifierAdapterException
     */
    public void deleteIdentifier(String identifier) throws IdentifierAdapterException;

    public PrefixSiteInfo resolveSiteByProxy(String prefixIdentifier) throws IdentifierAdapterException, IdentifierException;

    public PrefixSiteInfo resolveSiteByProxy(IDAdapter idAdapter,String prefixIdentifier,String[] types) throws IdentifierAdapterException, IdentifierException;
}
