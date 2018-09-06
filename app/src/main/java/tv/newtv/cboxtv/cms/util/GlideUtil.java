package tv.newtv.cboxtv.cms.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import tv.newtv.cboxtv.BuildConfig;


public class GlideUtil {

    @SuppressWarnings("ConstantConditions")
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

        RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                    target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target,
                                        boolean isFirstResource) {
                LogUtils.e("loadImage", String.format
                        ("failed e=%s:model=%s:target=%s:isFirstResource=%s", e != null ? e
                                        .toString()
                                        : "null", model != null ? model.toString() : "null",
                                target != null ? target.toString() : " null ",
                                isFirstResource ? "true" : "false"));
                return false;
            }
        };

        RequestOptions options = new RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565)
                .dontAnimate()
                .placeholder(placeHolderResId)
                .error(errorResId);

        if (isCorner) {
            options = options.transform(new RoundedCornersTransformation(4, 0));
        }

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .listener(requestListener)
                .into(imageView);
    }
}
