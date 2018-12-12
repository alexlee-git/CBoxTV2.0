package tv.newtv.cboxtv.player.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import tv.newtv.player.R;


class TipDialog {

    interface TipListener{
        void onClick(boolean isOK);
    }

    static void showBuilder(
            Context context,
            final int delaySeconds,
            final String notification,
            final TipListener listener) {
        if (context == null) {
            return ;
        }
        if(!(context instanceof Activity)){
            return;
        }
        if(((Activity) context).isFinishing()){
            return;
        }
        final AlertDialog ad = new AlertDialog.Builder(context).create();
        ad.show();
        ad.setCancelable(false);
        final View view = LayoutInflater.from(context).inflate(R.layout.layout_tip_dialog, null,
                false);
        final Window window = ad.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = context.getResources().getDimensionPixelOffset(R.dimen.width_900px);
            window.setAttributes(lp);

            window.setContentView(view);
            TextView textView = (TextView) view.findViewById(R.id.text_view);
            TextView ensure = view.findViewById(R.id.ensure);
            TextView unensure = view.findViewById(R.id.unensure);
            final TextView notificationView = view.findViewById(R.id.notification_view);
            notificationView.setText(notification);

            final int[] currentTime = {0};

            final Runnable updateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (currentTime[0] < delaySeconds) {
                        if(!ad.isShowing()){
                            return;
                        }
                        currentTime[0] += 1;
                        notificationView.setText(String.format("%s(%s)",notification,Integer
                                .toString(delaySeconds - currentTime[0])));
                        view.postDelayed(this, 1000);
                    } else {
                        if (listener != null) {
                            listener.onClick(true);
                        }
                        ad.dismiss();
                    }
                }
            };

            view.postDelayed(updateRunnable, 1000);

            ensure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(true);
                    }
                    ad.dismiss();
                }
            });
            unensure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(false);
                    }
                    ad.dismiss();
                }
            });
            unensure.requestFocus();
        } else {
            ad.dismiss();
        }
    }

}
