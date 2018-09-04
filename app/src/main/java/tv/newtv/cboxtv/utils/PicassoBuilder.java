package tv.newtv.cboxtv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv
 * 创建事件:         10:44
 * 创建人:           weihaichao
 * 创建日期:          2018/4/19
 */
public class PicassoBuilder {
    private static PicassoBuilder builder;

    private LruCache mLruCache;

    private PicassoBuilder(Context context) {
        build(context);
    }

    public static void init(Context context) {
        if (builder == null) {
            synchronized (PicassoBuilder.class) {
                if (builder == null)
                    builder = new PicassoBuilder(context);
            }
        }
    }

    public static PicassoBuilder getBuilder() {
        return builder;
    }

    public void pause(Object tag){

    }

    public void resume(Object tag){

    }

    public void clearImageMemory(String uri){
        if (!TextUtils.isEmpty(uri)) {
            mLruCache.clearKeyUri(uri);
        }
    }

    private void build(Context context) {
        //配置缓存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 15;
        mLruCache = new LruCache(cacheSize);// 设置缓存大小
        //配置线程池
        //ExecutorService executorService = Executors.newFixedThreadPool(8);

        Picasso picasso = new Picasso.Builder(context.getApplicationContext())
                .memoryCache(mLruCache)
                //.executor(executorService)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.e("Picasso", "download failed url=" + uri);
                    }
                })
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .build();
        Picasso.setSingletonInstance(picasso);
    }
}
