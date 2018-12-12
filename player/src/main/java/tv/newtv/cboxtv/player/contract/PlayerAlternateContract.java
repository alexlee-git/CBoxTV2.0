package tv.newtv.cboxtv.player.contract;

import android.content.Context;
import android.text.TextUtils;

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
        void onAlternateResult(List<Alternate> alternateList, int currentPlayIndex, String title,
                               String channelId);

        void onAlterItemResult(String contentId, Content content, boolean isLive);

        void onAlterLookTimeChange(Long time);
    }

    public interface Presenter extends ICmsPresenter {
        void requestAlternate(String alternateId, String title, String channelId);

        Alternate getCurrentAlternate();

        int getCurrentPlayIndex();

        String getCurrrentTitle();

        boolean equalsAlternate(String id);

        String getCurrrentChannel();

        boolean playNext();

        boolean needTipAlternate();

        void alternateTipComplete();

        void addHistory();

        void resetKeepLook();

        void playAlternateItem(Alternate current);
    }

    public static class AlternatePresenter extends CmsServicePresenter<View> implements
            Presenter, ContentContract.View {

        private static final String TAG = "AlternatePresenter";
        private String currentAlternateId;
        private String currrentTitle;
        private String currrentChannel;
        private boolean needShowChangeView = true;
        private IAlternate mAlternate;
        private String currentRequestId;
        private String currentSubUUID;
        private ContentContract.Presenter mContent;

        private List<Alternate> mAlternates;
        private Alternate currentAlternate;

        private int currentPlayIndex = 0;

        private Long requestID = 0L;

        private Disposable mDisposable;
        private Long endTime = 0L;

        private Long keepLook = 0L;

        private boolean isLive = false;

        private Observable<Long> observable;

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
            dispose();
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
        public boolean needTipAlternate() {
            return needShowChangeView;
        }

        @Override
        public void alternateTipComplete() {
            needShowChangeView = false;
        }

        @Override
        public void addHistory() {
            Player.get().addLBHistory(currentAlternateId);
        }

        @Override
        public void resetKeepLook() {
            keepLook = 0L;
        }

        @Override
        public void requestAlternate(final String alternateId, final String title, final String
                channelId) {
            if (requestID != 0L) {
                mAlternate.cancel(requestID);
                requestID = 0L;
            }
            dispose();

            needShowChangeView = TextUtils.equals(alternateId, currentAlternateId);

            currentAlternateId = alternateId;
            currrentTitle = title;
            currrentChannel = channelId;
//            mAlternates = Cache.getInstance().get(Cache.CACHE_TYPE_ALTERNATE, alternateId);
//            if (mAlternates != null) {
//                parseAlternate(title, channelId);
//                return;
//            }
            if (mAlternate != null) {
                requestID = mAlternate.getTodayAlternate(Libs.get().getAppKey(), Libs.get()
                                .getChannelId(),
                        alternateId, new DataObserver<ModelResult<List<Alternate>>>() {
                            @Override
                            public void onResult(ModelResult<List<Alternate>> result, long
                                    requestCode) {
                                if (result.isOk()) {
                                    mAlternates = result.getData();
//                                    Cache.getInstance().put(Cache.CACHE_TYPE_ALTERNATE,
//                                            alternateId, mAlternates);
                                    parseAlternate(title, channelId);
                                } else {
                                    if (getView() != null)
                                        getView().onError(getContext(), result.getErrorMessage());
                                }
                            }

                            @Override
                            public void onError(@Nullable String desc) {
                                if (getView() != null)
                                    getView().onError(getContext(), desc);
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
                                LogUtils.d(TAG, "[Alternate time=" + System.currentTimeMillis() +
                                        " " + "endTime=" + endTime + "]");
                                if (System.currentTimeMillis() > endTime) {
                                    dispose();
                                    playNext();
                                }

                                keepLook += 1000;

                                if (getView() != null) {
                                    getView().onAlterLookTimeChange(keepLook);
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
                if (currentPlayIndex > 0 && currentPlayIndex < mAlternates
                        .size()) {
                    currentAlternate = mAlternates.get(currentPlayIndex);
                    playAlternateItem(currentAlternate);
                    if (getView() != null) {
                        getView().onAlternateResult(mAlternates,
                                currentPlayIndex, title, channelId);
                    }
                } else {
                    if (getView() != null)
                        getView().onError(getContext(), "当前没有可以播放的节目");
                }
            } else {
                if (getView() != null)
                    getView().onError(getContext(), "当前没有可以播放的节目");
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
                    //直播
                    if (getView() != null) {
                        getView().onAlterItemResult(uuid, content, isLive);
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
        public void onError(@NotNull Context context, @Nullable String desc) {

        }
    }
}
