package tv.newtv.cboxtv.utils;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.cms.bean.Content;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.SystemUtils;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.uc.v2.TimeUtil;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.utils
 * 创建事件:         09:41
 * 创建人:           weihaichao
 * 创建日期:          2018/5/8
 */
public class DBUtil {

    /**
     * 移除订阅
     *
     * @param contentID
     * @param callback
     */
    public static void UnSubcribe(String userId, String contentID, DBCallback<String> callback, String tableName) {
        DataSupport.delete(tableName).condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .eq(DBConfig.USERID, userId)
                .build()
                .withCallback(callback).excute();
    }


    /**
     * 添加订阅
     *
     * @param entity
     * @param callback
     */
    public static void AddSubcribe(String userId, Content entity, Bundle bundle, DBCallback<String> callback, String tableName) {
        ContentValues contentValues = new ContentValues();
        Log.d("sub", "AddSubcribe contentid : " + entity.getContentID());
        if (!TextUtils.isEmpty(entity.getContentID())) {
            contentValues.put(DBConfig.CONTENT_ID, entity.getContentID());
        }
        if (!TextUtils.isEmpty(entity.getContentUUID())) {
            contentValues.put(DBConfig.CONTENTUUID, entity.getContentUUID());
        }
        if (entity.getContentType() != null) {
            contentValues.put(DBConfig.CONTENTTYPE, entity.getContentType());
        }

        if (!TextUtils.isEmpty(entity.getGrade()) && !TextUtils.equals(entity.getGrade(), "0.0") && !TextUtils.equals(entity.getGrade(), "0")) {
            contentValues.put(DBConfig.CONTENT_GRADE, entity.getGrade());
        }
        contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
        contentValues.put(DBConfig.IMAGEURL, entity.getVImage());
        contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());
        contentValues.put(DBConfig.USERID, userId);
        //2018.12.17 wqs 梳理更新至多少集逻辑
        if (!TextUtils.isEmpty(entity.getRecentNum())) {
            contentValues.put(DBConfig.EPISODE_NUM, entity.getRecentNum());
        }
        if (!TextUtils.isEmpty(entity.getSeriesSum())) {
            contentValues.put(DBConfig.TOTAL_CNT, entity.getSeriesSum());
        }
        //2018.12.18 wqs 更新至多少集策略修改为cms返回recentMsg字段显示更新状态
        if (!TextUtils.isEmpty(entity.getRecentMsg())) {
            contentValues.put(DBConfig.RECENT_MSG, entity.getRecentMsg());
        }
        String updateTime = bundle.getString(DBConfig.UPDATE_TIME);
        if (TextUtils.isEmpty(updateTime)) {
            contentValues.put(DBConfig.UPDATE_TIME, TimeUtil.getInstance().getCurrentTimeInMillis());
        } else {
            contentValues.put(DBConfig.UPDATE_TIME, updateTime);
        }

        DataSupport.insertOrReplace(tableName)
                .withValue(contentValues)
                .withCallback(callback).excute();
    }


    /**
     * 检查是否属于订阅
     *
     * @param contentID
     * @param callback
     */
    public static void CheckSubscrip(String userId, String contentID, DBCallback<String> callback, String tableName) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(callback).excute();
    }

    /**
     * 检查是否属于收藏
     *
     * @param contentID
     * @param callback
     */
    public static void CheckCollect(String userId, String contentID, DBCallback<String> callback, String tableName) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(callback).excute();
    }


    /**
     * 移除收藏
     *
     * @param contentID
     * @param callback
     */
    public static void UnCollect(String userId, String contentID, DBCallback<String> callback, String tableName) {
        Log.d("lxl", "userId : " + userId + ", contentID : " + contentID + ", tableName : " + tableName);
        DataSupport.delete(tableName)
                .condition()
                .eq(DBConfig.USERID, userId)
                .eq(DBConfig.CONTENT_ID, contentID)
                .build()
                .withCallback(callback).excute();
    }

    /**
     * 更新收藏
     *
     * @param entity
     * @param callback
     */
    public static void PutCollect(String userId, Content entity, Bundle bundle, DBCallback<String> callback, String tableName) {
        if (entity != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBConfig.CONTENTUUID, entity.getContentUUID());
            contentValues.put(DBConfig.CONTENT_ID, entity.getContentID());
            contentValues.put(DBConfig.CONTENTTYPE, entity.getContentType());
            contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
            contentValues.put(DBConfig.IMAGEURL, entity.getVImage());
            contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());
            contentValues.put(DBConfig.USERID, userId);
            //2018.12.17 wqs 梳理更新至多少集逻辑
            if (!TextUtils.isEmpty(entity.getRecentNum())) {
                contentValues.put(DBConfig.EPISODE_NUM, entity.getRecentNum());
            }
            if (!TextUtils.isEmpty(entity.getSeriesSum())) {
                contentValues.put(DBConfig.TOTAL_CNT, entity.getSeriesSum());
            }
            //2018.12.18 wqs 更新至多少集策略修改为cms返回recentMsg字段显示更新状态
            if (!TextUtils.isEmpty(entity.getRecentMsg())) {
                contentValues.put(DBConfig.RECENT_MSG, entity.getRecentMsg());
            }
            if (!TextUtils.isEmpty(entity.getGrade()) && !TextUtils.equals(entity.getGrade(), "0.0") && !TextUtils.equals(entity.getGrade(), "0")) {
                contentValues.put(DBConfig.CONTENT_GRADE, entity.getGrade());
            }
            String updateTime = bundle.getString(DBConfig.UPDATE_TIME);
            if (TextUtils.isEmpty(updateTime)) {
                contentValues.put(DBConfig.UPDATE_TIME, TimeUtil.getInstance().getCurrentTimeInMillis());
            } else {
                contentValues.put(DBConfig.UPDATE_TIME, updateTime);
            }

            DataSupport.insertOrReplace(tableName)
                    .withValue(contentValues)
                    .withCallback(callback).excute();
        }
    }

    public static void addHistory(String userId, Content mInfo, Bundle bundle, DBCallback<String> callback, String tableName) {
        if (mInfo == null) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        if (mInfo.getContentType() != null) {
            contentValues.put(DBConfig.CONTENTTYPE, mInfo.getContentType());
        }
        contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
        if (!TextUtils.isEmpty(mInfo.getVImage())) {
            contentValues.put(DBConfig.IMAGEURL, mInfo.getVImage());
        }
        if (!TextUtils.isEmpty(mInfo.getTitle())) {
            contentValues.put(DBConfig.TITLE_NAME, mInfo.getTitle());
        }

        String indexStr = bundle.getString(DBConfig.PLAYINDEX);
        int index = Integer.parseInt(indexStr);
        contentValues.put(DBConfig.PLAYINDEX, indexStr);
        if (!TextUtils.isEmpty(mInfo.getGrade()) && !TextUtils.equals(mInfo.getGrade(), "0.0") && !TextUtils.equals(mInfo.getGrade(), "0")) {
            contentValues.put(DBConfig.CONTENT_GRADE, mInfo.getGrade());
        }
        if (!TextUtils.isEmpty(mInfo.getVideoType())) {
            contentValues.put(DBConfig.VIDEO_TYPE, mInfo.getVideoType());
        }

//        List<ProgramSeriesInfo.ProgramsInfo> programsInfos = mInfo.getData();
//        if (programsInfos != null) {
//            ProgramSeriesInfo.ProgramsInfo info = programsInfos.get(index);
//            if (info != null) {
//                contentValues.put(DBConfig.PLAYINDEX, info.getPeriods());
//            }
//        }

        String progress = bundle.getString(DBConfig.PLAY_PROGRESS);
        if (!TextUtils.isEmpty(progress)) {
            contentValues.put(DBConfig.PLAY_PROGRESS, progress);
        }

        String seriesUUID = mInfo.getContentID();
        if (Constant.CONTENTTYPE_CP.equals(mInfo.getContentType())) {
            if (!TextUtils.isEmpty(mInfo.getCsContentIDs())) {
                seriesUUID = mInfo.getCsContentIDs().split("\\|")[0];
            }
            contentValues.put(DBConfig.CONTENTTYPE, Constant.CONTENTTYPE_PS);
            contentValues.put(DBConfig.PLAYID, mInfo.getContentUUID());
        } else {
            seriesUUID = mInfo.getContentUUID();
            if (index >= 0 && mInfo.getData() != null && index < mInfo.getData().size()) {
                if (mInfo.getData() != null && mInfo.getData().size() != 0) {
                    contentValues.put(DBConfig.PLAYID, mInfo.getData().get(index).getContentUUID());
                }
            }
        }
        //2018.12.17 wqs 修改插入历史的条件为contentID
        String contentID = mInfo.getContentID();
        contentValues.put(DBConfig.CONTENT_ID, contentID);
        contentValues.put(DBConfig.PLAYPOSITION, bundle.getString(DBConfig.PLAYPOSITION));
        contentValues.put(DBConfig.CONTENTUUID, seriesUUID);

        String updateTime = bundle.getString(DBConfig.UPDATE_TIME);
        if (!TextUtils.isEmpty(updateTime)) {
            contentValues.put(DBConfig.UPDATE_TIME, updateTime);
        } else {
            contentValues.put(DBConfig.UPDATE_TIME, TimeUtil.getInstance().getCurrentTimeInMillis());
        }
        contentValues.put(DBConfig.USERID, userId);
        // contentValues.put(DBConfig.SUPERSCRIPT, mInfo.getrSuperScript());
        contentValues.put(DBConfig.CONTENT_DURATION, bundle.getString(DBConfig.CONTENT_DURATION));

        //2018.12.17 wqs 梳理更新至多少集逻辑
        if (!TextUtils.isEmpty(mInfo.getRecentNum())) {
            contentValues.put(DBConfig.EPISODE_NUM, mInfo.getRecentNum());
        }
        if (!TextUtils.isEmpty(mInfo.getSeriesSum())) {
            contentValues.put(DBConfig.TOTAL_CNT, mInfo.getSeriesSum());
        }
        //2018.12.18 wqs 更新至多少集策略修改为cms返回recentMsg字段显示更新状态
        if (!TextUtils.isEmpty(mInfo.getRecentMsg())) {
            contentValues.put(DBConfig.RECENT_MSG, mInfo.getRecentMsg());
        }
        if(!TextUtils.isEmpty(mInfo.getAlternateNumber())){
            contentValues.put(DBConfig.ALTERNATE_NUMBER,mInfo.getAlternateNumber());
        }
        DataSupport.insertOrUpdate(tableName)
                .condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .build()
                .withValue(contentValues)
                .withCallback(callback).excute();
    }


    public static void addAttention(String userId, Content entity, Bundle bundle, DBCallback<String> callback, String tableName) {
        if (entity == null) {
            return;
        }

        ContentValues contentValues = new ContentValues();
        if (!TextUtils.isEmpty(entity.getContentID())) {
            contentValues.put(DBConfig.CONTENT_ID, entity.getContentID());
        }
        if (!TextUtils.isEmpty(entity.getContentUUID())) {
            contentValues.put(DBConfig.CONTENTUUID, entity.getContentUUID());
        }

        if (!TextUtils.isEmpty(entity.getContentType())) {
            contentValues.put(DBConfig.CONTENTTYPE, entity.getContentType());
        }

        contentValues.put(DBConfig.USERID, userId);
        contentValues.put(DBConfig.IMAGEURL, entity.getVImage());
        contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
        contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());

        String updateTime = bundle.getString(DBConfig.UPDATE_TIME);
        if (TextUtils.isEmpty(updateTime)) {
            contentValues.put(DBConfig.UPDATE_TIME, TimeUtil.getInstance().getCurrentTimeInMillis());
        } else {
            contentValues.put(DBConfig.UPDATE_TIME, updateTime);
        }

        DataSupport.insertOrReplace(tableName)
                .condition()
                .eq(DBConfig.USERID, userId)
                .build()
                .withValue(contentValues)
                .withCallback(callback).excute();


    }

    public static void delAttention(String userId, String contentID, DBCallback<String> callback, String tableName) {
        DataSupport.delete(tableName).condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .eq(DBConfig.USERID, userId)
                .build()
                .withCallback(callback).excute();
    }

    public static void delHistory(String userId, String contentID, DBCallback<String> callback, String tableName) {
        DataSupport.delete(tableName).condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .eq(DBConfig.USERID, userId)
                .build()
                .withCallback(callback)
                .excute();
    }

    /**
     * 清空表数据
     *
     * @param tableName 表名
     * @param callback  回调
     */
    public static void clearTableAll(String tableName, DBCallback<String> callback) {
        Log.d("wqs", "wqs:clearTableData:tableName：" + tableName);
        DataSupport.delete(tableName)
                .withCallback(callback)
                .excute();
    }

    public static void addCarouselChannelRecord(String userId, String tableName, Bundle bundle, DBCallback<String> callback) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConfig.CONTENTUUID, bundle.getString(DBConfig.CONTENTUUID));
        contentValues.put(DBConfig.CONTENT_ID, bundle.getString(DBConfig.CONTENT_ID));
        contentValues.put(DBConfig.TITLE_NAME, bundle.getString(DBConfig.TITLE_NAME));
        contentValues.put(DBConfig.IS_FINISH, bundle.getString(DBConfig.IS_FINISH));
        contentValues.put(DBConfig.REAL_EXCLUSIVE, bundle.getString(DBConfig.REAL_EXCLUSIVE));
        contentValues.put(DBConfig.ISSUE_DATE, bundle.getString(DBConfig.ISSUE_DATE));
        contentValues.put(DBConfig.LAST_PUBLISH_DATE, bundle.getString(DBConfig.LAST_PUBLISH_DATE));
        contentValues.put(DBConfig.SUB_TITLE, bundle.getString(DBConfig.SUB_TITLE));
        contentValues.put(DBConfig.UPDATE_TIME, bundle.getString(DBConfig.UPDATE_TIME));
        contentValues.put(DBConfig.USERID, bundle.getString(DBConfig.USERID));
        contentValues.put(DBConfig.IMAGEURL, bundle.getString(DBConfig.H_IMAGE));
        contentValues.put(DBConfig.H_IMAGE, bundle.getString(DBConfig.H_IMAGE));
        contentValues.put(DBConfig.VIP_FLAG, bundle.getString(DBConfig.VIP_FLAG));
        contentValues.put(DBConfig.CONTENTTYPE, bundle.getString(DBConfig.CONTENTTYPE));
        contentValues.put(DBConfig.ACTIONTYPE, bundle.getString(DBConfig.ACTIONTYPE));
        contentValues.put(DBConfig.ALTERNATE_NUMBER, bundle.getString(DBConfig.ALTERNATE_NUMBER));

        DataSupport.insertOrReplace(tableName)
                .condition()
                .eq(DBConfig.USERID, userId)
                .build()
                .withValue(contentValues)
                .withCallback(callback).excute();
    }

    public static void deleteCarouselChannelRecord(String contentID, DBCallback<String> callback) {
        DataSupport.delete(DBConfig.LB_COLLECT_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENT_ID, contentID)
                .eq(DBConfig.USERID, SystemUtils.getDeviceMac(LauncherApplication.AppContext))
                .build()
                .withCallback(callback).excute();
    }
}
