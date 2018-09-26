package tv.newtv;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.newtv.cms.DataObserver;
import com.newtv.cms.Model;
import com.newtv.cms.ModelFactory;
import com.newtv.cms.api.ICategory;
import com.newtv.cms.api.IFilter;
import com.newtv.cms.api.INav;
import com.newtv.cms.bean.CategoryTreeNode;
import com.newtv.cms.bean.FilterItem;
import com.newtv.cms.bean.ModelResult;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;

public class KeyCheckerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_checker);
    }

    private void request(){

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_UP){
            if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() ==
                    KeyEvent.KEYCODE_ENTER){
                request();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
