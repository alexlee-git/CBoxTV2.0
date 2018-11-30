package tv.newtv.cboxtv.player.contract;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.contract
 * 创建事件:         14:56
 * 创建人:           weihaichao
 * 创建日期:          2018/11/30
 */
class Cache {

    private static final Cache ourInstance = new Cache();

    static Cache getInstance() {
        return ourInstance;
    }

    private Cache() {

    }
}
