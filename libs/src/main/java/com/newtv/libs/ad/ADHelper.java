package com.newtv.libs.ad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.bean.CountDown;
import com.newtv.libs.util.AdJsonUtil;
import com.newtv.libs.util.FileUtil;
import com.newtv.libs.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.adsdk.AdSDK;
import tv.icntv.been.AdInfo;
import tv.icntv.been.AdInfos;
import tv.icntv.been.MaterialInfo;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.utils
 * 创建事件:         13:25
 * 创建人:           weihaichao
 * 创建日期:          2018/4/20
 */
public class ADHelper {

    private static final String TAG = ADHelper.class.getSimpleName();
    private static ADHelper instance;
    private String AdCache;


    private ADHelper() {
        AdCache = FileUtil.getCacheDirectory(Libs.get().getContext(), "").getAbsolutePath();

        FileDownloader.setup(Libs.get().getContext());

        FileDownloadUtils.setDefaultSaveRootPath(AdCache);
    }

    public static ADHelper getInstance() {
        if (instance == null) {
            synchronized (ADHelper.class) {
                if (instance == null) instance = new ADHelper();
            }
        }
        return instance;
    }

    public @Nullable
    AD parseADString(Context context, String jsonResult) {

        if (TextUtils.isEmpty(jsonResult)){
            return null;
        }
        List<AdInfos> adInfosList = AdJsonUtil.parseAdInfo(jsonResult);

        if (adInfosList == null || adInfosList.size() == 0 || adInfosList.get(0) == null ||
                adInfosList.get(0).m_info == null || adInfosList.get(0).m_info.size() ==
                0) {
            return null;
        }
        AdInfo adInfo = adInfosList.get(0).m_info.get(0);
        if (adInfo == null || adInfo.m_material == null || adInfo.m_material
                .size() == 0) {
            return null;
        }
        List<MaterialInfo> materialInfosList = adInfo.m_material;
        int adTime = 0;
        AD result = new AD();
        final List<AD.ADItem> paths = new ArrayList<>();

        for (final MaterialInfo materialItem : materialInfosList) {
            final int time = (materialItem.m_playTime == 0 ? 3 : materialItem.m_playTime);
            adTime += time;
            StringBuffer path = new StringBuffer();
            int isLocal = AdSDK.getInstance().getLocalAd(materialItem.m_filePath, path);
            if (isLocal == 0) {
                paths.add(new AD.ADItem(path.toString(), materialItem.m_fileName, materialItem
                        .m_type, true, time, adInfo.m_mid, adInfo.m_aid,
                        materialItem.m_id, materialItem.m_eventType, materialItem.m_eventContent,adInfo.m_pos));
                result.time = adTime;
            } else {
                File file = new File(AdCache + File.pathSeparator + materialItem.m_fileName);
                if (file.exists()) {
                    paths.add(new AD.ADItem(file.getAbsolutePath(), materialItem.m_fileName,
                            materialItem.m_type, true, time, adInfo.m_mid, adInfo.m_aid,
                            materialItem.m_id, materialItem.m_eventType, materialItem.m_eventContent,adInfo.m_pos));
                } else {
                    paths.add(new AD.ADItem(materialItem.m_filePath, materialItem.m_fileName,
                            materialItem.m_type, false,
                            time, adInfo.m_mid, adInfo.m_aid,
                            materialItem.m_id, materialItem.m_eventType, materialItem.m_eventContent,adInfo.m_pos));
                }
            }
        }

        result.time = adTime;
        result.adItems = paths;

        return result;
    }


    public interface ADCallback {
        void showAd(String type, String url);

        void showAdItem(AD.ADItem adItem);

        void updateTime(int total, int left);

        void complete();
    }


    public static class AD {

        private int time;
        private List<ADItem> adItems;
        private ADCallback adCallback;
        private int currentIndex = 0;
        private int ctime;
        private boolean mCancel = false;
        private CountDownLatch countDownLatch;


        public void cancel() {
            if (adItems != null) {
                adItems.clear();
                adItems = null;
            }
            adCallback = null;
            mCancel = true;
            countDownLatch = null;
            downloadListener = null;
        }

        private FileDownloadListener downloadListener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void completed(BaseDownloadTask task) {
                int index = (int) task.getTag();
                if (adItems == null) {
                    return;
                }
                AD.ADItem adItem = adItems.get(index);
                if (Constant.AD_IMAGE_TYPE.equals(adItem.AdType)) {
                    adItem.AdUrl = Uri.fromFile(new File(task.getPath())).toString();
                } else if (Constant.AD_VIDEO_TYPE.equals(adItem.AdType)) {
                    adItem.AdUrl = task.getPath();
                }
                adItem.isLocal = true;
                Log.e("ADHelper", "download complete : " + task.getPath());
                countDownLatch.countDown();
                checkStart(true);
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                Log.e("ADHelper", "download error complete:" + e.getMessage());
                countDownLatch.countDown();
                int index = (int) task.getTag();
                AD.ADItem adItem = adItems.get(index);
                adItem.isFailed = true;
                checkStart(true);
            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        };


        @Override
        public String toString() {
            return adItems != null ? adItems.toString() : "[]";
        }

        /* 下载所有AD广告 */
        private void downloadFiles(List<ADItem> adItems) {
            FileDownloadQueueSet queueSet = new FileDownloadQueueSet(downloadListener);
            List<BaseDownloadTask> tasks = new ArrayList<>();
            int count = adItems.size();
            for (int i = 0; i < count; i++) {
                AD.ADItem adItem = adItems.get(i);
                if (!adItem.isLocal) {
                    tasks.add(FileDownloader.getImpl().create(adItem.AdUrl)
                            .setTag(i));
                }
            }
            if (tasks.size() > 0) {
                queueSet.disableCallbackProgressTimes();
                queueSet.setAutoRetryTimes(1);
                queueSet.downloadTogether(tasks);
                countDownLatch = new CountDownLatch(tasks.size());
                queueSet.start();
            } else {
                checkStart(true);
            }
        }

        public void checkStart(boolean isReportAD) {
            if (countDownLatch != null && countDownLatch.getCount() != 0) {
                return;
            }
            currentIndex = 0;
            ctime = 0;
            time = 0;
            for (ADItem adItem : adItems) {
                if (!adItem.isFailed) {
                    time += adItem.PlayTime;
                }
            }
            adCallback.updateTime(time, time);
            doNext(isReportAD);
        }

        public AD setCallback(ADCallback callback) {
            adCallback = callback;
            return this;
        }

        public void start() {
            downloadFiles(adItems);
        }

        @SuppressLint("CheckResult")
        private void doNext(final boolean isReportAD) {
            if (mCancel) return;
            if (currentIndex >= adItems.size()) {
                if (adCallback != null)
                    adCallback.complete();
                return;
            }
            final ADItem adItem = adItems.get(currentIndex);
            LogUtils.d(TAG, adItem.toString());
            if (adCallback != null) {
                adCallback.showAd(adItem.AdType, adItem.AdUrl);
                adCallback.showAdItem(adItem);
            }

            if (isReportAD) {
                //日志上传sdk初始化
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                        e.onNext(AdSDK.getInstance().report(adItem.mid,
                                adItem.aid, adItem.id, null,
                                null,
                                null, null));
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean value) throws Exception {
                                Log.e("MM", "广告上报结果=" + value);
                            }
                        });
            }

            if(adItems.size() == 1 && TextUtils.equals(adItems.get(0).position,Constant.AD_DETAILPAGE_BANNER)){
                return;
            }

            CountDown countDown = new CountDown(adItem.PlayTime);
            countDown.listen(new CountDown.Listen() {
                @Override
                public void onCount(final int t) {
                    ctime++;
                    if (adCallback != null) {
                        adCallback.updateTime(time, time - ctime);
                    }
                }

                @Override
                public void onComplete() {
                    currentIndex += 1;
                    doNext(isReportAD);
                }

                @Override
                public void onCancel() {

                }
            });
            countDown.start();
        }

        public static class ADItem {
            public String AdUrl;                   //广告地址
            public String AdType;                  //广告类型
            public boolean isLocal;                //是否是本地
            public int PlayTime;                   //播放时长
            public boolean isFailed = false;       //是否下载失败
            public String FileName;                //文件名
            public String mid;
            public String aid;
            public String id;
            public String eventType;               //广告互动类型
            public String eventContent;            //广告互动内容
            public String position;

            public ADItem(String type){
                AdType = type;
            }

            public ADItem(String url, String name, String type, boolean local, int time, String
                    m, String a, String id, String eventType, String eventContent,String position) {
                AdUrl = url;
                FileName = name;
                AdType = type;
                isLocal = local;
                PlayTime = time;
                mid = m;
                aid = a;
                this.id = id;
                this.eventType = eventType;
                this.eventContent = eventContent;
                this.position = position;
            }

            @Override
            public String toString() {
                return "ADItem{" +
                        "AdUrl='" + AdUrl + '\'' +
                        ", AdType='" + AdType + '\'' +
                        ", isLocal=" + isLocal +
                        ", PlayTime=" + PlayTime +
                        ", isFailed=" + isFailed +
                        ", FileName='" + FileName + '\'' +
                        ", mid='" + mid + '\'' +
                        ", aid='" + aid + '\'' +
                        ", id='" + id + '\'' +
                        ", eventType='" + eventType + '\'' +
                        ", eventContent='" + eventContent + '\'' +
                        ", position='" + position + '\'' +
                        '}';
            }
        }
    }
}
