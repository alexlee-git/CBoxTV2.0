package tv.newtv.cboxtv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.newtv.cms.bean.Nav;
import com.newtv.cms.contract.AdContract;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.util.LogUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.libs
 * 创建事件:         10:52
 * 创建人:           weihaichao
 * 创建日期:          2018/11/7
 * <p>
 * <p>
 * <p>
 * <p>
 * page > 二级  > 一级
 */
public class BackGroundManager {

    private static BackGroundManager manager;
    private String mCurrentId;
    private HashMap<String, BGDrawable> bgHashmap = new HashMap<>();
    private AdContract.Presenter mAdPresenter;
    private HashMap<String, BgItem> backGroundMaps;
    private BGCallback mBGCallback;
    private List<Target> targetList = new ArrayList<>();
    private BgItem NavBgItem;

    private BackGroundManager() {
        backGroundMaps = new HashMap<>();
        mAdPresenter = new AdContract.AdPresenter(LauncherApplication.AppContext, null);
    }

    public static BackGroundManager getInstance() {
        if (manager == null) {
            synchronized (BackGroundManager.class) {
                if (manager == null) manager = new BackGroundManager();
            }
        }
        return manager;
    }

    /**
     * 设置导航ID
     *
     * @param id
     */
    public void setCurrentNav(String id, boolean waitPage) {
        if (backGroundMaps.containsKey(id)) {
            BgItem bgItem = backGroundMaps.get(id);
            if (bgItem.level == 0) {
                //TODO 显示一级导航背景图
                NavBgItem = bgItem;
            } else if (bgItem.level == 1) {
                NavBgItem = bgItem;
                String pid = bgItem.parentId;
                if (backGroundMaps.containsKey(pid)) {
                    NavBgItem = backGroundMaps.get(pid);
                }
            }
        } else {
            NavBgItem = null;
        }

        if (!waitPage) {
            if (NavBgItem == null) {
                clearBackGround();
            } else {
                setShowId(Libs.get().getContext(), id);
            }
        }

    }

    void registView(BGCallback callback) {
        mBGCallback = callback;
    }

    private void clearBackGround() {
        if (mBGCallback != null && mBGCallback.getTargetView() != null) {
            mBGCallback.getTargetView().setBackground(null);
        }
    }

    private void setShowId(Context context, String id) {
        mCurrentId = id;
        if (backGroundMaps.containsKey(id)) {
            BgItem current = backGroundMaps.get(id);
            if (current.isAd) {
                setAdBG(context, current);
            } else {
                setCmsBG(context, current);
            }
        } else {
            clearBackGround();
        }
    }

    public void setCurrentPageId(Context context, String id, boolean isAd, String background,
                                 boolean isShow) {
        if (isAd || !TextUtils.isEmpty(background)) {
            if (backGroundMaps.containsKey(id)) {
                BgItem bgItem = backGroundMaps.get(id);
                if (BgItem.FROM_NAV.equals(bgItem.from)) {
                    backGroundMaps.remove(id);
                    bgHashmap.remove(id);
                }
                NavBgItem = null;
            }
            if (!backGroundMaps.containsKey(id)) {
                BgItem bgItem = new BgItem();
                bgItem.contentId = id;
                bgItem.isAd = isAd;
                bgItem.background = background;
                bgItem.from = BgItem.FROM_PAGE;
                backGroundMaps.put(id, bgItem);
            }
        }
        if (isShow) {
            setShowId(context, id);
        }
    }

    /**
     * 加载广告背景图 TODO
     */
    private void setAdBG(final Context context, final BgItem bgItem) {
        if (!bgHashmap.containsKey(bgItem.contentId) || bgHashmap.get(bgItem.contentId).drawable
                == null) {
            mAdPresenter.getAdByType(Constant.AD_TOPIC, bgItem.contentId, "", null, new AdContract
                    .Callback() {
                @Override
                public void showAd(@Nullable String type, @Nullable String url, @NotNull HashMap<?,
                        ?> hashMap) {
                    if (TextUtils.isEmpty(url)) {
                        setCmsBG(context, bgItem);
                    } else {
                        loadImage(context, bgItem.contentId, url);
                    }
                }
            });
        } else {
            if (bgItem.contentId.equals(mCurrentId) && mBGCallback != null && mBGCallback
                    .getTargetView()
                    != null) {
                mBGCallback.getTargetView().setBackground(bgHashmap.get(bgItem.contentId).drawable);
            }
        }
    }

    // 如果cms设置为专题，加载cms背景图
    private void setCmsBG(Context context, BgItem bgItem) {
        if (!TextUtils.isEmpty(bgItem.background)) {
            loadImage(context, bgItem.contentId, bgItem.background);
        } else {
            clearBackGround();
        }
    }

    /**
     * 加载背景图
     *
     * @param context
     * @param uuid
     * @param url
     */
    private void loadImage(Context context, String uuid, String url) {
        if (!bgHashmap.containsKey(uuid) || bgHashmap.get(uuid).drawable == null) {
            bgHashmap.put(uuid, new BGDrawable(url));
            requestImage(bgHashmap.get(uuid), context, uuid, url, true);
        } else {
            if (uuid.equals(mCurrentId) && mBGCallback != null && mBGCallback.getTargetView() !=
                    null) {
                mBGCallback.getTargetView().setBackground(bgHashmap.get(uuid).drawable);
            }
        }
    }

    /**
     * 网络请求背景图片
     *
     * @param bgDrawable
     * @param context
     * @param uuid
     * @param url
     */
    private void requestImage(final BGDrawable bgDrawable, final Context context,
                              final String uuid,
                              final String url, final boolean isRetry) {
        Log.i("BackGroundManager", "requestImage: " + url);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bgDrawable.drawable = new BitmapDrawable(context.getResources(), bitmap);
                if (uuid.equals(mCurrentId) && mBGCallback != null && mBGCallback.getTargetView()
                        != null) {
                    mBGCallback.getTargetView().setBackground(bgHashmap.get(uuid).drawable);
                }
                targetList.remove(this);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                if (isRetry) {
                    requestImage(bgDrawable, context, uuid, url, false);
                } else {
                    bgHashmap.remove(uuid);
                    clearBackGround();
                }
                targetList.remove(this);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (uuid.equals(mCurrentId) && mBGCallback != null && mBGCallback.getTargetView()
                        != null) {
                    mBGCallback.getTargetView().setBackground(placeHolderDrawable);
                }
            }
        };
        targetList.add(target);
        Picasso.get().load(url).into(target);
    }

    private void parseNav(Nav nav, int level, String parent) {
        if (!TextUtils.isEmpty(nav.getPoster())) {
            BgItem bgItem = new BgItem();
            bgItem.contentId = nav.getId();
            bgItem.level = level;
            bgItem.isAd = false;
            bgItem.background = nav.getPoster();
            bgItem.parentId = parent;
            bgItem.from = BgItem.FROM_NAV;
            backGroundMaps.put(nav.getId(), bgItem);
        }
    }

    private void parseNav(List<Nav> navList, int level, String parentId) {
        for (Nav nav : navList) {
            parseNav(nav, level, parentId);
            if (nav.getChild() != null && nav.getChild().size() > 0) {
                parseNav(nav.getChild(), level + 1, nav.getId());
            }
        }
    }

    public void parseNavigation(List<Nav> navList) {
        parseNav(navList, 0, null);
        LogUtils.e("background", backGroundMaps.toString());
    }

    public interface BGCallback {
        /**
         * 获取当前注册方，要更改背景的View
         */
        View getTargetView();
    }

    private static class BgItem {

        private static final String FROM_NAV = "Nav";       //来源于导航
        private static final String FROM_PAGE = "Page";     //来源于页面

        private String background;  //  背景图
        private String contentId;   //  ID
        private int level;          //  对应 FROM_NAV  0为1级导航，1为2级导航 2为Page 以此类推
        private String parentId;    //  父级ID 对应FROM_NAV 如果该导航存在父级，则该ID对应父级的contentId
        private String from;        //  来源
        private boolean isAd = false; // 是否来源于广告系统

        @Override
        public String toString() {
            return "BgItem{" +
                    "background='" + background + '\'' +
                    ", contentId='" + contentId + '\'' +
                    ", level=" + level +
                    ", parentId='" + parentId + '\'' +
                    '}';
        }
    }

    static class BGDrawable {
        public BitmapDrawable drawable;
        public String url;

        BGDrawable(String url) {
            this.url = url;
        }
    }
}
