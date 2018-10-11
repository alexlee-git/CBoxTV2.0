package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import io.reactivex.disposables.Disposable;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.ProgramSeriesInfo;
import tv.newtv.cboxtv.player.ProgramsInfo;
import tv.newtv.cboxtv.views.custom.CurrentPlayImageView;
import tv.newtv.cboxtv.views.widget.HorizontalRecycleView;
import tv.newtv.cboxtv.views.widget.NewTvRecycleAdapter;
import tv.newtv.cboxtv.views.widget.RecycleFocusItemDecoration;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         13:54
 * 创建人:           weihaichao
 * 创建日期:          2018/7/27
 */
public class EpisodeHorizontalListView extends RelativeLayout implements IEpisode {

    private String mContentUuid;
    private Disposable mDisposable;
    private HorizontalRecycleView mRecycleView;
    private ProgramSeriesInfo mProgramSeriesInfo;
    private TextView mTitleText;
    private String title = "播放列表";
    private NewTvRecycleAdapter<ProgramsInfo, ViewHolder> mAdapter;
    private onEpisodeItemClick onItemClickListener;


    @Override
    public void destroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        onItemClickListener = null;
        mAdapter = null;
    }

    public EpisodeHorizontalListView(Context context) {
        this(context, null);
    }

    public EpisodeHorizontalListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EpisodeHorizontalListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    public void setOnItemClick(onEpisodeItemClick listener) {
        onItemClickListener = listener;
    }

    public void setTitle(String text) {
        title = text;
        if (mTitleText != null) {
            mTitleText.setText(title);
        }
    }


    private void initialize(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.episode_horizontal_layout, this, true);
        mRecycleView = findViewById(R.id.list_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager
                .HORIZONTAL, false));
        mRecycleView.addItemDecoration(new RecycleFocusItemDecoration(getResources()
                .getDimensionPixelOffset(R.dimen.width_40px)));
        mRecycleView.setDirectors(findViewById(R.id.dir_left), findViewById(R.id.dir_right));

        mTitleText = findViewById(R.id.id_title);
        if (mTitleText != null) {
            mTitleText.setText(title);
        }

        mAdapter = new NewTvRecycleAdapter<ProgramsInfo, ViewHolder>() {
            @Override
            public List<ProgramsInfo> getDatas() {
                return mProgramSeriesInfo != null ? mProgramSeriesInfo.getData() : null;
            }

            @Override
            public ViewHolder createHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .program_horizontal_layout, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void bind(ProgramsInfo data, ViewHolder holder, boolean
                    select) {
                holder.posterView.placeHolder(R.drawable.focus_384_216)
                        .errorHolder(R.drawable.focus_384_216).hasCorner
                        (true).load(data.gethImage());
                holder.titleText.setText(data.getTitle());
                holder.posterView.setIsPlaying(select);
            }

            @Override
            public void onItemClick(ProgramsInfo data, int position) {
                mAdapter.setSelectedIndex(position);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }

            @Override
            public void onFocusChange(View view, int position, boolean hasFocus) {

            }
        };
        mRecycleView.setAdapter(mAdapter);
    }

    @Override
    public String getContentUUID() {
        return mContentUuid;
    }

    public void setCurrentPlay(int index) {
        if (!hasFocus()) {
            int first = mRecycleView.getFirstVisiblePosition();
            int last = mRecycleView.getLastVisiblePosition();
            if (index < first || index > last) {
                mRecycleView.smoothScrollToPosition(index);
            }
        }

        if (mAdapter != null) {
            mAdapter.setSelectedIndex(index);
        }

    }

    public void setContentUUID(String uuid, ProgramSeriesInfo info) {
        mContentUuid = uuid;
        mProgramSeriesInfo = info;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean interuptKeyEvent(KeyEvent event) {
        if (!hasFocus() && mRecycleView.getChildCount() > 0) {
            mRecycleView.requestDefaultFocus(mAdapter.getSelectedIndex());
            return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    private static class ViewHolder extends NewTvRecycleAdapter.NewTvViewHolder implements
            OnFocusChangeListener {
        private CurrentPlayImageView posterView;
        private TextView titleText;
        private FocusRelativeLayout modleView;

        public ViewHolder(View itemView) {
            super(itemView);

            modleView = itemView.findViewById(R.id.id_module_view);
            posterView = itemView.findViewById(R.id.iv_player);
            titleText = itemView.findViewWithTag("tag_poster_title");

            modleView.setOnFocusChangeListener(this);
            modleView.setResizeView(itemView);
            modleView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    performClick();
                }
            });
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
//            titleText.setSelected(hasFocus);

            performFocus(hasFocus);
            if (hasFocus) {
                itemView.bringToFront();
            }
        }
    }
}
