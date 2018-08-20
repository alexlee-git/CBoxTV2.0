package tv.newtv.cboxtv.cms.mainPage;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.listPage.model.NavListPageInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.INotifyPageSelectedListener;
import tv.newtv.cboxtv.cms.mainPage.viewholder.UniversalViewHolder;


public class NavListPageAdapter extends RecyclerView.Adapter<UniversalViewHolder> {

    private List<NavListPageInfoResult.NavInfo> mNavInfos;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private String mDefaultFocusId;
    private UniversalViewHolder mCurSelectedNavItem;
    private int mTheLoveIdx; // 上次或当前选中的导航item的索引值
    private INotifyPageSelectedListener mNotifyPageSelectedListener;

    private final int NAV_ITEM_TYPE_TEXT = 0;
    private final int NAV_ITEM_TYPE_ICON = 1;

    private static final int NAV_ITEM_WIDTH_2 = 116;  // 2个字时,导航item的宽度
    private static final int NAV_ITEM_WIDTH_3 = 166; // 3个字时,导航item的宽度
    private static final int NAV_ITEM_WIDTH_4 = 216; // 4个字时,导航item的宽度
    private static final int NAV_ITEM_WIDTH_5 = 266; // 5个字时,导航item的宽度

    private static final int NAV_TEXT_WIDTH_2 = 100;
    private static final int NAV_TEXT_WIDTH_3 = 150;
    private static final int NAV_TEXT_WIDTH_4 = 200;
    private static final int NAV_TEXT_WIDTH_5 = 250;

    public NavListPageAdapter(Context context, List<NavListPageInfoResult.NavInfo> navInfos, String defaultFocusId) {
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
        final NavListPageInfoResult.NavInfo info = mNavInfos.get(mPosition);
        Log.e(Constant.TAG, "---NavInfoResult.NavInfo--position--" + mPosition);
        if (info == null) {
            return;
        }

        String iconUrl = info.getTitle();
        if (iconUrl.contains("CCTV")) {
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
        String iconUrl = mNavInfos.get(mPosition).getTitle();
//        if (!TextUtils.isEmpty(iconUrl)) {
        if (iconUrl.contains("CCTV")) {
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
    private void onTextNavBindViewHolder(NavListPageInfoResult.NavInfo info, final UniversalViewHolder holder, final int position) {
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
        } else if (textLength == 5) {
            itemWidth = NAV_ITEM_WIDTH_5;
            textWidth = NAV_TEXT_WIDTH_5;
        } else {
            itemWidth = NAV_ITEM_WIDTH_5;
            textWidth = NAV_TEXT_WIDTH_5;
        }

        TextView navTextView = (TextView) holder.getViewByTag("tag_nav_item_title");
        if (navTextView != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) navTextView.getLayoutParams();
            lp.width = textWidth;
            navTextView.setLayoutParams(lp);
            navTextView.setText(info.getTitle());
            navTextView.setAlpha(0.3f);
        }

        ImageView navBackground = (ImageView) holder.getViewByTag("tag_nav_item_focus");
        if (navBackground != null && navTextView != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) navBackground.getLayoutParams();
            lp.width = itemWidth;
            navBackground.setLayoutParams(lp);

            int length = navTextView.getText().length();
            if ("VIP".equals(navTextView.getText())) {
                length = 2;
            }
            if (length <= 2) {
                navBackground.setImageResource(R.drawable.nav_2);
            } else if (length == 3) {
                navBackground.setImageResource(R.drawable.nav_3);
            } else {
                navBackground.setImageResource(R.drawable.nav_4);
            }
        }
    }

    /**
     * icon形式的导航栏viewholer的数据绑定逻辑
     */

    private void onIconNavBindViewHolder(NavListPageInfoResult.NavInfo info, UniversalViewHolder holder, int position) {
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
        } else if (textLength == 5) {
            itemWidth = NAV_ITEM_WIDTH_5;
            textWidth = NAV_TEXT_WIDTH_5;
        } else {
            itemWidth = NAV_ITEM_WIDTH_5;
            textWidth = NAV_TEXT_WIDTH_5;
        }


        final ImageView navBackground = (ImageView) holder.getViewByTag("tag_nav_item_title");
        if (navBackground != null) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) navBackground.getLayoutParams();
            lp.width = textWidth;
            navBackground.setLayoutParams(lp);
//            Picasso.with(mContext).load(info.getIcon()).into(navBackground);
            Picasso.with(mContext).load(R.drawable.cctv_selected).into(navBackground);
            navBackground.setAlpha(0.3f);
        }
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

