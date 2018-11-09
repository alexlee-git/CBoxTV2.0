package tv.newtv.cboxtv.uc.bean;

/**
 * 项目名称:         熊猫ROM-launcher应用
 * 包名:            tv.newtv.tvlauncher
 * 创建时间:         下午3:41
 * 创建人:           lixin
 * 创建日期:         2018/9/7
 */


public class HistoryRecordInfo {
    private String scrore;    // 评分
    private String name;      // 剧集名称
    private String posterUrl; // 海报地址
    private String watchProgress;  // 观看进度
    private String episodeProgress; // 剧集进度
    private boolean hasFreshMeat; // 是否有更新

    public String getScrore() {
        return scrore;
    }

    public String getName() {
        return name;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getWatchProgress() {
        return watchProgress;
    }

    public String getEpisodeProgress() {
        return episodeProgress;
    }

    public boolean isHasFreshMeat() {
        return hasFreshMeat;
    }

    public void setScrore(String scrore) {
        this.scrore = scrore;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setWatchProgress(String watchProgress) {
        this.watchProgress = watchProgress;
    }

    public void setEpisodeProgress(String episodeProgress) {
        this.episodeProgress = episodeProgress;
    }

    public void setHasFreshMeat(boolean hasFreshMeat) {
        this.hasFreshMeat = hasFreshMeat;
    }

    public HistoryRecordInfo(String scrore, String name, String posterUrl, String watchProgress, String episodeProgress, boolean hasFreshMeat) {
        this.scrore = scrore;
        this.name = name;
        this.posterUrl = posterUrl;
        this.watchProgress = watchProgress;
        this.episodeProgress = episodeProgress;
        this.hasFreshMeat = hasFreshMeat;
    }
}
