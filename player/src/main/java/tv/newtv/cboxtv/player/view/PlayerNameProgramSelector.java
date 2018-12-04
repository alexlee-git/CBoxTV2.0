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

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;

import java.util.List;

import tv.newtv.player.R;

/**
 * Created by wangkun on 2018/2/7.
 */

public class PlayerNameProgramSelector extends FrameLayout{

    private static final String TAG = PlayerNameProgramSelector.class.getName();
    private static final int DISMISS_VIEW = 3001;
    private static final long DISMISS_VIEW_DELAY_TIME = 5000;
    private Context mContext;

    private PlayerRecyclerView mPlayerRecyclerView;
    private NameProgramSelectorAdapter mNameProgramSelectorAdapter;
    private AnimationSet mAnimationIn,mAnimationOut;
    private NameProgramSelectorHandler mHandler;
    private int mPlayingIndex;
    private Content mProgramSeriesInfo;

    public PlayerNameProgramSelector(@NonNull Context context) {
        this(context,null);
    }

    public PlayerNameProgramSelector(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public PlayerNameProgramSelector(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context.getApplicationContext();
        initView(context);
        initData(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.player_name_program_selector,this);
        mPlayerRecyclerView = (PlayerRecyclerView) view.findViewById(R.id.player_name_program_selector_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        mPlayerRecyclerView.setLayoutManager(linearLayoutManager);
    }
    private void initData(Context context){
        mHandler = new NameProgramSelectorHandler();
        mAnimationIn = (AnimationSet) AnimationUtils.loadAnimation(context,R.anim.seekbar_in);
        mAnimationOut = (AnimationSet) AnimationUtils.loadAnimation(context,R.anim.seekbar_out);
    }

    public void setProgramSeriesInfo(Content programSeriesInfo, int index) {
        Log.i(TAG, "setProgramSeriesInfo: ");
        mProgramSeriesInfo = programSeriesInfo;
        mPlayingIndex = index;
        if(mNameProgramSelectorAdapter==null){
            mNameProgramSelectorAdapter = new NameProgramSelectorAdapter(mContext,R.layout.player_name_program_selector_item,programSeriesInfo.getData());
            mPlayerRecyclerView.setAdapter(mNameProgramSelectorAdapter);
        } else {
            mNameProgramSelectorAdapter.notifyDataSetChanged();
        }
    }

    class NameProgramSelectorAdapter extends PlayerRecyclerViewAdapter{

        private List<SubContent> datas;

        public NameProgramSelectorAdapter(Context context, int layoutId, List datas) {
            super(context, layoutId, datas);
            this.datas = datas;
        }
        @Override
        public void onBindViewHolder(PlayerRecylerViewHolder holder, final int position) {
            Log.i(TAG, "onBindViewHolder: "+position);
            SubContent programsInfo = datas.get(position);
            if(programsInfo==null){
                return;
            }
//            TextView dateTextView = holder.itemView.findViewById(R.id.name_program_selector_item_date);
            TextView nameTextView = (TextView) holder.itemView.findViewById(R.id.name_program_selector_item_name);
//            dateTextView.setText("20101010期"+position);
            nameTextView.setText(programsInfo.getTitle());
            ImageView playingImageView = (ImageView) holder.itemView.findViewById(R.id.name_program_selector_item_playing_imageview);
            if(position==mPlayingIndex){
                playingImageView.setVisibility(View.VISIBLE);
            } else {
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
                    mNameProgramSelectorAdapter.notifyDataSetChanged();
//                    NewTVLauncherPlayerViewManager.getInstance().playProgramSeries(null,mProgramSeriesInfo,false,position,0);
                    NewTVLauncherPlayerViewManager.getInstance().playVod(null,mProgramSeriesInfo,
                            position,0);
                    dismiss();
                }
            });
        }

    }

    class NameProgramSelectorHandler extends Handler {

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
        NewTVLauncherPlayerViewManager.getInstance().setShowingView(NewTVLauncherPlayerView.SHOWING_NAME_PROGRAM_SELECTOR);
    }
    public void dismiss(){
        Log.i(TAG, "dismiss: ");
        mHandler.removeMessages(DISMISS_VIEW);

        if(mPlayerRecyclerView.indexOfChild(mPlayerRecyclerView.getFocusedChild())>mPlayingIndex){
            mPlayerRecyclerView.scrollToPosition(mPlayingIndex);

        } else {

            mPlayerRecyclerView.scrollToPosition(mPlayingIndex+4);
        }
        setVisibility(View.INVISIBLE);
        startAnimation(mAnimationOut);
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
