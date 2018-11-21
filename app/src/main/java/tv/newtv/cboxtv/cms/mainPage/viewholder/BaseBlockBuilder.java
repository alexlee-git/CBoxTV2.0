package tv.newtv.cboxtv.cms.mainPage.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.newtv.cms.bean.Page;
import com.newtv.cms.bean.Program;
import com.newtv.cms.contract.AdContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.GlideUtil;
import com.newtv.libs.util.ImageUtils;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.cms.mainPage.viewholder
 * 创建事件:         17:41
 * 创建人:           weihaichao
 * 创建日期:          2018/11/19
 */
abstract class BaseBlockBuilder implements IBlockBuilder {

    private static final String TAG = "BaseBlockBuilder";
    String PlayerUUID = "";
    private AdContract.Presenter mAdPresenter;
    private String PicassoTag = "";

    BaseBlockBuilder(Context context) {

        mAdPresenter = new AdContract.AdPresenter(context, null);
    }

    @Override
    public void setPlayerUUID(String uuid) {
        PlayerUUID = uuid;
    }

    @Override
    public void setPicassoTag(String tag) {
        PicassoTag = tag;
    }

    @Override
    public void destroy() {
        if (mAdPresenter != null) {
            mAdPresenter.destroy();
            mAdPresenter = null;
        }
    }

    protected void loadPosterToImage(Page moduleItem, Program info, RecycleImageView recycleImageView,
                                  boolean hasCorner) {
        if (info.isAd() != 1) {
            showPosterByCMS(recycleImageView, info.getImg(),
                    hasCorner);
        } else {
            Log.e(Constant.TAG, "block id : " + moduleItem.getBlockId() + ", " +
                    "cellcode" +
                    " : "
                    + info.getCellCode() + ", isAd : " + info.isAd());
            showPosterByAD(moduleItem, recycleImageView, info,
                    hasCorner);
        }
    }

    /**
     * 推荐位显示广告
     */
    protected void showPosterByAD(final Page moduleItem, final
    RecycleImageView imageView, final Program info, final boolean hasCorner) {

        if (imageView == null) {
            return;
        }

        if (info == null) {
            return;
        }

        if (moduleItem == null) {
            return;
        }

        mAdPresenter.getAdByType(Constant.AD_DESK, moduleItem.getBlockId() + "_" + info
                        .getCellCode(),
                "", null, new AdContract.Callback() {
                    @Override
                    public void showAd(@Nullable String type, @Nullable String url, @Nullable
                            HashMap<?, ?> hashMap) {
                        if (TextUtils.isEmpty(url)) {
                            showPosterByCMS(imageView, info.getImg(), hasCorner);
                        } else {
                            int width = imageView.getLayoutParams().width;
                            int height = imageView.getLayoutParams().height;
                            int placeHolderResId = ImageUtils.getProperPlaceHolderResId(imageView
                                    .getContext(), width, height);
                            if (placeHolderResId != 0) {
                                imageView.Tag(PicassoTag).placeHolder(placeHolderResId)
                                        .hasCorner(hasCorner).load(url);
                            } else {
                                imageView.Tag(PicassoTag).hasCorner(hasCorner).load(url);
                            }
                        }
                    }
                });
    }

    /**
     * 推荐位显示海报
     */
    void showPosterByCMS(final RecycleImageView imageView, final String
            imgUrl, boolean isCorner) {
        if (imageView != null) {
            int width = imageView.getLayoutParams().width;
            int height = imageView.getLayoutParams().height;

            int placeHolderResId = ImageUtils.getProperPlaceHolderResId(imageView.getContext(),
                    width, height);
            GlideUtil.loadImage(imageView.getContext(), imageView,
                    imgUrl, placeHolderResId, placeHolderResId, isCorner);
        } else {
            Log.e(Constant.TAG, "未找到的控件地址 : ");
        }
    }
}
