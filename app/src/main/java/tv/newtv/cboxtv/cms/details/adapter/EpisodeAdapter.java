package tv.newtv.cboxtv.cms.details.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;

/**
 * Created by gaoleichao on 2018/4/3.
 */

public class EpisodeAdapter extends BaseRecyclerAdapter<String, EpisodeAdapter.EpisodeViewholder> {

    private Context context;
    private OnItemEnterKeyListener listener;

    private int mCurrenPage;

    public void destroy(){
        listener = null;
        context = null;
    }

    public EpisodeAdapter(Context context, OnItemEnterKeyListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public EpisodeViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        EpisodeViewholder viewHolder = new EpisodeViewholder(LayoutInflater.from(context).inflate(R.layout.btn_selelct_episode, null));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EpisodeViewholder holder, final int position) {
        holder.mTextTv.setText(mList.get(position));
        if (position == mCurrenPage) {
            holder.mTextTv.setTextColor(ContextCompat.getColor(context,R.color.color_62c0eb));
        } else {
            holder.mTextTv.setTextColor(ContextCompat.getColor(context,R.color.detail_tvcolor));
        }
    }

    public void setCurrenPage(int mCurrenPage) {
        if (this.mCurrenPage != mCurrenPage) {
            this.mCurrenPage = mCurrenPage;

        }
    }


    class EpisodeViewholder extends RecyclerView.ViewHolder {
        private TextView mTextTv;
        private RelativeLayout mEpisodeFocusView;

        public EpisodeViewholder(View itemView) {
            super(itemView);
            mTextTv = (TextView) itemView.findViewById(R.id.tv_episode_text);
            mEpisodeFocusView = (RelativeLayout) itemView.findViewById(R.id.rl_episode_focus);
            mEpisodeFocusView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        mEpisodeFocusView.setBackgroundResource(R.drawable.select_count_focus);
                        listener.onEnterKey(view, getAdapterPosition());
                    } else {
                        mEpisodeFocusView.setBackgroundResource(R.color.color_transparent);
                    }
                }
            });

            mEpisodeFocusView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        return false;
                    }

                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        return true;
                    }

                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        listener.onEnterKey(v, getAdapterPosition());
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (getAdapterPosition() == (getItemCount() - 1)) {
                            return true;
                        }
                    }

                    return false;
                }
            });
        }
    }

    public interface OnItemEnterKeyListener {
        void onEnterKey(View v, int position);
    }

}
