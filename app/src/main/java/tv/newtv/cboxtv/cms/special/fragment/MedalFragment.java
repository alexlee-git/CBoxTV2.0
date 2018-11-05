package tv.newtv.cboxtv.cms.special.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;

import java.util.ArrayList;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.special.adapter.MedalAdapter;
import tv.newtv.cboxtv.views.CommonSpacesItemDecoration;

/**
 * 奖牌榜
 */
public class MedalFragment extends BaseSpecialContentFragment{
    private static final String TAG = "MedalFragment";

    private RecyclerView recyclerView;
    private ModelResult<ArrayList<Page>> moduleInfoResult;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_medal;
    }

    @Override
    protected void onItemContentResult(Content content) {

    }

    @Override
    protected void setUpUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        initRecyclerView();
    }

    @Override
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {
        moduleInfoResult = infoResult;
        Log.i(TAG, "setModuleInfo: "+infoResult);
        initRecyclerView();
    }

    private void initRecyclerView(){
        if(moduleInfoResult != null && recyclerView != null){
            recyclerView.setLayoutManager(new LinearLayoutManager(LauncherApplication.AppContext,
                    LinearLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(new CommonSpacesItemDecoration(0,0,0,25));
//            MedalAdapter adapter = new MedalAdapter(moduleInfoResult.getAsianList(),getContext());
//            recyclerView.setAdapter(adapter);
        }
    }

}
