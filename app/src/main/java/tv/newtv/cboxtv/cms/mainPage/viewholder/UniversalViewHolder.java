package tv.newtv.cboxtv.cms.mainPage.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tv.newtv.cboxtv.views.custom.ICustomPlayer;


/**
 * Created by lixin on 2018/1/31.
 */

public class UniversalViewHolder extends RecyclerView.ViewHolder {

    private Map<String, View> mViews;
    private boolean custom = false;
    private String mPageUUID;
    private List<ICustomPlayer> playerViews;

    UniversalViewHolder(View itemView, String pageUUID) {
        super(itemView);
        mPageUUID = pageUUID;

        if (itemView instanceof ViewGroup) {
            ((ViewGroup) itemView).setClipChildren(false);
            ((ViewGroup) itemView).setClipToPadding(false);
        }
        mViews = new HashMap<>(Constant.BUFFER_SIZE_8);
    }

    public void setCustom() {
        custom = true;
    }

    public boolean isCustom() {
        return custom;
    }

    public void bind() {
        traverseViewGroup(itemView);
    }

    public void detached() {
        LogUtils.d("Holder", "detached " + playerViews);
    }

    public void attached() {
        LogUtils.d("Holder", "attached " + playerViews);
    }

    public void destroy() {
        if (itemView instanceof AutoBlockType) {
            ((AutoBlockType) itemView).destroy();
        }

        if (playerViews != null) {
            for (ICustomPlayer player : playerViews) {
                player.destroy();
            }
            playerViews.clear();
            playerViews = null;
        }

        if (mViews != null) {

            mViews.clear();
            mViews = null;
        }
    }

    // 遍历viewGroup
    private void traverseViewGroup(View view) {
        if (null == view) {
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            LinkedList<ViewGroup> linkedList = new LinkedList<>();
            linkedList.add(viewGroup);

            if (playerViews == null) {
                playerViews = new ArrayList<>();
            } else {
                playerViews.clear();
            }

            while (!linkedList.isEmpty()) {
                ViewGroup current = linkedList.removeFirst();
                if (current instanceof ICustomPlayer) {
                    playerViews.add((ICustomPlayer) current);
                }
                for (int i = 0; i < current.getChildCount(); i++) {
                    if (current.getChildAt(i) instanceof ViewGroup) {
                        linkedList.addLast((ViewGroup) current.getChildAt(i));
                    }
                }
            }
        }
    }

    public View getViewByTag(String tag) {
        View targetView = null;
        if (!TextUtils.isEmpty(tag)) {
            if (mViews != null) {
                targetView = mViews.get(tag);
                if (targetView != null) {
                    return targetView;
                } else {
                    targetView = itemView.findViewWithTag(tag);
                    mViews.put(tag, targetView);
                }
            }
        } else {
            LogUtils.d(Constant.TAG, "invalid view tag");
        }
        return targetView;
    }

    public void releaseImageView() {
        if (mViews == null) {
            return;
        }
        Set<String> keySet = mViews.keySet();
        for (String key : keySet) {
            View view = mViews.get(key);
            if (view instanceof ImageView) {
                ((ImageView) view).setImageDrawable(null);
            }
        }
    }

    public UniversalViewHolder setImageResource(String tag, int resId) {
        ImageView imageView = (ImageView) getViewByTag(tag);
        if (imageView != null) {
            imageView.setImageResource(resId);
        }
        return this;
    }

    public UniversalViewHolder setText(String tag, String text) {
        TextView textView = (TextView) getViewByTag(tag);
        if (textView != null) {
            textView.setText(text);
        }
        return this;
    }

//    @Override
//    public void onListVisiItemChange(String contentId, int first, int last) {
//        if (TextUtils.equals(contentId, mPageUUID) && getAdapterPosition() >= first &&
//                getAdapterPosition() <= last) {
//            //显示出来了
//            if(playerViews != null){
//                for(ICustomPlayer player : playerViews){
//                    player.onWindowVisibleChange(View.VISIBLE);
//                }
//            }
//
//        } else {
//            //没显示出来
//            if(playerViews != null){
//                for(ICustomPlayer player : playerViews){
//                    player.onWindowVisibleChange(View.GONE);
//                }
//            }
//        }
//    }
}
