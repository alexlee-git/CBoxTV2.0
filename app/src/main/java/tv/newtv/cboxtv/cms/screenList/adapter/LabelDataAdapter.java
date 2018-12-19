package tv.newtv.cboxtv.cms.screenList.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.SubContent;
import com.newtv.libs.util.DisplayUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.utils.SpannableBuilderUtils;


public class LabelDataAdapter extends RecyclerView.Adapter<LabelDataAdapter.MyHolder> {

    private Context context;
    private List<SubContent> list;
    private MyHolder holder;


    public LabelDataAdapter(Context context, List<SubContent> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.label_data, viewGroup, false);
        holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, int i) {
        if (!TextUtils.isEmpty(list.get(i).getTitle()))
            myHolder.label_title.setText(list.get(i).getTitle());

        //评分
        if (list.get(i).getGrade() != null
                && !TextUtils.equals(list.get(i).getGrade(), "0")
                && !TextUtils.equals(list.get(i).getGrade(), "0.0")
                && !TextUtils.equals(list.get(i).getGrade(), null)) {
            myHolder.labelGrade.setVisibility(View.VISIBLE);
            myHolder.labelGrade.setText(list.get(i).getGrade());
        }

        //更新
        if (!TextUtils.isEmpty(list.get(i).getRecentMsg())) {
            myHolder.labelNum.setVisibility(View.VISIBLE);
            myHolder.labelNum.setText(SpannableBuilderUtils.builderMsg(list.get(i).getRecentMsg()));
        } else if (!TextUtils.isEmpty(list.get(i).getRecentNum())){
            myHolder.labelNum.setText(SpannableBuilderUtils.builderNum(list.get(i).getRecentNum()));
        }

        Picasso.get()
                .load(list.get(i).getVImage())     //图片加载地址
                .placeholder(R.drawable.focus_240_360)
                .error(R.drawable.deful_user)   //图片记载失败时显示的页面
                .noFade()       //设置淡入淡出效果
//                        .resize(240, 360)
//                        .centerInside()
                .fit()      //智能展示图片，对于图片的大小和imageview的尺寸进行了测量，计算出最佳的大小和最佳的质量显示出来
                .transform(new PosterCircleTransform(context))
                .into(myHolder.label_img);

        myHolder.frameLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onItemGetFocus(v);
                    myHolder.focus.setVisibility(View.VISIBLE);
                    myHolder.label_title.setSelected(true);
                } else {
                    onItemLoseFocus(v);
                    myHolder.focus.setVisibility(View.INVISIBLE);
                    myHolder.label_title.setSelected(false);

                }
            }
        });

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int myHolderLayoutPosition = myHolder.getLayoutPosition();
                mOnItemClickListener.onItemClick(v, myHolderLayoutPosition);
            }
        });


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView label_title;
        private ImageView label_img;
        private ImageView focus;
        private FrameLayout frameLayout;

        //更新和评分
        private TextView labelNum, labelGrade;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setFocusable(true);
            frameLayout = itemView.findViewById(R.id.focusLayout);
            label_title = itemView.findViewById(R.id.label_title);
            label_img = itemView.findViewById(R.id.label_img);
            focus = itemView.findViewById(R.id.focus);

            labelNum = itemView.findViewById(R.id.label_num);
            labelGrade = itemView.findViewById(R.id.label_grade);

            DisplayUtils.adjustView(context, label_img, focus, R.dimen.width_17dp, R.dimen.width_17dp);

        }
    }

    private void onItemGetFocus(View view) {
        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }

    private void onItemLoseFocus(View view) {
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
