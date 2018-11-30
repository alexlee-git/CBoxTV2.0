package tv.newtv.cboxtv.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.uc.UserStatus;
import com.newtv.libs.uc.pay.ExterPayBean;

import java.util.List;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.uc.v2.LoginActivity;
import tv.newtv.cboxtv.uc.v2.Pay.PayChannelActivity;
import tv.newtv.cboxtv.uc.v2.Pay.PayOrderActivity;
import tv.newtv.cboxtv.uc.v2.listener.ICollectionStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.IFollowStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.IHisoryStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.INotifyLoginStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.INotifyMemberStatusCallback;
import tv.newtv.cboxtv.uc.v2.listener.ISubscribeStatusCallback;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;
import tv.newtv.cboxtv.uc.v2.sub.QueryUserStatusUtil;

import static java.lang.String.valueOf;

public class UserCenterUtils {
    private static final String TAG = UserCenterUtils.class.getSimpleName();

    public static void init() {
        initLoginStatus();
        initMemberStatus();
    }

    public static void setLogin(boolean login) {
        UserStatus.setIsLogin(login);
        if (login) {
            initMemberStatus();
        } else {
            UserStatus.setMemberSatus(UserStatus.SIGN_MEMBER_OPEN_NOT);
        }
    }

    public static void initLoginStatus() {
        getLoginStatus(new INotifyLoginStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(boolean status) {
                UserStatus.setIsLogin(status);
            }
        });
    }

    public static void initMemberStatus() {
        getMemberStatus(new INotifyMemberStatusCallback() {
            @Override
            public void notifyLoginStatusCallback(String status, Bundle memberBundle) {
                UserStatus.setMemberSatus(status);
            }
        });
    }

    //登陆状态
    public static void getLoginStatus(INotifyLoginStatusCallback callback) {
        QueryUserStatusUtil.getInstance().getLoginStatus(LauncherApplication.AppContext, callback);
    }

    //会员状态
    public static void getMemberStatus(INotifyMemberStatusCallback callBack) {
        QueryUserStatusUtil.getInstance().getMemberStatus(LauncherApplication.AppContext, callBack);
    }

    public static void getHistoryState(final String field, final String value, final String order, IHisoryStatusCallback callack) {
        UserCenterRecordManager.getInstance().queryContenthistoryStatus(LauncherApplication.AppContext, field, value, order, callack);
    }

    //添加历史记录
    public static void addHistory(Content mProgramSeriesInfo, int index, int position, int duration, DBCallback<String> callback) {
        try {
            if (mProgramSeriesInfo != null) {
                Bundle bundle = new Bundle();
                bundle.putString(DBConfig.TITLE_NAME, mProgramSeriesInfo.getTitle());
                bundle.putString(DBConfig.IMAGEURL, mProgramSeriesInfo.getVImage());
                bundle.putString(DBConfig.PLAYPOSITION, String.valueOf(position));
                bundle.putString(DBConfig.CONTENT_DURATION, String.valueOf(duration));
                bundle.putString(DBConfig.CONTENT_GRADE, mProgramSeriesInfo.getGrade());
                bundle.putString(DBConfig.VIDEO_TYPE, mProgramSeriesInfo.getVideoType());
                bundle.putString(DBConfig.TOTAL_CNT, mProgramSeriesInfo.getSeriesSum());
                //bundle.putString(DBConfig.SUPERSCRIPT, mProgramSeriesInfo.getrSuperScript());
                bundle.putString(DBConfig.CONTENTUUID, mProgramSeriesInfo.getContentID());
                bundle.putString(DBConfig.CONTENTTYPE, mProgramSeriesInfo.getContentType());
                bundle.putString(DBConfig.PLAYINDEX, valueOf(index));
                bundle.putString(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);

                List<SubContent> subContents = mProgramSeriesInfo.getData();
                if (subContents != null && subContents.size() > 0) {
                    if (index >= 0 && index < subContents.size()) {
                        SubContent subContent = subContents.get(index);
                        bundle.putString(DBConfig.PROGRAM_CHILD_NAME, subContent.getTitle());
                        bundle.putString(DBConfig.PLAYID, subContent.getContentUUID());
                    }
                }

                UserCenterRecordManager.getInstance().addRecord(
                        UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_HISTORY,
                        LauncherApplication.AppContext,
                        bundle,
                        mProgramSeriesInfo, callback);
            } else {
                Log.e(TAG, "ywy addHistory data info is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除多条记录
    //param uuIds 待删除数据的content_uuid值,如果是全部则传"clean", 如果是多个则用逗号将id隔开
    public static void deleteSomeHistory(Content mProgramSeriesInfo, String uuIds, DBCallback<String> dbCallback) {
        if (null != mProgramSeriesInfo) {
            UserCenterRecordManager.getInstance().deleteRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_HISTORY,
                    LauncherApplication.AppContext,
                    uuIds,
                    mProgramSeriesInfo.getContentType(),
                    null,
                    dbCallback
            );
        }
    }

    //删除所有记录
    public static void deleteAllHistory(Content mProgramSeriesInfo, DBCallback<String> dbCallback) {
        if (null != mProgramSeriesInfo) {
            UserCenterRecordManager.getInstance().deleteRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_HISTORY,
                    LauncherApplication.AppContext,
                    "clean",
                    mProgramSeriesInfo.getContentType(),
                    null,
                    dbCallback
            );
        }
    }

    //是否收藏
    public static void getCollectState(String contentUUID, ICollectionStatusCallback mICollectionStatusCallback) {
        UserCenterRecordManager.getInstance().queryContentCollectionStatus(LauncherApplication.AppContext, contentUUID, mICollectionStatusCallback);
    }

    //添加收藏
    public static void addCollect(Content mProgramSeriesInfo, int index, DBCallback<String> dbCallback) {
        /*programset_id 节目ID
            programset_name 节目名称
            is_program 节目类型,是否为节目集(0-节目集，1-普通节目)
            poster 海报url
            program_child_id 自视频ID
            score 评分
            video_type 节目一级分类
            total_count 节目总集数
            superscript 角标Id
            content_type 节目类型
            latest_episode 观看集数
            action_type 动作类型*/
        if (mProgramSeriesInfo != null) {
            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENTUUID, mProgramSeriesInfo.getContentID());
            bundle.putString(DBConfig.TITLE_NAME, mProgramSeriesInfo.getTitle());
            bundle.putString(DBConfig.IMAGEURL, mProgramSeriesInfo.getVImage());
            bundle.putString(DBConfig.CONTENT_GRADE, mProgramSeriesInfo.getGrade());
            bundle.putString(DBConfig.VIDEO_TYPE, mProgramSeriesInfo.getVideoType());
            bundle.putString(DBConfig.TOTAL_CNT, mProgramSeriesInfo.getSeriesSum());
            //bundle.putString(DBConfig.SUPERSCRIPT, mProgramSeriesInfo.getrSuperScript());
            bundle.putString(DBConfig.CONTENTTYPE, mProgramSeriesInfo.getContentType());
            bundle.putString(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);

            List<SubContent> subContents = mProgramSeriesInfo.getData();
            if (subContents != null && subContents.size() > 0) {
                if (index >= 0 && index < subContents.size()) {
                    SubContent subContent = subContents.get(index);
                    bundle.putString(DBConfig.PROGRAM_CHILD_NAME, subContent.getTitle());
                    bundle.putString(DBConfig.PLAYID, subContent.getContentUUID());
                }
            }

            UserCenterRecordManager.getInstance().addRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_COLLECT,
                    LauncherApplication.AppContext,
                    bundle,
                    mProgramSeriesInfo, dbCallback);
        } else {
            Log.e(TAG, "ywy addCollect data info is null");
        }

    }

    //删除多条收藏
    //param uuIds 待删除数据的content_uuid值,如果是全部则传"clean", 如果是多个则用逗号将id隔开
    public static void deleteSomeCollect(Content mProgramSeriesInfo, String uuIds, DBCallback<String> dbCallback) {
        //2018.10.23 wqs 加了mProgramSeriesInfo对象判空，是因为详情页刚加载数据，此时点击取消收藏，此对象可能为空，因此需要添加非空判断
        if (mProgramSeriesInfo != null) {
            UserCenterRecordManager.getInstance().deleteRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_COLLECT,
                    LauncherApplication.AppContext,
                    uuIds,
                    mProgramSeriesInfo.getContentType(),
                    null,
                    dbCallback
            );
        } else {
            Log.e(TAG, "----deleteSomeCollect:mProgramSeriesInfo == null");
        }
    }

    //删除所有收藏
    public static void deleteAllCollect(Content mProgramSeriesInfo, DBCallback<String> dbCallback) {
        if (null != mProgramSeriesInfo) {
            UserCenterRecordManager.getInstance().deleteRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_COLLECT,
                    LauncherApplication.AppContext,
                    "clean",
                    mProgramSeriesInfo.getContentType(),
                    null,
                    dbCallback
            );
        }
    }

    //是否关注
    public static void getAttentionState(String contentUUID, IFollowStatusCallback mIFollowStatusCallback) {
        UserCenterRecordManager.getInstance().queryContentFollowStatus(LauncherApplication.AppContext, contentUUID, mIFollowStatusCallback);
    }

    //添加关注
    public static void addAttention(Content mProgramSeriesInfo, DBCallback<String> callback) {
        if (mProgramSeriesInfo != null) {
            /*programset_id 节目ID
            programset_name 节目名称
            is_program 视频类型,是否为节目集(0-节目集，1-普通节目)
            poster 海报url
            content_type 内容类型
            action_type 动作类型*/

            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENTUUID, mProgramSeriesInfo.getContentID());
            bundle.putString(DBConfig.TITLE_NAME, mProgramSeriesInfo.getTitle());
            bundle.putString(DBConfig.VIDEO_TYPE, mProgramSeriesInfo.getVideoType());
            bundle.putString(DBConfig.IMAGEURL, mProgramSeriesInfo.getVImage());
            bundle.putString(DBConfig.CONTENTTYPE, mProgramSeriesInfo.getContentType());
            bundle.putString(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
            UserCenterRecordManager.getInstance().addRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_FOLLOW,
                    LauncherApplication.AppContext,
                    bundle,
                    mProgramSeriesInfo, callback);
        } else {
            Log.e(TAG, "ywy addAttention data info is null");
        }
    }

    //删除多条关注
    //param uuIds 待删除数据的content_uuid值,如果是全部则传"clean", 如果是多个则用逗号将id隔开
    public static void deleteSomeAttention(Content mProgramSeriesInfo, String uuIds, DBCallback<String> dbCallback) {
        //2018.10.23 wqs 加了mProgramSeriesInfo对象判空，是因为详情页刚加载数据，此时点击取消关注，此对象可能为空，因此需要添加非空判断
        if (mProgramSeriesInfo != null) {
            UserCenterRecordManager.getInstance().deleteRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_FOLLOW,
                    LauncherApplication.AppContext,
                    uuIds,
                    mProgramSeriesInfo.getContentType(),
                    null,
                    dbCallback
            );
        } else {
            Log.e(TAG, "----deleteSomeAttention:mProgramSeriesInfo == null");
        }

    }

    //删除所有关注
    public static void deleteAllAttention(Content mProgramSeriesInfo, DBCallback<String> dbCallback) {
        if (null != mProgramSeriesInfo) {
            UserCenterRecordManager.getInstance().deleteRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_FOLLOW,
                    LauncherApplication.AppContext,
                    "clean",
                    mProgramSeriesInfo.getContentType(),
                    null,
                    dbCallback
            );
        }
    }


    //是否订阅
    public static void getSuncribeState(String contentUUID, ISubscribeStatusCallback mISubscribeStatusCallback) {
        UserCenterRecordManager.getInstance().queryContentSubscribeStatus(LauncherApplication.AppContext, contentUUID, mISubscribeStatusCallback);
    }

    //添加订阅
    public static void addSubcribe(Content mProgramSeriesInfo, int index, int position, DBCallback<String> callback) {
        /*programset_id 节目ID
            programset_name 节目名称
            is_program 节目类型,是否为节目集(0-节目集，1-普通节目)
            poster 海报url
            program_child_id 自视频ID
            score 评分
            video_type 节目一级分类
            total_count 节目总集数
            superscript 角标id
            content_type 节目类型
            latest_episode 观看集数
            action_type 动作类型*/
        if (mProgramSeriesInfo != null) {
            Bundle bundle = new Bundle();
            bundle.putString(DBConfig.CONTENTUUID, mProgramSeriesInfo.getContentID());
            bundle.putString(DBConfig.TITLE_NAME, mProgramSeriesInfo.getTitle());
            bundle.putString(DBConfig.IMAGEURL, mProgramSeriesInfo.getVImage());
            bundle.putString(DBConfig.CONTENT_GRADE, mProgramSeriesInfo.getGrade());
            if (mProgramSeriesInfo.getData() != null && mProgramSeriesInfo.getData().get(index) != null) {
                bundle.putString(DBConfig.PLAYID, mProgramSeriesInfo.getData().get(index).getContentUUID());
            }
            bundle.putString(DBConfig.VIDEO_TYPE, mProgramSeriesInfo.getVideoType());
            bundle.putString(DBConfig.TOTAL_CNT, mProgramSeriesInfo.getSeriesSum());
            //bundle.putString(DBConfig.SUPERSCRIPT, mProgramSeriesInfo.getrSuperScript());
            bundle.putString(DBConfig.CONTENTTYPE, mProgramSeriesInfo.getContentType());
            bundle.putString(DBConfig.PLAYINDEX, String.valueOf(position));
            bundle.putString(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);

            UserCenterRecordManager.getInstance().addRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_SUBSCRIBE,
                    LauncherApplication.AppContext,
                    bundle,
                    mProgramSeriesInfo,
                    callback);
        } else {
            Log.e(TAG, "ywy addSubcribe data info is null");
        }

    }

    //删除多条订阅
    //param uuIds 待删除数据的content_uuid值,如果是全部则传"clean", 如果是多个则用逗号将id隔开
    public static void deleteSomeSubcribet(Content mProgramSeriesInfo, String uuIds, DBCallback<String> dbCallback) {
        //2018.10.23 wqs 加了mProgramSeriesInfo对象判空，是因为详情页刚加载数据，此时点击取消订阅，此对象可能为空，因此需要添加非空判断
        if (mProgramSeriesInfo != null) {
            String contentType = mProgramSeriesInfo.getContentType();
            if (TextUtils.isEmpty(contentType)) {
                contentType = "";
            }
            UserCenterRecordManager.getInstance().deleteRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_SUBSCRIBE,
                    LauncherApplication.AppContext,
                    uuIds,
                    contentType,
                    null,
                    dbCallback
            );
        } else {
            Log.e(TAG, "----deleteSomeSubcribet:mProgramSeriesInfo == null");
        }

    }

    //删除所有订阅
    public static void deleteAllSubcribe(Content mProgramSeriesInfo, DBCallback<String> dbCallback) {
        if (null != mProgramSeriesInfo) {
            UserCenterRecordManager.getInstance().deleteRecord(
                    UserCenterRecordManager.USER_CENTER_RECORD_TYPE.TYPE_SUBSCRIBE,
                    LauncherApplication.AppContext,
                    "clean",
                    mProgramSeriesInfo.getContentType(),
                    null,
                    dbCallback
            );
        }
    }

    //1 单点包月
    public static void startVIP1(Activity activity, Content mProgramSeriesInfo, String action) {
        ExterPayBean mExterPayBean = setExterPayBean(mProgramSeriesInfo, action);
        Intent mIntent = new Intent(activity, PayChannelActivity.class);
        mIntent.putExtra("payBean", mExterPayBean);
        activity.startActivity(mIntent);
    }

    //3 vip
    public static void startVIP3(Activity activity, Content mProgramSeriesInfo, String action) {
        ExterPayBean mExterPayBean = setExterPayBean(mProgramSeriesInfo, action);
        Intent mIntent = new Intent(activity, PayChannelActivity.class);
        mIntent.putExtra("payBean", mExterPayBean);
        activity.startActivity(mIntent);
    }

    //4 单点
    public static void startVIP4(Activity activity, Content mProgramSeriesInfo, String action) {
        ExterPayBean mExterPayBean = setExterPayBean(mProgramSeriesInfo, action);
        Intent mIntent = new Intent(activity, PayOrderActivity.class);
        mIntent.putExtra("payBean", mExterPayBean);
        activity.startActivity(mIntent);
    }

    public static void startLoginActivity(Activity activity, Content mProgramSeriesInfo, String action, Boolean isPay) {
        ExterPayBean mExterPayBean = setExterPayBean(mProgramSeriesInfo, action);
        Intent intent = new Intent(LauncherApplication.AppContext, LoginActivity.class);
        intent.putExtra("payBean", mExterPayBean);
        intent.putExtra("ispay", isPay);
        activity.startActivity(intent);
    }

    private static ExterPayBean setExterPayBean(Content mProgramSeriesInfo, String action) {
        ExterPayBean mExterPayBean = new ExterPayBean();
        mExterPayBean.setContentUUID(mProgramSeriesInfo.getContentUUID());
        mExterPayBean.setContentType(mProgramSeriesInfo.getContentType());
        mExterPayBean.setContentid(mProgramSeriesInfo.getContentID());
        mExterPayBean.setVipProductId(mProgramSeriesInfo.getVipProductId());
        mExterPayBean.setMAMID(mProgramSeriesInfo.getMAMID());
        mExterPayBean.setVipFlag(mProgramSeriesInfo.getVipFlag());
        mExterPayBean.setAction(action);
        mExterPayBean.setTitle(mProgramSeriesInfo.getTitle());
        return mExterPayBean;
    }

}
