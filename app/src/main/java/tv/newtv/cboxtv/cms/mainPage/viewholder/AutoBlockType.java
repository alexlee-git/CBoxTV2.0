package tv.newtv.cboxtv.cms.mainPage.viewholder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.DefaultConstract;
import com.newtv.libs.util.GsonUtil;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage
 * 创建事件:         16:40
 * 创建人:           weihaichao
 * 创建日期:          2018/10/18
 */
public class AutoBlockType extends LinearLayout implements DefaultConstract.View {

    private DefaultConstract.Presenter mPresenter;
    private BlockBuilder blockBuilder;

    public void destroy(){
        if(blockBuilder != null){
            blockBuilder.destroy();
            blockBuilder = null;
        }
        if(mPresenter != null){
            mPresenter.destroy();
            mPresenter = null;
        }
    }

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

    private void initialize(AttributeSet attrs, int defStyleAttr) {
        setOrientation(VERTICAL);
        setBackgroundColor(Color.RED);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams
                .MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        blockBuilder = new BlockBuilder(getContext());
    }

    public void build(Page page) {
        if (mPresenter == null) {
            mPresenter = new DefaultConstract.DefaultPresenter(getContext(), this);
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
        if(extend != null && extend.containsKey("index")) {
            index = Integer.parseInt((String) extend.get("index"));
        }
        Page page = GsonUtil.fromjson(result, Page.class);
        if (page != null) {
            int viewType = blockBuilder.getItemViewType(1, page);
            UniversalViewHolder holder = blockBuilder.onCreateViewHolder(this, viewType);
            blockBuilder.build(page, holder.itemView, 1);
            if(index != -1) {
                addView(holder.itemView, index);
            }else{
                addView(holder.itemView);
            }
        }
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }
}
