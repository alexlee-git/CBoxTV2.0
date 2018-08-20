package tv.newtv;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.SplashActivity;
import tv.newtv.key.KeyHelper;
import tv.newtv.key.KeyType;

public class KeyCheckerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_checker);

        Intent intent = new Intent(this,SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putString("action","OPEN_SPECIAL|Page");
        bundle.putString("params","id=90943e7a-479a-11e8-8bed-c7d8a7a18cc4&uri=d211f144-4d00-11e8" +
                "-a49e-c7d8a7a18cc4&fid=aa138e6a-47ae-11e8-8bed-c7d8a7a18cc4");
        intent.putExtras(bundle);
        Log.e("keyChecker",intent.toUri(0));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
