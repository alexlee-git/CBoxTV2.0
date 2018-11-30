# 央视影音说明文档

## productFlavors 说明
xiaomi:小米渠道
* 对应的cms地址是：http://epg.cloud.ottcn.com/

letv:乐视渠道
* 对应的cms地址是：http://epg.cloud.ottcn.com/

panda:熊猫渠道
* 对应的cms地址是：http://epg.cloud.ottcn.com/

xunma:讯码渠道
* 对应的cms地址是：http://epg.cloud.ottcn.com/

cboxtest：测试渠道
* 对应的cms地址是：http://111.32.132.156/ 这个环境没有调取boot_guide接口
* 对应的广告系统是测试环境 http://api.adott.ottcn.org/
* 对应的播控鉴权测试地址是：http://stage-bzo.cloud.ottcn.com/

## 启动页广告点击进入详情页配置方法 ##
如果想在启动页加载广告的时候，用户按确定键进入详情页，需要广告系统配置参数：
* event_type是uri
* event_content是json格式：
```
{ 
  "actionType": "OPEN_DETAILS",
  "contentType": "PS",
  "contentUUID": "29880",
  "actionURI": ""
}
```
#### actionType包括：
* OPEN_DETAILS:打开详情页 对应的contentType是：PS节目集 FG人物 TV栏目 CG节目合集
* OPEN_LISTPAGE:打开列表页 contentType可以为空
* OPEN_SPECIAL:打开专题页  contentType可以为空


## 版本说明：
1.2 是亚运会版本
1.3 是二期之前的中间版本

1.4 版本是二期第二阶段版本，主要包括边看边买
2.0 版本是二期第三阶段版本(11月底完成测试并给厂商)，主要包括用户中心+支付
2.1 版本是二期第四阶段版本(二期最后一个版本，11月底提测)，主要包括串播、详情页优化、兑换券等

master2.0是二期版本
测试环境
```
<Service id="DeviceBoot">
    <status>000</status>
    <message>OK</message>
    <templateId>60000185</templateId>
    <!--  下发服务接口集合  -->
    <addressList>
        <address url="https://bzo.cloud.ottcn.com/" name="SERVER_TIME"/>
        <address url="http://172.25.5.144/" name="VERSION_UP"/>
        <address url="http://api.adott.ottcn.org/" name="AD"/>
        <address url="log.cloud.ottcn.com:14630" name="LOG"/>
        <address url="https://bzo.cloud.ottcn.com/" name="ACTIVATE"/>
        <address url="https://cdndispatchnewtv.ottcn.com" name="CDN"/>
        <address url="http://bzo.cloud.ottcn.com/" name="PERMISSTION_CHECK"/>
        <address url="http://111.32.138.57:80/" name="SEARCH"/>
        <address url="https://k.cloud.ottcn.com" name="DYNAMIC_KEY"/>
        <address url="https://terminal2.cloud.ottcn.com/" name="ACTIVATE2"/>
        <address url="http://172.25.5.144/" name="IS_ORIENTED"/>
        <address url="http://testcms31.ottcn.com:30013/" name="CMS"/>
        <address url="490" name="PAGE_MEMBER"/>
        <address url="489" name="PAGE_COLLECTION"/>
        <address url="488" name="PAGE_SUBSCRIPTION"/>
        <address url="487" name="PAGE_USERCENTER"/>
        <address url="http://img.cloud.ottcn.com/n3images/endH5/pages/cctvMedia/about.html" name="HTML_PATH_ABOUT_US"/>
        <address url="http://img.cloud.ottcn.com/n3images/endH5/pages/cctvMedia/helper.html" name="HTML_PATH_HELPER"/>
        <address url="http://img.cloud.ottcn.com/n3images/endH5/pages/cctvMedia/protocol.html" name="HTML_PATH_MEMBER_PROTOCOL"/>
        <address url="3|7" name="MEMBER_CENTER_PARAMS"/>
        <address url="http://stage-bzo.cloud.ottcn.com" name="PAY"/>
        <address url="http://stage-bzo.cloud.ottcn.com/" name="USER"/>
        <address url="http://stage-bzo.cloud.ottcn.com/" name="USER_BEHAVIOR"/>
        <address url="http://stage-bzo.cloud.ottcn.com/" name="PRODUCT"/>
        
        <address url="http://testcms31.ottcn.com:30013/" name="NEW_CMS"/>
        <address url="http://111.32.138.57:80/" name="NEW_SEARCH"/>
        <address url="666" name="HOTSEARCH_CONTENTID"/>
        <address url="555" name="EXIT_CONTENTID"/>
        
        <address url="http://img.cloud.ottcn.com/n3images/definitionImg/test/4k.png" name="MARK_IS4K"/>
        <address url="http://img.cloud.ottcn.com/n3images/productImg/prod/%1$s.png" name="MARK_VIPPRODUCTID"/>
        <address url="http://img.cloud.ottcn.com/n3images/operateImg/prod/%1$s.png" name="MARK_NEW_REALEXCLUSIVE"/>
    </addressList>
</Service>
```