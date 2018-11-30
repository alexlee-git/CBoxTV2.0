package tv.newtv.cboxtv.player.contract;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.newtv.cms.CmsServicePresenter;
import com.newtv.cms.DataObserver;
import com.newtv.cms.ICmsPresenter;
import com.newtv.cms.ICmsView;
import com.newtv.cms.api.IPlayChk;
import com.newtv.cms.bean.CdnUrl;
import com.newtv.cms.bean.ChkRequest;
import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.ad.ADConfig;
import com.newtv.libs.uc.UserStatus;
import com.newtv.libs.util.Encryptor;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.NetworkManager;
import com.newtv.libs.util.SharePreferenceUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import tv.newtv.cboxtv.player.ChkPlayResult;
import tv.newtv.cboxtv.player.PlayerConfig;
import tv.newtv.cboxtv.player.PlayerConstants;
import tv.newtv.cboxtv.player.PlayerErrorCode;
import tv.newtv.cboxtv.player.model.VideoDataStruct;
import tv.newtv.cboxtv.player.util.PlayerNetworkRequestUtils;
import tv.newtv.cboxtv.player.vip.VipCheck;
import tv.newtv.player.R;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.player.contract
 * 创建事件:         17:16
 * 创建人:           weihaichao
 * 创建日期:          2018/10/12
 */
public class VodContract {

    public interface View extends ICmsView {
        void onVodchkResult(VideoDataStruct videoDataStruct, String contentUUID);

        void onChkError(String code, String desc);
    }

    public interface Presenter extends ICmsPresenter {
        /**
         * @param programId
         * @param albumId
         * @param isCarouse
         */
        void checkVod(String programId, String albumId, boolean isCarouse);
    }

    public static class VodPresenter extends CmsServicePresenter<View> implements Presenter {

        private static final String TAG = VodPresenter.class.getSimpleName();

        public VodPresenter(@NotNull Context context, @NotNull View view) {
            super(context, view);
        }

        /**
         * 解析CDN播放地址
         *
         * @param playResult
         * @return
         */
        private String translateCdnUrl(ChkPlayResult playResult) {
            LogUtils.i(TAG, "translateCdnUrl: ");
            List<CdnUrl> mediaCDNInfos = playResult.getData();
            List<CdnUrl> specifiedCDNInfos = new ArrayList<>();
            for (int i = 0; i < mediaCDNInfos.size(); i++) {
                CdnUrl mediaCDNInfo = mediaCDNInfos.get(i);
                if (TextUtils.equals(mediaCDNInfo.getCNDId(), mediaCDNInfos.get(0).getCNDId())) {
                    specifiedCDNInfos.add(mediaCDNInfo);
                    //测试专用
                    LogUtils.i(TAG, "sendSharpnessesToSetting: " + mediaCDNInfo.toString());
                }
            }

            if (specifiedCDNInfos.size() < 1) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string
                        .program_info_no_data), Toast.LENGTH_SHORT).show();
                LogUtils.e("鉴权接口后没有返回视频地址");
                if (getView() != null)
                    getView().onChkError(PlayerErrorCode.PROGRAM_CDN_EMPTY, PlayerErrorCode
                            .getErrorDesc(getContext(), PlayerErrorCode.PROGRAM_CDN_EMPTY));
                return null;
            }
            for (int j = 0; j < specifiedCDNInfos.size(); j++) {
                CdnUrl mediaCDNInfo = specifiedCDNInfos.get(j);
                if (PlayerConstants.SHARPNESS_HD.equals(mediaCDNInfo.getMediaType())) {
                    return mediaCDNInfo.getPlayURL();
                } else if ("ts".equals(mediaCDNInfo.getMediaType()) || "TS".equals(mediaCDNInfo
                        .getMediaType())) {
                    return mediaCDNInfo.getPlayURL();
                } else if ("M3U8".equals(mediaCDNInfo.getMediaType().toUpperCase())) {
                    return mediaCDNInfo.getPlayURL();
                }
            }
            return null;
        }

        /**
         * 创建鉴权数据
         *
         * @param contentId
         * @param seriesID
         * @return
         */
        ChkRequest createVodRequestBean(String contentId, String seriesID, boolean isCarouse) {
            ChkRequest playCheckRequestBean = new ChkRequest();

            playCheckRequestBean.setAppKey(Libs.get().getAppKey());
            playCheckRequestBean.setChannelId(Libs.get().getChannelId());
            playCheckRequestBean.setSource("NEWTV");
            playCheckRequestBean.setId(contentId);
            playCheckRequestBean.setCarouselFlag(isCarouse);
            if (!TextUtils.isEmpty(seriesID)) {
                playCheckRequestBean.setAlbumId(seriesID);
            }
            ChkRequest.Product productInfo = new ChkRequest.Product();
            productInfo.setId(1);

            List<ChkRequest.Product> productList = new ArrayList<>();
            productList.add(productInfo);
            playCheckRequestBean.setProductDTOList(productList);
            return playCheckRequestBean;
        }

        /**
         * 解析鉴权结果
         *
         * @param result
         */
        private void parseResult(String result) {
            final ChkPlayResult playResult = PlayerNetworkRequestUtils.getInstance()
                    .parsePlayPermissionCheckResult(result);
            if (playResult == null) {
                LogUtils.i(TAG, "onResponse: programDetailInfo==null");
                String errorCode = PlayerNetworkRequestUtils.getErrorCode(result);
                if (!NetworkManager.getInstance().isConnected()) {
                    Toast.makeText(getContext(), getContext().getResources()
                            .getString(R.string
                                    .search_fail_agin), Toast.LENGTH_SHORT).show();
                } else {
                    String toastText = "";
                    switch (errorCode) {
                        case PlayerErrorCode.USER_NOT_LOGIN:
                        case PlayerErrorCode.USER_TOKEN_IS_EXPIRED:
                        case PlayerErrorCode.USER_NOT_BUY:
                            toastText = "付费内容需购买后才能观看";
                            break;

                        default:
                            toastText = getContext().getResources()
                                    .getString(R.string.check_error) + errorCode;
                    }
                    Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
                }
                if (getView() != null)
                    getView().onChkError(errorCode, getContext().getResources()
                            .getString(R
                                    .string.check_error));
                LogUtils.e("调用鉴权接口后没有返回数据");
                return;
            }
            if (playResult.getData().size() < 1) {
                LogUtils.i(TAG, "onResponse: programDetailInfo.getData().size()<1");
                if (getView() != null)
                    getView().onChkError(PlayerErrorCode.PROGRAM_SERIES_EMPTY, PlayerErrorCode
                            .getErrorDesc(getContext(), PlayerErrorCode.PROGRAM_SERIES_EMPTY));
                LogUtils.e("暂无节目内容");
                return;
            }
            String playUrl = translateCdnUrl(playResult);
            if (TextUtils.isEmpty(playUrl)) {
                if (getView() != null) {
                    getView().onChkError(PlayerErrorCode.PROGRAM_PLAY_URL_EMPTY, PlayerErrorCode
                            .getErrorDesc(getContext(), PlayerErrorCode.PROGRAM_PLAY_URL_EMPTY));
                }
                return;
            }

            final VideoDataStruct videoDataStruct = new VideoDataStruct();
            if (playResult.getEncryptFlag()) {
                videoDataStruct.setKey(Encryptor.decrypt(Constant.APPSECRET,
                        playResult.getDecryptKey()));
            }
            LogUtils.i(TAG, "playViewgetEncryptFlag:" + playResult.getEncryptFlag()
                    + ",key=" + Encryptor.decrypt(Constant.APPSECRET, playResult.getDecryptKey()));
            videoDataStruct.setPlayType(0);

            videoDataStruct.setPlayUrl(playUrl);
            videoDataStruct.setProgramId(playResult.getContentUUID());

            String duration = playResult.getDuration();
            String seriesUUIDs = playResult.getProgramSeriesUUIDs();
            String flag = playResult.getVipFlag();
            if (!TextUtils.isEmpty(duration)) {
                videoDataStruct.setDuration(Integer.parseInt(playResult
                        .getDuration()));
                SharedPreferences.Editor editor = getContext().getSharedPreferences("durationConfig", Context.MODE_PRIVATE).edit();
                if (!TextUtils.isEmpty(seriesUUIDs)){
                    editor.putString("seriesUUID",seriesUUIDs);
                }
                if (!TextUtils.isEmpty(flag)){
                    editor.putString("vipFlag",flag);
                }
                editor.putString("duration", duration);
                editor.apply();
            }

            videoDataStruct.setDataSource(PlayerConstants.DATASOURCE_ICNTV);
            videoDataStruct.setDeviceID(Constant.UUID);
            videoDataStruct.setCategoryIds(playResult.getCategoryIds());
            ADConfig.getInstance().setProgramId(playResult.getContentUUID());
            ADConfig.getInstance().setCategoryIds(playResult.getCategoryIds());
            ADConfig.getInstance().setDuration(playResult.getDuration());
            if (playResult.getPrograms() != null && playResult.getPrograms().size() > 0) {
                String contentId = playResult.getPrograms().get(0).getProgramSeriesId();
                ADConfig.getInstance().setSeriesID(contentId, false);
                videoDataStruct.setSeriesId(contentId);
            } else {
                ADConfig.getInstance().setSeriesID("", false);
            }

            String vipFlag = playResult.getVipFlag();

            if (!TextUtils.isEmpty(vipFlag) && VipCheck.VIP_FLAG_VIP.equals(vipFlag) &&
                    !UserStatus.isVip()) {
                callBack(true, videoDataStruct, playResult);
            } else if (!TextUtils.isEmpty(vipFlag) && (VipCheck.VIP_FLAG_BUY.equals(vipFlag)
                    || VipCheck.VIP_FLAG_VIP_BUY.equals(vipFlag) && !UserStatus.isVip())) {
                VipCheck.isBuy(playResult.getVipProductId(), playResult.getContentUUID(),
                        getContext(),
                        new VipCheck.BuyFlagListener() {
                            @Override
                            public void buyFlag(boolean buyFlag) {
                                callBack(!buyFlag, videoDataStruct, playResult);
                            }
                        });
            } else {
                callBack(false, videoDataStruct, playResult);
            }
        }

        private void callBack(boolean isTrySee, VideoDataStruct videoDataStruct, ChkPlayResult
                playResult) {
//            isTrySee = true;
            if (isTrySee) {
                videoDataStruct.setTrySee(true);
                videoDataStruct.setFreeDuration(playResult.getFreeDuration());
//                videoDataStruct.setFreeDuration("300");
            }
            if (getView() != null) {
                getView().onVodchkResult(videoDataStruct, playResult.getContentUUID());
            }
        }

        @Override
        public void checkVod(String programId, String albumId, boolean isCarouse) {
            final IPlayChk playChk = getService(SERVICE_CHK_PLAY);
            String token = SharePreferenceUtils.getToken(getContext());
            String resultToken = TextUtils.isEmpty(token) ? "" : "Bearer " + token;
            if (playChk != null) {
                ChkRequest request = createVodRequestBean(programId, albumId, isCarouse);
                playChk.check(request, resultToken, new DataObserver<String>() {
                    @Override
                    public void onResult(String result, long requestCode) {
                        if (TextUtils.isEmpty(result)) {
                            LogUtils.i(TAG, "onResponse: responseBody==null");
                            if (!NetworkManager.getInstance().isConnected()) {
                                if (getView() != null)
                                    getView().onChkError(PlayerErrorCode.INTERNET_ERROR,
                                            PlayerErrorCode.getErrorDesc(getContext(),
                                                    PlayerErrorCode
                                                            .PERMISSION_CHECK_RESULT_EMPTY));
                            } else {
                                if (getView() != null)
                                    getView().onChkError(PlayerErrorCode
                                                    .PERMISSION_CHECK_RESULT_EMPTY,
                                            PlayerErrorCode.getErrorDesc(getContext(),
                                                    PlayerErrorCode
                                                    .PERMISSION_CHECK_RESULT_EMPTY));
                            }

                            LogUtils.e(TAG, "调用鉴权接口后没有返回数据");
                            return;
                        }

                        parseResult(result);
                    }

                    @Override
                    public void onError(@Nullable String desc) {
                        LogUtils.e(TAG, "onFailure: " + desc);
                        if (!NetworkManager.getInstance().isConnected()) {
                            if (getView() != null)
                                getView().onChkError(PlayerErrorCode.INTERNET_ERROR,
                                        PlayerErrorCode.getErrorDesc(getContext(),
                                                PlayerErrorCode
                                                        .PERMISSION_CHECK_RESULT_EMPTY));
                        } else {
                            if (getView() != null)
                                getView().onChkError(PlayerErrorCode.PERMISSION_CHECK_RESULT_EMPTY,
                                        PlayerErrorCode.getErrorDesc(getContext(), PlayerErrorCode
                                                .PERMISSION_CHECK_RESULT_EMPTY));
                        }
                    }
                });
            }
        }
    }
}
