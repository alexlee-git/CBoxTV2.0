package tv.newtv.cboxtv.cms.screenList.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.screenList.bean.TabBean;


/**
 * Created by 冯凯 on 2018/9/30.
 */
public class FirstLabelAdapter extends RecyclerView.Adapter<FirstLabelAdapter.FirstLabelViewHolder> {

    Context context;
    List<TabBean.DataBean.ChildBean> childBeans;

    public FirstLabelAdapter(Context context, List<TabBean.DataBean.ChildBean> childBeans) {
        this.context = context;
        this.childBeans = childBeans;
    }


    @NonNull
    @Override
    public FirstLabelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.label, viewGroup, false);
        FirstLabelViewHolder labelViewHolder = new FirstLabelViewHolder(view);
        return labelViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FirstLabelViewHolder labelViewHolder, final int i) {
        labelViewHolder.textView.setText(childBeans.get(i).getTitle());

        labelViewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {


            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d("FirstLabelAdapter", "我到这里了");
                    EventBus.getDefault().post(childBeans.get(i));

                    labelViewHolder.textView.setBackgroundResource(R.drawable.search_title_bg_focus);

                } else {
                    labelViewHolder.textView.setBackgroundColor(0);
                    labelViewHolder.textView.setBackgroundResource(R.drawable.search_title_bg);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return childBeans.size();
    }

    class FirstLabelViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public FirstLabelViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setFocusable(true);
            textView = itemView.findViewById(R.id.text);
        }
    }
}



