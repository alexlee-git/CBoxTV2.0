package tv.newtv.cboxtv.cms.superscript;

import android.content.Context;
import android.text.TextUtils;

import com.newtv.cms.bean.Corner;
import com.newtv.cms.bean.CornerCondition;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.contract.CornerContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.BuildConfig;

/**
 * Created by lixin on 2018/3/9.
 * <p>
 * 角标数据存放地址:/data/data/{packagename}/cache/super.json
 */

public class SuperScriptManager implements CornerContract.View {


    private volatile static SuperScriptManager mInstance;

    private final String CACHE_FILE_NAME = "super.json";
    private final String TAG = "superscript";
    private String mLocalUpdateTime; // 本地缓存的角标对应的时间戳信息
    private Map<String, Corner> mSuperscriptMap;
    private CornerContract.Presenter mPresenter;


    private SuperScriptManager() {
    }

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

    private boolean isContain(List<CornerCondition> conditions, Object item) {
        boolean suit = false;
        try {
            Field[] fields = item.getClass().getDeclaredFields();
            fieldLoop:
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(item);
                for (CornerCondition condition : conditions) {
                    if (TextUtils.equals(condition.getFieldName(), name) && value != null) {
                        String result = "";
                        if (value instanceof String) {
                            result = value.toString();
                        } else if (value instanceof Integer) {
                            result = Integer.toString((Integer) value);
                        }
                        LogUtils.d("SuperScriptMananger",
                                String.format("fname=%s fvalue=%s cname=%s cvalue = %s",
                                        name, result, condition.getFieldName(), condition
                                                .getFieldValue())
                        );
                        if (!TextUtils.equals(condition.getFieldValue(), result)) {
                            suit = false;
                            break fieldLoop;
                        } else {
                            suit = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            suit = false;
        }

        return suit;
    }

    public List<Corner> findSuitCorner(Object item) {
        List<Corner> cornerList = new ArrayList<>();
        Collection<Corner> corners = mSuperscriptMap.values();
        for (Corner corner : corners) {
            List<CornerCondition> conditionList = corner.getCornerCondition();
            if (conditionList != null) {
                if (isContain(conditionList, item)) {
                    cornerList.add(corner);
                }
            }
        }
        return cornerList;
    }

    public void init(Context context) {
        mPresenter = new CornerContract.CornerPresenter(context, this);
        initSuperscriptRepository(context);
    }

    /**
     * 加载本地角标库数据
     */
    private void loadLocalSuperscriptRepository(Context context) {
        FileReader fileReader = null;
        BufferedReader reader = null;
        try {
            File file = new File(context.getCacheDir(), CACHE_FILE_NAME);
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
    private void initSuperscriptRepository(Context context) {
        loadLocalSuperscriptRepository(context);
        mPresenter.getCorner(BuildConfig.APP_KEY, BuildConfig.CHANNEL_ID);
    }

    private ModelResult<List<Corner>> parseSuperscriptInfo(String from, JSONObject
            json) {
        try {
            ModelResult<List<Corner>> superscript = new ModelResult<>();
            superscript.setErrorMessage(json.optString("errorMessage"));
            superscript.setErrorCode(json.optString("errorCode"));

            String updateTime = json.optString("updateTime");
            if ("local".equals(from)) {
                mLocalUpdateTime = updateTime;
                LogUtils.i(TAG, "解析到本地角标的时间为 : " + mLocalUpdateTime);
            }
            superscript.setUpdateTime(updateTime);

            JSONArray array = json.optJSONArray("data");
            JSONObject item;
            Corner info;
            for (int i = 0; i < array.length(); i++) {
                item = array.optJSONObject(i);
                info = new Corner();
                if (item.has("cornerId"))
                    info.setCornerId(item.optString("cornerId"));
                if (item.has("cornerImg"))
                    info.setCornerImg(item.optString("cornerImg"));
                if (item.has("cornerPosition"))
                    info.setCornerPosition(item.optString("cornerPosition"));
                if (item.has("cornerCondition")) {
                    JSONArray condition = item.getJSONArray("cornerCondition");
                    if (condition != null && condition.length() > 0) {
                        List<CornerCondition> conditionList = new ArrayList<>();
                        int size = condition.length();
                        for (int index = 0; index < size; index++) {
                            JSONObject conditionItem = condition.getJSONObject(index);
                            conditionList.add(new CornerCondition(
                                    conditionItem.optString("fieldName"),
                                    conditionItem.optString("fieldValue")
                            ));
                        }
                        info.setCornerCondition(conditionList);
                    }
                }

                if (mSuperscriptMap == null) {
                    mSuperscriptMap = new HashMap<>(Constant.BUFFER_SIZE_8);
                }
                mSuperscriptMap.put(info.getCornerId(), info);
                LogUtils.i(TAG, "解析到角标信息 " + info.toString());
            }
            return superscript;
        } catch (Exception e) {
            LogUtils.e(e);
            return null;
        }
    }

    /**
     * 更新本地角标数据
     *
     * @param data
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void updateLocalInfo(Context context, String data) {
        FileWriter fileWriter = null;
        try {
            File file = new File(context.getCacheDir(), CACHE_FILE_NAME);
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

    public synchronized Corner getSuperscriptInfoById(String id) {
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

    @Override
    public void onCornerResult(@NotNull Context context, @Nullable String data) {
        try {
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
            updateLocalInfo(context, data);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @Override
    public void tip(@NotNull Context context, @NotNull String message) {

    }

    @Override
    public void onError(@NotNull Context context, @Nullable String desc) {

    }
}
