package tv.newtv.cboxtv;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.newtv.cms.bean.Nav;
import com.newtv.cms.contract.NavContract;

import java.util.List;

import tv.newtv.cboxtv.cms.details.view.myRecycleView.HorizontalRecyclerView;
import tv.newtv.cboxtv.views.widget.RecycleSpaceDecoration;

public class DetailTextPopuView extends PopupWindow {

    private View inflate;
    private List<Nav> navs;
    private TextView titleView;
    private TextView contentView;
    public void showPopup(Context context, View parents,String title,String content) {
        inflate = LayoutInflater.from(context).inflate(R.layout.activity_description, null);
        titleView = inflate.findViewById(R.id.title);
        contentView = inflate.findViewById(R.id.content);
        setContentView(inflate);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setAnimationStyle(R.style.popu_anim);
        setFocusable(true);
        inflate.requestFocus();
        setBackgroundDrawable(new BitmapDrawable());
        initView(context, parents);
        titleView.setText(title);
        contentView.setText(content);
    }

    private void initView(Context context, final View parents) {
        showAtLocation(parents, Gravity.TOP, 0, 0);
    }
}