package com.newtv.cms.bean

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.bean
 * 创建事件:         09:53
 * 创建人:           weihaichao
 * 创建日期:          2018/9/26
 */


data class CategoryTreeNode(
        val id: String,
        val title: String,
        val child: List<CategoryTreeNode>
)