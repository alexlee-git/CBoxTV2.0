package tv.newtv.cboxtv.cms.special.viewholder;

import android.content.Context;
import android.widget.FrameLayout;

import com.newtv.cms.bean.Page;

import java.util.List;

import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.mainPage.model.ModuleItem;
import tv.newtv.cboxtv.cms.mainPage.viewholder.UniversalAdapter;
import tv.newtv.cboxtv.cms.mainPage.viewholder.UniversalViewHolder;
import tv.newtv.cboxtv.cms.special.SpecialContract;

/**
 * Created by lin on 2018/3/10.
 */

public class SpecialUniversalAdapter extends UniversalAdapter {

    private int selectPosition = -1;

    public SpecialUniversalAdapter(Context context, List<Page> datas) {
        super(context, datas);
    }

    @Override
    public void onBindViewHolder(UniversalViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);


        if(selectPosition == -1 && position == 0){
            holder.itemView.requestFocus();
            selectPosition = position;
        }
    }
}
