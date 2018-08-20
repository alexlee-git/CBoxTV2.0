package tv.newtv.cboxtv.player.videoview;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.player.videoview
 * 创建事件:         13:01
 * 创建人:           weihaichao
 * 创建日期:          2018/6/11
 */
public interface PlayerCallback {
    void onEpisodeChange(int index, int position);

    void onPlayerClick(VideoPlayerView videoPlayerView);

    void AllPalyComplete(boolean isError, String info, VideoPlayerView videoPlayerView);

    void ProgramChange();
}
