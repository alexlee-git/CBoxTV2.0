package tv.newtv.cboxtv.player;

import java.util.LinkedHashMap;

public interface iPlayCallBackEvent {

	public void onPrepared(LinkedHashMap<String, String> definitionDatas);//
	public void onCompletion();//
	public void onVideoBufferStart(String typeString);//
	public void onVideoBufferEnd(String typeString);
	public void onTimeout();
	public void changePlayWithDelay(int delay, String url);
	public void onError(int what, int extra, String msg);
}
