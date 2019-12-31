简体中文

![Alt text](./res/iiit.jpg)



#                                  ID-SDK

## 简介

欢迎使用 ID-SDK 。智能分布式标识系统是由工业互联网与物联网研究所（简称工物所）研发、用于对标识的管理和解析的系统。为方便用户使用该系统，工物所提供了ID-SDK以方便标识系统用户定制开发自有系统、快速接入工业互联网标识体系节点，如下图。这里向您介绍如何获取ID-SDK并快速使用。如果您在使用ID-SDK的过程中遇到任何问题，欢迎在当前GitHub[提交 Issues](https://github.com/4iot-dev/ID-SDK/issues/new)。
![Alt text](./res/relations.jpg)


## 环境要求

#####1\. 若您希望通过连接标识服务系统使用或者了解ID-SDK开源项目，请与我们联系，我们将提供详细的环境资料，以便于您进行快速开发或调试。联系邮箱：fengyuan@caict.ac.cn
#####2\. ID-SDK运行依赖gson-2.3.1.jar、commons-codec-1.12.jar、commons-logging-1.2.jar、log4j-1.2.17.jar、bcprov-jdk15on-1.62.jar、hutool-all-4.6.3.jar。  
添加依赖包步骤如下：
######1）在Eclipse项目中导入JAR包将ID-SDK-2.0.1.jar以及其依赖的gson-2.3.1.jar，commons-codec-1.12.jar，commons-logging-1.2.jar，log4j-1.2.17.jar，bcprov-jdk15on-1.62.jar，hutool-all-4.6.3.jar拷贝到您的项目中；
######2）在Eclipse中选择您的工程，右击选择 Properties > Java Build Path > Add JARs。
#####3\.ID-SDK需要JDK1.8以上的版本。
##  ID-SDK使用  
&ensp; &ensp;ID-SDK目录：ID-SDK开源代码  
&ensp; &ensp;ID-SDK-DEMO目录：ID-SDK开发包演示demo  
&ensp; &ensp;通过ID-SDK连接标识系统进行标识操作，参照ID-SDK-DEMO，主要分3个步骤：
#####1\. 创建ID-SDK的连接通道管理对象IChannelManageService。
#####2\. 使用连接通道管理对象创建与标识系统的连接通道对象IIDManageServiceChannel。
#####3\. 通过IIDManageServiceChannel对象可以进行标识查询或者管理操作。
 详见[ID-SDK开发指南](./ID-SDK开发指南.md)

## 文档

- [Change](./Change.md)
- [apidoc](./apidoc.md)
- [LICENSE](./LICENSE)
- [ID-SDK开发指南](./ID-SDK开发指南.md)

## 问题反馈

[提交 Issue](https://github.com/4iot-dev/ID-SDK/issues/new)。
邮件反馈：fengyuan@caict.ac.cn
我们将尽快给予回复。

## 发行说明

ID-SDK首次在开源平台发布的版本是2.0.0，在发布2.0.1版本时，SDK包名称以及源码包结构发生了变化，故2.0.0版本的用户升级到2.0.1版须做出引用调整：  
1) IDIS-SDK-2.0.0.jar更换为ID-SDK-2.0.1  
2) 引用SDK中内容时，导入包路径需要调整，如"import cn.ac.caict.iiiiot.idisc.*****;"要改为"import cn.ac.caict.iiiiot.id.client.****;"  

各版本更新将记录在[Change](./Change.md)中。

## 许可证

[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)

版权所有 2019 工业互联网与物联网研究所