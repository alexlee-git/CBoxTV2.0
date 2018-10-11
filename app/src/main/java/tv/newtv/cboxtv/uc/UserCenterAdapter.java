package tv.newtv.cboxtv.uc;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.libs.util.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.menu.BaseRecyclerAdapter;
import tv.newtv.cboxtv.uc.bean.UserCenterPageBean;
import tv.newtv.cboxtv.uc.listener.OnRecycleItemClickListener;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * Created by gaoleichao on 2018/3/27.
 */

public class UserCenterAdapter extends BaseRecyclerAdapter<UserCenterPageBean, RecyclerView
        .ViewHolder> {

    protected static final int TYPE_HEAD = 1001;
    private Context context;
    private Interpolator mSpringInterpolator;
    private OnRecycleItemClickListener<UserCenterPageBean.Bean> listener;
    private View firstView;

    public UserCenterAdapter(Context context, OnRecycleItemClickListener<UserCenterPageBean.Bean>
            listener) {
        this.context = context;
        this.listener = listener;
        mSpringInterpolator = new OvershootInterpolator(2.2f);
    }

    public View getFirstView() {
        return firstView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == TYPE_HEAD) {
            viewHolder = new HeadViewHolder(LayoutInflater.from(context).inflate(R.layout
                    .fragment_usercenter_btn, null));
        } else {
            viewHolder = new ContentViewHolder(LayoutInflater.from(context).inflate(R.layout
                    .fragment_usercenter_content, null));
        }
        return viewHolder;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder viewHolder = (HeadViewHolder) holder;
            setPosterData(viewHolder.mBtnHistory, R.drawable.usercenter_watchnote_icon, null);
            setPosterData(viewHolder.mBtnSubscribe, R.drawable.usercenter_subscribe_icon, null);
            setPosterData(viewHolder.mBtnCollect, R.drawable.usercenter_collect_icon, null);
            setPosterData(viewHolder.mBtnAttention, R.drawable.usercenter_attention_icon, null);
            setPosterData(viewHolder.mBtnVersion, R.drawable.usercenter_version_icon, null);
            setPosterData(viewHolder.mBtnAbout, R.drawable.usercenter_about_icon, null);

        } else if (holder instanceof ContentViewHolder) {
            ContentViewHolder viewHolder = (ContentViewHolder) holder;
            UserCenterPageBean moduleItem = mList.get(position - 1);
            setDataList(position, viewHolder, moduleItem);
        }
    }

    private void onItemLoseFocus(View view) {
        ImageView focusImageView = (ImageView) view.findViewWithTag("tag_img_focus");
        if (focusImageView != null) {
            focusImageView.setVisibility(View.GONE);
        }

        TextView titleView = (TextView) view.findViewWithTag("tag_poster_title");
        if (titleView != null) {
            titleView.setSelected(false);
        }

        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.startAnimation(sa);
    }

    private void onItemGetFocus(View view) {
        ImageView focusImageView = (ImageView) view.findViewWithTag("tag_img_focus");
        if (focusImageView != null) {
            focusImageView.setVisibility(View.VISIBLE);
        }

        TextView titleView = (TextView) view.findViewWithTag("tag_poster_title");
        if (titleView != null) {
            titleView.setSelected(true);
        }

        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(400);
        sa.setInterpolator(mSpringInterpolator);
        view.bringToFront();
        view.startAnimation(sa);
    }

    private void setDataList(int position, ContentViewHolder viewHolder, UserCenterPageBean
            entity) {
        viewHolder.titleIconIv.setVisibility(View.VISIBLE);
        viewHolder.titleTv.setVisibility(View.VISIBLE);
        viewHolder.titleTv.setText(entity.title);
        List<UserCenterPageBean.Bean> data = entity.data;
        if (data == null || data.size() == 0) {
            //暂无数据
            switch (position) {
                case 1:
                    setPosterData(viewHolder.viewList.get(0), R.drawable.uc_no_history, "");
                    break;
                case 2:
                    setPosterData(viewHolder.viewList.get(0), R.drawable.uc_no_subscribe, "");
                    break;
                case 3:
                    setPosterData(viewHolder.viewList.get(0), R.drawable.uc_no_collect, "");
                    break;
                case 4:
                    setPosterData(viewHolder.viewList.get(0), R.drawable.uc_no_attention, "");
                    break;
                case 5:
                    setPosterData(viewHolder.viewList.get(0), 1, "");
                    break;
            }
            setViewGone(1, viewHolder.viewList);
        } else {
//            setViewGone(data.size(), viewHolder.viewList);
            for (int i = 0; i <= 5; i++) {
                if (data.size() > i) {
                   setViewGVisible(viewHolder.viewList.get(i));
                    if (TextUtils.isEmpty(data.get(i)._imageurl)) {
                        setPosterData(viewHolder.viewList.get(i), R.drawable.deful_user, data.get(i)
                                ._title_name);
                    } else {
                        setPosterData(viewHolder.viewList.get(i), data.get(i)._imageurl, data.get(i)
                                ._title_name);
                    }
                } else {
                    if (viewHolder.viewList.get(i).hasFocus()) {
                        View view = FocusFinder.getInstance().findNextFocus((ViewGroup)
                                        viewHolder.itemView,
                                viewHolder.viewList.get(i), View.FOCUS_LEFT);
                        if (view != null) {
                            view.requestFocus();
                        } else {
                            view = FocusFinder.getInstance().findNextFocus((ViewGroup)
                                            viewHolder.itemView,
                                    viewHolder.viewList.get(i), View.FOCUS_RIGHT);
                            if (view != null) {
                                view.requestFocus();
                            }
                        }
                    }
                    setViewGone(viewHolder.viewList.get(i));
                }
            }
        }
    }

    private void setViewGone(FrameLayout container){
        int count = container.getChildCount();
        for(int index=0;index<count;index++){
            container.getChildAt(index).setVisibility(View.GONE);
        }
        container.setVisibility(View.GONE);
    }

    private void setViewGVisible(FrameLayout container){
        int count = container.getChildCount();
        for(int index=0;index<count;index++){
            container.getChildAt(index).setVisibility(View.VISIBLE);
        }
        ImageView img = container.findViewWithTag("tag_img_focus");

        if (container.hasFocus()){
            img.setVisibility(View.VISIBLE);//隐藏焦点框
        }else{
            img.setVisibility(View.GONE);//隐藏焦点框
        }
        container.setVisibility(View.VISIBLE);
    }

    private void setViewGone(int start, List<FrameLayout> viewList) {
        int size = viewList.size();
        for (int i = start; i < size; i++) {
            FrameLayout frameLayout = viewList.get(i);
            int count = frameLayout.getChildCount();
            for(int index=0;index<count;index++){
                frameLayout.getChildAt(index).setVisibility(View.GONE);
            }
            frameLayout.setVisibility(View.GONE);
        }
    }

    private void setPosterData(FrameLayout mModuleView, Object img, String title) {
        mModuleView.setVisibility(View.VISIBLE);
        RecycleImageView posterIv = (RecycleImageView) mModuleView.findViewWithTag("tag_poster_image");
        ImageView focusIv = (ImageView) mModuleView.findViewWithTag("tag_img_focus");

        DisplayUtils.adjustView(context,posterIv,focusIv,R.dimen.width_17dp,R.dimen.width_16dp);//UI适配

        TextView subTitleTv = (TextView) mModuleView.findViewWithTag("tag_poster_title");
        if (posterIv != null && img != null) {
            if (img instanceof String) {
                if (!TextUtils.isEmpty((String) img)) {
                    posterIv
                            .placeHolder(R.drawable.focus_240_360)
                            .errorHolder(R.drawable.focus_240_360)
                            .hasCorner(true)
                            .load((String) img);
                } else {
                    posterIv.placeHolder(R.drawable.focus_240_360)
                            .errorHolder(R.drawable.deful_user)
                            .hasCorner(true)
                            .load(R.drawable.deful_user);
                }
            } else {
                posterIv
                        .placeHolder(R.drawable.focus_240_360)
                        .errorHolder(R.drawable.deful_user)
                        .hasCorner(true)
                        .load((int) img);
            }
            mModuleView.setVisibility(View.VISIBLE);
        }
        if (subTitleTv != null) {
            if (!TextUtils.isEmpty(title)) {
                subTitleTv.setVisibility(View.VISIBLE);
                subTitleTv.setText(title);
            } else {
                subTitleTv.setVisibility(View.GONE);
                subTitleTv.setText("");
            }
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder implements View
            .OnFocusChangeListener, View.OnKeyListener {
        private ImageView titleIconIv;
        private TextView titleTv;
        private FrameLayout mModuleView1, mModuleView2, mModuleView3, mModuleView4, mModuleView5,
                mModuleView6;
        private List<FrameLayout> viewList;

        public ContentViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(false);
            titleIconIv = (ImageView) itemView.findViewById(R.id.id_module_8_title_icon);
            titleTv = (TextView) itemView.findViewById(R.id.id_module_8_title);
            mModuleView1 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view1);
            mModuleView2 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view2);
            mModuleView3 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view3);
            mModuleView4 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view4);
            mModuleView5 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view5);
            mModuleView6 = (FrameLayout) itemView.findViewById(R.id.id_module_8_view6);
            viewList = new ArrayList<>();
            viewList.add(mModuleView1);
            viewList.add(mModuleView2);
            viewList.add(mModuleView3);
            viewList.add(mModuleView4);
            viewList.add(mModuleView5);
            viewList.add(mModuleView6);
            mModuleView1.setOnFocusChangeListener(this);
            mModuleView2.setOnFocusChangeListener(this);
            mModuleView3.setOnFocusChangeListener(this);
            mModuleView4.setOnFocusChangeListener(this);
            mModuleView5.setOnFocusChangeListener(this);
            mModuleView6.setOnFocusChangeListener(this);
            mModuleView1.setOnKeyListener(this);
            mModuleView2.setOnKeyListener(this);
            mModuleView3.setOnKeyListener(this);
            mModuleView4.setOnKeyListener(this);
            mModuleView5.setOnKeyListener(this);
            mModuleView6.setOnKeyListener(this);
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                onItemGetFocus(view);
            } else {
                onItemLoseFocus(view);
            }
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (listener != null) {
                    int position = 0;
                    switch (v.getId()) {
                        case R.id.id_module_8_view1:
                            position = 0;
                            break;
                        case R.id.id_module_8_view2:
                            position = 1;
                            break;
                        case R.id.id_module_8_view3:
                            position = 2;
                            break;
                        case R.id.id_module_8_view4:
                            position = 3;
                            break;
                        case R.id.id_module_8_view5:
                            position = 4;
                            break;
                        case R.id.id_module_8_view6:
                            position = 5;
                            break;
                    }
                    if (mList != null && mList.size() != 0) {
                        if (mList.get(getAdapterPosition() - 1).data != null && mList.get
                                (getAdapterPosition() - 1).data.size() != 0) {

                            listener.onItemClick(v, position, mList.get(getAdapterPosition() - 1)
                                    .data.get(position));
                        }
                    }

                }
                return true;
            }else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                View rightView = FocusFinder.getInstance().findNextFocus((ViewGroup) v.getParent().getParent(), v,
                        View.FOCUS_RIGHT);
                if (rightView==null){
                    return true;
                }else {
                    return false;
                }

            }
            return false;
        }
    }

    class HeadViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener,
            View.OnKeyListener {

        private FrameLayout mBtnHistory, mBtnSubscribe, mBtnCollect, mBtnAttention, mBtnVersion,
                mBtnAbout;

        public HeadViewHolder(View itemView) {
            super(itemView);
            itemView.setFocusable(false);
            mBtnHistory = (FrameLayout) itemView.findViewById(R.id.id_usercenter_btn_history);
            mBtnSubscribe = (FrameLayout) itemView.findViewById(R.id.id_usercenter_btn_subscribe);
            mBtnCollect = (FrameLayout) itemView.findViewById(R.id.id_usercenter_btn_collect);
            mBtnAttention = (FrameLayout) itemView.findViewById(R.id.id_usercenter_btn_attention);
            mBtnVersion = (FrameLayout) itemView.findViewById(R.id.id_usercenter_btn_version);
            mBtnAbout = (FrameLayout) itemView.findViewById(R.id.id_usercenter_btn_about);
            mBtnHistory.setOnFocusChangeListener(this);
            mBtnSubscribe.setOnFocusChangeListener(this);
            mBtnCollect.setOnFocusChangeListener(this);
            mBtnAttention.setOnFocusChangeListener(this);
            mBtnVersion.setOnFocusChangeListener(this);
            mBtnAbout.setOnFocusChangeListener(this);

            mBtnHistory.setOnKeyListener(this);
            mBtnSubscribe.setOnKeyListener(this);
            mBtnCollect.setOnKeyListener(this);
            mBtnAttention.setOnKeyListener(this);
            mBtnVersion.setOnKeyListener(this);
            mBtnAbout.setOnKeyListener(this);

            firstView = mBtnHistory;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (listener != null) {
                    listener.onItemClick(v, 0, null);
                }
                return true;
            }
            return false;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                onItemGetFocus(view);
            } else {
                onItemLoseFocus(view);
            }
        }
    }
}
