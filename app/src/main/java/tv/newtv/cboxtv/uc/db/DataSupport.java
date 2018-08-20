package tv.newtv.cboxtv.uc.db;

import android.content.Context;

import tv.newtv.cboxtv.LauncherApplication;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.newtv.dancetv.db
 * 创建事件:         12:33
 * 创建人:           weihaichao
 * 创建日期:          2018/2/24
 */

public class DataSupport {

    private static DataSupport dataSupport;
    private DataHelper dataHelper;

    /**
     * @param context
     */
    public static void init(Context context) {
        if (dataSupport == null) {
            synchronized (DataSupport.class) {
                if (dataSupport == null) dataSupport = new DataSupport(context);
            }
        }
    }

    private DataSupport(Context context) {
        dataHelper = new DataHelper(context);
    }

    /**
     * 更新
     * @param tableName
     * @return
     */
    public static SqlBuilder update(String tableName) {
//        if(dataSupport == null) {
//            DataSupport.init(LauncherApplication.AppContext);
//        }
        return SqlBuilder.create()
                .withTable(tableName)
                .withSqlAction(SqlExcuters.ACTION_UPDATE)
                .withDatabase(dataSupport.dataHelper.getWritableDatabase());
    }

    /**
     * 删除
     * @param tableName
     * @return
     */
    public static SqlBuilder delete(String tableName) {
//        if(dataSupport == null) {
//            DataSupport.init(LauncherApplication.AppContext);
//        }
        return SqlBuilder.create()
                .withSqlAction(SqlExcuters.ACTION_DEL)
                .withTable(tableName)
                .withValue(null)
                .withDatabase(dataSupport.dataHelper.getWritableDatabase());
    }

    /**
     * 插入
     * @param tableName
     * @return
     */
    public static SqlBuilder insert(String tableName) {
//        if(dataSupport == null) {
//            DataSupport.init(LauncherApplication.AppContext);
//        }
        return SqlBuilder.create()
                .withSqlAction(SqlExcuters.ACTION_INSERT)
                .withTable(tableName)
                .withDatabase(dataSupport.dataHelper.getWritableDatabase());
    }

    /**
     * 查找
     * @param tableName
     * @return
     */
    public static SqlBuilder search(String tableName) {
//        if(dataSupport == null) {
//            DataSupport.init(LauncherApplication.AppContext);
//        }
        return SqlBuilder.create()
                .withSqlAction(SqlExcuters.ACTION_SELECT)
                .withTable(tableName)
                .withValue(null)
                .withDatabase(dataSupport.dataHelper.getWritableDatabase());
    }

    /**
     * 插入全部
     * @param tableName
     * @return
     */
    public static SqlBuilder insertAll(String tableName) {
//        if(dataSupport == null) {
//            DataSupport.init(LauncherApplication.AppContext);
//        }
        return SqlBuilder.create()
                .withSqlAction(SqlExcuters.ACTION_INSERT_ALL)
                .withTable(tableName)
                .withDatabase(dataSupport.dataHelper.getWritableDatabase());
    }

    /**
     * 插入或更新
     * @param tableName
     * @return
     */
    public static SqlBuilder insertOrUpdate(String tableName) {
//        if(dataSupport == null) {
//            DataSupport.init(LauncherApplication.AppContext);
//        }
        return SqlBuilder.create()
                .withSqlAction(SqlExcuters.ACTION_INSERT_OR_UPDATE)
                .withTable(tableName)
                .withDatabase(dataSupport.dataHelper.getWritableDatabase());
    }

    /**
     * 插入或替换
     * @param tableName
     * @return
     */
    public static SqlBuilder insertOrReplace(String tableName) {
//        if(dataSupport == null) {
//            DataSupport.init(LauncherApplication.AppContext);
//        }
        return SqlBuilder.create()
                .withSqlAction(SqlExcuters.ACTION_INSERT_OR_REPLACE)
                .withTable(tableName)
                .withDatabase(dataSupport.dataHelper.getWritableDatabase());
    }


}
