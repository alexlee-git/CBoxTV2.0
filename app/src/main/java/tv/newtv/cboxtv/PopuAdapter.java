package tv.newtv.cboxtv;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.newtv.libs.util.SharePreferenceUtils;

import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.uc.v2.LoginActivity;


public class PopuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String token;
    private List<Nav> navs;
    private Map<Integer, Nav> map;

    public PopuAdapter(Context context, List<Nav> navs) {
        this.context = context;
        this.navs = navs;
    }

    public PopuAdapter(Context context, List<Nav> navs, Map<Integer, Nav> map) {
        this.context = context;
        this.navs = navs;
        this.map = map;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(context).inflate(R.layout.item_popu_nav, parent, false);
        RecyclerView.ViewHolder popu = new PopuViewHolder(inflate);

        new Thread(new Runnable() {
            @Override
            public void run() {
                token = SharePreferenceUtils.getToken(context.getApplicationContext());
            }
        }).start();

        return popu;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (navs != null) {
            ((PopuViewHolder) holder).navName.setText(navs.get(position).getTitle());
            ((PopuViewHolder) holder).navFrame.setBackgroundResource(R.drawable.circle_no);
            Glide.with(context).load(navs.get(position).getDefaultIcon()).into(((PopuViewHolder) holder).navImg);
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
//            itemView.requestLayout();
            itemView.setFocusable(true);
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
            navImg.setOnKeyListener(this);
            navImg.setOnFocusChangeListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v != null) {
                v.setSelected(hasFocus);
            }
            if (hasFocus) {
                Glide.with(context).load(navs.get(getAdapterPosition()).getFocusIcon()).into(navImg);
                navName.setTextColor(Color.parseColor("#FFFFFF"));
                onItemGetFocus(v, getAdapterPosition());
            } else {
                Glide.with(context).load(navs.get(getAdapterPosition()).getDefaultIcon()).into(navImg);
                navName.setTextColor(Color.parseColor("#80FFFFFF"));
                navFrame.setBackgroundResource(R.drawable.circle_no);
                onItemLostFocus(v);
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
                int adapterPosition = getAdapterPosition();

                if (nav != null) {
                    if (!TextUtils.isEmpty(nav.getTitle())) {
                        String id = nav.getId();
                        for (Integer i : map.keySet()) {
                            String navId = map.get(i).getId();
                            if (id.equals(navId)){
                                adapterPosition = i;
                            }
                        }
                        Class clazz = MainActivity.class;
                        if (TextUtils.isEmpty(token) && nav.getTitle().equals("我的")) {
                            clazz = LoginActivity.class;
                        }
                        Intent intent = new Intent();
                        intent.putExtra("action", "panel");
                        intent.putExtra("params", adapterPosition + "&");
                        intent.putExtra(Constant.ACTION_FROM, false);
                        intent.setClass(context, clazz);
                        context.startActivity(intent);
                        boolean isBackground = ActivityStacks.get().isBackGround();
                        if (!isBackground && clazz == MainActivity.class) {
                            ActivityStacks.get().finishAllActivity();
                        }
                    }
                }
                return true;
            }

            return false;
        }
    }

    private void onItemLostFocus(View view) {
        // 直接缩小view
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator bigx = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f);
        ObjectAnimator bigy = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f);
        animatorSet.play(bigx).with(bigy);
        animatorSet.setDuration(300);
        animatorSet.start();
    }


    private void onItemGetFocus(View view, int adapterPosition) {
        FrameLayout viewById = view.findViewById(R.id.nav_frame);
        viewById.setBackgroundResource(R.drawable.circle_foucs);
        //直接放大view
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator bigx = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f);
        ObjectAnimator bigy = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f);
        animatorSet.play(bigx).with(bigy);
        animatorSet.setDuration(300);
        animatorSet.start();
    }


}
