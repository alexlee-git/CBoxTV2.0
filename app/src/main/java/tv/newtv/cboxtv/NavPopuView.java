package tv.newtv.cboxtv;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
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
import com.newtv.cms.contract.NavContract;

import java.util.List;

import tv.newtv.cboxtv.cms.details.view.myRecycleView.HorizontalRecyclerView;
import tv.newtv.cboxtv.views.widget.RecycleSpaceDecoration;

public class NavPopuView extends PopupWindow {
    private View inflate;
    private HorizontalRecyclerView navRecycle;
    private List<Nav> navs;

    public void showPopup(Context context, View parents) {
        inflate = LayoutInflater.from(context).inflate(R.layout.navigation_popu, null);
        setContentView(inflate);
        ListDataSave listDataSave = new ListDataSave(context,"navData");
        navs = listDataSave.getDataList("nav");
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
        ImageView ivLeft = inflate.findViewById(R.id.iv_left);
        ImageView ivRight = inflate.findViewById(R.id.iv_right);
        showAtLocation(parents, Gravity.TOP, 0, 0);
        navRecycle.addItemDecoration(new RecycleSpaceDecoration(context.getResources().getDimensionPixelSize(R.dimen.width_72px), context.getResources().getDimensionPixelSize(R.dimen.width_72px)));//new SpacesItemDecoration(ScreenUtils.dp2px(30))

        PopuAdapter adapter = new PopuAdapter(context, navs);
        navRecycle.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        navRecycle.setAdapter(adapter);

        navRecycle.addOnScrollListener(new RecyclerView.OnScrollListener
                () {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastPosition = ((LinearLayoutManager) recyclerView
                            .getLayoutManager()).findLastVisibleItemPosition();
                    int firstVisibleItemPosition = ((LinearLayoutManager)
                            recyclerView.getLayoutManager())
                            .findFirstVisibleItemPosition();
                    if (firstVisibleItemPosition == 0) {
                        ivLeft.setVisibility(View.INVISIBLE);
                    } else {
                        ivLeft.setVisibility(View.VISIBLE);
                    }
                    if (lastPosition == navs.size() - 1) {

                        ivRight.setVisibility(View.INVISIBLE);

                    } else {
                        ivRight.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }
}