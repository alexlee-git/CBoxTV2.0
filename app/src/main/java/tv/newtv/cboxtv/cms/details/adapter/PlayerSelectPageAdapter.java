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

public class PlayerSelectPageAdapter extends BaseRecyclerAdapter<String, PlayerSelectPageAdapter.PlayerSelectPageholder> {

    private Context context;
    private OnItemEnterKeyListener listener;

    public PlayerSelectPageAdapter(Context context, OnItemEnterKeyListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public PlayerSelectPageholder onCreateViewHolder(ViewGroup parent, int viewType) {
        PlayerSelectPageholder viewHolder = new PlayerSelectPageholder(LayoutInflater.from(context).inflate(R.layout.btn_selelct_episode, null));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlayerSelectPageholder holder, final int position) {
        holder.mTextTv.setText(mList.get(position));
        holder.itemView.postInvalidate();
        if (position == selectIndex) {
            holder.mTextTv.setTextColor(ContextCompat.getColor(context,R.color.color_62c0eb));
        } else {
            holder.mTextTv.setTextColor(ContextCompat.getColor(context,R.color.detail_tvcolor));
        }

    }

    private int selectIndex = 0;

    public void setSelectIndex(int index) {
        selectIndex = index;
        notifyDataSetChanged();
    }


    class PlayerSelectPageholder extends RecyclerView.ViewHolder {
        private TextView mTextTv;
        private RelativeLayout mEpisodeFocusView;

        public PlayerSelectPageholder(View itemView) {
            super(itemView);
            mTextTv = (TextView) itemView.findViewById(R.id.tv_episode_text);
            mEpisodeFocusView = (RelativeLayout) itemView.findViewById(R.id.rl_episode_focus);
            mEpisodeFocusView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    setSelected(b);
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

        public void setSelected(boolean value) {
            mEpisodeFocusView.setBackgroundResource(value ? R.drawable.select_count_focus : R.color.color_transparent);
        }


    }

    public interface OnItemEnterKeyListener {
        void onEnterKey(View v, int position);
    }

}
