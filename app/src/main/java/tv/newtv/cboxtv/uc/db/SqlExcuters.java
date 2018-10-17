package tv.newtv.cboxtv.uc.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import tv.newtv.cboxtv.cms.MainLooper;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.newtv.dancetv.db
 * 创建事件:         12:39
 * 创建人:           weihaichao
 * 创建日期:          2018/2/24
 */

class SqlExcuters {
    static final int ACTION_INSERT = 0x01;
    static final int ACTION_DEL = 0x02;
    static final int ACTION_UPDATE = 0x03;
    static final int ACTION_SELECT = 0x04;
    static final int ACTION_INSERT_ALL = 0x05;
    static final int ACTION_INSERT_OR_UPDATE = 0x06;
    static final int ACTION_INSERT_OR_REPLACE = 0x07;

    private static SqlExcuters excuters = new SqlExcuters();

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private SqlExcuters() {
    }

    /**
     * @param action
     * @param args
     * @return
     */
    @SuppressWarnings("unchecked,unused")
    static void excute(int action, Object... args) {
        switch (action) {
            case ACTION_INSERT:
                excuters.executorService.execute(new InsertRunnable(args));
                break;
            case ACTION_DEL:
                excuters.executorService.execute(new DelRunnable(args));
                break;
            case ACTION_SELECT:
                excuters.executorService.execute(new SearchRunnable(args));
                break;
            case ACTION_UPDATE:
                excuters.executorService.execute(new UpdateRunnable(args));
                break;
            case ACTION_INSERT_ALL:
                excuters.executorService.execute(new InsertAllRunnable(args));
                break;
            case ACTION_INSERT_OR_UPDATE:
                excuters.executorService.execute(new InsertOrUpdateRunnable(args));
                break;
            case ACTION_INSERT_OR_REPLACE:
                excuters.executorService.execute(new InsertOrReplaceRunnable(args));
                break;
        }
    }

    /**
     * 插入数据
     */
    @SuppressWarnings("unchecked")
    private static class InsertRunnable implements Runnable {
        private String tableName;
        private SQLiteDatabase db;
        private ContentValues contentValues;
        private DBCallback callback;

        InsertRunnable(Object... args) {
            tableName = (String) args[0];
            db = (SQLiteDatabase) args[1];
            contentValues = (ContentValues) args[3];
            callback = (DBCallback) args[4];
        }

        @Override
        public void run() {
            final long result = db.insert(tableName, null, contentValues);
            MainLooper.get().post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null)
                        callback.onResult(result != -1 ? 0 : (int) result, "");
                }
            });

        }
    }

    /**
     * 插入数据
     */
    @SuppressWarnings("unchecked")
    private static class InsertAllRunnable implements Runnable {
        private String tableName;
        private SQLiteDatabase db;
        private ContentValues[] contentValues;
        private DBCallback callback;

        InsertAllRunnable(Object... args) {
            tableName = (String) args[0];
            db = (SQLiteDatabase) args[1];
            contentValues = (ContentValues[]) args[3];
            callback = (DBCallback) args[4];
        }

        @Override
        public void run() {
            String sql = "REPLACE INTO " + tableName + " (%s) VALUES (%s)";
            db.beginTransaction();
            SQLiteStatement stmt = null;
            List<String> keys = null;
            /* 批量插入值 */
            for (ContentValues contentValue : contentValues) {
                Set<String> keySet = contentValue.keySet();
                if (keys == null) {
                    /* 构建SQL模板 */
                    keys = new ArrayList<>();
                    keys.addAll(keySet);
                    sql = buildSql(keys, sql);
                    stmt = db.compileStatement(sql);
                }
                /* 插入值 */
                int index = 1;
                for (String key : keySet) {
                    String value = contentValue.getAsString(key);
                    if (!TextUtils.isEmpty(value)) {
                        stmt.bindString(index, value);
                    }
                    index++;
                }
                stmt.executeInsert();
                stmt.clearBindings();
            }

            db.setTransactionSuccessful();
            db.endTransaction();

            MainLooper.get().post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null)
                        callback.onResult(0, "");
                }
            });

        }

        String buildSql(List<String> keys, String tempSql) {
            StringBuilder keyStr = new StringBuilder();
            StringBuilder valueStr = new StringBuilder();
            for (String key : keys) {
                if (keyStr.length() > 0) keyStr.append(",");
                keyStr.append(key);

                if (valueStr.length() > 0) valueStr.append(",");
                valueStr.append("?");
            }
            String sqlResult = String.format(tempSql, keyStr.toString(), valueStr.toString());
            Log.e("sqlExcutor", "buildsql = " + sqlResult);
            return sqlResult;
        }
    }

    /**
     * 更新数据
     */
    @SuppressWarnings("unchecked")
    public static class UpdateRunnable implements Runnable {
        private String tableName;
        private SQLiteDatabase db;
        private ContentValues contentValues;
        private SqlCondition condition;
        private DBCallback callback;

        UpdateRunnable(Object... args) {
            tableName = (String) args[0];
            db = (SQLiteDatabase) args[1];
            condition = (SqlCondition) args[2];
            contentValues = (ContentValues) args[3];
            callback = (DBCallback) args[4];
        }

        @Override
        public void run() {
            final int result = db.update(tableName, contentValues, condition.getClause(), condition
                    .getArgs());
            MainLooper.get().post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null)
                        callback.onResult(result != -1 ? 0 : result, "");
                }
            });

        }
    }

    /**
     * 删除数据
     */
    @SuppressWarnings("unchecked")
    public static class DelRunnable implements Runnable {
        private String tableName;
        private SQLiteDatabase db;
        private SqlCondition condition;
        private DBCallback callback;

        DelRunnable(Object... args) {
            tableName = (String) args[0];
            db = (SQLiteDatabase) args[1];
            condition = (SqlCondition) args[2];
            callback = (DBCallback) args[4];
        }

        @Override
        public void run() {
            final int result = db.delete(tableName, condition.getClause(), condition.getArgs());
            MainLooper.get().post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null)
                        callback.onResult(result != -1 ? 0 : result, "");
                }
            });

        }
    }

    public static class InsertOrReplaceRunnable implements Runnable {
        private static final String INSERT_OR_REPLACT_SQL =
                "INSERT OR REPLACE INTO %s (%s) VALUES (%s)";
        private String tableName;
        private SQLiteDatabase db;
        private ContentValues contentValues;
        private SqlCondition condition;
        private DBCallback callback;

        InsertOrReplaceRunnable(Object... args) {
            tableName = (String) args[0];
            db = (SQLiteDatabase) args[1];
            condition = (SqlCondition) args[2];
            contentValues = (ContentValues) args[3];
            callback = (DBCallback) args[4];
        }


        @Override
        public void run() {
            Set<String> keys = contentValues.keySet();
            String[] values = new String[keys.size()];
            StringBuilder keyStr = new StringBuilder();
            StringBuilder valueStr = new StringBuilder();
            int index = 0;
            String id = contentValues.getAsString(DBConfig.CONTENTUUID);
            for (String key : keys) {
                if (keyStr.length() > 0) keyStr.append(",");
                keyStr.append(key);
                if (valueStr.length() > 0) valueStr.append(",");
                values[index] = contentValues.getAsString(key);
                valueStr.append("?");
                index++;
            }
            String sqlResult = String.format(Locale.getDefault(), INSERT_OR_REPLACT_SQL,
                    tableName, keyStr.toString(),
                    valueStr.toString());
            db.execSQL(sqlResult, values);
            MainLooper.get().post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null)
                        callback.onResult(0, "数据更新成功");
                }
            });

        }
    }

    /**
     * 删除数据
     */
    @SuppressWarnings("unchecked")
    public static class InsertOrUpdateRunnable implements Runnable {
        private String tableName;
        private SQLiteDatabase db;
        private ContentValues contentValues;
        private SqlCondition condition;
        private DBCallback callback;

        InsertOrUpdateRunnable(Object... args) {
            tableName = (String) args[0];
            db = (SQLiteDatabase) args[1];
            condition = (SqlCondition) args[2];
            contentValues = (ContentValues) args[3];
            callback = (DBCallback) args[4];
        }

        @Override
        public void run() {
            long id = db.insertWithOnConflict(tableName, null, contentValues, SQLiteDatabase
                    .CONFLICT_IGNORE);
            if (id == -1) {
                final int result = db.update(tableName, contentValues, condition.getClause(), condition
                        .getArgs());
                MainLooper.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onResult(result != -1 ? 0 : result, result != -1 ? "更新成功" : "更新失败");
                        }
                    }
                });

            } else {
                MainLooper.get().post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null)
                            callback.onResult(0, "写入成功");
                    }
                });

            }

        }
    }

    /**
     * 搜索数据
     */
    @SuppressWarnings("unchecked")
    public static class SearchRunnable implements Runnable {
        private String tableName;
        private SQLiteDatabase db;
        private SqlCondition condition;
        private DBCallback<String> callback;

        SearchRunnable(Object... args) {
            tableName = (String) args[0];
            db = (SQLiteDatabase) args[1];
            condition = (SqlCondition) args[2];
            callback = (DBCallback<String>) args[4];
        }

        @Override
        public void run() {
            Cursor cursor = null;
            if (condition != null) {
                cursor = db.query(condition.getDistinct(), tableName, condition.getSelect(),
                        condition.getClause(),
                        condition.getArgs(),
                        condition.getGroupBy(), null, condition.getOrderBy(), condition.getLimit());
            } else {
                cursor = db.query(false, tableName, null, null, null, null, null, null,
                        null);
            }
            JsonArray videoInfos = null;

            try{
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getCount() > 0) {
                        videoInfos = new JsonArray();
                        do {
                            videoInfos.add(translateCursor(cursor));
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }


            final JsonArray finalVideoInfos = videoInfos;
            MainLooper.get().post(new Runnable() {
                @Override
                public void run() {
                    if (callback != null)
                        callback.onResult(0, finalVideoInfos != null ? finalVideoInfos.toString() : "");
                }
            });

        }

        private JsonObject translateCursor(Cursor cursor) {
            JsonObject videoInfo = new JsonObject();
            for (String name : cursor.getColumnNames()) {
                int index = cursor.getColumnIndex(name);
                int type = cursor.getType(index);
                switch (type) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        int intValue = cursor.getInt(index);
                        videoInfo.addProperty(name, intValue);
                        break;
                    default:
                        String strValue = cursor.getString(index);
                        videoInfo.addProperty(name, TextUtils.isEmpty(strValue) ? null :
                                strValue);
                        break;
                }
            }
            return videoInfo;
        }

    }
}
