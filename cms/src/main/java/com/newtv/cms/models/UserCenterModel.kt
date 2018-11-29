@file:Suppress("SpellCheckingInspection")

package com.newtv.cms.models

import com.newtv.cms.*
import com.newtv.cms.api.IUserCenter
import okhttp3.RequestBody

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms.models
 * 创建事件:         15:43
 * 创建人:           weihaichao
 * 创建日期:          2018/11/23
 */
internal class UserCenterModel : BaseModel(), IUserCenter {
    override fun getLoginQRCode(Authorization: String, response_type: String, client_id: String, channel_code: String, observer: DataObserver<String>) {
        val excutor: Executor<String> =
                buildExecutor(Request.userCenter.getLoginQRCode(Authorization,
                        response_type, client_id, channel_code), null)
        excutor.observer(observer)
                .execute()
    }

    override fun getAccessToken(Authorization: String, grant_type: String, device_code: String, client_id: String, observer: DataObserver<String>) {
        val excutor: Executor<String> = buildExecutor(Request.userCenter.getAccessToken
        (Authorization, grant_type, device_code, client_id), null)
        excutor.observer(observer).execute()
    }

    override fun refreshToken(Authorization: String, refresh_token: String, client_id: String, grant_type: String, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.refreshToken
        (Authorization, refresh_token, client_id, grant_type), null)
        executor.observer(observer).execute()
    }

    override fun getUser(Authorization: String, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.getUser(Authorization), null)
        executor.observer(observer).execute()
    }

    override fun getUserTime(Authorization: String, productId: String, appKey: String, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.getUserTime
        (Authorization, productId, appKey), null)
        executor.observer(observer).execute()
    }

    override fun sendSMSCode(Authorization: String, response_type: String, client_id: String, mobile: String, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.sendSMSCode
        (Authorization, response_type, client_id, mobile), null)
        executor.observer(observer).execute()
    }

    override fun verifySMSCode(Authorization: String, grant_type: String, client_id: String, mobile: String, sms_code: String, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.verifySMSCode
        (Authorization, grant_type, client_id, mobile, sms_code), null)
        executor.observer(observer).execute()
    }

    override fun getPayResponse(Authorization: String, requestBody: RequestBody, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.getPayResponse
        (Authorization, requestBody), null)
        executor.observer(observer).execute()
    }

    override fun getPayResponse_new(Authorization: String, requestBody: RequestBody, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.getPayResponse_new
        (Authorization, requestBody), null)
        executor.observer(observer).execute()
    }

    override fun getPayChannel(observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.getPayChannel(), null)
        executor.observer(observer).execute()
    }

    override fun getPayResult(Authorization: String, orderId: String, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.getPayResult
        (Authorization, orderId), null)
        executor.observer(observer).execute()
    }

    override fun getProductPrice(prdId: String, channelId: String, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.getProductPrice(prdId,
                channelId), null)
        executor.observer(observer).execute()
    }

    override fun getProductPrices(productId: String, prdId: String, prdType: String, channelId: String, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.getProductPrices
        (productId, prdId, prdType, channelId), null)
        executor.observer(observer).execute()
    }

    override fun getProduct(prdId: String, observer: DataObserver<String>) {
        val executor: Executor<String> = buildExecutor(Request.userCenter.getProduct(prdId), null)
        executor.observer(observer).execute()
    }

    override fun getPayFlag(Authorization: String, productIds: Array<String>, appKey: String, channelId: String, contentUuid: String, observer: DataObserver<String>) {
        val executor:Executor<String> = buildExecutor(Request.userCenter.getPayFlag
        (Authorization, productIds, appKey, channelId, contentUuid),null)
        executor.observer(observer).execute()
    }

    override fun getRefreshOrder(Authorization: String, order: String, observer: DataObserver<String>) {
        val executor:Executor<String> = buildExecutor(Request.userCenter.getRefreshOrder
        (Authorization, order),null)
        executor.observer(observer).execute()
    }

    override fun addHistory(authorization: String, user_id: String, channel_code: String, app_key: String, programset_id: String, programset_name: String, is_program: String, poster: String, program_progress: String, user_name: String, program_dur: String, program_watch_dur: String, is_panda: Boolean, check_record: Boolean, program_child_id: String, grade: String, videoType: String, totalCnt: String, superscript: String, contentType: String, curEpisode: String, actionType: String, observer: DataObserver<String>) {
        val executor:Executor<String> = buildExecutor(Request.userCenter.addHistory
        (authorization, user_id, channel_code, app_key, programset_id, programset_name,
                is_program, poster, program_progress, user_name, program_dur, program_watch_dur,
                is_panda, check_record, program_child_id, grade, videoType, totalCnt, superscript, contentType, curEpisode, actionType),null)
        executor.observer(observer).execute()
    }

    override fun deleteHistory(Authorization: String, is_program: String, channel_code: String, app_key: String, program_child_ids: String, programset_ids: String, observer: DataObserver<String>) {
        val executor:Executor<String> = buildExecutor(Request.userCenter.deleteHistory
        (Authorization, is_program, channel_code, app_key, program_child_ids, programset_ids),null)
        executor.observer(observer).execute()
    }

    override fun getHistoryList(Authorization: String, app_key: String, channel_code: String, user_id: String, offset: String, limit: String, observer: DataObserver<String>) {
        val executor:Executor<String> = buildExecutor(Request.userCenter.getHistoryList
        (Authorization, app_key, channel_code, user_id, offset, limit),null)
        executor.observer(observer).execute()
    }

    override fun addCollect(authorization: String, user_id: String, channel_code: String, app_key: String, programset_id: String, programset_name: String, is_program: String, poster: String, program_child_id: String, score: String, video_type: String, total_count: String, superscript: String, content_type: String, latest_episode: String, action_type: String, observer: DataObserver<String>) {
        val executor:Executor<String> = buildExecutor(Request.userCenter.addCollect
        (authorization, user_id, channel_code, app_key, programset_id, programset_name,
                is_program, poster, program_child_id, score, video_type, total_count,
                superscript, content_type, latest_episode, action_type),null)
        executor.observer(observer).execute()
    }

    override fun deleteCollect(authorization: String, user_id: String, is_program: String, channel_code: String, app_key: String, programset_ids: Array<String>, observer: DataObserver<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCollectList(Authorization: String, user_id: String, is_program: String, app_key: String, channel_code: String, offset: String, limit: String, observer: DataObserver<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addFollow(authorization: String, user_id: String, channel_code: String, app_key: String, programset_id: String, programset_name: String, is_program: String, poster: String, content_type: String, action_type: String, observer: DataObserver<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteFollow(authorization: String, user_id: String, is_program: String, channel_code: String, app_key: String, programset_ids: Array<String>, observer: DataObserver<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFollowList(authorization: String, user_id: String, is_program: String, app_key: String, channel_code: String, offset: String, limit: String, observer: DataObserver<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addSubscribes(authorization: String, user_id: String, channel_code: String, app_key: String, programset_id: String, programset_name: String, is_program: String, poster: String, program_child_id: String, score: String, video_type: String, total_count: String, superscript: String, content_type: String, latest_episode: String, action_type: String, observer: DataObserver<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteSubscribes(authorization: String, user_id: String, is_program: String, channel_code: String, app_key: String, programset_ids: Array<String>, observer: DataObserver<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSubscribesList(authorization: String, user_id: String, is_program: String, app_key: String, channel_code: String, offset: String, limit: String, observer: DataObserver<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(): String {
        return Model.MODEL_USERCENTER
    }


}