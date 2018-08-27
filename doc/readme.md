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

master2.0是二期版本