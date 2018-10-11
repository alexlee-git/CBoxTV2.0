package tv.newtv.cboxtv.cms.mainPage.model;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.player.ProgramsInfo;

/**
 * Created by lixin on 2018/1/24.
 */

public class ModuleInfoResult {
    public static final int NAV_PAGE = 1;
    public static final int SPECIAL_PAGE = 2;
    public static final int IS_AD_PAGE = 1;

    private String errorMessage;
    private String errorCode;
    private int isNav;   // 0：普通页，1：导航页，2：专题页

    //只有当 isNav=1或者2时，也就是导航页和专题页会有下面的字段
    private String pageTitle;
    private String subTitle;
    private String templateZT;
    private String description;
    private String background;
    private int    isAd; // 0：不是广告位 1；是广告位
    private int    isCollection = 0;
    private List<ModuleItem> data;

    @Override
    public String toString() {
        return "ModuleInfoResult{" +
                "errorMessage='" + errorMessage + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", isNav=" + isNav +
                ", pageTitle='" + pageTitle + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", templateZT='" + templateZT + '\'' +
                ", description='" + description + '\'' +
                ", background='" + background + '\'' +
                ", isAd=" + isAd +
                ", isCollection=" + isCollection +
                ", data=" + data +
                '}';
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getTemplateZT() {
        return templateZT;
    }

    public void setTemplateZT(String templateZT) {
        this.templateZT = templateZT;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPageBackground() {
        return background;
    }

    public void setPageBackground(String pageBackground) {
        this.background = pageBackground;
    }

    public int getIsAd() {
        return isAd;
    }

    public void setIsAd(int isAd) {
        this.isAd = isAd;
    }

    public int getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(int isCollection) {
        this.isCollection = isCollection;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getIsNav() {
        return this.isNav;
    }

    public void setIsNav(int isNav) {
        this.isNav = isNav;
    }

    public List<ModuleItem> getDatas() {
        return data;
    }

    public void setmDatas(List<ModuleItem> mDatas) {
        this.data = mDatas;
    }

    /**
     * 获取亚运会奖牌榜的数据
     * @return
     */
    public List<ProgramsInfo> getAsianList(){
        List<ProgramsInfo> result = new ArrayList<>();
        if(data == null){
            return result;
        }

        for(ModuleItem item : data){
            List<ProgramsInfo> datas = item.getDatas();
            if(datas != null && datas.size() > 0){
//                List<ExtendAttr> extendAttr = datas.get(0).getExtendAttr();
//                if(extendAttr != null && extendAttr.size() > 0){
//                    if(ExtendAttr.TYPE_ASIAN_GAMES.equals(extendAttr.get(0).getType())){
//                        result.addAll(datas);
//                    }
//                }
            }
        }

        return result;
    }
}
