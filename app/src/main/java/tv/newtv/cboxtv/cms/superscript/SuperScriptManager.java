package tv.newtv.cboxtv.cms.superscript;

import android.content.Context;
import android.text.TextUtils;

import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.cms.net.NetClient;
import tv.newtv.cboxtv.cms.superscript.model.SuperscriptInfo;
import tv.newtv.cboxtv.cms.superscript.model.SuperscriptInfoResult;

/**
 * Created by lixin on 2018/3/9.
 *
 * 角标数据存放地址:/data/data/{packagename}/cache/super.json
 */

public class SuperScriptManager {

    private String mLocalUpdateTime; // 本地缓存的角标对应的时间戳信息
    private Context mContext;
    private Map<String, SuperscriptInfo> mSuperscriptMap;

    private final String CACHE_FILE_NAME = "super.json";
    private final String TAG = "superscript";

    private SuperScriptManager() {}

    private volatile static SuperScriptManager mInstance;

    public static SuperScriptManager getInstance() {
        if (mInstance == null) {
            synchronized (SuperScriptManager.class) {
                if (mInstance == null) {
                    mInstance = new SuperScriptManager();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;

        initSuperscriptRepository();
    }

    /**
     * 加载本地角标库数据
     */
    private void loadLocalSuperscriptRepository() {
        FileReader fileReader =  null;
        BufferedReader reader = null;
        try {
            File file = new File(mContext.getCacheDir(), CACHE_FILE_NAME);
            if (!file.exists()) {
                LogUtils.e(TAG, "角标信息的缓存文件尚不存在");
                return;
            }

            String line;
            StringBuilder dataButt = new StringBuilder(Constant.BUFFER_SIZE_256);
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
            while ((line = reader.readLine()) != null) {
                dataButt.append(line);
            }

            parseSuperscriptInfo("local", new JSONObject(dataButt.toString()));

            fileReader.close();
            reader.close();
        } catch (Exception e) {
            LogUtils.e(e);
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                LogUtils.e(ex);
            }
        }
    }

    /**
     * 初始化角标库
     */
    private void initSuperscriptRepository() {
        loadLocalSuperscriptRepository();

        NetClient.INSTANCE.getSuperScriptApi()
                .getSuperscriptInfos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(ResponseBody value) {
                        try {
                            String data = value.string();
                            JSONObject json = new JSONObject(data);

                            String updateTime = json.optString("updateTime");

                            if (!TextUtils.isEmpty(mLocalUpdateTime)
                                    && !TextUtils.isEmpty(updateTime)
                                    && Long.parseLong(mLocalUpdateTime) >= Long.parseLong(updateTime)) {
                                LogUtils.e(TAG, "本地角标时间大于等于服务端的时间, 无需更新");
                                return;
                            }

                            // 解析网络数据
                            LogUtils.e(TAG, "解析服务端角标数据");
                            parseSuperscriptInfo("server", json);

                            // 更新本地角标数据
                            updateLocalInfo(data);
                        } catch (Exception e) {
                            LogUtils.e(e);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onComplete() {}
                });
    }

    private SuperscriptInfoResult<SuperscriptInfo> parseSuperscriptInfo(String from, JSONObject json) {
        try {
            SuperscriptInfoResult<SuperscriptInfo> superscript = new SuperscriptInfoResult<>();
            superscript.setErrMsg(json.optString("errorMessage"));
            superscript.setErrCode(json.optString("errorCode"));

            String updateTime = json.optString("updateTime");
            if ("local".equals(from)) {
                mLocalUpdateTime = updateTime;
                LogUtils.i(TAG, "解析到本地角标的时间为 : " + mLocalUpdateTime);
            }
            superscript.setUpdateTime(updateTime);

            JSONArray array = json.optJSONArray("data");
            JSONObject item;
            SuperscriptInfo info;
            for (int i = 0; i < array.length(); i++) {
                item = array.optJSONObject(i);
                info = new SuperscriptInfo();
                info.setCornerId(item.optString("cornerId"));
                info.setCornerDesc(item.optString("cornerDesc"));
                info.setCornerName(item.optString("cornerName"));
                info.setCornerImg(item.optString("cornerImg"));
                info.setCornerTitle(item.optString("cornerTitle"));
                info.setCornerType(item.optString("cornerType"));
                if (mSuperscriptMap == null) {
                    mSuperscriptMap = new HashMap<>(Constant.BUFFER_SIZE_8);
                }
                mSuperscriptMap.put(info.getCornerId(), info);
                LogUtils.i("解析到角标信息 id : " + info.getCornerId() + ", img : " + info.getCornerImg());
            }
            return superscript;
        } catch (Exception e) {
            LogUtils.e(e);
            return null;
        }
    }

    /**
     * 更新本地角标数据
     * @param data
     */
    private void updateLocalInfo(String data) {
        FileWriter fileWriter = null;
        try {
            File file = new File(mContext.getCacheDir(), CACHE_FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }

            fileWriter = new FileWriter(file);
            fileWriter.write(data);
            fileWriter.flush();

            fileWriter.close();
            //fileWriter = null;

            LogUtils.i(TAG, "更新本地角标库完毕");
        } catch (Exception e) {
            LogUtils.e(e);

            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException exception) {
                    LogUtils.e(exception);
                }
            }

        }
    }

    public synchronized SuperscriptInfo getSuperscriptInfoById(String id) {
        if (mSuperscriptMap == null) {
            return null;
        }
        return mSuperscriptMap.get(id);
    }

    public void unit() {
        if (mSuperscriptMap != null) {
            mSuperscriptMap.clear();
            mSuperscriptMap = null;
        }

         mInstance = null;
    }
}
