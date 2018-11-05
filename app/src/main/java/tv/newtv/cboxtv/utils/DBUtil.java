package tv.newtv.cboxtv.utils;

import android.content.ContentValues;
import android.text.TextUtils;

import com.newtv.cms.bean.Content;
import com.newtv.libs.Constant;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;
import com.newtv.libs.util.LogUtils;

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
    public static void UnSubcribe(String contentUuId, DBCallback<String> callback) {
        DataSupport.delete(DBConfig.SUBSCRIBE_TABLE_NAME).condition()
                .eq(DBConfig.CONTENTUUID, contentUuId)
                .build()
                .withCallback(callback).excute();
    }


    /**
     * 添加订阅
     *
     * @param entity
     * @param callback
     */
    public static void AddSubcribe(Content entity, DBCallback<String> callback) {
        //TODO 写入本地数据库 历史记录
        ContentValues contentValues = new ContentValues();
        if (entity.getContentID() != null) {
            contentValues.put(DBConfig.CONTENTUUID, entity.getContentID());
        }
        if (entity.getContentType() != null) {
            contentValues.put(DBConfig.CONTENTTYPE, entity.getContentType());
        }
        contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
        contentValues.put(DBConfig.IMAGEURL, entity.getVImage());
        contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());
        contentValues.put(DBConfig.UPDATE_TIME, com.newtv.libs.util.Utils.getSysTime());
        DataSupport.insertOrReplace(DBConfig.SUBSCRIBE_TABLE_NAME)
                .withValue(contentValues)
                .withCallback(callback).excute();
    }


    /**
     * 检查是否属于订阅
     *
     * @param contentUuId
     * @param callback
     */
    public static void CheckSubscrip(String contentUuId, DBCallback<String> callback) {
        DataSupport.search(DBConfig.SUBSCRIBE_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentUuId)
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
    public static void CheckCollect(String contentUuId, DBCallback<String> callback) {
        DataSupport.search(DBConfig.COLLECT_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENTUUID, contentUuId)
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
    public static void UnCollect(String contentUuId, DBCallback<String> callback) {
        DataSupport.delete(DBConfig.COLLECT_TABLE_NAME).condition()
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
    public static void PutCollect(Content entity, DBCallback<String> callback) {
        if (entity != null) {
            //TODO 写入本地数据库 历史记录
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBConfig.CONTENTUUID, entity.getContentID());
            contentValues.put(DBConfig.CONTENTTYPE, entity.getContentType());
            contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
            contentValues.put(DBConfig.IMAGEURL, entity.getVImage());
            contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());
            contentValues.put(DBConfig.UPDATE_TIME, com.newtv.libs.util.Utils.getSysTime());
            DataSupport.insertOrReplace(DBConfig.COLLECT_TABLE_NAME)
                    .withValue(contentValues)
                    .withCallback(callback).excute();
        }

    }

    public static void addHistory(Content mInfo, int index, int Position, DBCallback<String>
            callback) {
        if (mInfo == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();

        /* 添加视频相关信息 */
        if (mInfo.getContentType() != null) {//视频类型
            contentValues.put(DBConfig.CONTENTTYPE, mInfo.getContentType());
        }
        contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);//打开事件

        if (!TextUtils.isEmpty(mInfo.getVImage())) {//视频竖图
            contentValues.put(DBConfig.IMAGEURL, mInfo.getVImage());
        }
        if (!TextUtils.isEmpty(mInfo.getTitle())) {//视频标题
            contentValues.put(DBConfig.TITLE_NAME, mInfo.getTitle());
        }
        contentValues.put(DBConfig.PLAYINDEX, index);//播放索引
        contentValues.put(DBConfig.PLAYPOSITION, Position);//播放到的位置
        contentValues.put(DBConfig.UPDATE_TIME, com.newtv.libs.util.Utils.getSysTime());//更新日期

        /* 添加ID相关记录 */
        contentValues.put(DBConfig.CONTENTUUID, mInfo.getContentID());

        if (Constant.CONTENTTYPE_PG.equals(mInfo.getContentType()) || Constant.CONTENTTYPE_CP
                .equals(mInfo.getContentType())) {
            contentValues.put(DBConfig.PLAYID, mInfo.getContentUUID());
        } else {
            if (index >= 0 && mInfo.getData() != null && index < mInfo.getData().size()) {
                if (mInfo.getData() != null && mInfo.getData().size() != 0) {
                    contentValues.put(DBConfig.PLAYID, mInfo.getData().get(index).getContentID());
                }
            }
        }

        String seriesUUID = "";
        if (TextUtils.isEmpty(mInfo.getContentID())) {
            if (TextUtils.isEmpty(seriesUUID)) {
                return;
            }
            contentValues.put(DBConfig.CONTENTUUID, seriesUUID);
        } else {
            seriesUUID = mInfo.getCsContentIDs();
        }

        LogUtils.d("insert", contentValues.toString());
        DataSupport.insertOrUpdate(DBConfig.HISTORY_TABLE_NAME)
                .condition()
                .eq(DBConfig.CONTENTUUID, seriesUUID)
                .build()
                .withValue(contentValues)
                .withCallback(callback).excute();
    }

    public static void addAttention(Content entity, DBCallback<String> callback) {
        //TODO 写入本地数据库 历史记录
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
        contentValues.put(DBConfig.IMAGEURL, entity.getVImage());
        contentValues.put(DBConfig.ACTIONTYPE, Constant.OPEN_DETAILS);
        contentValues.put(DBConfig.TITLE_NAME, entity.getTitle());
        contentValues.put(DBConfig.UPDATE_TIME, com.newtv.libs.util.Utils.getSysTime());
        DataSupport.insertOrReplace(DBConfig.ATTENTION_TABLE_NAME)
                .withValue(contentValues)
                .withCallback(callback).excute();


    }

    public static void delAttention(String contentUuId, DBCallback<String> callback) {

        DataSupport.delete(DBConfig.ATTENTION_TABLE_NAME).condition()
                .eq(DBConfig.CONTENTUUID, contentUuId)
                .build()
                .withCallback(callback).excute();

    }
}
