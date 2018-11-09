package tv.newtv.cboxtv.uc.v2.sub;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tv.newtv.cboxtv.R;

/**
 * 项目名称:         熊猫ROM-launcher应用
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午5:54
 * 创建人:           lixin
 * 创建日期:         2018/9/11
 */


public class UserCenterUniversalViewHolder extends RecyclerView.ViewHolder {

    public ImageView superscript;
    public ImageView poster;
    public TextView score;
    public TextView title;
    public TextView subTitle;
    public ImageView mask;
    public TextView episode;
    public ImageView focus;

    public UserCenterUniversalViewHolder(View itemView) {
        super(itemView);

        superscript = itemView.findViewById(R.id.id_superscript);
        poster   = itemView.findViewById(R.id.id_poster);
        score    = itemView.findViewById(R.id.id_score);
        title    = itemView.findViewById(R.id.id_title);
        subTitle = itemView.findViewById(R.id.id_subtitle);
        episode  = itemView.findViewById(R.id.id_episode_data);
        mask     = itemView.findViewById(R.id.id_mask);
        focus    = itemView.findViewById(R.id.id_focus);
    }
}
