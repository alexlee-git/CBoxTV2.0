package tv.newtv.cboxtv.uc.bean;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.RxBus;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;


import static android.content.Context.MODE_PRIVATE;


/**
 * Created by simple on 16/12/20.
 * <p>
 * 下载监听
 */

public class DownloadReceiver extends BroadcastReceiver {

    private File file;
    private SharedPreferences pref;
    private Context context;
    private long downId;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Bundle bundle = intent.getExtras();
        downId = bundle.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        //下载完成或点击通知栏
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE) ||
                intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {

            sendMsgByIntentService(context);
        }
    }
    private void sendMsgByIntentService(Context context) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction("startIntentService");
        context.startService(intent);
    }

    public class MyIntentService extends IntentService{
        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         * @param name Used to name the worker thread, important only for debugging.
         */
        public MyIntentService(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            if("startIntentService".equals(intent.getAction())){
                queryFileUri(context, downId);
            }
        }
    }

    private void queryFileUri(Context context, long downloadApkId) {
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadApkId);
        Cursor c = dManager.query(query);
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PENDING:

                    break;
                case DownloadManager.STATUS_PAUSED:

                    break;
                case DownloadManager.STATUS_RUNNING:
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:

                    String downloadFileUrl = c
                            .getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    installApk(context, Uri.parse(downloadFileUrl));
//                    context.unregisterReceiver();
                    break;
                case DownloadManager.STATUS_FAILED:

                    Updater.showToast(context,"下载失败，开始重新下载...");
                    break;
            }
            c.close();
        }
    }

    private void installApk(Context context, Uri uri) {
        file = new File(uri.getPath());
        if (!file.exists()) {

            return;
        }
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            String packageName = context.getPackageName();
            Uri providerUri = FileProvider
                    .getUriForFile(context, packageName+".fileprovider", file);
//            Uri providerUri = FileProvider
//                    .getUriForFile(context, "com.simplepeng.updaterlibrary.fileprovider", file);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(providerUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        pref = context.getSharedPreferences("VersionMd5",MODE_PRIVATE);
        String versionmd5 = pref.getString("versionmd5", null);
        Log.e("versionmd5", versionmd5+"");
        String fileMD5 = getFileMD5(file);
        Log.e("fileMD5", fileMD5+"");
        if (!TextUtils.isEmpty(versionmd5)&&!TextUtils.isEmpty(fileMD5)){
            if (versionmd5.equals(fileMD5)){
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                context.startActivity(intent);
            }else {
                RxBus.get().post(Constant.UP_VERSION_IS_SUCCESS,"version_up_faild");
                Toast.makeText(context,"下载文件有误,请返回页面,重新下载",Toast.LENGTH_SHORT).show();
            }

        }


    }




    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            LogUtils.e(e.toString());
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
