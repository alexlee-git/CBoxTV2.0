package tv.newtv.cboxtv.cms.mainPage;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyPageSelectedListener;
import tv.newtv.cboxtv.cms.mainPage.model.NavInfoResult;
import tv.newtv.cboxtv.cms.mainPage.viewholder.UniversalViewHolder;

/**
 * Created by lixin on 2018/1/12.
 */

public class NavBarAdapter extends RecyclerView.Adapter<UniversalViewHolder> {

    private List<NavInfoResult.NavInfo> mNavInfos;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private String mDefaultFocusId;
    private UniversalViewHolder mCurSelectedNavItem;
    private int mTheLoveIdx; // 上次或当前选中的导航item的索引值
    private INotifyPageSelectedListener mNotifyPageSelectedListener;

    private final int NAV_ITEM_TYPE_TEXT = 0;
    private final int NAV_ITEM_TYPE_ICON = 1;

    private static final int NAV_ITEM_WIDTH_2 = 96;  // 2个字时,导航item的宽度
    private static final int NAV_ITEM_WIDTH_3 = 134; // 3个字时,导航item的宽度
    private static final int NAV_ITEM_WIDTH_4 = 184; // 4个字时,导航item的宽度

    private static final int NAV_TEXT_WIDTH_2 = 80;
    private static final int NAV_TEXT_WIDTH_3 = 116;
    private static final int NAV_TEXT_WIDTH_4 = 166;

    public NavBarAdapter(Context context, List<NavInfoResult.NavInfo> navInfos, String defaultFocusId) {
        mContext = context;
        mNavInfos = navInfos;
        mLayoutInflater = LayoutInflater.from(context);
        mDefaultFocusId = defaultFocusId;
    }

    @Override
    public UniversalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == NAV_ITEM_TYPE_ICON) {
            itemView = mLayoutInflater.inflate(R.layout.layout_nav_bar_item_icon, null, false);
        } else {
            itemView = mLayoutInflater.inflate(R.layout.layout_nav_bar_item_text, null, false);
        }
        return new UniversalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UniversalViewHolder holder, final int position) {
        int mPosition = position % mNavInfos.size();
        final NavInfoResult.NavInfo info = mNavInfos.get(mPosition);
        Log.e(Constant.TAG, "---NavInfoResult.NavInfo--position--" + mPosition);
        if (info == null) {
            return;
        }

        String iconUrl = info.getIcon1();
        if (!TextUtils.isEmpty(iconUrl)) {
            onIconNavBindViewHolder(info, holder, mPosition);
        } else {
            onTextNavBindViewHolder(info, holder, mPosition);
        }
    }

    @Override
    public int getItemCount() {
        // return (mNavInfos != null) ? mNavInfos.size() : 0;
        if (mNavInfos.size() == 0) {
            return mNavInfos.size();
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public int getItemViewType(int position) {
        int mPosition = position % mNavInfos.size();
        String iconUrl = mNavInfos.get(mPosition).getIcon1();
        if (!TextUtils.isEmpty(iconUrl)) {
            return NAV_ITEM_TYPE_ICON;
        } else {
            return NAV_ITEM_TYPE_TEXT;
        }
    }

    public void setNotifyPageSelectedListener(INotifyPageSelectedListener listener) {
        mNotifyPageSelectedListener = listener;
    }

    /**
     * 文本形式导航栏的viewhoder的数据绑定逻辑
     */
    private void onTextNavBindViewHolder(final NavInfoResult.NavInfo info, final UniversalViewHolder holder, final int position) {
        int itemWidth;
        int textWidth;
        int textLength = info.getTitle().length();
        if (textLength == 2) {
            itemWidth = NAV_ITEM_WIDTH_2;
            textWidth = NAV_TEXT_WIDTH_2;
        } else if (textLength == 3) {
            itemWidth = NAV_ITEM_WIDTH_3;
            textWidth = NAV_TEXT_WIDTH_3;
        } else if (textLength == 4) {
            itemWidth = NAV_ITEM_WIDTH_4;
            textWidth = NAV_TEXT_WIDTH_4;
        } else {
            itemWidth = NAV_ITEM_WIDTH_4;
            textWidth = NAV_TEXT_WIDTH_4;
        }

        TextView navTextView = (TextView) holder.getViewByTag("tag_nav_item_title");
        if (navTextView != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) navTextView.getLayoutParams();
            lp.width = textWidth;
            navTextView.setLayoutParams(lp);
            navTextView.setText(info.getTitle());
//            holder.itemView.setTag(navTextView);
        }

//        ImageView navBackground = (ImageView) holder.getViewByTag("tag_nav_item_focus");
//        if (navBackground != null) {
//            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) navBackground.getLayoutParams();
//            lp.width = itemWidth;
//            navBackground.setLayoutParams(lp);
//
//            int length = navTextView.getText().length();
//            if ("VIP".equals(navTextView.getText())) {
//                length = 2;
//            }
//            if (length <= 2) {
//                navBackground.setImageResource(R.drawable.nav_2);
//            } else if (length == 3) {
//                navBackground.setImageResource(R.drawable.nav_3);
//            } else {
//                navBackground.setImageResource(R.drawable.nav_4);
//            }
//        }

//        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    setNavItemToNormal(mCurSelectedNavItem);
//                    setNavItemFocused(holder);
//
//                    mTheLoveIdx = position;
//                    if (mNotifyPageSelectedListener != null) {
//                        mNotifyPageSelectedListener.notifyPageSelected(position);
//                    }
//                }
//            }
//        });

//        holder.itemView.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
//                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                    if (keycode == KeyEvent.KEYCODE_DPAD_UP) {
//                        setNavItemSelectedButNotFocused(holder);
////                if (StatusBarManager.getInstance().processKeyEvent(keyEvent)) {
////                    return true;
////                }
//                    }
//
//                    if (keycode == KeyEvent.KEYCODE_DPAD_DOWN) {
//                        Log.e(Constant.TAG, "MainPageManager flag : " + MainPageManager.getInstance().isNoPageData());
//                        if (MainPageManager.getInstance().isNoPageData()) {
//                            return true;
//                        } else {
//                            setNavItemSelectedButNotFocused(holder);
//                        }
//                        return false;
//                    }
//
//                }
//                return false;
//            }
//        });

//        // 让默认选中的item处于选中状态
//        if (mDefaultFocusId != null && mDefaultFocusId.equals(info.getContentID())) {
//            setNavItemSelectedButNotFocused(holder);
//            mTheLoveIdx = position;
//        }
    }

    /**
     * icon形式的导航栏viewholer的数据绑定逻辑
     */
    private void onIconNavBindViewHolder(NavInfoResult.NavInfo info, UniversalViewHolder holder, int position) {

    }

    public void setNavItemSelectedButNotFocused(UniversalViewHolder holder) {
        if (holder != null) {
            TextView itemText = (TextView) holder.getViewByTag("tag_nav_item_title");
            if (itemText != null) {
                itemText.setTextColor(Color.parseColor("#ea6617"));
            }

            ImageView itemFocus = (ImageView) holder.getViewByTag("tag_nav_item_focus");
            if (itemFocus != null) {
                itemFocus.setVisibility(View.INVISIBLE);
            }
            mCurSelectedNavItem = holder;
        }
    }

    public void setNavItemFocused(UniversalViewHolder holder) {
        if (holder != null) {
            TextView itemText = (TextView) holder.getViewByTag("tag_nav_item_title");
            if (itemText != null) {
                itemText.setTextColor(Color.parseColor("#cccbcb"));
            }

            ImageView itemFocus = (ImageView) holder.getViewByTag("tag_nav_item_focus");
            if (itemFocus != null) {
                itemFocus.setVisibility(View.VISIBLE);
            }
        }
        mCurSelectedNavItem = holder;
    }

    /**
     * @param holder
     */
    public void setNavItemToNormal(UniversalViewHolder holder) {
        if (holder == null) {
            return;
        }

        TextView itemText = (TextView) holder.getViewByTag("tag_nav_item_title");
        if (itemText != null) {
            itemText.setTextColor(Color.parseColor("#cccbcb"));
        }

        ImageView itemFocus = (ImageView) holder.getViewByTag("tag_nav_item_focus");
        if (itemFocus != null) {
            itemFocus.setVisibility(View.INVISIBLE);
        }
    }

    public int getTheLoveIdx() {
        return mTheLoveIdx;
    }
}

