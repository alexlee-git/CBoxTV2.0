package tv.newtv.key;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.utils.DeviceUtil;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv
 * 创建事件:         17:44
 * 创建人:           weihaichao
 * 创建日期:          2018/6/13
 */
public final class KeyHelper {
    private static final String TAG = KeyHelper.class.getSimpleName();

    private static AbstractKey keyInstance;

    private KeyHelper() {
    }

    /**
     * 检查Key类型
     *
     * @param event 按键类型
     * @return Key类型
     */
    public static KeyType getKeyType(KeyEvent event) {
        if (keyInstance == null || !isSupport(event)) return KeyType.UNKNOWN;
        return keyInstance.getKeyType(event);
    }

    /**
     * 检查Key类型
     *
     * @param event 按键类型
     * @return Key类型
     */
    public static boolean isKeyType(KeyEvent event, KeyType keyType) {
        return keyInstance != null && isSupport(event) && keyInstance.isKeyType(event, keyType);
    }

    /**
     * 检查Key类型
     *
     * @param event 按键类型
     * @return Key类型
     */
    public static boolean isKeyTypeWithAction(KeyEvent event, KeyType keyType, int Action) {
        return keyInstance != null && isSupport(event) && keyInstance.isKeyType(event, keyType,
                Action);
    }

    /**
     * @param event
     * @return
     */
    public static boolean isBack(KeyEvent event) {
        return keyInstance != null && isSupport(event) && keyInstance.isKeyType(event, KeyType
                .BACK);
    }

    /**
     * @param event
     * @param action
     * @return
     */
    public static boolean isBack(KeyEvent event, int action) {
        return keyInstance != null && isSupport(event) && keyInstance.isKeyType(event, KeyType
                .BACK, action);
    }

    @SuppressWarnings("ConstantConditions")
    public static void init(Context context) {
        if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA)) {
            keyInstance = new XunMaKey();
        } else {
            keyInstance = new DefaultKey();
        }

        Log.d(TAG, keyInstance != null ? "init keyAction=" + keyInstance.getKeyName() : "init key" +
                " = null");
    }

    private static boolean isSupport(KeyEvent event) {
        return keyInstance != null && keyInstance.isSupport(event);
    }
}
