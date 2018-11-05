package tv.newtv.cboxtv.cms.special.fragment;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.libs.Constant;
import com.newtv.libs.util.ScaleUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.special.OnItemAction;
import tv.newtv.cboxtv.cms.util.JumpUtil;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         19:07
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class ScoreFragment extends BaseSpecialContentFragment {
    AiyaRecyclerView recyclerView;
    private ModelResult<ArrayList<Page>> moduleInfoResult;
    private int currentIndex = 0;


    @Override
    protected int getLayoutId() {
        return R.layout.score_layout;
    }

    @Override
    protected void onItemContentResult(Content content) {

    }

    @Override
    protected void setUpUI(View view) {
        recyclerView = view.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(LauncherApplication.AppContext,
                LinearLayoutManager.VERTICAL, false));
        ScoreAdapter scoreAdapter = new ScoreAdapter();


        recyclerView.setAdapter(scoreAdapter);
        if (moduleInfoResult != null) {
            scoreAdapter.refreshData(moduleInfoResult.getData(),getView()!=null?getView().findFocus()
                    :null).notifyDataSetChanged();
        }
//        view.requestFocus();
//        recyclerView.getDefaultFocusView();
//        recyclerView.scrollToPosition(0);
//        recyclerView.setFocusView(view.findViewById(R.id.));
//        scoreAdapter.setItemPosition(0);
        scoreAdapter.setOnItemAction(new OnItemAction<Page>() {
            @Override
            public void onItemFocus(View item) {

            }

            @Override
            public void onItemClick(Page item, int index) {

            }

            @Override
            public void onItemChange(int before, int current) {

            }
        });

    }

    @Override
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {
        moduleInfoResult = infoResult;
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            ((ScoreAdapter) recyclerView.getAdapter()).refreshData(infoResult.getData(),getView()
                    !=null?getView().findFocus():null)
                    .notifyDataSetChanged();
            recyclerView.requestFocus();
        }
    }




    private static class ScoreAdapter extends RecyclerView.Adapter<ScoreViewHolder> {
        private List<Page> mModuleItems;
        private OnItemAction<Page> mOnItemAction;
        private boolean hasFocus;

        void setOnItemAction(OnItemAction<Page> onItemAction) {
            mOnItemAction = onItemAction;
        }

        public ScoreAdapter refreshData(List<Page> moduleItems,View focusItem) {
            mModuleItems = moduleItems;
            hasFocus = focusItem != null;
            return this;
        }

        @Override
        public ScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .score_item_layout, parent, false);

            return new ScoreViewHolder(view, this);
        }

        private Page getItem(int pos) {
            if (mModuleItems != null && pos < mModuleItems.size() && pos >= 0) {
                return mModuleItems.get(pos);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final ScoreViewHolder holder, int position) {
            Page moduleItem = getItem(position);
            holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus){
                        hasFocus = true;
                        if (mOnItemAction != null) mOnItemAction.onItemFocus(view);
                        ScaleUtils.getInstance().onItemGetFocus(view, holder.data_1_focus);
                    } else {
                        ScaleUtils.getInstance().onItemLoseFocus(view, holder.data_1_focus);
                    }

                }
            });

            if (moduleItem != null) {
                holder.setData(moduleItem.getBlockTitle(), moduleItem.getPrograms());
            }

            if(!hasFocus && position == 0){
                holder.requestFocus();
            }

        }

        @Override
        public int getItemCount() {
            return mModuleItems != null ? mModuleItems.size() : 0;
        }

        public void setItemPosition( int position) {
//            position
        }
    }

    private static class ScoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Resources resources;
        private String[] ids = new String[]{"level", "icon", "name", "total", "victory", "ping",
                "lost", "js", "score"};
        private ScoreAdapter mAdapter;
        public FrameLayout data_1_focus;

        public void requestFocus(){
            data_1_focus.requestFocus();
        }


        public ScoreViewHolder(View itemView, ScoreAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;
            resources = itemView.getContext().getResources();
             data_1_focus = itemView.findViewById(R.id.data_1);

            itemView.findViewById(R.id.data_1).setOnClickListener(this);
            itemView.findViewById(R.id.data_2).setOnClickListener(this);
            itemView.findViewById(R.id.data_3).setOnClickListener(this);
            itemView.findViewById(R.id.data_4).setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int index = -1;
            switch (v.getId()) {
                case R.id.data_1:
                    index = 0;
                    break;
                case R.id.data_2:
                    index = 1;
                    break;
                case R.id.data_3:
                    index = 2;
                    break;
                case R.id.data_4:
                    index = 3;
                    break;
            }
//            String uuid = mAdapter.getItem(getAdapterPosition()).getDatas().get(index).getExtendAttr().get(0).getSeriesSubUUID();
//            if (!TextUtils.isEmpty(uuid)) {
//                JumpUtil.detailsJumpActivity(v.getContext(), Constant.CONTENTTYPE_PS, uuid);
//            }
        }

        private int getId(String name) {
            return resources.getIdentifier(name, "id", itemView.getContext()
                    .getPackageName());
        }

        public void setData(String title, List<Program> data) {
            ((TextView) itemView.findViewById(R.id.match_title)).setText(title);
            for (int index = 1; index < 5; index++) {
                String boxName = "data_" + index;
                int id = getId(boxName);
                View target = itemView.findViewById(id);
                if (data == null || index > data.size()) {
                    if (target != null) {
                        target.setVisibility(View.GONE);
                    }
                    continue;
                }

                Program programInfo = data.get(index - 1);
                for (int t = 0; t < ids.length; t++) {
                    String targetname = boxName + "_" + ids[t];
                    int cell_id = getId(targetname);
                    View cell_target = itemView.findViewById(cell_id);
                    if (cell_target != null) {
                        if (cell_target instanceof TextView) {
                            ((TextView) cell_target).setText(getValue(programInfo, t));
                        } else if (cell_target instanceof ImageView) {
                            if (programInfo != null && !TextUtils.isEmpty(programInfo.getImg())) {
                                Picasso.get()
                                        .load(getValue(programInfo, t))
                                        .into((ImageView) cell_target);
                            }
                        }
                    }
                }
            }
        }

        private String getValue(Program info, int index) {
            if (info == null)
                return "N/A";

            switch (index) {
                case 0:
//                    if (info.getExtendAttr() != null && info.getExtendAttr().size() > 0)
//                        return info.getExtendAttr().get(0).getOrder();
//                    else
                        return "N/A";
                case 1:
//                    return info.getImg();
                case 2:
                    return info.getTitle();
                case 3:
//                    if (info.getExtendAttr() != null && info.getExtendAttr().size() > 0)
//                        return info.getExtendAttr().get(0).getMatch();
//                    else
                        return "N/A";
                case 4:
//                    if (info.getExtendAttr() != null && info.getExtendAttr().size() > 0)
//                        return info.getExtendAttr().get(0).getWin();
//                    else
                        return "N/A";
                case 5:
//                    if (info.getExtendAttr() != null && info.getExtendAttr().size() > 0)
//                        return info.getExtendAttr().get(0).getDraw();
//                    else
                        return "N/A";
                case 6:
//                    if (info.getExtendAttr() != null && info.getExtendAttr().size() > 0)
//                        return info.getExtendAttr().get(0).getLost();
//                    else
                        return "N/A";
                case 7:
//                    if (info.getExtendAttr() != null && info.getExtendAttr().size() > 0)
//                        return info.getExtendAttr().get(0).getGoal();
//                    else
                        return "N/A";
                case 8:
//                    if (info.getExtendAttr() != null && info.getExtendAttr().size() > 0)
//                        return info.getExtendAttr().get(0).getScore();
//                    else
                        return "N/A";
                default:
                    return "N/A";
            }
        }

    }

}
