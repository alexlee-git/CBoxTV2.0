package tv.newtv.cboxtv.cms.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.LogUploadUtils;
import com.newtv.libs.util.ScaleUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.views.detail.EpisodeHorizontalListView;
import tv.newtv.cboxtv.views.detail.IEpisode;
import tv.newtv.cboxtv.views.detail.PersonDetailHeadView;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.SuggestView;
import tv.newtv.cboxtv.views.detail.onEpisodeItemClick;

/**
 * Created by linzy on 2018/4/2.
 *
 */

/**
 * 人物详情页(新开发)
 */
public class PersonsDetailsActivityNew extends BaseActivity implements View.OnFocusChangeListener {

    @BindView(R.id.person_head)
    PersonDetailHeadView personDetailHeadView;

    @BindView(R.id.hostProgramView)
    EpisodeHorizontalListView hostProgramView;

    @BindView(R.id.taProgramView)
    EpisodeHorizontalListView taProgramView;

    @BindView(R.id.taRelationPerson)
    SuggestView taRelationPerson;

    @BindView(R.id.id_scroll_view)
    SmoothScrollView scrollView;

    private String contentUUID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details_new);
        ButterKnife.bind(this);

        init();
        requestData();
    }

    private void requestData() {
        hostProgramView.setHorizontalItemLayout(R.layout.program_horizontal_normal_land_layout);
        hostProgramView.setContentUUID(EpisodeHorizontalListView.TYPE_PERSON_HOST_LV,contentUUID,hostProgramView);// 获取主持列表

        taProgramView.setHorizontalItemLayout(R.layout.program_horizontal_normal_land_layout);
        taProgramView.setContentUUID(EpisodeHorizontalListView.TYPE_PERSON_RELATION_LV,contentUUID,taProgramView);// 获取相关节目列表

        Content content = new Content();
        content.setContentID(contentUUID);
        taRelationPerson.setContentUUID(SuggestView.TYPE_PERSON_FIGURES,content,taRelationPerson); //获取TA相关的名人数据
    }

    private void init() {
        contentUUID = getIntent().getStringExtra("content_uuid");

        if (TextUtils.isEmpty(contentUUID)) {
            Toast.makeText(this, "人物信息有误", Toast.LENGTH_SHORT).show();
            return;
        } else {
            personDetailHeadView.setContentUUID(contentUUID);
            ADConfig.getInstance().setSeriesID(contentUUID);
        }

        hostProgramView.setOnItemClick(new onEpisodeItemClick() {
            @Override
            public void onItemClick(int position, SubContent data) {
                JumpUtil.detailsJumpActivity(PersonsDetailsActivityNew.this, data.getContentType(), data.getContentID());
            }
        });

        taProgramView.setOnItemClick(new onEpisodeItemClick() {
            @Override
            public void onItemClick(int position, SubContent data) {
                JumpUtil.detailsJumpActivity(getApplicationContext(), data.getContentType(), data.getContentID());
            }
        });

        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "2," + contentUUID);
    }

    @Override
    protected void onDestroy() {
        ViewGroup viewGroup = findViewById(R.id.id_scroll_view);
        if (viewGroup != null) {
            int size = viewGroup.getChildCount();
            for (int index = 0; index < size; index++) {
                View view = viewGroup.getChildAt(index);
                if (view instanceof IEpisode) {
                    ((IEpisode) view).destroy();
                }
            }
            if (viewGroup instanceof SmoothScrollView) {
                ((SmoothScrollView) viewGroup).destroy();
            }
        }

        personDetailHeadView = null;
        hostProgramView = null;
        taProgramView = null;
        taRelationPerson = null;

        super.onDestroy();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.person_detail_ad_fl:
                if (hasFocus) {
                    ScaleUtils.getInstance().onItemGetFocus(v);
                } else {
                    ScaleUtils.getInstance().onItemLoseFocus(v);
                }
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (interruptKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }

        //TODO 防止视频列表项快速点击时候，焦点跳至播放器，进入大屏时候，播放器顶部出现大片空白
        if (scrollView != null && scrollView.isComputeScroll() && personDetailHeadView != null &&
                personDetailHeadView.hasFocus()) {
            if (event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                return true;
            }
        }

        if (BuildConfig.FLAVOR.equals(DeviceUtil.XUN_MA) && event.getAction() == KeyEvent
                .ACTION_UP) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ESCAPE:
                    finish();
                    return super.dispatchKeyEvent(event);
            }
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                return super.dispatchKeyEvent(event);
            }
            ViewGroup viewGroup = findViewById(R.id.id_scroll_view);
            int size = viewGroup.getChildCount();
            for (int index = 0; index < size; index++) {
                View view = viewGroup.getChildAt(index);
                if (view != null) {
                    if (!view.hasFocus()) {
                        continue;
                    }
                    if (view instanceof IEpisode && ((IEpisode) view).interuptKeyEvent
                            (event)) {
                        return true;
                    } else {
                        View toView = null;
                        int pos = index;
                        int dir = 0;
                        boolean condition = false;
                        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                            dir = -1;
                            condition = true;
                        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                            dir = 1;
                            condition = true;
                        }
                        while (condition) {
                            pos += dir;
                            if (pos < 0 || pos > viewGroup.getChildCount()) {
                                condition = false;
                            }
                            toView = viewGroup.getChildAt(pos);
                            if (toView != null) {
                                if (toView instanceof IEpisode && ((IEpisode) toView)
                                        .interuptKeyEvent
                                                (event)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
