package tv.newtv.cboxtv.uc.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.newtv.dancetv.db.v1
 * 创建事件:         09:33
 * 创建人:           weihaichao
 * 创建日期:          2018/2/26
 */

public class SqlBuilder {
    private String mTableName;                  // 表名称
    private int mAction;                        // 数据库操作事件
    private ContentValues mValues;              // 更新的数据集
    private ContentValues[] valueArray;         // 更新的数据集队列
    private DBCallback<String> mCallback;       // 数据库事件回调接口
    private SQLiteDatabase mDatabase;           // SqliteDatabase实例
    private SqlCondition mCondition;            // 搜索条件

    private SqlBuilder() {}

    SqlBuilder withDatabase(SQLiteDatabase database) {
        mDatabase = database;
        return this;
    }

    static SqlBuilder create() {
        return new SqlBuilder();
    }

    SqlBuilder withSqlAction(int action) {
        mAction = action;
        return this;
    }

    SqlBuilder withTable(String table) {
        mTableName = table;
        return this;
    }

    public SqlBuilder withValue(ContentValues contentValues) {
        mValues = contentValues;
        return this;
    }

    public SqlBuilder withValues(ContentValues[] contentValues) {
        valueArray = contentValues;
        return this;
    }

    public SqlBuilder withCallback(DBCallback<String> callback) {
        mCallback = callback;
        return this;
    }

    public SqlCondition condition() {
        if (mCondition == null) {
            mCondition = SqlCondition.prepare(this);
        }
        return mCondition;
    }


    public void excute() {
        SqlExcuters.excute(mAction, mTableName, mDatabase, mCondition, mAction == SqlExcuters
                        .ACTION_INSERT_ALL ? valueArray : mValues,
                mCallback);
    }
}
