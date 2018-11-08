package tv.newtv.cboxtv.cms.details;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.views.detail.DetailPageActivity;
import tv.newtv.cboxtv.views.detail.EpisodeHorizontalListView;
import tv.newtv.cboxtv.views.detail.IEpisode;
import tv.newtv.cboxtv.views.detail.PersonDetailHeadView;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.SuggestView;
import tv.newtv.cboxtv.views.detail.onEpisodeItemClick;

/**
 * Created by linzy on 2018/4/2.
 * 人物详情页(新开发)
 */
public class PersonsDetailsActivityNew extends DetailPageActivity {

    @BindView(R.id.person_head)
    PersonDetailHeadView personDetailHeadView;

    @BindView(R.id.hostProgramView)
    EpisodeHorizontalListView hostProgramView;

    @BindView(R.id.taProgramView)
    EpisodeHorizontalListView taProgramView;

    @BindView(R.id.taRelationPerson)
    SuggestView taRelationPerson;

    @BindView(R.id.root_view)
    SmoothScrollView scrollView;

    @BindView(R.id.up_top)
    LinearLayout upTop;

    @Override
    protected boolean interruptDetailPageKeyEvent(KeyEvent event) {
        if (scrollView != null && scrollView.isComputeScroll() && personDetailHeadView != null &&
                personDetailHeadView.hasFocus()) {
            return event.getKeyCode() == KeyEvent
                    .KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
        }
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details_new);
        ButterKnife.bind(this);

        init();
        if (fromOuter) {
            new CountDownTimer(5 * 1000, 1000) {
                @Override
                public void onTick(long l) {
                    upTop.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    upTop.setVisibility(View.GONE);
                }
            }.start();
        }
        requestData();
    }

    private void requestData() {
        hostProgramView.setHorizontalItemLayout(R.layout.program_horizontal_normal_land_layout);
        hostProgramView.setContentUUID(EpisodeHorizontalListView.TYPE_PERSON_HOST_LV,
                getContentUUID(), hostProgramView);// 获取主持列表

        taProgramView.setHorizontalItemLayout(R.layout.program_horizontal_normal_land_layout);
        taProgramView.setContentUUID(EpisodeHorizontalListView.TYPE_PERSON_RELATION_LV,
                getContentUUID(), taProgramView);// 获取相关节目列表

        Content content = new Content();
        content.setContentID(getContentUUID());
        taRelationPerson.setContentUUID(SuggestView.TYPE_PERSON_FIGURES, content,
                taRelationPerson); //获取TA相关的名人数据
    }

    private void init() {

        if (TextUtils.isEmpty(getContentUUID())) {
            Toast.makeText(this, "人物信息有误", Toast.LENGTH_SHORT).show();
            return;
        } else {
            personDetailHeadView.setContentUUID(getContentUUID());
            ADConfig.getInstance().setSeriesID(getContentUUID());
        }

        hostProgramView.setOnItemClick(new onEpisodeItemClick() {
            @Override
            public void onItemClick(int position, SubContent data) {
                JumpUtil.detailsJumpActivity(PersonsDetailsActivityNew.this, data.getContentType
                        (), data.getContentID());
            }
        });

        taProgramView.setOnItemClick(new onEpisodeItemClick() {
            @Override
            public void onItemClick(int position, SubContent data) {
                JumpUtil.detailsJumpActivity(getApplicationContext(), data.getContentType(), data
                        .getContentID());
            }
        });

        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "2," + getContentUUID());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        personDetailHeadView = null;
        hostProgramView = null;
        taProgramView = null;
        taRelationPerson = null;
    }
}
