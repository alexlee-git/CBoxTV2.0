package tv.newtv.cboxtv.player;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.player
 * 创建事件:         17:46
 * 创建人:           slp
 */
public class PlayerUrlConfig {

    private volatile static PlayerUrlConfig instance;

    private boolean isFromDetailPage = false;
    private String playUrl = "";
    private String playingContentId = "";//节目id

    public static PlayerUrlConfig getInstance() {
        if (instance == null) {
            synchronized (PlayerUrlConfig.class) {
                if (instance == null) instance = new PlayerUrlConfig();
            }
        }
        return instance;
    }


    public boolean isFromDetailPage() {
        return isFromDetailPage;
    }

    public void setFromDetailPage(boolean fromDetailPage) {
        isFromDetailPage = fromDetailPage;
    }



    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getPlayingContentId() {
        return playingContentId;
    }

    public void setPlayingContentId(String playingContentId) {
        this.playingContentId = playingContentId;
    }
}
