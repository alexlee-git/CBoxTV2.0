package tv.newtv.cboxtv.uc.v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.newtv.libs.Constant;
import com.newtv.libs.util.ScaleUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.SoftWareInfoActivity;

public class VersionUpdateOneActivity extends BaseActivity implements View.OnFocusChangeListener {


    @BindView(R.id.cb_version)
    CheckBox cbVersion;
    @BindView(R.id.tv_update_info)
    TextView tvUpdateInfo;
    @BindView(R.id.ll_update_container)
    LinearLayout llUpdateContainer;
    @BindView(R.id.ll_software_info)
    LinearLayout llSoftwareInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_update_one);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.VERSION_UPDATE) {
            tvUpdateInfo.setText("有新版本");
        } else {
            tvUpdateInfo.setText("已是新版");
        }
    }

    private void initView() {
        findViewById(R.id.ll_update_container).setOnFocusChangeListener(this);
        findViewById(R.id.ll_software_info).setOnFocusChangeListener(this);
        llUpdateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VersionUpdateOneActivity.this, VersionUpdateTwoActivity.class);
                startActivity(intent);
            }

        });
        llSoftwareInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VersionUpdateOneActivity.this, SoftWareInfoActivity.class);
                startActivity(intent);
            }

        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ScaleUtils.getInstance().onItemGetFocus(v);
        } else {
            ScaleUtils.getInstance().onItemLoseFocus(v);
        }
        CheckBox title = v.findViewWithTag("InfoTitle");
        if (title != null) {
            title.setChecked(hasFocus);
        }
    }

}
