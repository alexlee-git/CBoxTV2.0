package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         14:07
 * 创建人:           weihaichao
 * 创建日期:          2018/9/25
 */

data class Page(
        var programs: List<Program>? = null,
        var blockId: String? = null,
        var blockTitle: String? = null,
        var blockImg: String? = null,
        var haveBlockTitle: String? = null,
        var rowNum: String? = null,
        var colNum: String? = null,
        var layoutCode: String? = null,         //
        var blockType: String? = null           //区块类型
) {
    override fun toString(): String {
        return "Page(programs=$programs, blockId='$blockId', blockTitle='$blockTitle', blockImg='$blockImg', haveBlockTitle='$haveBlockTitle', rowNum='$rowNum', colNum='$colNum', layoutCode='$layoutCode', blockType='$blockType')"
    }
}

