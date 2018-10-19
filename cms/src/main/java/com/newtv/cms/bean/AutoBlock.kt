package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         09:52
 * 创建人:           weihaichao
 * 创建日期:          2018/10/19
 */

data class AutoBlock(
    val total: Int,
    val rows: List<Row>
)

data class Row(
    val contentId: String,
    val mamId: String,
    val uuid: String,
    val title: String,
    val subTitle: String,
    val pinyin: String,
    val tags: String,
    val contentType: String,
    val videoType: String,
    val videoClass: String,
    val createDate: String,
    val status: String,
    val createUserId: String,
    val createUserName: String,
    val definition: String,
    val duration: String,
    val grade: Double,
    val hImage: String,
    val movieLevel: String,
    val offlineDate: String,
    val publishDate: String,
    val siteId: String,
    val vImage: String,
    val copyright: String,
    val copyrightId: String,
    val actors: String,
    val airtime: String,
    val payStatus: String,
    val isCanSearch: String,
    val bidType: String,
    val drm: String,
    val area: String,
    val deleteFlag: String,
    val director: String,
    val lastModifiedDate: String,
    val lastModifiedUserId: String,
    val subCount: String,
    val seriesSum: String,
    val description: String,
    val distributeChannelIds: String,
    val finishDistributeChannelIds: String,
    val mainProductId: String,
    val mainVipFlag: String,
    val channelProduct: String,
    val language: String,
    val realExclusive: String,
    val extendObject: String,
    val medias: String,
    val outId: String,
    val crClassId: String,
    val ppvCode: String,
    val dataProvider: String,
    val publishPlatformIds: String,
    val finishPlatformIds: String,
    val cpId: String,
    val filmCpId: String,
    val sort: String,
    val isExquisite: String,
    val firstCategoryId: String,
    val firstCategoryTitle: String,
    val secondCategoryId: String,
    val secondCategoryTitle: String
)