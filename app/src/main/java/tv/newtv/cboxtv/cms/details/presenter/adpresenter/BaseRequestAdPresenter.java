package tv.newtv.cboxtv.cms.details.presenter.adpresenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.ad.ADConfig;
import tv.newtv.cboxtv.cms.util.RxBus;
import tv.newtv.cboxtv.player.PlayerConfig;

public abstract class BaseRequestAdPresenter implements ADConfig.ColumnListener {
    private static final String TAG = "BaseRequestAdPresenter";

    private boolean columnIsGet = false;
    protected String adType;
    protected String adLoc;
    private boolean isDestory;
    protected Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            excute();
        }
    };

    public void getAD(final String adType, final String adLoc) {
        this.adType = adType;
        this.adLoc = adLoc;

        ADConfig.getInstance().setListener(this);
        if(columnIsGet || !TextUtils.isEmpty(ADConfig.getInstance().getColumnId())){
            excute();
        }else {
            handler.sendEmptyMessageDelayed(0,5000);
        }
    }

    @SuppressLint("CheckResult")
    private void excute(){
        Log.i(TAG, "getAD: "+ ADConfig.getInstance().toString());
        RxBus.get().post(Constant.INIT_SDK, Constant.INIT_ADSDK);

        final StringBuffer sb = new StringBuffer();

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                PlayerConfig playerConfig = PlayerConfig.getInstance();
                ADConfig config = ADConfig.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                addExtend(stringBuilder,"panel",playerConfig.getFirstChannelId());
                addExtend(stringBuilder,"secondpanel",playerConfig.getSecondChannelId());
                addExtend(stringBuilder,"topic",playerConfig.getTopicId());
                addExtend(stringBuilder,"secondcolumn",config.getSecondColumnId());
                addExtend(stringBuilder,"program",config.getProgramId());
                e.onNext(AdSDK.getInstance().getAD(adType, config.getColumnId(), config.getSeriesID(), adLoc, null, stringBuilder.toString(), sb));
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer result) throws Exception {
                        if(!isDestory){
                            dealResult(sb.toString());
                        }
                    }
                });
    }

    protected abstract void dealResult(String result);

    private void addExtend(StringBuilder result,String key, String value){
        if(TextUtils.isEmpty(value)){
            return;
        }
        if(TextUtils.isEmpty(result)){
            result.append(key+"="+value);
        } else {
            result.append("&"+key+"="+value);
        }
    }

    public void destroy(){
        isDestory = true;
        handler.removeCallbacksAndMessages(null);
        ADConfig.getInstance().setColumnId("");
        ADConfig.getInstance().setSecondColumnId("");
        ADConfig.getInstance().removeListener(this);
    }

    @Override
    public void receive() {
        columnIsGet = true;
    }
}
