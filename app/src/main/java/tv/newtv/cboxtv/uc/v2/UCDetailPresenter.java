package tv.newtv.cboxtv.uc.v2;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newtv.libs.db.DBCallback;
import com.newtv.libs.db.DBConfig;
import com.newtv.libs.db.DataSupport;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc
 * 创建事件:         16:35
 * 创建人:           weihaichao
 * 创建日期:          2018/8/27
 */
class UCDetailPresenter<T> implements DBCallback<String> {
    private int mType;

    private @Nullable
    DetailCallback<T> mDetailCallback;

    private Gson mGson;

    UCDetailPresenter(@BaseUCDetailActivity.DetailType int type, @Nullable DetailCallback<T> callback) {
        mType = type;
        mDetailCallback = callback;
        mGson = new Gson();
    }

    public void onStop() {

    }

    public void onDestroy() {
        mDetailCallback = null;
        mGson = null;
    }

    public void requestData() {
        String tableName = getTableName();
        if (TextUtils.isEmpty(tableName)) {
            if (mDetailCallback != null) {
                mDetailCallback.onResult(new ArrayList<T>());
            }
            return;
        }
        DataSupport.search(tableName)
                .condition()
                .OrderBy(DBConfig.ORDER_BY_TIME)
                .build()
                .withCallback(this)
                .excute();
    }

    private String getTableName() {
        switch (mType) {
            case BaseUCDetailActivity.DETAIL_TYPE_HISTORY:
                return DBConfig.HISTORY_TABLE_NAME;
            case BaseUCDetailActivity.DETAIL_TYPE_COLLECTION:
                return  DBConfig.COLLECT_TABLE_NAME;
            case BaseUCDetailActivity.DETAIL_TYPE_ATTENTION:
                return DBConfig.ATTENTION_TABLE_NAME;
            case BaseUCDetailActivity.DETAIL_TYPE_SUBSCRIBE:
                return DBConfig.SUBSCRIBE_TABLE_NAME;
            default:
                return null;
        }
    }

    @Override
    public void onResult(int code, String result) {
        if(mDetailCallback == null) return;
        if (TextUtils.isEmpty(result)) {
            if (mDetailCallback != null)
                mDetailCallback.onResult(new ArrayList<T>());
            return;
        }
        Type type = new TypeToken<List<T>>() {
        }.getType();
        List<T> returnValue = mGson.fromJson(result, type);

        if (mDetailCallback != null)
            mDetailCallback.onResult(returnValue);
    }
}
