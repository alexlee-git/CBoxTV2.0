package tv.newtv.cboxtv.cms.special.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.AiyaRecyclerView;
import tv.newtv.cboxtv.cms.special.OnItemAction;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.cms.util.PosterCircleTransform;
import tv.newtv.cboxtv.views.widget.RecycleSpaceDecoration;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         11:06
 * 创建人:           weihaichao
 * 创建日期:          2018/4/26
 */
public class BallRoundFragment extends BaseSpecialContentFragment {

    private AiyaRecyclerView recyclerView;
    private ModelResult<ArrayList<Page>> mModuleInfoResult;

    @Override
    protected int getLayoutId() {
        return R.layout.ball_round_layout;
    }

    @Override
    protected void onItemContentResult(String uuid, Content content, int playIndex) {

    }

    @Override
    protected void setUpUI(View view) {
        recyclerView = view.findViewById(R.id.recycle_view);
        int space = view.getContext().getResources().getDimensionPixelOffset(R.dimen.height_10px);
        recyclerView.setAlign(AiyaRecyclerView.ALIGN_START);
        recyclerView.addItemDecoration(new RecycleSpaceDecoration(space, 0));
        recyclerView.setLayoutManager(new LinearLayoutManager(LauncherApplication.AppContext,
                LinearLayoutManager.VERTICAL, false));
        BallRoundAdapter adapter = new BallRoundAdapter();
        adapter.setOnFocus(new OnItemAction<Program>() {
            @Override
            public void onItemFocus(View item) {

            }

            @Override
            public void onItemClick(Program item,int index) {
                JumpUtil.activityJump(LauncherApplication.AppContext,item);
            }

            @Override
            public void onItemChange(int before, int current) {

            }
        });
        recyclerView.setAdapter(adapter);

        if (mModuleInfoResult != null) UpdateUI();
    }

    @Override
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {
        mModuleInfoResult = infoResult;
        if (UiReady) {
            UpdateUI();
        }
    }

    private void UpdateUI() {
        List<Program> infos = mModuleInfoResult.getData().get(0).getPrograms();
        ((BallRoundAdapter) recyclerView.getAdapter()).refresh(infos).notifyDataSetChanged();
    }

    private static class BallRoundAdapter extends RecyclerView.Adapter<BallRoundViewHolder> {

        private List<Program> mValues;
        private OnItemAction<Program> onItemFocus;
        private String currentUUID;

        void setOnFocus(OnItemAction<Program> onFocus) {
            onItemFocus = onFocus;
        }

        BallRoundAdapter refresh(List<Program> value) {
            mValues = value;
            return this;
        }

        private Program getItem(int pos) {
            if (mValues != null && pos >= 0 && mValues.size() > pos) {
                return mValues.get(pos);
            }
            return null;
        }

        @Override
        public BallRoundViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(LauncherApplication.AppContext).inflate(R.layout
                    .ball_round_item_layout, parent, false);
            return new BallRoundViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final BallRoundViewHolder holder, int position) {
            Program programInfo = getItem(position);
            holder.poster.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        onItemFocus.onItemFocus(holder.itemView);
                    }
                }
            });
            holder.poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Program info = getItem(holder.getAdapterPosition());
                    if(info != null) {
                        currentUUID = info.getContentId();
                        onItemFocus.onItemClick(info,holder.getAdapterPosition());
                    }
                }
            });
            if (programInfo != null) {
                holder.setData(programInfo,holder.itemView.getContext());
                if(TextUtils.isEmpty(currentUUID) && position == 0){
                    holder.poster.requestFocus();
                }
            }
        }

        @Override
        public int getItemCount() {
            return mValues != null ? mValues.size() : 0;
        }
    }


    private static class BallRoundViewHolder extends RecyclerView.ViewHolder {

        private ImageView poster;

        BallRoundViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.round_ball_poster);
        }

        public void setData(Program programInfo, Context mcontext) {
            int radius=mcontext.getResources().getDimensionPixelOffset(R.dimen.width_4px);
            Picasso.get()
                    .load(programInfo.getImg())
                    .transform(new PosterCircleTransform(mcontext, radius))
                    .into(poster);
        }
    }
}
