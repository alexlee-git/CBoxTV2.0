package tv.newtv.cboxtv.cms.mainPage.menu;

import java.util.HashSet;
import java.util.Set;

/**
 * 如果导航包含专题页面，需要根据需求更新背景图
 */
public class BGEvent {
    public String contentUUID;
    public boolean isAd;
    public String bgImageUrl;
    public Set<String> childSet;

    public BGEvent(String contentUUID, boolean isAd, String bgImageUrl) {
        this.contentUUID = contentUUID;
        this.isAd = isAd;
        this.bgImageUrl = bgImageUrl;
    }

    public void add(String uuid){
        if(childSet == null){
            childSet = new HashSet<>();
        }
        childSet.add(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BGEvent bgEvent = (BGEvent) o;
        return isAd == bgEvent.isAd &&
                equals(contentUUID, bgEvent.contentUUID) &&
                equals(bgImageUrl, bgEvent.bgImageUrl) &&
                equals(childSet, bgEvent.childSet);
    }

    private boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public String toString() {
        return "BGEvent{" +
                "contentUUID='" + contentUUID + '\'' +
                ", isAd=" + isAd +
                ", bgImageUrl='" + bgImageUrl + '\'' +
                ", childSet=" + childSet +
                '}';
    }
}
