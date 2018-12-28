package tv.newtv.cboxtv.cms.details.presenter.adpresenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.util.RxBus;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.icntv.adsdk.AdSDK;
import tv.newtv.cboxtv.player.model.RequestAdParameter;
import tv.newtv.cboxtv.player.PlayerConfig;

public abstract class BaseRequestAdPresenter implements ADConfig.ColumnListener {
    private static final String TAG = "BaseRequestAdPresenter";

    private boolean columnIsGet = false;
    protected String adType;
    protected String adLoc;
    private boolean isDestory;
    private RequestAdParameter requestAdParameter;

    private Disposable mDisposable;

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

        ADConfig.getInstance().registerListener(this);
        if(columnIsGet || !TextUtils.isEmpty(ADConfig.getInstance().getSecondColumnId())){
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
                addExtend(stringBuilder, "type",config.getVideoType());
                addExtend(stringBuilder,"secondtype",config.getVideoClass());
                if (!TextUtils.isEmpty(config.getCarousel())) {
                    addExtend(stringBuilder, "carousel", config.getCarousel());
                }
                requestAdParameter = new RequestAdParameter();
                requestAdParameter.setExtend(stringBuilder.toString());
                requestAdParameter.setProgram(config.getProgramId());
                requestAdParameter.setSeriesId(config.getSeriesID());
                e.onNext(AdSDK.getInstance().getAD(adType, config.getColumnId(), config.getSeriesID(), adLoc, null, stringBuilder.toString(), sb));
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if(!isDestory){
                            dealResult(sb.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dispose();
                    }
                });
    }

    private void dispose(){
        if(mDisposable != null){
            if(!mDisposable.isDisposed()){
                mDisposable.dispose();
            }
            mDisposable = null;
        }
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

    public void onResume(){}

    public void onStop(){}

    public void destroy(){
        isDestory = true;
        dispose();
        handler.removeCallbacksAndMessages(null);
//        ADConfig.getInstance().reset();
        ADConfig.getInstance().removeListener(this);
    }

    @Override
    public void receive() {
        columnIsGet = true;
    }

    public RequestAdParameter getRequestAdParameter() {
        return requestAdParameter;
    }
}
