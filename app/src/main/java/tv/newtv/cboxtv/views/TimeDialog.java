package tv.newtv.cboxtv.views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.views.detail.HeadPlayerView;

public class TimeDialog {
    private static final int TIME_MSG = 1;
    private static AlertDialog ad;
    private static int time = 5;
    public static Boolean isDisMiss = true;
    private static TextView textView = null;
    private static TextView ensure = null;
    private static TextView notificationView = null;
    private static Handler handler = null;

    private static void initHandler(){
        if(handler == null){
            handler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case TIME_MSG:
                            if(textView != null && time > 0){
                                textView.setText("播放已结束  ("+ --time +"S)");
                                if(time == 0){
                                    ensure.setBackgroundColor(Color.parseColor("#0077f2"));
                                }else {
                                    handler.sendEmptyMessageDelayed(TIME_MSG,1000);
                                }
                            }
                            break;
                    }
                }
            };
        }
    }

    public static void showBuilder(Context context, HeadPlayerView headPlayerView){
        showBuilder(context,"",null,headPlayerView);
    }

    public static void showBuilder(Context context, String notification, final View.OnClickListener listener, final HeadPlayerView headPlayerView) {
        if (context == null) {
            return;
        }

        initHandler();
        time = 5;

        ad = new AlertDialog.Builder(context).create();
        ad.show();
        ad.setCancelable(false);
        Window window = ad.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = context.getResources().getDimensionPixelOffset(R.dimen.width_900px);
        window.setAttributes(lp);
        window.setContentView(R.layout.layout_time_dialog);
        textView = (TextView) window.findViewById(R.id.text_view);
        ensure = window.findViewById(R.id.ensure);
        notificationView = window.findViewById(R.id.notification_view);
        notificationView.setText(notification);

        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time <= 0) {
                    headPlayerView.continuePlayVideo();
                    TimeDialog.dismiss();
                    if(listener != null){
                        listener.onClick(v);
                    }
                    isDisMiss = false;
                }
            }
        });
        handler.sendEmptyMessageDelayed(TIME_MSG,1000);
    }

    public static void dismiss() {
        if (ad != null && ad.isShowing()) {
            ad.dismiss();
            ad = null;
        }
        textView  = null;
        ensure = null;
        handler = null;
    }

}
