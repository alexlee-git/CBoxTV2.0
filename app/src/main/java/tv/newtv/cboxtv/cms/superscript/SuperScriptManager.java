package tv.newtv.cboxtv.cms.superscript;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.cms.bean.Corner;
import com.newtv.cms.bean.CornerCondition;
import com.newtv.cms.bean.ModelResult;
import com.newtv.cms.bean.Program;
import com.newtv.cms.bean.SubContent;
import com.newtv.cms.contract.CornerContract;
import com.newtv.libs.Constant;
import com.newtv.libs.util.DisplayUtils;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.views.custom.RecycleImageView;

/**
 * Created by lixin on 2018/3/9.
 * <p>
 * 角标数据存放地址:/data/data/{packagename}/cache/super.json
 */

public class SuperScriptManager implements CornerContract.View {

    public static final String BLOCK_CORNER_LEFT_TOP = "CORNER_LEFT_TOP";
    public static final String BLOCK_CORNER_LEFT_BOTTOM = "CORNER_LEFT_BOTTOM";
    public static final String BLOCK_CORNER_RIGHT_TOP = "CORNER_RIGHT_TOP";
    public static final String BLOCK_VIP_CORNER_RIGHT_TOP = "CORNER_VIP_RIGHT_TOP";
    public static final String BLOCK_CORNER_RIGHT_BOTTOM = "CORNER_RIGHT_BOTTOM";
    private volatile static SuperScriptManager mInstance;
    private final String CACHE_FILE_NAME = "super.json";
    private final String TAG = "superscript";
    private int CORNER_WIDTH = -1;
    private int CORNER_HEIGHT = -1;
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

    public void processVipSuperScript(Context context, SubContent info, final String layoutCode,
                                      final int posterIndex, final ViewGroup parent) {
        if (info == null || parent == null) {
            return;
        }
        if (!TextUtils.isEmpty(info.getVipFlag()) && "1|3|4".contains(info.getVipFlag())) {
            Corner corner = new Corner();
            corner.setCornerImg("vip");
            corner.setCornerPosition("2");
            addVipSuperscript(context, corner, parent, posterIndex);
        } else {
            RecycleImageView imageView = parent.findViewWithTag(BLOCK_VIP_CORNER_RIGHT_TOP);
            if (imageView != null) {
                parent.removeView(imageView);
            }
        }

    }

    private void addVipSuperscript(Context context, Corner corner, ViewGroup parent, int
            posterIndex) {
        RecycleImageView imageView = parent.findViewWithTag(BLOCK_VIP_CORNER_RIGHT_TOP);
        if (imageView == null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(CORNER_WIDTH, CORNER_HEIGHT);
            lp.rightMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_WIDTH);
            lp.topMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_HEIGHT);
            lp.gravity = Gravity.RIGHT | Gravity.END;
            imageView = new RecycleImageView(context);
            imageView.setTag(BLOCK_VIP_CORNER_RIGHT_TOP);
            imageView.setLayoutParams(lp);
            parent.addView(imageView, posterIndex, lp);
        }
        showCorner(corner, imageView);
    }

    @SuppressLint("CheckResult")
    public void processSuperscript(Context context, final String layoutCode, final int posterIndex,
                                   final Object info, final ViewGroup parent) {
        if (info == null || parent == null) {
            return;
        }
        if (CORNER_WIDTH == -1) {
            CORNER_WIDTH = context.getResources().getDimensionPixelSize(R.dimen.width_75px);
        }
        if (CORNER_HEIGHT == -1) {
            CORNER_HEIGHT = context.getResources().getDimensionPixelSize(R.dimen.height_30px);
        }
        Observable.create(new ObservableOnSubscribe<List<Corner>>() {
            @Override
            public void subscribe(ObservableEmitter<List<Corner>> e) throws Exception {
                List<Corner> cornerList = SuperScriptManager.getInstance().findSuitCorner(info);
                e.onNext(cornerList);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Corner>>() {
                    @Override
                    public void accept(List<Corner> cornerList) throws Exception {
                        List<String> corners = new ArrayList<>();
                        if (cornerList != null && cornerList.size() > 0) {
                            for (Corner corner : cornerList) {
                                if (Corner.LEFT_TOP.equals(corner.getCornerPosition())) {
                                    addLeftTopSuperscript(context, corner, parent, posterIndex);
                                    corners.add(Corner.LEFT_TOP);
                                } else if (Corner.LEFT_BOTTOM.equals(corner.getCornerPosition())) {
                                    addLeftBottomSuperscript(context, layoutCode, corner, parent,
                                            posterIndex);
                                    corners.add(Corner.LEFT_BOTTOM);
                                } else if (Corner.RIGHT_TOP.equals(corner.getCornerPosition())) {
                                    addRightTopSuperscript(context, corner, parent, posterIndex);
                                    corners.add(Corner.RIGHT_TOP);
                                } else if (Corner.RIGHT_BOTTOM.equals(corner.getCornerPosition())) {
                                    addRightBottomSuperscript(context, layoutCode, corner, parent,
                                            posterIndex);
                                    corners.add(Corner.RIGHT_BOTTOM);
                                }
                            }
                        }
                        if (corners.indexOf(Corner.LEFT_TOP) == -1) {
                            ImageView leftTop = parent.findViewWithTag(BLOCK_CORNER_LEFT_TOP);
                            if (leftTop != null) {
                                parent.removeView(leftTop);
                            }
                        }
                        if (corners.indexOf(Corner.LEFT_BOTTOM) == -1) {
                            ImageView leftBottom = parent.findViewWithTag(BLOCK_CORNER_LEFT_BOTTOM);
                            if (leftBottom != null) {
                                parent.removeView(leftBottom);
                            }
                        }
                        if (corners.indexOf(Corner.RIGHT_TOP) == -1) {
                            ImageView rightTop = parent.findViewWithTag(BLOCK_CORNER_RIGHT_TOP);
                            if (rightTop != null) {
                                parent.removeView(rightTop);
                            }
                        }
                        if (corners.indexOf(Corner.RIGHT_BOTTOM) == -1) {
                            ImageView rightBottom = parent.findViewWithTag
                                    (BLOCK_CORNER_RIGHT_BOTTOM);
                            if (rightBottom != null) {
                                parent.removeView(rightBottom);
                            }
                        }

                        if (TextUtils.equals(layoutCode, "layout_008")) {
                            if (info instanceof Program) {
                                addRecentMsgText(context, ((Program) info).getRecentMsg(),
                                        parent, corners.contains(Corner.LEFT_BOTTOM));
                                addGradeMsgText(context, ((Program) info).getGrade(), parent,
                                        corners.contains(Corner.RIGHT_BOTTOM));
                            } else if (info instanceof SubContent) {
                                if (!TextUtils.isEmpty(((SubContent) info).getRecentNum())) {
                                    addRecentMsgText(context, String.format("更新至%s集", (
                                            (SubContent) info).getRecentNum()), parent, corners
                                            .contains(Corner.LEFT_BOTTOM));
                                } else {
                                    removeRecentMsg(parent);
                                }
                                if (!TextUtils.isEmpty(((SubContent) info).getGrade())) {
                                    addGradeMsgText(context, ((SubContent) info).getGrade(),
                                            parent, corners.contains(Corner.RIGHT_BOTTOM));
                                } else {
                                    removeGradeMsg(parent);
                                }
                            }
                        }
                    }
                });
    }


    private void removeRecentMsg(ViewGroup parent) {
        TextView recentText = parent.findViewWithTag("TEXT_RECENT_MSG");
        if (recentText != null) {
            parent.removeView(recentText);
        }
    }

    /**
     * 添加更新到多少集的角标
     *
     * @param context
     * @param message
     * @param parent
     */
    private void addRecentMsgText(Context context, String message, ViewGroup parent, boolean
            containOther) {
        if (containOther) return;
        TextView recentText = parent.findViewWithTag("TEXT_RECENT_MSG");
        if (!TextUtils.isEmpty(message) && !TextUtils.equals(message,"null")) {
            if (recentText == null) {
                recentText = new TextView(context);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                        .WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.LEFT | Gravity.START | Gravity.BOTTOM;
                int leftMargin = context.getResources().getDimensionPixelSize(R.dimen.width_12px);
                int bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.width_11px);
                int fontSize = context.getResources().getDimensionPixelSize(R.dimen.width_11px);
                lp.leftMargin = leftMargin;
                lp.bottomMargin = bottomMargin;
                recentText.setTextSize(fontSize);
                recentText.setTextColor(Color.WHITE);
                recentText.setLayoutParams(lp);
                recentText.setBackground(context.getResources().getDrawable(R.drawable
                        .update_item_black_bg));
                recentText.setTag("TEXT_RECENT_MSG");
                parent.addView(recentText, lp);
            }

            recentText.setVisibility(View.VISIBLE);
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String value = matcher.group(0);
                if (!TextUtils.isEmpty(value)) {
                    if (!TextUtils.equals("0", value)) {
                        message = message.replace(value, String.format("<font " +
                                "color='#955D06'>%s</font>", value));
                        CharSequence charSequence = Html.fromHtml(message);
                        recentText.setText(charSequence);
                    } else {
                        removeRecentMsg(parent);
                    }
                    return;
                }
                recentText.setText(message);
            } else {
                recentText.setText(message);
            }
        } else {
            removeRecentMsg(parent);
        }
    }

    private void removeGradeMsg(ViewGroup parent) {
        TextView recentText = parent.findViewWithTag("TEXT_GRADE_MSG");
        if (recentText != null) {
            parent.removeView(recentText);
        }
    }

    /**
     * 添加影片打分角标
     *
     * @param context
     * @param message
     * @param parent
     */
    private void addGradeMsgText(Context context, String message, ViewGroup parent, boolean
            containOther) {
        if (containOther) return;
        TextView gradeText = parent.findViewWithTag("TEXT_GRADE_MSG");

        if (!TextUtils.isEmpty(message)
                && !TextUtils.equals(message, "null")
                && !TextUtils.equals(message, "0.0")
                && !TextUtils.equals(message, "0")) {
            if (gradeText == null) {
                gradeText = new TextView(context);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                        .WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.RIGHT | Gravity.END | Gravity.BOTTOM;
                int rightMargin = context.getResources().getDimensionPixelSize(R.dimen.width_12px);
                int bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.width_11px);
                int fontSize = context.getResources().getDimensionPixelSize(R.dimen.width_11px);
                lp.rightMargin = rightMargin;
                lp.bottomMargin = bottomMargin;
                gradeText.setTextSize(fontSize);
                gradeText.setTextColor(context.getResources().getColor(R.color.color_62c0eb));
                gradeText.setLayoutParams(lp);
                gradeText.setBackground(context.getResources().getDrawable(R.drawable
                        .update_item_black_bg));
                gradeText.setTag("TEXT_GRADE_MSG");
                parent.addView(gradeText, lp);
            }

            gradeText.setVisibility(View.VISIBLE);
            gradeText.setText(message);
//            message = String.format("<font " +
//                    "color='#ffffff'>%s</font>", message);
//            CharSequence charSequence = Html.fromHtml(message);
//            gradeText.setText(charSequence);
        } else {
            removeGradeMsg(parent);
        }

    }

    private void addLeftTopSuperscript(Context context, Corner corner, ViewGroup parent, int
            postIndex) {
        RecycleImageView imageView = parent.findViewWithTag(BLOCK_CORNER_LEFT_TOP);
        if (imageView == null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(CORNER_WIDTH, CORNER_HEIGHT);
            lp.leftMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_WIDTH);
            lp.topMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_HEIGHT);

            imageView = new RecycleImageView(context);
            imageView.setLayoutParams(lp);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setTag(BLOCK_CORNER_LEFT_TOP);
            parent.addView(imageView, postIndex, lp);
        }
        showCorner(corner, imageView);
    }

    private void addLeftBottomSuperscript(Context context, String layoutCode, Corner corner,
                                          ViewGroup parent, int posterIndex) {

        TextView recentText = parent.findViewWithTag("TEXT_RECENT_MSG");
        if (recentText != null) {
            recentText.setText("");
            recentText.setVisibility(View.GONE);
        }

        RecycleImageView imageView = parent.findViewWithTag(BLOCK_CORNER_LEFT_BOTTOM);
        if (imageView == null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(CORNER_WIDTH, CORNER_HEIGHT);
            lp.leftMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_WIDTH);
            if (TextUtils.equals(layoutCode, "layout_005")) {
                lp.bottomMargin = DisplayUtils.translate(101, DisplayUtils.SCALE_TYPE_HEIGHT);
            } else {
                lp.bottomMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_HEIGHT);
            }

            lp.gravity = Gravity.BOTTOM;
            imageView = new RecycleImageView(context);
            imageView.setTag(BLOCK_CORNER_LEFT_BOTTOM);
            imageView.setLayoutParams(lp);
            parent.addView(imageView, posterIndex, lp);
        }

        showCorner(corner, imageView);
    }

    private void addRightTopSuperscript(Context context, Corner corner, ViewGroup parent, int
            posterIndex) {
        RecycleImageView vip = parent.findViewWithTag(BLOCK_VIP_CORNER_RIGHT_TOP);
        if (vip != null) {
            return;
        }

        RecycleImageView imageView = parent.findViewWithTag(BLOCK_CORNER_RIGHT_TOP);
        if (imageView == null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(CORNER_WIDTH, CORNER_HEIGHT);
            lp.rightMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_WIDTH);
            lp.topMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_HEIGHT);
            lp.gravity = Gravity.RIGHT | Gravity.END;
            imageView = new RecycleImageView(context);
            imageView.setTag(BLOCK_CORNER_RIGHT_TOP);
            imageView.setLayoutParams(lp);
            parent.addView(imageView, posterIndex, lp);
        }
        showCorner(corner, imageView);
    }

    private void addRightBottomSuperscript(Context context, String layoutCode, Corner corner,
                                           ViewGroup parent,
                                           int posterIndex) {

        TextView gradeText = parent.findViewWithTag("TEXT_GRADE_MSG");
        if (gradeText != null) {
            gradeText.setText("");
            gradeText.setVisibility(View.GONE);
        }

        RecycleImageView imageView = parent.findViewWithTag(BLOCK_CORNER_RIGHT_BOTTOM);
        if (imageView == null) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(CORNER_WIDTH, CORNER_HEIGHT);
            if (TextUtils.equals(layoutCode, "layout_005")) {
                lp.bottomMargin = DisplayUtils.translate(101, DisplayUtils.SCALE_TYPE_HEIGHT);
            } else {
                lp.bottomMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_HEIGHT);
            }
            lp.rightMargin = DisplayUtils.translate(12, DisplayUtils.SCALE_TYPE_WIDTH);
            lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            imageView = new RecycleImageView(context);
            imageView.setTag(BLOCK_CORNER_RIGHT_BOTTOM);
            imageView.setLayoutParams(lp);
            parent.addView(imageView, posterIndex, lp);
        }

        // 加载角标x
        showCorner(corner, imageView);
    }

    private void showCorner(Corner corner, RecycleImageView target) {
        String superUrl = corner.getCornerImg();
        if (!TextUtils.isEmpty(superUrl)) {
            if ("2".equals(corner.getCornerPosition()) && TextUtils.equals("vip", corner
                    .getCornerImg())) {
                target.load(R.drawable.vip);
                return;
            }
            target.hasCorner(false).useResize(true).load(superUrl);
        }
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
                    if (!TextUtils.isEmpty(item.optString("cornerCondition"))) {
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
    public void onError(@NotNull Context context, @NotNull String code, @Nullable String desc) {

    }
}
