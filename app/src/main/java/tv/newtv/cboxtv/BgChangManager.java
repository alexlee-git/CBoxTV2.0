package tv.newtv.cboxtv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.cms.details.view.ADSdkCallback;
import tv.newtv.cboxtv.cms.mainPage.menu.BGEvent;
import tv.newtv.cboxtv.cms.util.ADsdkUtils;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         14:13
 * 创建人:           weihaichao
 * 创建日期:          2018/6/5
 */
public class BgChangManager {
    private static final String TAG = "BgChangManager";

    private static BgChangManager instance;
    private String mCurrentId;
    private BGCallback mCallback;
    private HashMap<String, BGDrawable> bgHashmap = new HashMap<>();

    private Map<String,BGEvent> secondLevelMap = new HashMap<>();
    private List<BGEvent> firstLevel = new ArrayList<>();

    private Context applicationContext;
    private int retry = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(++retry < 10){
                setCurrent(applicationContext,mCurrentId);
            }
        }
    };

    public static BgChangManager getInstance() {
        if (instance == null) {
            synchronized (BgChangManager.class) {
                if (instance == null) {
                    instance = new BgChangManager();
                }
            }
        }
        return instance;
    }

    /**
     * 注册当前更换背景事件
     *
     * @param callback
     */
    public void registTargetView(BGCallback callback) {
        mCallback = callback;
    }

    public void dispatchFirstLevelEvent(Context context,BGEvent event){
        BGEvent bgEvent = addToFirstLevel(event);
        setCurrent(bgEvent.contentUUID);
        dispatchEvent(context,bgEvent);
    }

    /**
     * 触发更换背景事件
     *
     * @param context
     * @param event
     */
    public void dispatchEvent(Context context, BGEvent event) {
        if(event == null || event.contentUUID == null || !event.contentUUID.equals(mCurrentId)){
            return;
        }

        if(isCancel(event.contentUUID)){
            return;
        }
        if (bgHashmap.containsKey(event.contentUUID)) {
            BGDrawable bgDrawable = bgHashmap.get(event.contentUUID);
            if (bgDrawable.drawable != null) {
                mCallback.getTargetView().setBackground(bgDrawable.drawable);
                return;
            }
        }

        if (event.isAd) {
            setAdBG(context, event);
        } else {
            setCmsBG(context, event);
        }
    }

    public void addEvent(Context context,BGEvent bgEvent){
        //专题频道
        if(mCurrentId.equals(bgEvent.contentUUID) && searchInFirstLevel(bgEvent.contentUUID) != null){
            firstLevel.remove(searchInFirstLevel(bgEvent.contentUUID));
            firstLevel.add(bgEvent);
            dispatchEvent(context,bgEvent);
            return;
        }

        //提供二级频道数据
        if(!secondLevelMap.containsKey(bgEvent.contentUUID)){
            secondLevelMap.put(bgEvent.contentUUID,bgEvent);
        }else{
            BGEvent event = secondLevelMap.get(bgEvent.contentUUID);
            if(!event.equals(bgEvent)){
                secondLevelMap.put(bgEvent.contentUUID, bgEvent);
            }
        }
    }

    /**
     * 加载广告背景图 TODO
     */
    private void setAdBG(final Context context, final BGEvent bgEvent) {
        ADsdkUtils.getAD(Constant.AD_TOPIC, bgEvent.contentUUID, -1, new ADSdkCallback() {
            @Override
            public void showAd(String type, String url) {
                super.showAd(type, url);
                if (TextUtils.isEmpty(url)) {
                    setCmsBG(context, bgEvent);
                } else {
                    loadImage(context, bgEvent.contentUUID, url);
                }
            }
        });
    }

    // 如果cms设置为专题，加载cms背景图
    private void setCmsBG(Context context, BGEvent bgEvent) {
        if (!TextUtils.isEmpty(bgEvent.bgImageUrl)) {
            loadImage(context, bgEvent.contentUUID, bgEvent.bgImageUrl);
        }else {
            mCallback.getTargetView().setBackground(null);
        }
    }

    public void setCurrent(String uuid){
        mCurrentId = uuid;
    }

    /**
     * 设置当前显示的UUID对应
     * @param context
     * @param uuid
     */
    public void setCurrent(Context context, String uuid) {
        mCurrentId = uuid;
        handler.removeCallbacksAndMessages(null);
        if(secondLevelMap.get(uuid) == null){
            applicationContext = context.getApplicationContext();
            handler.sendEmptyMessageDelayed(0,1000);
            return;
        }
        retry = 0;
        dispatchEvent(context, secondLevelMap.get(uuid));
    }

    /**
     * 加载背景图
     * @param context
     * @param uuid
     * @param url
     */
    private void loadImage(Context context, String uuid, String url) {
        if (BuildConfig.DEBUG) {
            if (url.contains("http://172.25.102.19/")) {
                url = url.replace("http://172.25.102.19/", "http://111.32.132.156/");
            }
            if (url.contains("http://172.25.101.210/")) {
                url = url.replace("http://172.25.101.210/", "http://111.32.132.156/");
            }
        }

        if (!bgHashmap.containsKey(uuid) || bgHashmap.get(uuid).drawable == null) {
            bgHashmap.put(uuid, new BGDrawable(url));
            requestImage(bgHashmap.get(uuid), context, uuid, url);
        } else {
            if (uuid.equals(mCurrentId) && mCallback != null) {
                mCallback.getTargetView().setBackground(bgHashmap.get(uuid).drawable);
            }
        }
    }

    /**
     * 网络请求背景图片
     * @param bgDrawable
     * @param context
     * @param uuid
     * @param url
     */
    private void requestImage(final BGDrawable bgDrawable, final Context context, final String uuid,
                              final String url) {
        Picasso.get().load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bgDrawable.drawable = new BitmapDrawable(context.getResources(), bitmap);
                if (uuid.equals(mCurrentId) && mCallback != null) {
                    mCallback.getTargetView().setBackground(bgHashmap.get(uuid).drawable);
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                mCallback.getTargetView().setBackground(null);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    public void add(String parentUUID,String uuid){
        for(BGEvent bgEvent : firstLevel){
            if(bgEvent.contentUUID.equals(parentUUID)){
                bgEvent.add(uuid);
                break;
            }
        }
    }

    public BGEvent addToFirstLevel(BGEvent bgEvent){
        for(BGEvent bg : firstLevel){
            if(bg.contentUUID.equals(bgEvent.contentUUID)){
                return bg;
            }
        }
        firstLevel.add(bgEvent);
        return bgEvent;
    }

    /**
     * 一级频道广告可以拦截二级频道广告
     * @param uuid
     * @return
     */
    public boolean isCancel(String uuid){
        BGEvent bgEvent = searchParentInFirstLevel(uuid);
        if (bgEvent != null && bgEvent.isAd && bgEvent.bgImageUrl != null){
            return true;
        }
        return false;
    }

    public BGEvent searchInFirstLevel(String uuid){
        for(BGEvent bg : firstLevel){
            if(bg.contentUUID.equals(uuid)){
                return bg;
            }
        }
        return null;
    }

    private BGEvent searchParentInFirstLevel(String uuid){
        for(BGEvent bgEvent : firstLevel){
            if(bgEvent.childSet != null && bgEvent.childSet.size() > 0){
                if(bgEvent.childSet.contains(uuid)){
                    return bgEvent;
                }
            }
        }
        return null;
    }

    public interface BGCallback {

        /**
         * 获取当前注册方，要更改背景的View
         * @return
         */
        View getTargetView();
    }

    static class BGDrawable {
        public BitmapDrawable drawable;
        public String url;

        BGDrawable(String url) {
            this.url = url;
        }
    }

}
