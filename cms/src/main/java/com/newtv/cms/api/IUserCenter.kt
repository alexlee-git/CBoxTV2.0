package com.newtv.cms.api

import com.newtv.cms.DataObserver
import okhttp3.RequestBody

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.api
 * 创建事件:         15:43
 * 创建人:           weihaichao
 * 创建日期:          2018/11/23
 */
interface IUserCenter {
    fun getLoginQRCode(Authorization: String, response_type: String, client_id: String,
                       channel_code: String,observer: DataObserver<String>)
    fun getAccessToken(Authorization: String, grant_type: String, device_code: String, client_id: String,observer: DataObserver<String>)
    fun refreshToken(Authorization: String, refresh_token: String, client_id: String, grant_type: String,observer: DataObserver<String>)
    fun getUser(Authorization: String,observer: DataObserver<String>)
    fun getUserTime(Authorization: String, productId: String, appKey: String,observer: DataObserver<String>)
    fun sendSMSCode(Authorization: String, response_type: String, client_id: String, mobile: String,observer: DataObserver<String>)
    fun verifySMSCode(Authorization: String, grant_type: String, client_id: String, mobile: String, sms_code: String,observer: DataObserver<String>)
    fun getPayResponse(Authorization: String, requestBody: RequestBody,observer: DataObserver<String>)
    fun getPayResponse_new(Authorization: String, requestBody: RequestBody,observer: DataObserver<String>)
    fun getPayChannel(observer: DataObserver<String>)
    fun getPayResult(Authorization: String, orderId: String,observer: DataObserver<String>)
    fun getProductPrice(prdId: String, channelId: String,observer: DataObserver<String>)
    fun getProductPrices(productId: String, prdId: String, prdType: String, channelId: String,observer: DataObserver<String>)
    fun getProduct(prdId: String,observer: DataObserver<String>)
    fun getPayFlag(Authorization: String, productIds: Array<String>, appKey: String, channelId: String, contentUuid: String,observer: DataObserver<String>)
    fun getRefreshOrder(Authorization: String, order: String,observer: DataObserver<String>)
    fun addHistory(authorization: String, user_id: String, channel_code: String, app_key: String, programset_id: String, programset_name: String, is_program: String, poster: String, program_progress: String, user_name: String, program_dur: String, program_watch_dur: String, is_panda: Boolean, check_record: Boolean, program_child_id: String, grade: String, videoType: String, totalCnt: String, superscript: String, contentType: String, curEpisode: String, actionType: String,observer: DataObserver<String>)
    fun deleteHistory(Authorization: String, is_program: String, channel_code: String, app_key: String, program_child_ids: String, programset_ids: String,observer: DataObserver<String>)
    fun getHistoryList(Authorization: String, app_key: String, channel_code: String, user_id: String, offset: String, limit: String,observer: DataObserver<String>)
    fun addCollect(authorization: String, user_id: String, channel_code: String, app_key: String, programset_id: String, programset_name: String, is_program: String, poster: String, program_child_id: String, score: String, video_type: String, total_count: String, superscript: String, content_type: String, latest_episode: String, action_type: String,observer: DataObserver<String>)
    fun deleteCollect(authorization: String, user_id: String, is_program: String, channel_code: String, app_key: String, programset_ids: Array<String>,observer: DataObserver<String>)
    fun getCollectList(Authorization: String, user_id: String, is_program: String, app_key: String, channel_code: String, offset: String, limit: String,observer: DataObserver<String>)
    fun addFollow(authorization: String, user_id: String, channel_code: String, app_key: String, programset_id: String, programset_name: String, is_program: String, poster: String, content_type: String, action_type: String,observer: DataObserver<String>)
    fun deleteFollow(authorization: String, user_id: String, is_program: String, channel_code: String, app_key: String, programset_ids: Array<String>,observer: DataObserver<String>)
    fun getFollowList(authorization: String, user_id: String, is_program: String, app_key: String, channel_code: String, offset: String, limit: String,observer: DataObserver<String>)
    fun addSubscribes(authorization: String, user_id: String, channel_code: String, app_key: String, programset_id: String, programset_name: String, is_program: String, poster: String, program_child_id: String, score: String, video_type: String, total_count: String, superscript: String, content_type: String, latest_episode: String, action_type: String,observer: DataObserver<String>)
    fun deleteSubscribes(authorization: String, user_id: String, is_program: String, channel_code: String, app_key: String, programset_ids: Array<String>,observer: DataObserver<String>)
    fun getSubscribesList(authorization: String, user_id: String, is_program: String, app_key: String, channel_code: String, offset: String, limit: String,observer: DataObserver<String>)
}