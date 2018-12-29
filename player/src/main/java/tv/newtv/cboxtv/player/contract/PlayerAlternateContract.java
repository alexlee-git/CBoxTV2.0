package tv.newtv.cboxtv.player.contract;

import android.content.Context;
import android.text.TextUtils;

import com.newtv.cms.AlternateRefresh;
import com.newtv.cms.CmsErrorCode;
import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IAlternate;
import com.newtv.cms.bean.Alternate;
import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;
import com.newtv.cms.util.CmsUtil;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.ServerTime;
import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import tv.newtv.cboxtv.player.Player;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.contract
 * 创建事件:         10:55
 * 创建人:           weihaichao
 * 创建日期:          2018/11/29
 */
public class PlayerAlternateContract {
    public interface View extends ICmsView {
        void onAlternateResult(String alternateId,List<Alternate> alternateList, int
                currentPlayIndex, String title, String channelId);

        void onAlternateTimeChange(String current, String end);

        void onAlterItemResult(String contentId, Content content, boolean isLive, boolean isFirst);

        void onAlternateError(String code, String desc);
    }

    public interface Presenter extends ICmsPresenter {
        void requestAlternate(String alternateId, String title, String channelId);

        void updateAlternate(String alternateId, String title, String channelId);

        Alternate getCurrentAlternate();

        int getCurrentPlayIndex();

        String getCurrrentTitle();

        boolean equalsAlternate(String id);

        String getCurrrentChannel();

        boolean playNext();

        String needRequestAd();

        boolean needTipAlternate();

        void alternateTipComplete();

        void addHistory();

        void playAlternateItem(Alternate current);
    }

    public static class AlternatePresenter extends CmsServicePresenter<View> implements
            Presenter, ContentContract.View, AlternateRefresh.AlternateCallback {

        private static final String TAG = "AlternatePresenter";
        private String currentAlternateId;                  //当前轮播台ID
        private String currrentTitle;                       //当前轮播台名称
        private String currrentChannel;                     //当前轮播台台号
        private boolean needShowChangeView = true;          //是否需要显示切台广告
        private IAlternate mAlternate;                      // 轮播请求接口
        private String currentRequestId;                    //当前请求数据的ID
        private String currentSubUUID;                      //
        private boolean isFirstChangeAlternate = false;     //是否为切台之后第一次播放
        private ContentContract.Presenter mContent;         // 内容请求接口

        private List<Alternate> mAlternates;                //当前轮播内容列表
        private Alternate currentAlternate;                 //当前播放的轮播内容

        private int currentPlayIndex = 0;

        private Long requestID = 0L;

        private Disposable mDisposable;
        private Long endTime = 0L;

        private boolean isLive = false;

        private String mNeedRequestAd = "0";

        private Observable<Long> observable;

        private AlternateRefresh mAlternateRefresh;

        public AlternatePresenter(@NotNull Context context, @Nullable View view) {
            super(context, view);
            mAlternate = getService(SERVICE_ALTERNATE);
            mContent = new ContentContract.ContentPresenter(getContext(), this);
            observable = Observable.interval(1000, TimeUnit.MILLISECONDS);
        }

        public boolean equalsAlternate(String id) {
            return TextUtils.equals(id, currentAlternateId);
        }

        @Override
        public String getCurrrentChannel() {
            return currrentChannel;
        }

        @Override
        public String getCurrrentTitle() {
            return currrentTitle;
        }

        @Override
        public void destroy() {
            super.destroy();
            if (mContent != null) {
                mContent.destroy();
                mContent = null;
            }
            clear();
        }

        private void clear(){
            if(mAlternateRefresh != null){
                mAlternateRefresh.detach();
                mAlternateRefresh = null;
            }
            dispose();
            mNeedRequestAd = "1";
            mAlternates = null;
            currentAlternate = null;
        }

        @Override
        public int getCurrentPlayIndex() {
            return currentPlayIndex;
        }

        @Override
        public Alternate getCurrentAlternate() {
            return currentAlternate;
        }

        @Override
        public boolean playNext() {
            NewTVLauncherPlayerViewManager.getInstance().stop();
            currentAlternate = null;
            isFirstChangeAlternate = false;
            if (mAlternates != null) {
                currentPlayIndex += 1;
                if (currentPlayIndex < mAlternates.size()) {
                    currentAlternate = mAlternates.get(currentPlayIndex);
                }
            }
            if (currentAlternate != null) {
                playAlternateItem(currentAlternate);
                return true;
            }
            return false;
        }

        @Override
        public String needRequestAd() {
            return mNeedRequestAd;
        }

        @Override
        public boolean needTipAlternate() {
            return needShowChangeView;
        }

        @Override
        public void alternateTipComplete() {
            needShowChangeView = false;
        }

        @Override
        public void addHistory() {
//            Player.get().addLBHistory(currentAlternateId);
        }

        @Override
        public void updateAlternate(String alternateId, String title, String channelId) {
            clear();

            needShowChangeView = TextUtils.equals(alternateId, currentAlternateId);

            currentAlternateId = alternateId;
            currrentTitle = title;
            currrentChannel = channelId;

            isFirstChangeAlternate = true;
        }

        @Override
        public void requestAlternate(final String alternateId, final String title, final String
                channelId) {
            clear();

            updateAlternate(alternateId, title, channelId);

            if (requestID != 0L) {
                mAlternate.cancel(requestID);
                requestID = 0L;
            }
            dispose();

            if (mAlternate != null) {
                requestID = mAlternate.getTodayAlternate(Libs.get().getAppKey(), Libs.get()
                                .getChannelId(),
                        alternateId, new DataObserver<ModelResult<List<Alternate>>>() {
                            @Override
                            public void onResult(ModelResult<List<Alternate>> result, long
                                    requestCode) {
                                if (result.isOk()) {
                                    mAlternates = result.getData();
                                    mNeedRequestAd = result.isAd();
                                    parseAlternate(title, channelId);
                                } else {
                                    if (getView() != null)
                                        getView().onError(getContext(), result.getErrorCode(),
                                                result
                                                        .getErrorMessage());
                                }
                            }

                            @Override
                            public void onError(@NotNull String code, @Nullable String desc) {
                                if (getView() != null)
                                    getView().onError(getContext(), code, desc);
                            }
                        });
            }
        }



        private void dispose() {
            if (mDisposable != null) {
                if (!mDisposable.isDisposed()) {
                    mDisposable.dispose();
                }
                mDisposable = null;
            }
            endTime = 0L;
        }

        private void startAlternateTimer() {

            if (currentPlayIndex < mAlternates.size() - 1) {
                Alternate next = mAlternates.get(currentPlayIndex + 1);
                if (next != null) {
                    endTime = CmsUtil.parse(next.getStartTime());
                }
            }

            if (mDisposable == null) {
                mDisposable = observable
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {

                                String currentTime = ServerTime.get().formatCurrentTime("HH:mm:ss");
                                String endFormatTime = ServerTime.get().format("HH:mm:ss",
                                        endTime);

                                LogUtils.d(TAG, "[Alternate time=" + currentTime +
                                        " " + "endTime=" + endFormatTime + "]");

                                if (getView() != null) {
                                    getView().onAlternateTimeChange(currentTime, endFormatTime);
                                }

                                if (System.currentTimeMillis() > endTime) {
                                    dispose();
                                    playNext();
                                }

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("Alternate", "interval exception = " + throwable
                                        .getMessage());
                                startAlternateTimer();
                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                LogUtils.e("Alternate", "interval complete");
                            }
                        });
            }
        }

        private void parseAlternate(String title, String channelId) {
            if (mAlternates != null && mAlternates.size() > 0) {
                currentPlayIndex = CmsUtil.binarySearch(mAlternates,
                        System.currentTimeMillis(), 0,
                        mAlternates.size() - 1);
                if (currentPlayIndex > 0 && currentPlayIndex < mAlternates.size()) {
                    currentAlternate = mAlternates.get(currentPlayIndex);
                    playAlternateItem(currentAlternate);

                    if(mAlternateRefresh != null && mAlternateRefresh.isDetached()){
                        mAlternateRefresh = null;
                    }
                    if(mAlternateRefresh == null) {
                        mAlternateRefresh = new AlternateRefresh(getContext(), this);
                    }
                    mAlternateRefresh.attach(currentAlternateId);
                    if (getView() != null) {
                        getView().onAlternateResult(currentAlternateId,mAlternates,
                                currentPlayIndex, title, channelId);
                    }
                } else {
                    if (getView() != null)
                        getView().onError(getContext(), CmsErrorCode.ALTERNATE_ERROR_PLAYLIST_EMPTY,
                                CmsErrorCode.getErrorMessage(CmsErrorCode.ALTERNATE_ERROR_PLAYLIST_EMPTY));
                }
            } else {
                if (getView() != null)
                    getView().onError(getContext(), CmsErrorCode.ALTERNATE_ERROR_PLAYLIST_EMPTY,
                            CmsErrorCode.getErrorMessage(CmsErrorCode.ALTERNATE_ERROR_PLAYLIST_EMPTY));
            }
        }

        @Override
        public void playAlternateItem(Alternate current) {
            dispose();

            currentRequestId = current.getContentID();
            currentSubUUID = current.getContentUUID();
            isLive = Constant.CONTENTTYPE_LV.equals(current.getContentType());

            mContent.getContent(currentRequestId, true);
            startAlternateTimer();
        }

        @Override
        public void onContentResult(@NotNull String uuid, @Nullable Content content) {
            if (TextUtils.equals(currentRequestId, uuid)) {
                if (content != null) {
                    if (!isLive) {
                        if (content.getData() != null) {
                            ArrayList<SubContent> subContents = new ArrayList<>();
                            for (SubContent sub : content.getData()) {
                                if (TextUtils.equals(sub.getContentUUID(),
                                        currentSubUUID)) {
                                    subContents.add(sub);
                                    break;
                                }
                            }
                            content.setData(subContents);
                        }
                    }
                    if (getView() != null) {
                        getView().onAlterItemResult(uuid, content, isLive, isFirstChangeAlternate);
                    }


                }
            }
        }

        @Override
        public void onSubContentResult(@NotNull String uuid, @Nullable ArrayList<SubContent>
                result) {

        }

        @Override
        public void tip(@NotNull Context context, @NotNull String message) {

        }

        @Override
        public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {
            if (getView() != null)
                getView().onAlternateError(code, desc);
        }

        @Override
        public void onChange(@NotNull String id, @NotNull Alternate title) {

        }

        @Override
        public void onError(@NotNull String id, @Nullable String code, @Nullable String desc) {
            if (TextUtils.equals(currentAlternateId, id)) {
                onError(getContext(), code, desc);
            }
        }
    }
}
