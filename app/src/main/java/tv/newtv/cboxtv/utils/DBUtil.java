package tv.newtv.cboxtv.utils;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;

import com.newtv.cms.bean.Content;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.Utils;

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
     * @param contentUuId
     * @param callback
     */
    public static void UnSubcribe(String userId, String contentUuId, DBCallback<String> callback, String tableName) {
        DataSupport.delete(tableName).condition()
                .eq(DBConfig.CONTENTUUID, contentUuId)
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
        //TODO 写入本地数据库 历史记录
        ContentValues contentValues = new ContentValues();
        if (entity.getContentID() != null) {
            contentValues.put(DBConfig.CONTENTUUID, entity.getContentID());
        }
        if (entity.getContentType() != null) {
            contentValues.put(DBConfig.CONTENTTYPE, entity.getContentType());
        }

        if (entity.getGrade() != null) {
            contentValues.put(DBConfig.CONTENT_GRADE, entity.getGrade());
        }
        contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
        contentValues.put(DBConfig.IMAGEURL, entity.getVImage());
        contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());

        String updateTime = bundle.getString(DBConfig.UPDATE_TIME);
        if (TextUtils.isEmpty(updateTime)) {
            contentValues.put(DBConfig.UPDATE_TIME, com.newtv.libs.util.Utils.getSysTime());
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
     * @param contentUuId
     * @param callback
     */
    public static void CheckSubscrip(String userId, String contentUuId, DBCallback<String> callback, String tableName) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentUuId)
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(callback).excute();
    }

    /**
     * 检查是否属于收藏
     *
     * @param contentUuId
     * @param callback
     */
    public static void CheckCollect(String userId, String contentUuId, DBCallback<String> callback, String tableName) {
        DataSupport.search(tableName)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentUuId)
                .eq(DBConfig.USERID, userId)
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(callback).excute();
    }


    /**
     * 移除收藏
     *
     * @param contentUuId
     * @param callback
     */
    public static void UnCollect(String userId, String contentUuId, DBCallback<String> callback, String tableName) {
        DataSupport.delete(tableName)
                .condition()
                .eq(DBConfig.USERID, userId)
                .eq(DBConfig.CONTENTUUID, contentUuId)
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
            //TODO 写入本地数据库 历史记录
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBConfig.CONTENTUUID, entity.getContentID());
            contentValues.put(DBConfig.CONTENTTYPE, entity.getContentType());
            contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
            contentValues.put(DBConfig.IMAGEURL, entity.getVImage());
            contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());
            contentValues.put(DBConfig.USERID, userId);
            String updateTime = bundle.getString(DBConfig.UPDATE_TIME);
            if (TextUtils.isEmpty(updateTime)) {
                contentValues.put(DBConfig.UPDATE_TIME, com.newtv.libs.util.Utils.getSysTime());
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

        if (!TextUtils.isEmpty(mInfo.getGrade())) {
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
            if(!TextUtils.isEmpty(mInfo.getCsContentIDs())) {
                seriesUUID = mInfo.getCsContentIDs().split("\\|")[0];
            }
            contentValues.put(DBConfig.CONTENTTYPE, Constant.CONTENTTYPE_PS);
            contentValues.put(DBConfig.PLAYID, mInfo.getContentUUID());
        }else{
            seriesUUID = mInfo.getContentID();
            if (index >= 0 && mInfo.getData() != null && index < mInfo.getData().size()) {
                if (mInfo.getData() != null && mInfo.getData().size() != 0) {
                    contentValues.put(DBConfig.PLAYID, mInfo.getData().get(index).getContentUUID());
                }
            }
        }

        contentValues.put(DBConfig.PLAYPOSITION, bundle.getString(DBConfig.PLAYPOSITION));
        contentValues.put(DBConfig.CONTENTUUID,seriesUUID);

        String updateTime = bundle.getString(DBConfig.UPDATE_TIME);
        if (!TextUtils.isEmpty(updateTime)) {
            contentValues.put(DBConfig.UPDATE_TIME, updateTime);
        } else {
            contentValues.put(DBConfig.UPDATE_TIME, Utils.getSysTime());
        }
        contentValues.put(DBConfig.USERID, userId);
        // contentValues.put(DBConfig.SUPERSCRIPT, mInfo.getrSuperScript());
        contentValues.put(DBConfig.CONTENT_DURATION, bundle.getString(DBConfig.CONTENT_DURATION));


        DataSupport.insertOrUpdate(tableName)
                .condition()
                .eq(DBConfig.CONTENTUUID, seriesUUID)
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
            contentValues.put(DBConfig.CONTENTUUID, entity.getContentID());
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
            contentValues.put(DBConfig.UPDATE_TIME, com.newtv.libs.util.Utils.getSysTime());
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

    public static void delAttention(String userId, String contentUuId, DBCallback<String> callback, String tableName) {
        DataSupport.delete(tableName).condition()
                .eq(DBConfig.CONTENTUUID, contentUuId)
                .eq(DBConfig.USERID, userId)
                .build()
                .withCallback(callback).excute();
    }

    public static void delHistory(String userId, String contentuuid, DBCallback<String> callback, String tableName) {
        DataSupport.delete(tableName).condition()
                .eq(DBConfig.CONTENTUUID, contentuuid)
                .eq(DBConfig.USERID, userId)
                .build()
                .withCallback(callback)
                .excute();
    }
}
