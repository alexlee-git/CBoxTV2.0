package tv.newtv.cboxtv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.newtv.cms.bean.Nav;
import com.newtv.libs.Constant;

import java.util.List;


public class PopuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Nav> navs;

    public PopuAdapter(Context context, List<Nav> navs) {
        this.context = context;
        this.navs = navs;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(context).inflate(R.layout.item_popu_nav, parent, false);
        RecyclerView.ViewHolder popu = new PopuViewHolder(inflate);
        return popu;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (navs != null) {
            ((PopuViewHolder) holder).navName.setText(navs.get(position).getTitle());
            ((PopuViewHolder) holder).navFrame.setBackgroundResource(R.drawable.circle_no);
            Glide.with(context).load(navs.get(position).getCurrentIcon()).into(((PopuViewHolder) holder).navImg);
        }

    }

    @Override
    public int getItemCount() {
        if (navs != null) {
            return navs.size();
        } else {
            return 0;
        }
    }


    class PopuViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnKeyListener {

        private final ImageView navImg;
        private final TextView navName;
        private final LinearLayout navParams;
        private final FrameLayout navFrame;


        public PopuViewHolder(View itemView) {
            super(itemView);
            itemView.requestLayout();
            navName = (TextView) itemView.findViewById(R.id.nav_name);
            navImg = (ImageView) itemView.findViewById(R.id.nav_img);
            navParams = itemView.findViewById(R.id.nav_params);
            navFrame = itemView.findViewById(R.id.nav_frame);
            navParams.setOnFocusChangeListener(this);
            navParams.setOnKeyListener(this);
            navName.setOnFocusChangeListener(this);
            navName.setOnKeyListener(this);
            navFrame.setOnFocusChangeListener(this);
            navFrame.setOnKeyListener(this);
            navImg.setOnFocusChangeListener(this);
            navImg.setOnFocusChangeListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v != null) {
                v.setSelected(hasFocus);
            }
            if (hasFocus) {
                navName.setTextColor(Color.parseColor("#FFFFFF"));
                onItemGetFocus(v, getAdapterPosition());
            } else {
                navName.setTextColor(Color.parseColor("#80FFFFFF"));
                navFrame.setBackgroundResource(R.drawable.circle_no);
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return false;
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    Nav nav = navs.get(getAdapterPosition());
                    if (nav != null) {
                        if (!TextUtils.isEmpty(nav.getTitle())) {
                            Intent intent = new Intent();
                            intent.putExtra("action", "panel");
                            intent.putExtra("params", getAdapterPosition() + "&");
                            intent.putExtra(Constant.ACTION_FROM, false);
                            intent.setClass(context, MainActivity.class);
                            context.startActivity(intent);
                            boolean isBackground = ActivityStacks.get().isBackGround();
                            if (!isBackground){
                                ActivityStacks.get().finishAllActivity();
                            }
                        }
                    }
                    return true;
                }

            return false;
        }
    }


    private void onItemGetFocus(View view, int adapterPosition) {
        FrameLayout viewById = view.findViewById(R.id.nav_frame);
        viewById.setBackgroundResource(R.drawable.circle_foucs);

    }


}
