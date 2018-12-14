package tv.newtv.cboxtv.cms.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.libs.Constant;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.util.LogUploadUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.JumpUtil;
import tv.newtv.cboxtv.views.detail.DetailPageActivity;
import tv.newtv.cboxtv.views.detail.EpisodeAdView;
import tv.newtv.cboxtv.views.detail.EpisodeHorizontalListView;
import tv.newtv.cboxtv.views.detail.PersonDetailHeadView;
import tv.newtv.cboxtv.views.detail.RecycleItemDecoration;
import tv.newtv.cboxtv.views.detail.SmoothScrollView;
import tv.newtv.cboxtv.views.detail.SuggestView;
import tv.newtv.cboxtv.views.detail.onEpisodeItemClick;
import tv.newtv.cboxtv.views.widget.RecycleFocusItemDecoration;

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


    @BindView(R.id.person_detail_ad_fl)
    EpisodeAdView mAdView;
    private long lastTime = 0;

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
    protected boolean isFull(KeyEvent event) {
        return false;
    }

    @Override
    protected void buildView(@Nullable Bundle savedInstanceState, String contentUUID) {
        setContentView(R.layout.activity_person_details_new);
        ButterKnife.bind(this);

        init(contentUUID);
        requestData(contentUUID);

//        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "2," + contentUUID);
    }

    private void requestData(String contentUUID) {

        hostProgramView.setHorizontalItemLayout(R.layout.item_details_horizontal_episode,6,R
                .drawable.focus_240_360,EpisodeHorizontalListView.DIRECTION_VERTICAL,R.id
                .column_update_date_layout,new RecycleItemDecoration(getResources()
                .getDimensionPixelOffset(R.dimen.width_33px),getResources()
                .getDimensionPixelOffset(R.dimen.width_15px)),R.id.column_update_item,R.id.column_update_date_tv
                );
        hostProgramView.setContentUUID(EpisodeHorizontalListView.TYPE_PERSON_HOST_LV,
                contentUUID, hostProgramView);// 获取主持列表

        taProgramView.setHorizontalItemLayout(R.layout.program_horizontal_normal_land_layout,4,R
                .drawable.focus_384_216,EpisodeHorizontalListView.DIRECTION_HORIZONTAL);
        taProgramView.setContentUUID(EpisodeHorizontalListView.TYPE_PERSON_RELATION_LV,
                contentUUID, taProgramView);// 获取相关节目列表

        Content content = new Content();
        content.setContentID(contentUUID);
        taRelationPerson.setContentUUID(SuggestView.TYPE_PERSON_FIGURES, content,
                taRelationPerson); //获取TA相关的名人数据

        mAdView.requestAD();
    }

    private void init(String contentUUID) {

        if (TextUtils.isEmpty(contentUUID)) {
            Toast.makeText(this, "人物信息有误", Toast.LENGTH_SHORT).show();
            return;
        } else {
            LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "2," + contentUUID);
            personDetailHeadView.setContentUUID(contentUUID);
            ADConfig.getInstance().setSeriesID(contentUUID);
        }

        personDetailHeadView.setTopView();

        hostProgramView.setOnItemClick(new onEpisodeItemClick<SubContent>() {
            @Override
            public boolean onItemClick(int position, SubContent data) {
                JumpUtil.detailsJumpActivity(PersonsDetailsActivityNew.this, data.getContentType
                        (), data.getContentID());
                return true;
            }
        });

        taProgramView.setOnItemClick(new onEpisodeItemClick<SubContent>() {
            @Override
            public boolean onItemClick(int position, SubContent data) {
                if (!TextUtils.isEmpty(data.getContentType())) {
                    if (System.currentTimeMillis() - lastTime >= 2000) {//判断距离上次点击小于2秒
                        lastTime = System.currentTimeMillis();//记录这次点击时间
                        JumpUtil.detailsJumpActivity(getApplicationContext(), data.getContentType(), data
                                .getContentID());
                    }
                }
                return true;
            }
        });

//        LogUploadUtils.uploadLog(Constant.LOG_NODE_DETAIL, "2," + contentUUID);
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
