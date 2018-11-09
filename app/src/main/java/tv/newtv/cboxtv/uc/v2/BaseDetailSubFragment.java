package tv.newtv.cboxtv.uc.v2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc.v2
 * 创建事件:         09:21
 * 创建人:           weihaichao
 * 创建日期:          2018/8/28
 */
public abstract class BaseDetailSubFragment extends Fragment {

    protected View contentView;
    protected DisplayMetrics metrics;

    protected abstract int getLayoutId();

    protected abstract void updateUiWidgets(View view);

    protected void init() {}

//    public static BaseDetailSubFragment newInstance(List<>) {
//
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        metrics = getActivity().getResources().getDisplayMetrics();

        if (contentView == null) {
            contentView = inflater.inflate(getLayoutId(), container, false);
            try {
                updateUiWidgets(contentView);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return contentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        contentView = null;
    }
}
