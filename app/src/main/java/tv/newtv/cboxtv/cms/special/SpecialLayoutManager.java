package tv.newtv.cboxtv.cms.special;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.newtv.cms.bean.ModelResult;

import tv.newtv.cboxtv.cms.mainPage.model.ModuleInfoResult;
import tv.newtv.cboxtv.cms.special.doubleList.fragment.NewSpecialFragment;
import tv.newtv.cboxtv.cms.special.fragment.BallPlayerFragment;
import tv.newtv.cboxtv.cms.special.fragment.BallRoundFragment;
import tv.newtv.cboxtv.cms.special.fragment.BaseSpecialContentFragment;
import tv.newtv.cboxtv.cms.special.fragment.DefaultSpecial;
import tv.newtv.cboxtv.cms.special.fragment.MedalFragment;
import tv.newtv.cboxtv.cms.special.fragment.SpecialThreeFragment;
import tv.newtv.cboxtv.cms.special.fragment.TopicTwoFragment;
import tv.newtv.cboxtv.cms.special.fragment.ProgramPageFragment;
import tv.newtv.cboxtv.cms.special.fragment.QXDFFragment;
import tv.newtv.cboxtv.cms.special.fragment.ScheduleFragment;
import tv.newtv.cboxtv.cms.special.fragment.ScoreFragment;
import tv.newtv.cboxtv.cms.special.fragment.ShooterFragment;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.special
 * 创建事件:         13:14
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
class SpecialLayoutManager {
    /**
     * special_002 赛程表
     * special_003 足球是圆的
     * special_004 播放页
     * special_005 绝对巨星
     * special_006 射手榜
     * special_007 球兴大发
     * special_008 积分榜
     * special_009 世界杯栏目详情页
     * special_010 首页
     * special_011 奖牌榜
     **/
    private static SpecialLayoutManager layoutManager;

    private SpecialLayoutManager() {

    }

    public static SpecialLayoutManager get() {
        if (layoutManager == null) {
            synchronized (SpecialLayoutManager.class) {
                if (layoutManager == null) layoutManager = new SpecialLayoutManager();
            }
        }
        return layoutManager;
    }

    BaseSpecialContentFragment GenerateFragment(
            int containerId,
            Bundle bundle,
            FragmentManager fragmentManager,
            ModelResult infoResult
    ) {
        if (infoResult == null) return null;
        String type = infoResult.getTemplateZT();
        BaseSpecialContentFragment baseSpecialContentFragment = (BaseSpecialContentFragment)
                fragmentManager.findFragmentByTag(type);
        if (baseSpecialContentFragment == null) {
            if ("special_006".equals(type)) {
                baseSpecialContentFragment = new ShooterFragment();
            } else if ("special_008".equals(type)) {
                baseSpecialContentFragment = new ScoreFragment();
            } else if ("special_005".equals(type)) {
                baseSpecialContentFragment = new DefaultSpecial();
            } else if ("special_002".equals(type)) {
                baseSpecialContentFragment = new ScheduleFragment();
            } else if ("special_003".equals(type)) {
                baseSpecialContentFragment = new BallRoundFragment();
            }else if ("special_007".equals(type)) {
                baseSpecialContentFragment = new QXDFFragment();
            }else if ("special_004".equals(type)) {
                baseSpecialContentFragment = new BallPlayerFragment();
            }else if ("special_009".equals(type)) {
                baseSpecialContentFragment = new ProgramPageFragment();
            }else if("special_011".equals(type)){
                baseSpecialContentFragment = new MedalFragment();
            }else if("special_012".equals(type)){
                baseSpecialContentFragment = new TopicTwoFragment();
            }else if("special_013".equals(type)){
                baseSpecialContentFragment = new NewSpecialFragment();
            } else if ("special_014".equals(type)) {
                baseSpecialContentFragment = new SpecialThreeFragment();
            }

        }

        if (baseSpecialContentFragment != null) {
            baseSpecialContentFragment.setArguments(bundle);
            baseSpecialContentFragment.setModuleInfo(infoResult);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (!baseSpecialContentFragment.isAdded()) {
                fragmentTransaction.add(containerId, baseSpecialContentFragment,
                        type);
            }
            fragmentTransaction.show(baseSpecialContentFragment)
                    .commitAllowingStateLoss();
        }
        return baseSpecialContentFragment;
    }

}
