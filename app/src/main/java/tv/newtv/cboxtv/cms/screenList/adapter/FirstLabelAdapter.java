package tv.newtv.cboxtv.cms.screenList.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newtv.cms.bean.CategoryTreeNode;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.RxBus;
import com.newtv.libs.util.SPrefUtils;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;


/**
 * Created by 冯凯 on 2018/9/30.
 */
public class FirstLabelAdapter extends RecyclerView.Adapter<FirstLabelAdapter.FirstLabelViewHolder> {

    Context context;
    List<CategoryTreeNode> childBeans;
    private int defaultFocusLab =-1;

    public FirstLabelAdapter(Context context, List<CategoryTreeNode> childBeans) {
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

        if (i==defaultFocusLab){
            MainLooper.get().postDelayed(new Runnable() {
                @Override
                public void run() {
                    labelViewHolder.itemView.requestFocus();
                }
            },50);
        }

        if (!TextUtils.isEmpty(childBeans.get(i).getTitle()))
        labelViewHolder.textView.setText(childBeans.get(i).getTitle());

        labelViewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {


            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String videoClassType = (String) SPrefUtils.getValue(context,"screenVideoClassType","");
                String videoType = (String) SPrefUtils.getValue(context,"screenVideoType","");
                if (hasFocus) {
                    if (!TextUtils.isEmpty(videoClassType) && !TextUtils.isEmpty(videoType)){
                        LogUploadUtils.uploadLog(Constant.LOG_NODE_FILTER, "0," + videoType+","+videoClassType+","+" "+","+" "+","+" "+",");
                    }
                    RxBus.get().post("labelId", childBeans.get(i));
                    RxBus.get().post("labelRecordView",labelViewHolder.itemView);
                    if (defaultFocusLab!=-1){
                        RxBus.get().post("defaultFocusLab",true);
                    }
                    labelViewHolder.textView.setBackgroundResource(R.drawable.screen_list_select);
                } else {
                    boolean isSecondMenu = (boolean) SPrefUtils.getValue(context,"isSecondMenu",false);
                    if (!TextUtils.isEmpty(videoType) && !isSecondMenu) {
                        LogUploadUtils.uploadLog(Constant.LOG_NODE_FILTER, "0," + videoType + "," + " " + "," + " " + "," + " " + "," + " " + ",");
                    }
                    labelViewHolder.textView.setBackgroundResource(R.drawable.screen_list_default);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return childBeans.size();
    }

    public void setdefaultFocus(int defaultFocusLab) {
        this.defaultFocusLab =defaultFocusLab;
    }

    class FirstLabelViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;

        public FirstLabelViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setFocusable(true);
            textView = itemView.findViewById(R.id.label_title);
        }
    }


}



