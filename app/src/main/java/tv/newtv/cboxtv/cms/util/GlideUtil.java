package tv.newtv.cboxtv.cms.util;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class GlideUtil {

    public static void loadImage(Context context, ImageView imageView, String url,
                                 int placeHolderResId, int errorResId, boolean isCorner) {

        imageView.setTag(null);

        RequestOptions options = new RequestOptions();

        if (isCorner) {
            options.transform(new GlideRoundTransform(context));
        }

        if (placeHolderResId != 0) {
            options.placeholder(placeHolderResId);
        }

        if (errorResId != 0) {
            options.error(errorResId);
        }

        Glide.with(imageView.getContext())
                .load(url)
                .apply(options)
                .into(imageView);

    }
}
