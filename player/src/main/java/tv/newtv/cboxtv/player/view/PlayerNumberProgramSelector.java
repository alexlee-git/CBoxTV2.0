package tv.newtv.cboxtv.player.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tv.newtv.cboxtv.player.PlayerPlayInfo;
import tv.newtv.cboxtv.player.PlayerPlayInfoItem;
import tv.newtv.cboxtv.player.ProgramSeriesInfo;
import tv.newtv.player.R;

/**
 * Created by wangkun on 2018/2/7.
 */

public class PlayerNumberProgramSelector extends FrameLayout{

    private static final String TAG = PlayerNumberProgramSelector.class.getName();
    private static final int DISMISS_VIEW = 3001;
    private static final long DISMISS_VIEW_DELAY_TIME = 5000;
    private Context mContext;

    private PlayerRecyclerView mPlayerRecyclerView;
    private NumberProgramSelectorAdapter mNumberProgramSelectorAdapter;
    private AnimationSet mAnimationIn,mAnimationOut;
    private NumberProgramSelectorHandler mHandler;
    private int mPlayingIndex;
    private ProgramSeriesInfo mProgramSeriesInfo;

    public PlayerNumberProgramSelector(@NonNull Context context) {
        this(context,null);
    }

    public PlayerNumberProgramSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public PlayerNumberProgramSelector(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context.getApplicationContext();
        initView(context);
        initData(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.player_number_program_selector,this);
        mPlayerRecyclerView = (PlayerRecyclerView) view.findViewById(R.id.player_number_program_selector_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        mPlayerRecyclerView.setLayoutManager(linearLayoutManager);
    }
    private void initData(Context context){
        mHandler = new NumberProgramSelectorHandler();
        mAnimationIn = (AnimationSet) AnimationUtils.loadAnimation(context,R.anim.seekbar_in);
        mAnimationOut = (AnimationSet) AnimationUtils.loadAnimation(context,R.anim.seekbar_out);
    }

    public void setProgramSeriesInfo(ProgramSeriesInfo programSeriesInfo, int index) {
        Log.i(TAG, "setProgramSeriesInfo: ");
        mProgramSeriesInfo = programSeriesInfo;
        mPlayingIndex = index;
        if(mNumberProgramSelectorAdapter==null){
            mNumberProgramSelectorAdapter = new NumberProgramSelectorAdapter(mContext,R.layout.player_number_program_selector_item,programSeriesInfo.getData());
            mPlayerRecyclerView.setAdapter(mNumberProgramSelectorAdapter);
        } else {

        }
    }

    class NumberProgramSelectorAdapter extends PlayerRecyclerViewAdapter{

        private List<PlayerPlayInfoItem> datas;

        public NumberProgramSelectorAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
            this.datas = datas;
        }
        @Override
        public void onBindViewHolder(PlayerRecylerViewHolder holder, final int position) {
            Log.i(TAG, "onBindViewHolder: "+position);
            TextView numberTextView = (TextView) holder.itemView.findViewById(R.id.number_program_selector_item_number);
//            numberTextView.setText(datas.get(position).getPeriods());
            numberTextView.setText((position+1)+"");
            ImageView playingImageView = (ImageView) holder.itemView.findViewById(R.id.number_program_selector_item_playing);
            if(position==mPlayingIndex){
                numberTextView.setVisibility(View.INVISIBLE);
                playingImageView.setVisibility(View.VISIBLE);
            } else {
                numberTextView.setVisibility(View.VISIBLE);
                playingImageView.setVisibility(View.INVISIBLE);
            }
            holder.itemView.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        v.setBackgroundResource(R.drawable.player_program_selector_focused_bg);
//                        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                        sa.setDuration(50);
//                        sa.setFillAfter(true);
//                        v.startAnimation(sa);

                        mHandler.removeMessages(DISMISS_VIEW);
                        mHandler.sendEmptyMessageDelayed(DISMISS_VIEW,DISMISS_VIEW_DELAY_TIME);
                    } else {
                        v.setBackgroundResource(R.drawable.player_program_selector_unfocused_bg);

//                        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//                        sa.setDuration(0);
//                        sa.setFillAfter(true);
//                        v.startAnimation(sa);
                    }
                }
            });
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick: ");
                    mPlayingIndex = position;
                    mNumberProgramSelectorAdapter.notifyDataSetChanged();
                    NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(null,mProgramSeriesInfo,false,position,0);
                    dismiss();
                }
            });
        }

    }

    class NumberProgramSelectorHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DISMISS_VIEW:
                    dismiss();
                    break;
            }
        }
    }
    public void show(){
        Log.i(TAG, "show: ");
        setVisibility(View.VISIBLE);
        bringToFront();
        startAnimation(mAnimationIn);
        mHandler.removeMessages(DISMISS_VIEW);
        mHandler.sendEmptyMessageDelayed(DISMISS_VIEW,DISMISS_VIEW_DELAY_TIME);
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView.SHOWING_NUMBER_PROGRAM_SELECTOR);
    }
    public void dismiss(){
        Log.i(TAG, "dismiss: ");
        mHandler.removeMessages(DISMISS_VIEW);

        setVisibility(View.INVISIBLE);
        startAnimation(mAnimationOut);
        mPlayerRecyclerView.scrollToPosition(mPlayingIndex+9);
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView.SHOWING_NO_VIEW);
    }
    public void release() {
        Log.i(TAG, "release: ");
        mPlayingIndex = 0;
        mProgramSeriesInfo = null;
        mContext = null;
        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler=null;
        }
    }
}
