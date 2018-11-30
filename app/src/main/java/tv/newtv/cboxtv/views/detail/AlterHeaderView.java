package tv.newtv.cboxtv.views.detail;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.ContentContract;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.player.videoview.VideoPlayerView;
import tv.newtv.cboxtv.views.custom.AlternateView;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detail
 * 创建事件:         14:21
 * 创建人:           weihaichao
 * 创建日期:          2018/11/13
 */
public class AlterHeaderView extends FrameLayout implements IEpisode, ContentContract.View, View
        .OnClickListener {

    private String mContentUUID;
    private ContentContract.Presenter mPresenter;

    private TextView alternateIdText;
    private TextView alternateFromText;
    private TextView alternateDescText;
    private VideoPlayerView alternateView;

    private View fullScreenBtn;
    private View collectBtn;
    private View payBtn;


    public AlterHeaderView(Context context) {
        this(context, null);
    }

    public AlterHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlterHeaderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs, defStyle);
    }

    public void stop() {
        if (alternateView != null) {
            alternateView.stop();
        }
    }

    public void onResume() {
        if (alternateView != null) {
//            alternateView.;
        }
    }

    public boolean isFullScreen() {
        if (alternateView != null) {
            alternateView.isFullScreen();
        }
        return false;
    }

    public void setCallback(AlternateView.AlternateCallback callback) {
//        alternateView.setCallback(callback);
    }

    public void prepareMediaPlayer() {
//        alternateView.prepareMediaPlayer();
    }

    public void play(String contentId, String contentUUID) {

//        if (alternateView != null) {
//            alternateView.play(contentId, contentUUID);
//        }
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(context).inflate(R.layout.alternate_head_layout, this, true);

        alternateIdText = findViewById(R.id.id_detail_title);
        alternateFromText = findViewById(R.id.id_detail_from);
        alternateDescText = findViewById(R.id.id_detail_desc);
        alternateView = findViewById(R.id.video_container);

        fullScreenBtn = findViewById(R.id.full_screen);
        collectBtn = findViewById(R.id.collect);
        payBtn = findViewById(R.id.vip_pay);

        fullScreenBtn.setOnClickListener(this);
        collectBtn.setOnClickListener(this);
        payBtn.setOnClickListener(this);


        mPresenter = new ContentContract.ContentPresenter(getContext(), this);
    }

    @Override
    public String getContentUUID() {
        return mContentUUID;
    }

    public void setContentUUID(String contentUUID) {
        mContentUUID = contentUUID;

        if (mPresenter == null) {
            mPresenter = new ContentContract.ContentPresenter(getContext(), this);
        }
        mPresenter.getContent(contentUUID, false);
    }

    @Override
    public boolean interruptKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (!hasFocus() && alternateView != null) {
                    alternateView.requestFocus();
                    return true;
                } else return hasFocus();
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                return alternateView != null && alternateView.hasFocus();
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                return payBtn.hasFocus();
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        if (alternateView != null) {
            alternateView.destroy();
        }
    }

    @Override
    public void onContentResult(@NotNull String uuid, @Nullable Content content) {
        if (content == null) {
            return;
        }
        if (alternateIdText != null) {
            alternateIdText.setText(String.format("%s %s", content.getAlternateNumber(), content
                    .getTitle()));
        }
        if (alternateFromText != null) {
        }

        if (alternateDescText != null) {
            alternateDescText.setText(content.getDescription());
        }

        if (alternateView != null) {
            alternateView.setSeriesInfo(content);
            alternateView.playAlternate(mContentUUID, content.getTitle(), content
                    .getAlternateNumber());
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.full_screen:
//                alternateView.enterFullScreen();
                break;
            case R.id.collect:

                break;
            case R.id.vip_pay:

                break;
        }
    }
}
