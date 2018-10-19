package tv.newtv.cboxtv.views.detailpage;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.disposables.Disposable;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.details.model.ProgramSeriesInfo;
import tv.newtv.cboxtv.cms.util.LogUtils;
import tv.newtv.cboxtv.views.CurrentPlayImageView;
import tv.newtv.cboxtv.views.HorizontalRecycleView;
import tv.newtv.cboxtv.views.NewTvRecycleAdapter;
import tv.newtv.cboxtv.views.RecycleFocusItemDecoration;
import tv.newtv.cboxtv.views.RecycleImageView;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.views.detailpage
 * 创建事件:         13:54
 * 创建人:           weihaichao
 * 创建日期:          2018/7/27
 *
 * 合集节目ListView
 */
public class EpisodeHorizontalListView extends RelativeLayout implements IEpisode {

    private String mContentUuid;
    private Disposable mDisposable;
    private HorizontalRecycleView mRecycleView;
    private ProgramSeriesInfo mProgramSeriesInfo;//数据
    private TextView mTitleText;
    private String title = "播放列表";
    private NewTvRecycleAdapter<ProgramSeriesInfo.ProgramsInfo, ViewHolder> mAdapter;
    private onEpisodeItemClick onItemClickListener;

    private Context context;

    @Override
    public void destroy() {

        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        if(mProgramSeriesInfo!=null)
        {
            mProgramSeriesInfo.getData().clear();
            mProgramSeriesInfo=null;
        }

        onItemClickListener = null;
        mAdapter = null;

    }

    public void clearData()
    {
        if(mProgramSeriesInfo!=null)
        {
            mProgramSeriesInfo.getData().clear();
            mProgramSeriesInfo=null;
        }
        mAdapter=null;
    }

    public EpisodeHorizontalListView(Context context) {
        this(context, null);
    }

    public EpisodeHorizontalListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context=context;
    }

    public EpisodeHorizontalListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
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

    public void initialize(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.episode_horizontal_layout, this, true);
        mRecycleView = findViewById(R.id.list_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager
                .HORIZONTAL, false));
        mRecycleView.addItemDecoration(new RecycleFocusItemDecoration(getResources()
                .getDimensionPixelOffset(R.dimen.width_50px)));
        mRecycleView.setDirectors(findViewById(R.id.dir_left), findViewById(R.id.dir_right));

        mTitleText = findViewById(R.id.id_title);
        if (mTitleText != null) {
            mTitleText.setText(title);
        }

        initAdapter();
    }

    public void initAdapter()
    {
        if(mAdapter==null) {
            mAdapter = new NewTvRecycleAdapter<ProgramSeriesInfo.ProgramsInfo, ViewHolder>() {

                private FocusRelativeLayout oldContainer;
                private int oldPosition;


                @Override
                public List<ProgramSeriesInfo.ProgramsInfo> getDatas() {
                    return mProgramSeriesInfo != null ? mProgramSeriesInfo.getData() : null;
                }

                @Override
                public ViewHolder createHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .program_horizontal_layout, parent, false);
                    LogUtils.i("Collection", "ViewHolder创建");
                    return new ViewHolder(view);
                }


                @Override
                public void bind(final ProgramSeriesInfo.ProgramsInfo data, final ViewHolder holder, boolean
                        select) {
                    LogUtils.i("Collection", "bind--显示-->" + mAdapter.getSelectedIndex());

                        holder.posterView.placeHolder(R.drawable.focus_384_216)
                                .errorHolder(R.drawable.focus_384_216).hasCorner
                                (true).load(data.gethImage());


                    holder.titleText.setText(data.getTitle());
                    holder.posterView.setIsPlaying(select);


                }

                @Override
                public void onItemClick(ProgramSeriesInfo.ProgramsInfo data, int position) {

                    LogUtils.i("CollectiononItemClick", "onItemClick--position-->" + position);

                    mAdapter.setSelectedIndex(position);

                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }

                @Override
                public void onFocusChange(View view, int position, boolean hasFocus) {
                    LogUtils.i("CollectiononItemClick", "onFocusChange--position-->" + position + "\thasFocus-->" + hasFocus);

                }
            };
        }
        mRecycleView.setAdapter(mAdapter);
    }

    @Override
    public String getContentUUID() {
        return mContentUuid;
    }

    public void setCurrentPlay(int index) {
        LogUtils.i("CollectionAdapter","index-->"+index);

        if (!hasFocus()) {
            int first = mRecycleView.getFirstVisiblePosition();
            int last = mRecycleView.getLastVisiblePosition();

            LogUtils.i("CollectionAdapter","first-->"+first+"\tlast-->"+last);

            if (index < first || index > last) {
                mRecycleView.smoothScrollToPosition(index);
            }
        }else {
            LogUtils.i("CollectionAdapter","已获得焦点");
            mRecycleView.smoothScrollToPosition(index);

        }

        if (mAdapter != null) {
            LogUtils.i("CollectionAdapter","mAdapter不为空");
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

    //按键
    @Override
    public boolean interuptKeyEvent(KeyEvent event) {
        if (!hasFocus() && mRecycleView.getChildCount() > 0) {
            mRecycleView.requestDefaultFocus(mAdapter.getSelectedIndex());
            LogUtils.i("Collection","interuptKeyEvent-->"+mAdapter.getSelectedIndex());
            return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        LogUtils.i("Collection","onLayout-->changed-->"+changed+"\tl-->"+l+"\tt-->"+t+"\tr-->"+r+"\tb-->"+b);
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
            titleText.setSelected(hasFocus);

            performFocus(hasFocus);
            if (hasFocus) {
                itemView.bringToFront();
            }
        }
    }
}
