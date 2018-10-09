package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:31
 * 创建人:           weihaichao
 * 创建日期:          2018/9/25
 */

data class Content(
        val description: String,//描述
        val language: String,//语言
        val title: String,//内容标题
        val playOrder: Int,//播放顺序 播放器进行自动播放时的排序方式，按集号排序：0正序，1倒序
        val MAMID: String,//内容来源 Newtv：自建媒资，tx：腾讯，wuxi：央视无锡 如果是tx，终端需要用腾讯SDK播放
        val duration: Int,//时长 单位：秒
        val categoryIDs: String,//所属栏目ID	String	多个id之间用 | 分割
        val subTitle: String,//副标题
        val vImage: String,//竖海报
        val movieLevel: Int,//影片等级 1、正片，2、预告片，3、花絮
        val seriesSum: Int,//总集数
        val definition: String,//清晰度 SD：标清、HD：高清
        val vipProductId: String,//产品包ID
        val contentType: String,//内容类型
        val area: String,//国家地区
        val priceNum: String,//最新付费节目数量 设置最后几集收费
        val videoType: String,//一级分类
        val director: String,//导演
        val contentID: Int,//内容Id
        val csContentIDs: String,//所属节目集ID	 节目所属节目集contentID   |  分隔
        val tags: String,//标签
        val actors: String,//主演
        val airtime: String,//年代
        val sortType: String,//排序方式 节目集子集列表显示的排序方式，按集号排序： 0正序，1倒序
        val grade: Int,//评分
        val premiereChannel: String,//首播频道
        val contentUUID: String,//UUID
        val hImage: String,//横海报
        val videoClass: String,//二级分类
        val vipFlag: String,//付费标识

        // FG
        val birthday: String,//生日
        val classify: String,//分类
        val country: String,//国家
        val district: String,//
        val enName: String,//英文名字
        val pyName: String,//名字拼音首字母

        //CP、PG
        val freeDuration: String, //试播时长 单位：秒
        val periods: String, //集号
        val crytokey: String,     //加密字段	 String
        val cgContentIDs: String, //所属节目合集ID		多个id之间用 | 分割
        val tvContentIDs: String,//所属电视栏目ID	多个id之间用 | 分割
        val CDNURL: List<CdnUrl>?,   //分发任务返回的CDN播放地址

        //LV、TV
        val liveLoopParam: List<LiveParam>?,    //直播循环参数	 String
        val lvID: String,    //直播id
        val presenter: String,     //主持人	 String
        val premiereTime: String     //首播时间	 String
)