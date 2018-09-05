package tv.newtv.cboxtv.cms.util;


import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import tv.newtv.cboxtv.BuildConfig;


public class GlideUtil {

    public static void loadImage(Context context, ImageView imageView, String url,
                                 int placeHolderResId, int errorResId, boolean isCorner) {

        imageView.setTag(null);

        if (BuildConfig.DEBUG) {
            if (url.contains("http://172.25.102.19/")) {
                url = url.replace("http://172.25.102.19/", "http://111.32.132.156/");
            }
            if (url.contains("http://172.25.101.210/")) {
                url = url.replace("http://172.25.101.210/", "http://111.32.132.156/");
            }
        }

        RequestListener requestListener = new RequestListener() {

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                LogUtils.e("loadImage", e.toString() + ":model=" + model + ":target=" + target
                        + ":isFirstResource=" + isFirstResource);
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        };

        if (isCorner) {
            RequestOptions options1 = RequestOptions.bitmapTransform(new RoundedCornersTransformation(4, 0));
            options1.placeholder(placeHolderResId).error(errorResId);
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(options1)
                    .listener(requestListener)
                    .into(imageView);
        } else {
            RequestOptions options2 = new RequestOptions().placeholder(placeHolderResId).error(errorResId);
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(options2)
                    .listener(requestListener)
                    .into(imageView);
        }
    }
}
