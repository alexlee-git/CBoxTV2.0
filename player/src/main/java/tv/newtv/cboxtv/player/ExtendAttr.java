package tv.newtv.cboxtv.player;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms.mainPage.model
 * 创建事件:         20:39
 * 创建人:           weihaichao
 * 创建日期:          2018/4/25
 */
public class ExtendAttr {
    public static final String TYPE_WORLD_CUP = "WorldCup";
    public static final String TYPE_ASIAN_GAMES= "AsianGames";
    /**
     * "score":"21",
     * "goal":"5/6",
     * "lost":"4",
     * "match":"6",
     * "draw":"3",
     * "win":"2",
     * "order":"1"
     */

    private String score;
    private String goal;
    private String lost;
    private String match;
    private String draw;
    private String win;
    private String order;
    private String seriesSubUUID;
    /**
     * "type":"AsianGames", //类型  拿到这个字段先判断type  "type":"WorldCup"（世界杯）,  "type":"AsianGames",（亚运会）
     "gold": "6", //金牌
     "siver": "8/4", //银牌
     "bronze": "1", //铜牌
     "total": "3", //奖牌总数
     "ranking": "0", //排名
     "seriesSubUUID": "a8747b0ddff847a781b9c91f66ff616f", //节目集id用于跳转节目集（本期不用）
     "order": "2" //序号
     */
    private String type;
    private String gold;
    private String siver;
    private String bronze;
    private String total;
    private String ranking;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public String getSiver() {
        return siver;
    }

    public void setSiver(String siver) {
        this.siver = siver;
    }

    public String getBronze() {
        return bronze;
    }

    public void setBronze(String bronze) {
        this.bronze = bronze;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getSeriesSubUUID() {
        return seriesSubUUID;
    }

    public void setSeriesSubUUID(String seriesSubUUID) {
        this.seriesSubUUID = seriesSubUUID;
    }
    public void setDraw(String draw) {
        this.draw = draw;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setLost(String lost) {
        this.lost = lost;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public String getDraw() {
        return draw;
    }

    public String getGoal() {
        return goal;
    }

    public String getLost() {
        return lost;
    }

    public String getMatch() {
        return match;
    }

    public String getOrder() {
        return order;
    }

    public String getScore() {
        return score;
    }

    public String getWin() {
        return win;
    }
}
