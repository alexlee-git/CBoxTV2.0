package tv.newtv.cboxtv.exit.model;

import android.service.voice.VoiceInteractionService;

import tv.newtv.cboxtv.exit.bean.RecommendBean;

public interface RecommendModel {
    void  requestRecommendData(CompleteListener listener);

     interface CompleteListener {
         void sendRecommendData(RecommendBean recommendBean);
    }
}
