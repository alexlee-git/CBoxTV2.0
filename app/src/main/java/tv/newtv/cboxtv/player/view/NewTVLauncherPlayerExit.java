package tv.newtv.cboxtv.player.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import tv.newtv.cboxtv.R;

/**
 * Created by wangkun on 2018/1/16.
 */

public class NewTVLauncherPlayerExit extends FrameLayout {
    
    private static final String TAG = "NewTVLauncherPlayerExit";
    private Context mContext;
    private Button mExitButton;
    public NewTVLauncherPlayerExit(@NonNull Context context) {
        this(context,null);
    }

    public NewTVLauncherPlayerExit(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NewTVLauncherPlayerExit(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(final Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.newtv_launcher_player_exit,this);
        mExitButton = (Button) view.findViewById(R.id.exit_button);
        mExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
//                NewTVLauncherPlayerViewManager.getInstance().closePlayerActivity();
                NewTVLauncherPlayerViewManager.getInstance().release();
            }
        });
    }

    public void show() {
        Log.i(TAG, "show: ");
        setVisibility(View.VISIBLE);
        bringToFront();
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView.SHOWING_EXIT_VIEW);

    }
    public void dismiss() {
        Log.i(TAG, "dismiss: ");
        setVisibility(View.INVISIBLE);
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView.SHOWING_NO_VIEW);
    }

    public void release() {
        Log.i(TAG, "release: ");

        mContext = null;
    }
}
