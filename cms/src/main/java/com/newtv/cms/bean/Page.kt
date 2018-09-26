package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         14:07
 * 创建人:           weihaichao
 * 创建日期:          2018/9/25
 */

data class Page(
    val programs: List<Program>,
    val blockId: Int,
    val blockTitle: String,
    val blockImg: String,
    val haveBlockTitle: String,
    val rowNum: String,
    val colNum: String,
    val layoutCode: String,
    val blockType: String
)

