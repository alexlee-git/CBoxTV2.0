package com.newtv.cms.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:31
 * 创建人:           weihaichao
 * 创建日期:          2018/9/25
 */

open class Content  : Serializable {
    var description: String? = null //描述
    var language: String? = null //语言
    var title: String? = null //内容标题
    var playOrder: String? = null //播放顺序 播放器进行自动播放时的排序方式，按集号排序：0正序，1倒序
    var MAMID: String? = null //内容来源 Newtv：自建媒资，tx：腾讯，wuxi：央视无锡 如果是tx，终端需要用腾讯SDK播放
    var duration: String? = null //时长 单位：秒
    var categoryIDs: String? = null //所属栏目ID	String	多个id之间用 | 分割
    var subTitle: String? = null //副标题
    var vImage: String? = null //竖海报
    var movieLevel: String? = null //影片等级 1、正片，2、预告片，3、花絮
    var seriesSum: String? = null //总集数
    var seriesType: String? = null //节目集类型 0：显示名称  1：显示集号
    var definition: String? = null //清晰度 SD：标清、HD：高清
    var vipProductId: String? = null //产品包ID BOSS系统的产品包唯一标识
    var contentType: String? = null //内容类型
    var area: String? = null //国家地区
    var priceNum: String? = null //最新付费节目数量 设置最后几集收费
    var videoType: String? = null //一级分类
    var director: String? = null //导演
    var recentNum:String? = null //已更新集数
    var new_realExclusive:String? = null //内容标识
    var is4k:String? = null //是否4K
    var isFinish:String? = null //是否已更新完  0-未完成  1-已完成
    var updateDate:String?=null //旗下节目最新更新至

    @SerializedName(value = "contentID",alternate = arrayOf("contentId"))
    var contentID: String? = null //内容Id
    var csContentIDs: String? = null //所属节目集ID	 节目所属节目集contentID   |  分隔
    var tags: String? = null //标签
    var actors: String? = null //主演
    var airtime: String? = null //年代
    var sortType: String? = null //排序方式 节目集子集列表显示的排序方式，按集号排序： 0正序，1倒序
    var grade: String? = null //评分
    var premiereChannel: String? = null //首播频道
    var contentUUID: String? = null //UUID
    var hImage: String? = null //横海报
    var videoClass: String? = null //二级分类
    var vipFlag: String? = null //付费标识
    var data: List<SubContent>? = null

    var liveUrl:String? = null
    var liveLoopType:String? = null
    var liveParam:List<LiveParam>? = null
    var playStartTime:String? = null
    var playEndTime:String? = null
    var IsTimeShift:String? = null

    // FG
    var birthday: String? = null //生日
    var classify: String? = null //分类
    var country: String? = null //国家
    var district: String? = null //
    var enName: String? = null //英文名字
    var pyName: String? = null //名字拼音首字母

    //CP、PG
    var freeDuration: String? = null //试播时长 单位：秒
    var periods: String? = null //集号
    var crytokey: String? = null     //加密字段	 String
    var cgContentIDs: String? = null //所属节目合集ID		多个id之间用 | 分割
    var tvContentIDs: String? = null //所属电视栏目ID	多个id之间用 | 分割
    var CDNURL: List<CdnUrl>? = null    //分发任务返回的CDN播放地址

    //LV、
    var playUrl:String? = null

    // TV
    var liveLoopParam: List<LiveParam>? = null     //直播循环参数	 String
    var lvID: String? = null    //直播id
    var presenter: String? = null     //主持人	 String
    var premiereTime: String? = null     //首播时间	 String


    // LB
    var alternateNumber:String? = null   //台号

}