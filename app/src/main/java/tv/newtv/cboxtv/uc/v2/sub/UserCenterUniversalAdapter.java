package tv.newtv.cboxtv.uc.v2.sub;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.newtv.cms.bean.Corner;
import com.newtv.libs.Constant;
import com.newtv.libs.util.RxBus;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.MainLooper;
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.v2.manager.UserCenterRecordManager;

/**
 * 项目名称:         央视影音
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午5:53
 * 创建人:           lixin
 * 创建日期:         2018/9/11
 */


public class UserCenterUniversalAdapter extends RecyclerView
        .Adapter<UserCenterUniversalViewHolder> {

    private final String TAG = "universal_adapter";
    private Context mContext;
    private List<UserCenterPageBean.Bean> mDatas;
    private String mContentType; // 用来区分历史 or 收藏 or 关注 or 订阅
    int recordPosition = -1;
    Boolean refresh = true;

    public UserCenterUniversalAdapter(Context context, List<UserCenterPageBean.Bean> datas,
                                      String contentType) {
        this.mContext = context;
        this.mDatas = datas;
        this.mContentType = contentType;
    }

    @Override
    public UserCenterUniversalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserCenterUniversalViewHolder(LayoutInflater.from(mContext).inflate(R.layout
                .item_usercenter_universal, parent, false));
    }

    @Override
    public void onBindViewHolder(final UserCenterUniversalViewHolder holder, final int position) {

        final UserCenterPageBean.Bean info = mDatas.get(position);
        if (refresh) {
            if (recordPosition != -1 && recordPosition == position) {
                MainLooper.get().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemView.requestFocus();

                    }
                },50);
            }
        } else {
            if (recordPosition != -1 && recordPosition == position + 1) {
                MainLooper.get().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemView.requestFocus();

                    }
                },50);
            }
        }

        if (info == null) {
            Log.d(TAG, "recommend info is null");
            return;
        }

        if (TextUtils.equals(mContentType, Constant.UC_HISTORY)) {
            // 角标 副标 & 更新剧集 & 评分
            String score = info.getGrade();
            if (!TextUtils.isEmpty(score) && !TextUtils.equals("null", score) && !TextUtils.equals(score, "0.0")) {
                holder.score.setText(score);
                holder.score.setVisibility(View.VISIBLE);
            } else {
                holder.score.setVisibility(View.INVISIBLE);
            }

            holder.subTitle.setText(UserCenterRecordManager.getInstance().getWatchProgress(info
                    .getPlayPosition(), info.getDuration()));
            holder.episode.setText(getEpisode(info));
            holder.mask.setVisibility(View.VISIBLE);
            if (holder.superscript != null) {
                if (!TextUtils.isEmpty(info.getSuperscript())) {
                    loadSuperscript(holder.superscript, info.getSuperscript());
                } else {
                    if (TextUtils.equals("1", info.getIsUpdate())) {
                        Picasso.get().load(R.drawable.superscript_update_episode).into(holder
                                .superscript);
                    }
                }
            }
        } else if (TextUtils.equals(mContentType, Constant.UC_SUBSCRIBE) || TextUtils.equals
                (mContentType, Constant.UC_COLLECTION)) {
            // 角标 & 主标 & 海报 & 更新剧集 & 评分
            String score = info.getGrade();
            if (!TextUtils.isEmpty(score) && !TextUtils.equals(score, "null") && !TextUtils.equals(score, "0.0")) {
                holder.score.setVisibility(View.VISIBLE);
                holder.score.setText(score);
            } else {
                holder.score.setVisibility(View.INVISIBLE);
            }

            holder.episode.setText(getEpisode(info));
            holder.mask.setVisibility(View.VISIBLE);
            if (holder.superscript != null) {
                if (!TextUtils.isEmpty(info.getSuperscript())) {
                    loadSuperscript(holder.superscript, info.getSuperscript());
                } else {
                    if (TextUtils.equals("1", info.getIsUpdate())) {
                        Picasso.get().load(R.drawable.superscript_update_episode).into(holder
                                .superscript);
                    }
                }
            }
        } else if (TextUtils.equals(mContentType, Constant.UC_FOLLOW)) {
            // 主标 & 海报 这两个下面统一做,这里只需将衬托剧集信息和评分区域的蒙版隐藏掉
            holder.mask.setVisibility(View.GONE);
            holder.score.setVisibility(View.GONE);
        }

        // 主标题
        holder.title.setText(info.get_title_name());

        // 海报
        String posterUrl = info.get_imageurl();
        if (!TextUtils.isEmpty(posterUrl) && !TextUtils.isEmpty("null") && holder.poster != null) {
            Picasso.get().load(posterUrl)
                    .placeholder(R.drawable.default_member_center_240_360_v2)
                    .error(R.drawable.deful_user)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .transform(new PosterCircleTransform(mContext, 4))
                    .into(holder.poster);
        } else {
            if (holder.poster != null) {
                holder.poster.setImageResource(R.drawable.deful_user);
            }
        }


        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {

                    doBigAnimation(view);
                    // title 设置selected为true
                    holder.title.setSelected(true);
                    holder.focus.setVisibility(View.VISIBLE);

                } else {
                    doSmallAnimation(view);
                    // title 设置selected为false
                    holder.title.setSelected(false);
                    holder.focus.setVisibility(View.INVISIBLE);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordPosition = position;
                RxBus.get().post("recordPosition",holder.getLayoutPosition());

                Log.d(TAG, "contentType : " + info.get_contenttype() + ", actionType : " + info.get_actiontype());
                JumpUtil.activityJump(mContext, info.get_actiontype(), info.get_contenttype(),
                        info.getContentId(), "");
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

    private void loadSuperscript(ImageView target, String superscriptId) {
        Corner info = SuperScriptManager.getInstance().getSuperscriptInfoById(superscriptId);
        if (info != null) {
            String superUrl = info.getCornerImg();
            if (superUrl != null) {
                Picasso.get().load(superUrl).into(target);
            }
        }
    }

    private String getEpisode(UserCenterPageBean.Bean entity) {
        String episode = entity.getEpisode_num();
        if (!TextUtils.isEmpty(episode) && !TextUtils.equals("null", episode)) {
            String totalCnt = entity.getTotalCnt();

            int cnt = Integer.parseInt(totalCnt);
            int episodeNum = Integer.parseInt(episode);

            String videoType = entity.getVideoType();
            Log.d(TAG, "videoType : " + videoType + ", name : " + entity.get_title_name() + ", " +
                    "cnt : " + cnt + ", episode : " + episode);
            if (TextUtils.equals(videoType, "电视剧")) {
                if (episodeNum < cnt) {
                    return ("更新至 " + episode + " 集");

                } else {
                    return (episode + " 集全");
                }
            } else if (TextUtils.equals(videoType, "综艺")) {
                if (episodeNum < cnt) {
                    return ("更新至 " + episode + " 期");
                } else {
                    return (episode + " 期全");
                }
            }
        }
        return "";
    }

    public void setRefresh(boolean b) {
        this.refresh = b;
    }


    @Override
    public long getItemId(int position) {
        return  position;
    }
}


