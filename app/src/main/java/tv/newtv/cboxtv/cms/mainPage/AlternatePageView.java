package tv.newtv.cboxtv.cms.mainPage;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.bean.Video;
import com.newtv.cms.contract.ContentContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.GlideUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerView;
import tv.newtv.cboxtv.player.view.NewTVLauncherPlayerViewManager;
import tv.newtv.cboxtv.views.custom.LivePlayView;
import tv.newtv.cboxtv.views.widget.NewTvRecycleAdapter;
import tv.newtv.cboxtv.views.widget.VerticalRecycleView;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage
 * 创建事件:         11:32
 * 创建人:           weihaichao
 * 创建日期:          2018/11/20
 */
public class AlternatePageView extends FrameLayout implements IProgramChange,
        NewTVLauncherPlayerView.ChangeAlternateListener, ContentContract.View {

    private LivePlayView mBlockPosterView;
    private VerticalRecycleView mRecycleView;
    private int curPlayIndex = 0;
    private Page mPage;
    private View firstFocusView;
    private ImageView posterView;
    private String mPageUUID;
    private Program mProgram;

    private ContentContract.Presenter mContentPresenter;

    public AlternatePageView(Context context) {
        this(context, null);
    }

    public AlternatePageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlternatePageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    public void setPageUUID(String uuid) {
        mPageUUID = uuid;
        mBlockPosterView.setPageUUID(mPageUUID);
    }

    public void setProgram(Page page) {
        mPage = page;
        setUp();
    }

    public View getFirstFocusView() {
        return firstFocusView;
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(context).inflate(R.layout.content_alternate_view_layout, this, true);
        mBlockPosterView = findViewById(R.id.block_poster);
        mBlockPosterView.setAlternateChange(this);
        mRecycleView = findViewById(R.id.alternate_list);
        firstFocusView = findViewById(R.id.focus_layout);
        posterView = findViewWithTag("poster_view");
        findViewById(R.id.focus_layout).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBlockPosterView.dispatchClick();
            }
        });

        setUp();
    }

    private void setUp() {
        if (mPage == null || mBlockPosterView == null || mRecycleView == null) {
            return;
        }
        if (mPage.getPrograms() != null && mPage.getPrograms().size() > 0) {
            setRecycleView();
            Program program = mPage.getPrograms().get(curPlayIndex);
            if (posterView != null) {
                GlideUtil.loadImage(getContext(), posterView, program.getImg(), R.drawable
                                .focus_528_296,
                        R.drawable.focus_528_296, true);
            }
            play(program);
        }
    }

    private void play(Program program) {

        NewTVLauncherPlayerViewManager.getInstance().stop();

        if(mContentPresenter != null){
            mContentPresenter.stop();
        }

        mProgram = program;
        if (Constant.CONTENTTYPE_LV.equals(program.getL_contentType())) {
            if (mContentPresenter == null) {
                mContentPresenter = new ContentContract.ContentPresenter(getContext(), this);
            }
            if(TextUtils.isEmpty(program.getL_id())){
                mBlockPosterView.onError(getContext(),"ID为空");
                return;
            }
            mContentPresenter.getContent(program.getL_id(), false);
        } else {
            mBlockPosterView.setProgramInfo(program, false, true);
        }
    }

    private void setRecycleView() {
        if (mRecycleView == null) return;
        AlternateAdapter adapter = (AlternateAdapter) mRecycleView.getAdapter();
        if (adapter == null) {
            adapter = new AlternateAdapter(this);
            mRecycleView.setAdapter(adapter);
        }
        adapter.setData(subList(mPage.getPrograms(), 1, 5));
    }

    @SuppressWarnings("SameParameterValue")
    private List<Program> subList(List<Program> value, int start, int length) {
        if (start < 0 || length < 0) return null;
        int toIndex = length + start;
        if (toIndex < 0 || toIndex < start) return null;
        if (value == null) return null;
        if (value.size() >= toIndex) return value.subList(start, toIndex);
        return value.subList(start, value.size() - start);
    }

    @Override
    public void onChange(Program data, int position) {
        curPlayIndex = position + 1;
        play(data);
    }

    @Override
    public void changeAlternate(String contentId, String title, String channel) {
        if (mRecycleView != null && mRecycleView.getAdapter() != null
                && mRecycleView.getAdapter() instanceof AlternateAdapter) {
            ((AlternateAdapter) mRecycleView.getAdapter()).notifyChange(contentId);
        }
    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {
        if(mProgram != null && TextUtils.equals(mProgram.getL_id(),uuid)){
            if(content != null) {
                Video video = new Video("LIVE",
                        content.getContentID(),
                        content.getContentUUID(),
                        content.getPlayUrl(),
                        null);
                mProgram.setVideo(video);
                mBlockPosterView.setProgramInfo(mProgram, false, true);
            }else{
                mBlockPosterView.onError(getContext(),"Error");
            }
        }
    }

    @Override
    public void onSubContentResult(@NotNull String uuid, @Nullable ArrayList<SubContent> result) {

    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }

    private static class AlternateAdapter extends NewTvRecycleAdapter<Program,
            AlternateViewHolder> {

        private IProgramChange mListener;
        private List<Program> mPrograms;

        AlternateAdapter(IProgramChange listener) {
            mListener = listener;
        }

        public void setData(List<Program> programs) {
            mPrograms = programs;
            notifyDataSetChanged();
        }

        void notifyChange(String contentId) {
            if (mPrograms != null && mPrograms.size() > 0) {
                for (Program program : mPrograms) {
                    if (TextUtils.equals(program.getL_id(), contentId)) {
                        int index = mPrograms.indexOf(program);
                        setSelectedIndex(index);
                    }
                }
            }
        }

        @Override
        public List<Program> getDatas() {
            return mPrograms;
        }

        @Override
        public AlternateViewHolder createHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.alternate_index_item_layout, parent, false);
            return new AlternateViewHolder(itemView);
        }

        @Override
        public void bind(Program data, AlternateViewHolder holder, boolean selected) {
            holder.mAlternateId.setText(data.getAlternateNumber());
            holder.mAlternateTitle.setText(data.getTitle());
            holder.mAlternateSubTitle.setText(data.getSubTitle());
            holder.itemView.setActivated(selected);
        }

        @Override
        public boolean onItemClick(Program data, int position) {
            if (mListener != null) {
                mListener.onChange(data, position);
            }
            return false;
        }

        @Override
        public void onFocusChange(View view, int position, boolean hasFocus) {

        }
    }

    private static class AlternateViewHolder extends NewTvRecycleAdapter.NewTvViewHolder
            implements OnFocusChangeListener, OnClickListener {

        private TextView mAlternateId, mAlternateTitle, mAlternateSubTitle;

        AlternateViewHolder(View itemView) {
            super(itemView);

            mAlternateId = itemView.findViewById(R.id.alternate_id);
            mAlternateTitle = itemView.findViewById(R.id.alternate_title);
            mAlternateSubTitle = itemView.findViewById(R.id.alternate_subtitle);

            itemView.setOnFocusChangeListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            performClick();
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            performFocus(b);
            mAlternateSubTitle.setSelected(b);
        }
    }
}
