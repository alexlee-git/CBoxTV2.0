package tv.newtv.cboxtv.cms.screenList.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.screenList.bean.LabelBean;
import tv.newtv.cboxtv.cms.util.RxBus;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public class secondLabelAdapter extends RecyclerView.Adapter<secondLabelAdapter.FirstMenuViewHolder> {
    private Context context;
    List<LabelBean.DataBean.FilterValueBean> list;
    LabelBean.DataBean dataBean;


    public secondLabelAdapter(List<LabelBean.DataBean.FilterValueBean> list, Context context, LabelBean.DataBean dataBean) {
        this.context = context;
        this.list = list;
        this.dataBean = dataBean;
    }

    @NonNull
    @Override
    public FirstMenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.label, viewGroup, false);
        FirstMenuViewHolder holder = new FirstMenuViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FirstMenuViewHolder firstMenuViewHolder, final int i) {

        firstMenuViewHolder.textView.setText(list.get(i).getTitle());
        firstMenuViewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {


            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    RxBus.get().post("labelKey", dataBean);
                    RxBus.get().post("labelValue", list.get(i));
                    firstMenuViewHolder.textView.setBackgroundResource(R.drawable.search_title_bg_focus);

                } else {
                    firstMenuViewHolder.textView.setBackgroundResource(R.drawable.search_title_bg);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class FirstMenuViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public FirstMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setFocusable(true);
            textView = itemView.findViewById(R.id.label_title);

        }
    }
}
