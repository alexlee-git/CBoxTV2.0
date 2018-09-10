package tv.newtv.cboxtv.cms.mainPage.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.cms.util.LogUtils;

/**
 * Created by lixin on 2018/1/31.
 */

public class UniversalViewHolder extends RecyclerView.ViewHolder {

    private Map<String, View> mViews;

    public void destroy(){
        if(mViews != null){
            mViews.clear();
            mViews = null;
        }
    }

    public UniversalViewHolder(View itemView) {
        super(itemView);
        if(itemView instanceof ViewGroup){
            ((ViewGroup) itemView).setClipChildren(false);
            ((ViewGroup) itemView).setClipToPadding(false);
        }
        mViews = new HashMap<>(Constant.BUFFER_SIZE_8);
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

    public UniversalViewHolder setImageByUrl(String tag, String url) {

        return this;
    }

    public void releaseImageView(){
        if(mViews == null){
            return;
        }

        Set<String> keySet = mViews.keySet();
        for(String key : keySet){
            View view = mViews.get(key);
            if(view instanceof ImageView){
                ((ImageView)view).setImageDrawable(null);
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
}
