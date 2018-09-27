package tv.newtv;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.newtv.cms.bean.Content;
import com.newtv.cms.bean.Nav;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv
 * 创建事件:         14:14
 * 创建人:           weihaichao
 * 创建日期:          2018/9/27
 */
public class NavActivity extends FragmentActivity implements NavContract.View {

    private static final String TAG = NavActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new NavContract.NavPresenter(this);
    }

    @Override
    public void onNavResult(List<Nav> result) {
        Log.e(TAG, "onNavResult: " + result);
    }

    @Override
    public void onContentResult(Content content) {
        Log.e(TAG, "onContentResult: " + content);
    }

    @Override
    public void setPresenter(@NotNull NavContract.Presenter presenter) {
        presenter.getNav();
        presenter.getContent("4329022");
    }

    @Override
    public void onError(@NotNull String desc) {
        Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
    }
}
