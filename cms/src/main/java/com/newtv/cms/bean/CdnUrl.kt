package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         16:03
 * 创建人:           weihaichao
 * 创建日期:          2018/10/8
 */
data class CdnUrl(
        val CNDId: String, //CDN平台ID
        val mediaType: String, //渠道定义的清晰度
        val playURL: String //视频播放地址,公网为完整CDN的视频播放地址，渠道业务按各分省的实际规则兑换播放地址。
)