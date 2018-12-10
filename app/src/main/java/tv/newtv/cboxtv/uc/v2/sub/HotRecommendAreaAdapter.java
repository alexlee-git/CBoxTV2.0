package tv.newtv.cboxtv.uc.v2.sub;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Program;
import com.newtv.libs.Constant;
import com.squareup.picasso.Picasso;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;

/**
 * 项目名称:         央视影音
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午6:39
 * 创建人:           lixin
 * 创建日期:         2018/9/20
 */


public class HotRecommendAreaAdapter extends RecyclerView.Adapter<HotRecommendAreaAdapter.MyViewHolder> {

    private List<Program> mDatas;
    private Context mContext;

    public HotRecommendAreaAdapter(Context context, List<Program> datas) {
        mContext = context;
        mDatas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_usercenter_universal, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Program info = mDatas.get(position);
        if (!TextUtils.isEmpty(info.getGrade())&& !TextUtils.equals(info.getGrade(), "null")) {
            holder.score.setText(info.getGrade());
        } else {
            if (holder.score != null) {
                holder.score.setVisibility(View.INVISIBLE);
            }
        }

        holder.name.setText(info.getTitle());
        // holder.episode.setText("更新至xxxx集");

        String posterUrl = info.getImg();
        if (!TextUtils.isEmpty(posterUrl)) {
            Picasso.get().load(posterUrl).transform(new PosterCircleTransform(mContext, 4)).placeholder(R.drawable.default_member_center_240_360_v2).error(R.drawable.deful_user).into(holder.poster);
        } else {
            Picasso.get().load(R.drawable.default_member_center_240_360_v2).into(holder.poster);
        }

//        String rSuperScript = info.getrSuperScript();
//        if (!TextUtils.isEmpty(rSuperScript)) {
//            Picasso.get().load(rSuperScript).into(holder.poster);
//        }

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    holder.focus.setVisibility(View.VISIBLE);
                    holder.name.setSelected(true);
                    doBigAnimation(view);
                } else {
                    holder.focus.setVisibility(View.INVISIBLE);
                    holder.name.setSelected(false);
                    doSmallAnimation(view);
                }
            }
        });

        if (position == 0) {
            holder.itemView.requestFocus();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JumpUtil.activityJump(mContext, Constant.OPEN_DETAILS, info.getL_contentType(), info.getL_id(), "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    private void doBigAnimation(View imageView) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator bigx = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.1f);
        ObjectAnimator bigy = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.1f);
        animatorSet.play(bigx).with(bigy);
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    private void doSmallAnimation(View imageView) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator bigx = ObjectAnimator.ofFloat(imageView, "scaleX", 1.1f, 1f);
        ObjectAnimator bigy = ObjectAnimator.ofFloat(imageView, "scaleY", 1.1f, 1f);
        animatorSet.play(bigx).with(bigy);
        animatorSet.setDuration(300);
        animatorSet.start();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView score;
        TextView name;
        TextView episode;
        ImageView poster;
        ImageView superscript;
        ImageView focus;

        public MyViewHolder(View itemView) {
            super(itemView);
            score = itemView.findViewById(R.id.id_score);
            name = itemView.findViewById(R.id.id_title);
            focus = itemView.findViewById(R.id.id_focus);
            episode = itemView.findViewById(R.id.id_episode_data);
            poster = itemView.findViewById(R.id.id_poster);
            superscript = itemView.findViewById(R.id.id_superscript);
        }
    }
}
