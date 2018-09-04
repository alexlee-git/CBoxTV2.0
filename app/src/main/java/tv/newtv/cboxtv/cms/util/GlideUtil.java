package tv.newtv.cboxtv.cms.util;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class GlideUtil {

    public static void loadImage(Context context, ImageView imageView, String url,
                                 int placeHolderResId, int errorResId, boolean isCorner) {

        imageView.setTag(null);



        if (isCorner) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .transform(new GlideRoundTransform(context))
                    .placeholder(placeHolderResId)
                    .error(errorResId)
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext())
                    .load(url)
                    .placeholder(placeHolderResId)
                    .error(errorResId)
                    .into(imageView);
        }
    }
}
