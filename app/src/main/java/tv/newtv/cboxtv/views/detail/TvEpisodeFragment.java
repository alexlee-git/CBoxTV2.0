package tv.newtv.cboxtv.views.detail;

import com.newtv.cms.bean.SubContent;
import com.newtv.libs.ad.ADHelper;

import java.util.List;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.views.detail
 * 创建事件:         15:55
 * 创建人:           weihaichao
 * 创建日期:          2018/10/25
 */
public class TvEpisodeFragment extends AbsEpisodeFragment {
    @Override
    public void setAdItem(ADHelper.AD.ADItem adItem) {

    }

    @Override
    public int getPageSize() {
        return 0;
    }

    @Override
    public void destroy() {

    }

    @Override
    public void clear() {

    }

    @Override
    public void setViewPager(ResizeViewPager viewPager, int position, EpisodeChange change) {

    }

    @Override
    public int getCurrentIndex() {
        return 0;
    }

    @Override
    public void requestDefaultFocus() {

    }

    @Override
    public void setSelectIndex(int index) {

    }

    @Override
    public void setData(List<SubContent> data) {

    }

    @Override
    public void requestFirst() {

    }

    @Override
    public void requestLast() {

    }
}
