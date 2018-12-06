package tv.newtv.cboxtv.uc.v2;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc.v2
 * 创建事件:         09:21
 * 创建人:           weihaichao
 * 创建日期:          2018/8/28
 */
public abstract class BaseDetailSubFragment extends Fragment {


    protected static final int MSG_SYNC_DATA_COMP = 10033;
    protected static final int MSG_INFLATE_PAGE = 10034;

    private DetailHandler mHandler;
    protected View contentView;
    protected DisplayMetrics metrics;

    protected abstract int getLayoutId();


    protected abstract void updateUiWidgets(View view);

    protected void init() {}

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandler != null){
            mHandler.release();
            mHandler = null;
        }
    }

    static class DetailHandler extends android.os.Handler {
        WeakReference<BaseDetailSubFragment> reference;

        void release(){
            removeCallbacksAndMessages(null);
            if(reference != null){
                reference.clear();
                reference = null;
            }
        }

        DetailHandler(BaseDetailSubFragment setFragment) {
            reference = new WeakReference<>(setFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if(reference.get() == null) return;
            if (msg.what == MSG_SYNC_DATA_COMP) {
                reference.get().checkDataSync();
            } else if (msg.what == MSG_INFLATE_PAGE) {
                Log.d("follow", "接收到 MSG_INFLATE_PAGE 消息");
                List<UserCenterPageBean.Bean> datas = (List<UserCenterPageBean.Bean>) msg.obj;
                if (datas != null && datas.size() > 0) {
                    reference.get().inflate(datas);
                } else {
                    reference.get().inflatePageWhenNoData();
                }
            } else {
                Log.d("sub", "unresolved msg : " + msg.what);
            }

        }
    }


    protected void removeMessages(int message){
        mHandler.removeMessages(message);
    }

    protected void sendEmptyMessage(int message){
        mHandler.sendEmptyMessage(message);
    }

    protected void sendEmptyMessageDelayed(int message,long delayMillis){
        mHandler.sendEmptyMessageDelayed(message,delayMillis);
    }

    protected void sendMessage(Message message){
        mHandler.sendMessage(message);
    }

    protected void sendMessageDelayed(Message message,long delayMillis){
        mHandler.sendMessageDelayed(message,delayMillis);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new DetailHandler(this);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        metrics = getActivity().getResources().getDisplayMetrics();

        if (contentView == null) {
            contentView = inflater.inflate(getLayoutId(), container, false);
            try {
                updateUiWidgets(contentView);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return contentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        contentView = null;
    }

    protected void hideView(View view) {
        if (view != null) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    protected void showView(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    protected void checkDataSync() {

    }

    protected void inflate(List<UserCenterPageBean.Bean> datas) {

    }

    protected void inflatePageWhenNoData() {

    }
}
