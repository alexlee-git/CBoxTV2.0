package tv.newtv.cboxtv.cms.screenList.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.newtv.libs.util.RxBus;

import org.w3c.dom.Text;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.screenList.bean.LabelBean;

/**
 * Created by 冯凯 on 2018/9/30.
 */
public class secondLabelAdapter extends RecyclerView.Adapter<secondLabelAdapter.SecondMenuViewHolder> {
    private Context context;
    List<LabelBean.DataBean.FilterValueBean> list;
    LabelBean.DataBean dataBean;
    private int default_record_position = -1;
    private int default_record_position_second = -1;


    public secondLabelAdapter(List<LabelBean.DataBean.FilterValueBean> list, Context context, LabelBean.DataBean dataBean) {
        this.context = context;
        this.list = list;
        this.dataBean = dataBean;
    }

    @NonNull
    @Override
    public SecondMenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.label, viewGroup, false);
        SecondMenuViewHolder holder = new SecondMenuViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SecondMenuViewHolder menuViewHolder, final int i) {
        if (!TextUtils.isEmpty(list.get(i).getTitle()))
            menuViewHolder.textView.setText(list.get(i).getTitle());
        if (default_record_position != -1) {
            if (default_record_position == i) {
                MainLooper.get().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        menuViewHolder.itemView.requestFocus();

                    }
                }, 50);
            }
        }
        if (default_record_position_second != -1) {
            if (default_record_position_second == i) {
                MainLooper.get().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        menuViewHolder.itemView.requestFocus();

                    }
                }, 100);
            }
        }

        menuViewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    RxBus.get().post("labelKey", dataBean);
                    RxBus.get().post("labelValue", list.get(i));
                    RxBus.get().post("menuRecordView", menuViewHolder.itemView);
                    if (default_record_position != -1) {
                        RxBus.get().post("record_position_first", true);
                    }
                    if (default_record_position_second != -1) {
                        RxBus.get().post("record_position_second", true);
                    }
                    menuViewHolder.textView.setBackgroundResource(R.drawable.screen_list_select);
                } else {
                    menuViewHolder.textView.setBackgroundResource(R.drawable.screen_list_default);

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setDefaultFocusFirst(int default_record_position) {
        this.default_record_position = default_record_position;
    }

    public void setDefaultFocusSecond(int default_record_position_second) {
        this.default_record_position_second = default_record_position_second;
    }

    class SecondMenuViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public SecondMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setFocusable(true);
            textView = itemView.findViewById(R.id.label_title);

        }
    }
}
