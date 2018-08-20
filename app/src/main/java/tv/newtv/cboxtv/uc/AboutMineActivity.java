package tv.newtv.cboxtv.uc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import tv.newtv.cboxtv.Constant;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.util.LogUploadUtils;

public class AboutMineActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnFocusChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_mine);

        LogUploadUtils.uploadLog(Constant.LOG_NODE_USER_CENTER,"9");//关于页面
        initView();
    }

    private void initView() {
        View versionInfo = findViewById(R.id.rl_version_info);
        versionInfo.setOnClickListener(this);
        versionInfo.setOnFocusChangeListener(this);

        View userDeal = findViewById(R.id.rl_user_deal);
        userDeal.setOnClickListener(this);
        userDeal.setOnFocusChangeListener(this);

        View software = findViewById(R.id.rl_software);
        software.setOnClickListener(this);
        software.setOnFocusChangeListener(this);
    }

    private void setFocusAnim(View view, boolean hasFocus) {
        if (hasFocus) {
            view.animate().scaleX(1.1f).scaleY(1.2f).setDuration(300).start();
        } else {
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_version_info:
                break;
            case R.id.rl_user_deal:
                jumpActivity(UserDealActivity.class);
                break;
            case R.id.rl_software:
                jumpActivity(SoftWareInfoActivity.class);
                break;
        }
    }

    public void jumpActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        setFocusAnim(v, hasFocus);
    }
}
