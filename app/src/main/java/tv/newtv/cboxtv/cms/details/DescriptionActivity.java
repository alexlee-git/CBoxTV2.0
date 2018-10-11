package tv.newtv.cboxtv.cms.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.TextView;

import com.newtv.libs.util.XunMaKeyUtils;

import tv.newtv.cboxtv.R;

public class DescriptionActivity extends Activity{
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    private TextView titleView;
    private TextView contentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        titleView = findViewById(R.id.title);
        contentView = findViewById(R.id.content);
        titleView.setText(getIntent().getStringExtra(TITLE));
        contentView.setText(getIntent().getStringExtra(CONTENT));
    }

    public static void runAction(Context context,String title,String content){
        Intent intent = new Intent(context,DescriptionActivity.class);
        intent.putExtra(TITLE,title);
        intent.putExtra(CONTENT,content);
        context.startActivity(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        XunMaKeyUtils.key(event);
        return super.dispatchKeyEvent(event);
    }
}
