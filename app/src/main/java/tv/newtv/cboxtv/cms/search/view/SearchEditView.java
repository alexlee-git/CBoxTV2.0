package tv.newtv.cboxtv.cms.search.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.newtv.libs.util.ScaleUtils;

import tv.newtv.cboxtv.R;

/**
 * Created by linzy on 2018/12/10.
 */

public class SearchEditView extends RelativeLayout {

    public SearchEditView(Context context) {
        this(context, null);
    }

    public SearchEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context){
         LayoutInflater.from(context).inflate(R.layout.search_edit_holder_item,this,true);
        FrameLayout mSearchView = findViewById(R.id.search_layout);
        mSearchView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        });
        mSearchView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                ScaleUtils.getInstance().onItemGetFocus(v);
            } else {
                ScaleUtils.getInstance().onItemLoseFocus(v);
            }
        });
    }

}
