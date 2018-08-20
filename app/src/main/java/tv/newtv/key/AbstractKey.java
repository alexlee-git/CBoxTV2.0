package tv.newtv.key;

import android.util.Log;
import android.view.KeyEvent;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv
 * 创建事件:         17:47
 * 创建人:           weihaichao
 * 创建日期:          2018/6/13
 */
public abstract class AbstractKey {

    private static final String TAG = "key";

    abstract KeyType getKeyType(KeyEvent event);

    abstract String getKeyName();

    /**
     * @param event   按键事件
     * @param keyType 按键类型
     * @param action  KeyEvent.ACTION_UP || KeyEvent.ACTION_DOWN
     * @return
     * @see KeyType
     */
    public boolean isKeyType(KeyEvent event, KeyType keyType, int action) {
        if (event == null || keyType == null) return false;
        KeyType type = getKeyType(event);
        return type == keyType && action == event.getAction();
    }

    /**
     * @param event
     * @return
     */
    public boolean isSpecialKeyType(KeyEvent event) {
        if (event == null) return false;
        KeyType type = getKeyType(event);
        return type == KeyType.VOLUMN_DOWN
                || type == KeyType.VOLUMN_UP
                || type == KeyType.VOLUMN_MUTE;
    }

    /**
     * @param event
     * @param keyType
     * @return
     */
    public boolean isKeyType(KeyEvent event, KeyType keyType) {
        if (event == null || keyType == null) return false;
        KeyType type = getKeyType(event);
        return type == keyType;
    }

    /**
     * @param event
     * @return
     */
    public boolean isSupport(KeyEvent event) {
        if (event == null) return false;
        KeyType keyType = getKeyType(event);
        if (keyType == KeyType.UNKNOWN) {
            Log.d("AbstractKey", event.toString());
        }
        return keyType != KeyType.UNKNOWN;
    }
}
