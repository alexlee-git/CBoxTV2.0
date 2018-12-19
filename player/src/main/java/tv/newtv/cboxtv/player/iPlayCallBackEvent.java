package tv.newtv.cboxtv.player;

import java.util.LinkedHashMap;

public interface iPlayCallBackEvent {

	void onPrepared(LinkedHashMap<String, String> definitionDatas);//
	void onCompletion(int type);//
	void onVideoBufferStart(String typeString);//
	void onVideoBufferEnd(String typeString);
	void onTimeout(int i);
	void changePlayWithDelay(int delay, String url);
	void onError(int what, int extra, String msg);
	void onAdStartPlaying();
}
