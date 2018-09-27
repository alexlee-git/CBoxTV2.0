package tv.newtv.cboxtv.player.menu;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;
import java.lang.ref.SoftReference;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.util.GsonUtil;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.player.menu.model.HeadMenuBean;
import tv.newtv.cboxtv.player.menu.model.LastMenuBean;
import tv.newtv.cboxtv.player.menu.model.Program;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;

/**
 * Created by TCP on 2018/4/17.
 */

public class MenuActivity extends BaseActivity {
    private static String TAG = "MenuActivity";
    /**
     * 次一级的actionUri
     */
    private String detailcolumnUUID = "be87b1d92b1947848ab785dd930b55ca";
    /**
     * 最后一级的contentUUID
     */
    private String contentUUID = "31929ffa9e114948bf158cfe989d6324";
    public static final String appKey = "8acb5c18e56c1988723297b1a8dc9260";
    public static final String channelId = "600001";
    private Disposable mDisposable;
    private String headString;
    private String lastString;
    private HeadMenuBean headBean;
    private LastMenuBean lastBean;
    private VideoPlayerView videoPlayerView;

    private MenuGroup menuGroup;

    @Override
    public int initLayout() {
        return R.layout.activity_menu;
    }

    @Override
    public void initView() {
        menuGroup = findViewById(R.id.menu_group);
        videoPlayerView = findViewById(R.id.video);
        menuGroup.setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        NetClient.INSTANCE.getMenuApi()
                .getCategoryTree(appKey, channelId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<HeadMenuBean>() {
                    @Override
                    public void accept(HeadMenuBean responseBody) throws Exception {
                        if (responseBody != null) {
                            menuGroup.setAllNodes(responseBody.getData());
                            getLastData();
                        }
                    }
                });
    }

    @Override
    public void initListener() {
        super.initListener();
        menuGroup.addOnSelectListener(new MenuGroup.OnSelectListener() {
            @Override
            public void select(Program program) {
                if (program != null) {
                    //videoPlayerView.setPlayInfo(program.convertProgramInfo());
                    //videoPlayerView.playSingleOrSeries(0,videoPlayerView.getCurrentPosition());
                }
            }
        });
    }

    private void getLastData() {
        String leftString = detailcolumnUUID.substring(0, 2);
        String rightString = detailcolumnUUID.substring(detailcolumnUUID.length() - 2, detailcolumnUUID.length());
        NetClient.INSTANCE.getMenuApi()
                .getLastList(appKey, channelId, leftString, rightString, detailcolumnUUID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe: ");
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            lastString = value.string();
                            lastBean = GsonUtil.fromjson(lastString, LastMenuBean.class);
                            menuGroup.setLastProgram(lastBean, "1d4726a86a0a4c0d875a16cb31ad5cdf", contentUUID);
                            menuGroup.setAppKeyAndChanelId(appKey, channelId);
                        } catch (IOException e) {
                            LogUtils.e(e.toString());
                        }
                        unSubscribe();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "onError: " + e);
                        unSubscribe();
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        unSubscribe();
                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        videoPlayerView.release();
    }

    /**
     * 解除绑定
     */
    private void unSubscribe() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    private Handler handler = new MyHandler(this);

    static class MyHandler extends Handler {
        private SoftReference<MenuActivity> softReference;

        public MyHandler(MenuActivity menuActivity) {
            softReference = new SoftReference<MenuActivity>(menuActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MenuActivity activity = softReference.get();
            if (activity != null) {
                activity.menuGroup.gone();
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean result = super.dispatchKeyEvent(event);

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                if(menuGroup.getVisibility() == View.GONE)
                    menuGroup.show();
                break;
        }
        if (menuGroup.getVisibility() == View.VISIBLE) {
            send();
        }
        return result;
    }
    private void send() {

        handler.removeMessages(0);
        handler.sendEmptyMessageDelayed(0, 3000);
    }
}
