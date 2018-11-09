package tv.newtv.cboxtv.cms.special.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.bean.SubContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.views.widget.NewTvRecycleAdapter;
import tv.newtv.cboxtv.views.widget.VerticalRecycleView;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.special.fragment
 * 创建事件:         14:57
 * 创建人:           weihaichao
 * 创建日期:          2018/11/8
 */
@SuppressWarnings("unchecked")
public class DoubleListFragment extends BaseSpecialContentFragment {

    private VerticalRecycleView mTopListView;
    private VerticalRecycleView mChildListView;
    private ArrayList<Program> topList;
    private VideoPlayerView mVideoPlayer;
    private HashMap<String, Content> cacheSubContents;

    private Callback topCallback = new Callback<Program>() {
        @Override
        public void bind(Program data, Holder holder) {
            holder.titleView.setText(data.getTitle());
        }

        @Override
        public void onItemClick(Program data, int position) {
            String subId = data.getL_id();
            if (cacheSubContents != null && cacheSubContents.containsKey(subId)) {
                onSubContentResult(subId, (ArrayList<SubContent>) cacheSubContents.get(subId)
                        .getData());
                return;
            }
            getContent(data.getL_id(), data.getL_contentType());
        }

        @Override
        public void onFocusChange(View view, int position, boolean hasFocus) {

        }
    };

    private Callback childCallback = new Callback<SubContent>() {
        @Override
        public void onItemClick(SubContent data, int position) {

        }

        @Override
        public void bind(SubContent data, Holder holder) {
            holder.titleView.setText(data.getTitle());
        }

        @Override
        public void onFocusChange(View view, int position, boolean hasFocus) {

        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_double_list;
    }

    @Override
    protected void onItemContentResult(String uuid, Content content) {
        if (cacheSubContents == null) {
            cacheSubContents = new HashMap<>();
        }
        cacheSubContents.put(uuid, content);
        updateChildList(content);
    }

    @Override
    protected void setUpUI(View view) {
        mTopListView = view.findViewById(R.id.top_list);
        mChildListView = view.findViewById(R.id.child_list);
        mVideoPlayer = view.findViewById(R.id.videoPlayer);

        updateUI();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void setModuleInfo(ModelResult<ArrayList<Page>> infoResult) {
        if (infoResult == null
                || infoResult.getData() == null
                || infoResult.getData().size() < 1
                || infoResult.getData().get(0).getPrograms() == null)
            return;
        topList = new ArrayList<>(infoResult.getData().get(0).getPrograms());
        updateUI();
    }

    private void updateChildList(Content programs) {
        if (mChildListView == null) return;
        if (mChildListView.getAdapter() == null) {
            mChildListView.setAdapter(new TopAdapter<>(null, childCallback));
        } else {
            if (mChildListView.getAdapter() instanceof TopAdapter) {
                ((TopAdapter<SubContent>) mChildListView.getAdapter()).update(programs.getData());
            }
        }
    }

    private void updateUI() {
        if (contentView != null && topList != null) {
            if (mTopListView == null) return;
            if (mTopListView.getAdapter() == null) {
                mTopListView.setAdapter(new TopAdapter<>(topList, topCallback));
            }
            mTopListView.getAdapter().notifyDataSetChanged();
        }
    }

    interface Callback<T> {
        void bind(T data, Holder holder);

        void onItemClick(T data, int position);

        void onFocusChange(View view, int position, boolean hasFocus);
    }

    private static class TopAdapter<T> extends NewTvRecycleAdapter<T, Holder> {
        private List<T> mDatas;
        private int currentIndex = -1;
        private Callback<T> mCallback;

        TopAdapter(List<T> programs, Callback<T> callback) {
            mDatas = programs;
            mCallback = callback;
        }

        T getSelectedItem() {
            if (currentIndex < 0 || currentIndex > mDatas.size() - 1 || mDatas == null) {
                return null;
            }
            return mDatas.get(currentIndex);
        }

        void update(List<T> value) {
            mDatas = value;
            notifyDataSetChanged();
        }

        @Override
        public List<T> getDatas() {
            return mDatas;
        }

        @Override
        public Holder createHolder(ViewGroup parent, int viewType) {
            Holder holder = new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_center_special, parent, false));
            holder.checkSelect(currentIndex);
            return holder;
        }

        @Override
        public void bind(T data, Holder holder, boolean selected) {
            mCallback.bind(data, holder);
        }

        @Override
        public void onItemClick(T data, int position) {
            if (currentIndex == position) {
                return;
            }
            currentIndex = position;
            mCallback.onItemClick(data, position);
        }

        @Override
        public void onFocusChange(View view, int position, boolean hasFocus) {
            mCallback.onFocusChange(view, position, hasFocus);
        }
    }

    private static class Holder extends NewTvRecycleAdapter.NewTvViewHolder implements View
            .OnFocusChangeListener, View.OnClickListener {

        TextView titleView;
        View playView;

        Holder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.topic_center_item);
            playView = itemView.findViewById(R.id.topic_center_item_player);
            itemView.setOnFocusChangeListener(this);
            itemView.setOnClickListener(this);
        }

        void checkSelect(int pos) {
            int adapterIndex = getAdapterPosition();
            if (adapterIndex == 0 && pos == -1) {
                itemView.performClick();
            }
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            performFocus(b);
            titleView.setSelected(b);
        }

        @Override
        public void onClick(View view) {
            playView.setVisibility(View.VISIBLE);
            performClick();
        }
    }
}
