package tv.newtv.cboxtv.cms.mainPage.menu;

import android.view.View;
import android.widget.TextView;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.views.MenuRecycleView;
import tv.newtv.cboxtv.views.RecycleImageView;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage.menu
 * 创建事件:         15:25
 * 创建人:           weihaichao
 * 创建日期:          2018/9/27
 */
class NavPageMenuViewHolder extends MenuRecycleView.MenuViewHolder {

    private static final int MODE_TEXT = 1;
    private static final int MODE_IMAGE = 2;
    TextView title;
    RecycleImageView img;
    private int currentMode;

    NavPageMenuViewHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title_text);
        img = itemView.findViewById(R.id.title_icon_nav);
    }

    @Override
    protected void setItemVisible(boolean show) {
        if (!show) {
            title.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
        } else {
            switch (currentMode) {
                case MODE_TEXT:
                    title.setVisibility(View.VISIBLE);
                    img.setVisibility(View.GONE);
                    break;
                case MODE_IMAGE:
                    title.setVisibility(View.GONE);
                    img.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    public void setText(String value) {
        currentMode = MODE_TEXT;
        title.setVisibility(isHidden ? View.GONE : View.VISIBLE);
        img.setVisibility(View.GONE);
        title.setText(value);
    }

    public void setImage(String url) {
        currentMode = MODE_IMAGE;
        title.setVisibility(View.GONE);
        img.setVisibility(isHidden ? View.GONE : View.VISIBLE);
        img.useResize(false).NoStore(false).hasCorner(false).load(url);
    }
}
