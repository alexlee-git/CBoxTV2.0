package tv.newtv.cboxtv;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;


/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv
 * 创建事件:         12:40
 * 创建人:           weihaichao
 * 创建日期:          2018/3/30
 */

public class DemoActivity extends FragmentActivity {


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_module_5_v2);

    }
}
