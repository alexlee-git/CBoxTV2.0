package tv.newtv.cboxtv.cms.mainPage.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newtv.cms.bean.AutoBlock;
import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.bean.Row;
import com.newtv.cms.contract.DefaultConstract;
import com.newtv.libs.util.GsonUtil;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import tv.newtv.cboxtv.MultipleClickListener;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.superscript.SuperScriptManager;
import tv.newtv.cboxtv.cms.util.ModuleLayoutManager;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage
 * 创建事件:         16:40
 * 创建人:           weihaichao
 * 创建日期:          2018/10/18
 */
public class AutoBlockType extends LinearLayout implements DefaultConstract.View {

    private static final String TAG = "AutoBlockType";
    private DefaultConstract.Presenter mPresenter;
    private BlockBuilder blockBuilder;
    private Page mPage;
    private UniversalViewHolder mHolder;


    public AutoBlockType(@NonNull Context context) {
        this(context, null);
    }

    public AutoBlockType(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoBlockType(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs, defStyleAttr);
    }

    public void destroy() {
        if (blockBuilder != null) {
            blockBuilder.destroy();
            blockBuilder = null;
        }
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
        if (mHolder != null) {
            mHolder.destroy();
            mHolder = null;
        }
        removeAllViews();
    }

    private void initialize(AttributeSet attrs, int defStyleAttr) {
        setOrientation(VERTICAL);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView
                .LayoutParams
                .MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
    }

    public void build(Page page) {
        mPage = page;
        if (mPresenter == null) {
            mPresenter = new DefaultConstract.DefaultPresenter(getContext(), this);
        }
        if (blockBuilder == null) {
            blockBuilder = new BlockBuilder(getContext());
        }
        List<Program> programs = page.getPrograms();
        int index = 0;
        for (Program program : programs) {
            HashMap<String, String> params = new HashMap<>();
            params.put("url", program.getDataUrl());
            params.put("index", Integer.toString(index));
            mPresenter.request(program.getDataUrl(), params);
            index++;
        }
    }

    @Override
    public void onResult(@NotNull String result, @Nullable HashMap<?, ?> extend) {
        int index = -1;
        if (extend != null && extend.containsKey("index")) {
            index = Integer.parseInt((String) extend.get("index"));
        }
        AutoBlock block = GsonUtil.fromjson(result, AutoBlock.class);
        if (block != null) {
            int viewType = blockBuilder.parseItemViewType(mPage);
            mHolder = blockBuilder.onCreateViewHolder(this, viewType);
            parseData(mHolder, block);
            if (index != -1) {
                addView(mHolder.itemView, index);
            } else {
                addView(mHolder.itemView);
            }
        }
    }

    private void parseData(UniversalViewHolder holder, AutoBlock blockData) {
        final String layoutCode = mPage.getLayoutCode(); // 形如"layout_002"
        final String layoutId = layoutCode.substring(layoutCode.indexOf("_") + 1); //

        // 处理组件标题与icon
        if (TextUtils.equals("1", mPage.getHaveBlockTitle())) {
            String moduleTitleText = mPage.getBlockTitle();
            if (!TextUtils.isEmpty(moduleTitleText)) {
                // 填充组件标题
                String titleId = "module_" + layoutId + "_title";
                TextView moduleTitleTextView = ((TextView) holder.itemView.findViewWithTag
                        (titleId));
                if (moduleTitleTextView != null) {
                    TextPaint paint = moduleTitleTextView.getPaint();
                    paint.setFakeBoldText(true);//字体加粗

                    moduleTitleTextView.setText(mPage.getBlockTitle());
                    moduleTitleTextView.setVisibility(View.VISIBLE);
                }

                String titleIconId = "module_" + layoutId + "_title_icon";
                ImageView titleIcon = (ImageView) holder.itemView.findViewWithTag(titleIconId);
                String iconUrl = mPage.getBlockImg();
                if (titleIcon != null) {
                    titleIcon.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(iconUrl) && !iconUrl.equals(titleIcon.getTag()
                    )) {
                        titleIcon.setTag(iconUrl);
                        Picasso.get().load(iconUrl).into(titleIcon);
                    }
                }
            }
        }

        int posSize = ModuleLayoutManager.getInstance().getSubWidgetSizeById(layoutCode);
        Log.i(TAG, "layoutCode=" + layoutCode);
        List<String> layoutList = ModuleLayoutManager.getInstance().getWidgetLayoutList
                (layoutCode);
        Log.i(TAG, "layoutList=" + layoutList);

        if (blockData.getRows() != null && blockData.getRows().size() > 0) {
            for (int i = 0; i < posSize; i++) {
                if (i >= blockData.getRows().size()) {
                    break;
                }
                final Row info = blockData.getRows().get(i);

                // viewId是形如 "1", "2"这样的字符串
                String viewId = Integer.toString(i + 1);

                // 拿1号组件的1号推荐位为例, 其海报控件的id为 : cell_001_1_poster
                String posterWidgetId = blockBuilder.generateViewId(layoutId, viewId, "cell",
                        "poster", "_");
                // 拿8号组件的1号推荐位为例, 其海报控件的id为 : cell_008_1_focus
                String focusWidgetId = blockBuilder.generateViewId(layoutId, viewId, "cell",
                        "focus",

                        "_");
                // 拿1号组件的1号推荐位为例, 其推荐位标题控件的id为 : cell_001_1_title
                String titleWidgetId = blockBuilder.generateViewId(layoutId, viewId, "cell",
                        "title",
                        "_");

                // 拿1号组件的1号推荐位为例, 其推荐位的根控件id为 : cell_001_1
                final String frameLayoutId = posterWidgetId.substring(0, posterWidgetId.indexOf
                        ("poster") - 1);

                layoutList.remove(frameLayoutId);

                // 给推荐位设置监听器
                final FrameLayout frameLayout = (FrameLayout) holder.itemView.findViewWithTag
                        (frameLayoutId);
                if (frameLayout != null) {
//                    frameLayout.setFocusable(true);
                    layoutList.remove(frameLayoutId);
                    frameLayout.setVisibility(VISIBLE);

                    //屏幕适配
                    if (!"005".equals(layoutId) && !"008".equals(layoutId)) {
                        ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        frameLayout.setLayoutParams(params);
                    }

                    // 按需添加角标控件
//                blockBuilder.processSuperscript(layoutCode, info, frameLayout);

                    // 按需添加标题控件
                    blockBuilder.processTitle(layoutCode, info.getTitle(), info.getSubTitle(),
                            frameLayout);

                    // onFocusChangeListener
                    frameLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (hasFocus) {
                                blockBuilder.onItemGetFocus(layoutId, view);

                                TextView title = (TextView) frameLayout.getTag(R.id.tag_title);
                                if (title != null) {
                                    title.setVisibility(View.VISIBLE);
                                    title.setSelected(true);
                                }
                            } else {
                                blockBuilder.onItemLoseFocus(layoutId, view);
                                TextView title = (TextView) frameLayout.getTag(R.id.tag_title);
                                if (title != null) {
                                    title.setSelected(false);
                                }
                            }
                        }
                    });

                    // onClickListener
                    frameLayout.setOnClickListener(new MultipleClickListener() {
                        @Override
                        protected void onMultipleClick(View view) {
                            blockBuilder.processOpenCell(view, info, mPage.getBlockId(),
                                    layoutCode);

                        }
                    });

                    // 如果是第5和第8号组件,则设置推荐位标题.因为按照约定只有第5和第8套组件留有推荐位标题控件
                    if (TextUtils.equals("005", layoutId) || TextUtils.equals("008",
                            layoutId)) {
                        TextView textView = (TextView) holder.itemView.findViewWithTag
                                (titleWidgetId);
                        View focusView = (View) holder.itemView.findViewWithTag(focusWidgetId);

                        if (textView != null && !TextUtils.isEmpty(info.getTitle())) {
                            textView.setText(info.getTitle());
                        }
                        frameLayout.setTag(R.id.tag_textview, textView);
                        frameLayout.setTag(R.id.tag_imageview, focusView);
                    }
                }

                // 是否圆角
                boolean hasCorner = blockBuilder.hasCorner4Cup(layoutId, viewId);
                // 加载海报图
                final View posterView = holder.itemView.findViewWithTag(posterWidgetId);
                RecycleImageView recycleImageView = null;
                if (posterView instanceof RecycleImageView) {
                    recycleImageView = (RecycleImageView) posterView;
                }

                if (recycleImageView != null) {
                    blockBuilder.showPosterByCMS(recycleImageView, info.getVImage(),
                            hasCorner);
                }

                SuperScriptManager.getInstance().processSuperscript(
                        getContext(),
                        "layout_008",
                        ((ViewGroup) holder.itemView).indexOfChild(posterView),
                        info,
                        (ViewGroup) posterView.getParent()
                );

            }
        }

        if (layoutList.size() > 0) {
            for (String layout : layoutList) {
                View target = holder.itemView.findViewWithTag(layout);
                if(target != null){
                    target.setVisibility(View.GONE);
                }
            }
        }
    }


    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @NotNull String code, @org.jetbrains
            .annotations.Nullable String desc) {

    }
}
