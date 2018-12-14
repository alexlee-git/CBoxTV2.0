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
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.newtv.cms.bean.Nav;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.cms.details.view.myRecycleView.HorizontalRecyclerView;
import com.newtv.libs.Cache;

import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.views.widget.RecycleSpaceDecoration;

public class NavPopuView extends PopupWindow {
    private View inflate;
    private AiyaRecyclerView navRecycle;
    private List<Nav> list;
    private List<Nav> navs;
    private Map<Integer, Nav> map;


    public void showPopup(Context context, View parents) {
        inflate = LayoutInflater.from(context).inflate(R.layout.navigation_popu, null);
        setContentView(inflate);
        navs = Cache.getInstance().get(Cache.CACHE_TYPE_NAV, "navId");
        if (navs==null){
            return;
        }
        list = new ArrayList<>(navs.size());
        map = new HashMap<>();
        Nav searchNav = null, meNav = null;
        for (int i = 0; i < navs.size(); i++) {
            if ("搜索".equals(navs.get(i).getTitle())){
                searchNav = navs.get(i);
            }else if ("我的".equals(navs.get(i).getTitle())){
                meNav = navs.get(i);
            }else {
                list.add(navs.get(i));
            }
            map.put(i,navs.get(i));
        }
        if (searchNav != null) {
            list.add(0, searchNav);
        }
        if (meNav != null) {
            list.add(meNav);
        }

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setAnimationStyle(R.style.popu_anim);
        setFocusable(true);
        inflate.requestFocus();
        setBackgroundDrawable(new BitmapDrawable());
        initView(context, parents);
    }

    private void initView(Context context, final View parents) {
        navRecycle = inflate.findViewById(R.id.nav_recycle);
        showAtLocation(parents, Gravity.TOP, 0, 0);
        navRecycle.addItemDecoration(new RecycleSpaceDecoration(context.getResources().getDimensionPixelSize(R.dimen.width_72px), context.getResources().getDimensionPixelSize(R.dimen.width_72px)));//new SpacesItemDecoration(ScreenUtils.dp2px(30))

        PopuAdapter adapter = new PopuAdapter(context, list,map);
        navRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        navRecycle.setAlign(AiyaRecyclerView.ALIGN_CENTER);
        navRecycle.setAdapter(adapter);


    }
}