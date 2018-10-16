package tv.newtv.cboxtv.cms;



/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms
 * 创建事件:         10:01
 * 创建人:           weihaichao
 * 创建日期:          2018/6/6
 */
public class DataCenter {

    private static DataCenter instance;

    public static DataCenter getInstance() {
        if (instance == null) {
            synchronized (DataCenter.class) {
                if (instance == null) instance = new DataCenter();
            }
        }
        return instance;
    }

}
