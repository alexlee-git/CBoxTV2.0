package tv.newtv.cboxtv.cms.listPage;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.view.myRecycleView.HorizontalRecyclerView;
import tv.newtv.cboxtv.cms.listPage.model.MarkListener;


/**
 * Created by caolonghe on 2018/3/8 0008.
 */

public class MarkManager {

    private static MarkManager mInstance;
    private RecycleRecommAdapter mRecycleRecommAdapter;
    private MarkListener markListener;

    private MarkManager(MarkListener markListener) {
        this.markListener = markListener;
    }

    public static MarkManager getInstance(MarkListener markListener) {
        if (mInstance == null) {
            synchronized (MarkManager.class) {
                if (mInstance == null) {
                    mInstance = new MarkManager(markListener);
                }
            }
        }
        return mInstance;
    }

    public void release() {
        if (mInstance != null) {
            mInstance = null;
        }
    }

    public void setMark(Context mContext, HorizontalRecyclerView mHorizontalRecyclerView, String data, String type) {
        String[] types = data.split(",");
        mRecycleRecommAdapter = new RecycleRecommAdapter(mContext, types, type);
        mHorizontalRecyclerView.setAdapter(mRecycleRecommAdapter);
    }

    class RecycleRecommAdapter extends RecyclerView.Adapter<RecycleRecommAdapter.MyViewHolder> {

        private Context mContext;
        private String[] types;
        private TextView oldView;
        private String type;

        public RecycleRecommAdapter(Context mContext, String[] data, String type) {
            this.mContext = mContext;
            types = data;
            this.type = type;
        }

        @Override
        public RecycleRecommAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecycleRecommAdapter.MyViewHolder holder = new RecycleRecommAdapter.MyViewHolder(LayoutInflater.from
                    (mContext).inflate(R.layout.listpage_item_dialog_mark, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecycleRecommAdapter.MyViewHolder holder, final int position) {
            if (types == null || types.length <= 0) {
                return;
            }
            if (position == 0) {
                holder.tv_name.setTextColor(Color.parseColor("#00cffb"));
                oldView = holder.tv_name;
            }
            holder.tv_name.setText(types[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.tv_name.setTextColor(Color.parseColor("#00cffb"));
                    //                   Toast.makeText(mContext, position + "" + types[position], Toast.LENGTH_SHORT).show();
                    if (oldView != null && oldView != holder.tv_name) {
                        oldView.setTextColor(Color.parseColor("#ededed"));
                    }
                    oldView = holder.tv_name;
                    markListener.showMarkdata(types[position], type);
                    ;
                }
            });
        }

        @Override
        public int getItemCount() {
            return types != null ? types.length : 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv_name;

            private MyViewHolder(View view) {
                super(view);
                tv_name = (TextView) view.findViewById(R.id.item_listpage_tv_name);
            }
        }
    }
}
