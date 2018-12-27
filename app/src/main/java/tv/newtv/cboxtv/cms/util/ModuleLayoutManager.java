package tv.newtv.cboxtv.cms.util;

import android.text.TextUtils;

import com.newtv.cms.bean.Page;
import com.newtv.libs.Constant;
import com.newtv.libs.util.DeviceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tv.newtv.cboxtv.BuildConfig;
import tv.newtv.cboxtv.R;

/**
 * Created by lixin on 2018/1/31.
 */
//布局模块管理
public class ModuleLayoutManager {

    private static ModuleLayoutManager mInstance;
    private boolean isAdaptationVesion = false;
    private Map<String, List<String>> mModuleDataBase; //
    private Map<Integer, Integer> mViewTypeAndLayoutResFileMap;// viewType<----映射---->布局文件
    private Map<String, Integer> mWidgetCounter; // 组件<----映射---->组件内推荐位个数

    private String[] layoutIds = new String[]{"layout_001", "layout_002", "layout_003",
            "layout_004", "layout_005",
            "layout_006", "layout_007", "layout_008", "layout_009", "layout_010", "layout_011",
            "layout_012", "layout_013", "layout_014", "layout_015", "layout_016", "layout_017",
            "layout_018", "layout_019", "layout_020", "layout_021", "layout_022", "layout_023",
            "layout_024", "layout_025", "layout_026", "layout_027", "layout_028", "layout_029",
            "layout_030", "layout_031", "layout_032"};
    private int[] layoutResIds = new int[]{R.layout.layout_module_1, R.layout.layout_module_2,
            R.layout.layout_module_3, R.layout.layout_module_4, R.layout.layout_module_5, R
            .layout.layout_module_6,
            R.layout.layout_module_7, R.layout.layout_module_8, R.layout.layout_module_9, R
            .layout.layout_module_10,
            R.layout.layout_module_11, R.layout.layout_module_12, R.layout.layout_module_13, R
            .layout.layout_module_14,
            R.layout.layout_module_15, R.layout.layout_module_16, R.layout.layout_module_17, R
            .layout.layout_module_18,
            R.layout.layout_module_19, R.layout.layout_module_20, R.layout.layout_module_21, R
            .layout.layout_module_22,
            R.layout.layout_module_23, R.layout.layout_module_24, R.layout.layout_module_25, R
            .layout.layout_module_26,
            R.layout.layout_module_27_other, R.layout.layout_module_28_other, R.layout
            .layout_module_29,
            R.layout.layout_module_30, R.layout.layout_module_31, R.layout.layout_module_32};

    private int[] layoutResIds_V2 = new int[]{R.layout.layout_module_1_v2, R.layout.layout_module_2_v2,
            R.layout.layout_module_3_v2, R.layout.layout_module_4_v2, R.layout.layout_module_5, R
            .layout.layout_module_6_v2,
            R.layout.layout_module_7_v2, R.layout.layout_module_8_v2, R.layout.layout_module_9_v2, R
            .layout.layout_module_10_v2,
            R.layout.layout_module_11_v2, R.layout.layout_module_12_v2, R.layout.layout_module_13_v2, R
            .layout.layout_module_14_v2,
            R.layout.layout_module_15_v2, R.layout.layout_module_16_v2, R.layout.layout_module_17_v2, R
            .layout.layout_module_18_v2,
            R.layout.layout_module_19_v2, R.layout.layout_module_20_v2, R.layout.layout_module_21_v2, R
            .layout.layout_module_22_v2,
            R.layout.layout_module_23_v2, R.layout.layout_module_24_v2, R.layout.layout_module_25_v2, R
            .layout.layout_module_26_v2,
            R.layout.layout_module_27_other_v2, R.layout.layout_module_28_other_v2, R.layout
            .layout_module_29_v2,
            R.layout.layout_module_30, R.layout.layout_module_31_v2, R.layout.layout_module_32};

    private String[] rightEdgeIds = new String[]{"cell_001_1", "cell_002_2", "cell_003_3",
            "cell_004_4", "cell_005_6", "cell_006_8", "cell_007_8", "cell_008_6",
            "cell_009_3", "cell_009_5", "cell_010_4", "cell_010_7", "cell_011_4",
            "cell_012_2", "cell_012_5", "cell_013_2", "cell_013_6", "cell_014_4",
            "cell_014_6", "cell_015_3", "cell_015_7", "cell_016_3", "cell_016_7",
            "cell_017_3", "cell_017_5", "cell_017_9", "cell_018_3", "cell_018_5",
            "cell_019_2", "cell_019_6", "cell_020_2", "cell_020_6", "cell_020_14",
            "cell_021_3", "cell_021_9", "cell_022_3", "cell_022_5", "cell_022_10",
            "cell_023_2", "cell_024_8", "cell_025_6", "cell_026_3", "cell_027_3",
            "cell_027_7", "cell_028_2", "cell_029_3", "cell_029_5", "cell_029_11"};

    private int[] subViewSizes = new int[]{1, 2, 3, 4, 6, 8, 8, 6, 5, 7, 4, 5, 6, 6, 7, 7, 9, 5,
            6, 14, 9, 10, 2, 8, 6, 3, 7, 2, 11, 12, 12, 6};
    private Map<String, List<String>> mFirstLineModules; // 所有组件的第一行的推荐位列表, 因为这些推荐位需要监听上按键
    private Map<String, String> mRightEdgeCells;


    private ModuleLayoutManager() {
        if (BuildConfig.FLAVOR.equals(DeviceUtil.LETV)|| BuildConfig.FLAVOR.equals(DeviceUtil.XIONG_MAO)
                ||BuildConfig.FLAVOR.equals(DeviceUtil.CBOXTEST)){
            isAdaptationVesion = true;
        }
        if (mModuleDataBase == null) {
            mModuleDataBase = new ConcurrentHashMap<String, List<String>>(Constant.BUFFER_SIZE_64) {
            };
        }

        if (mViewTypeAndLayoutResFileMap == null) {
            mViewTypeAndLayoutResFileMap = new ConcurrentHashMap<>(Constant.BUFFER_SIZE_64);
        }

        if (mWidgetCounter == null) {
            mWidgetCounter = new ConcurrentHashMap<>(Constant.BUFFER_SIZE_64);
        }

        if (mFirstLineModules == null) {
            mFirstLineModules = new HashMap<>(Constant.BUFFER_SIZE_64);
        }

        List<String> firstLineCellCodes;
        for (String ids : layoutIds) {
            switch (ids) {
                case "layout_001":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_001_1");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_002":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_002_1");
                    firstLineCellCodes.add("cell_002_2");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_003":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_003_1");
                    firstLineCellCodes.add("cell_003_2");
                    firstLineCellCodes.add("cell_003_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_004":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_004_1");
                    firstLineCellCodes.add("cell_004_2");
                    firstLineCellCodes.add("cell_004_3");
                    firstLineCellCodes.add("cell_004_4");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_005":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_005_1");
                    firstLineCellCodes.add("cell_005_2");
                    firstLineCellCodes.add("cell_005_3");
                    firstLineCellCodes.add("cell_005_4");
                    firstLineCellCodes.add("cell_005_5");
                    firstLineCellCodes.add("cell_005_6");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_006":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_006_1");
                    firstLineCellCodes.add("cell_006_2");
                    firstLineCellCodes.add("cell_006_3");
                    firstLineCellCodes.add("cell_006_4");
                    firstLineCellCodes.add("cell_006_5");
                    firstLineCellCodes.add("cell_006_6");
                    firstLineCellCodes.add("cell_006_7");
                    firstLineCellCodes.add("cell_006_8");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_007":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_007_1");
                    firstLineCellCodes.add("cell_007_2");
                    firstLineCellCodes.add("cell_007_3");
                    firstLineCellCodes.add("cell_007_4");
                    firstLineCellCodes.add("cell_007_5");
                    firstLineCellCodes.add("cell_007_6");
                    firstLineCellCodes.add("cell_007_7");
                    firstLineCellCodes.add("cell_007_8");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_008":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_008_1");
                    firstLineCellCodes.add("cell_008_2");
                    firstLineCellCodes.add("cell_008_3");
                    firstLineCellCodes.add("cell_008_4");
                    firstLineCellCodes.add("cell_008_5");
                    firstLineCellCodes.add("cell_008_6");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_009":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_009_1");
                    firstLineCellCodes.add("cell_009_2");
                    firstLineCellCodes.add("cell_009_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_010":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_010_1");
                    firstLineCellCodes.add("cell_010_2");
                    firstLineCellCodes.add("cell_010_3");
                    firstLineCellCodes.add("cell_010_4");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_011":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_011_1");
                    firstLineCellCodes.add("cell_011_2");
                    firstLineCellCodes.add("cell_011_3");
                    firstLineCellCodes.add("cell_011_4");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_012":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_012_1");
                    firstLineCellCodes.add("cell_012_2");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_013":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_013_1");
                    firstLineCellCodes.add("cell_013_2");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_014":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_014_1");
                    firstLineCellCodes.add("cell_014_2");
                    firstLineCellCodes.add("cell_014_3");
                    firstLineCellCodes.add("cell_014_4");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_015":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_015_1");
                    firstLineCellCodes.add("cell_015_2");
                    firstLineCellCodes.add("cell_015_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_016":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_016_1");
                    firstLineCellCodes.add("cell_016_2");
                    firstLineCellCodes.add("cell_016_3");
                    firstLineCellCodes.add("cell_016_4");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_017":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_017_1");
                    firstLineCellCodes.add("cell_017_2");
                    firstLineCellCodes.add("cell_017_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_018":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_018_1");
                    firstLineCellCodes.add("cell_018_2");
                    firstLineCellCodes.add("cell_018_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_019":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_019_1");
                    firstLineCellCodes.add("cell_019_2");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_020":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_020_1");
                    firstLineCellCodes.add("cell_020_2");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_021":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_021_1");
                    firstLineCellCodes.add("cell_021_2");
                    firstLineCellCodes.add("cell_021_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_022":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_022_1");
                    firstLineCellCodes.add("cell_022_2");
                    firstLineCellCodes.add("cell_022_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_023":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_023_1");
                    firstLineCellCodes.add("cell_023_2");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_024":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_024_1");
                    firstLineCellCodes.add("cell_024_2");
                    firstLineCellCodes.add("cell_024_3");
                    firstLineCellCodes.add("cell_024_4");
                    firstLineCellCodes.add("cell_024_5");
                    firstLineCellCodes.add("cell_024_6");
                    firstLineCellCodes.add("cell_024_7");
                    firstLineCellCodes.add("cell_024_8");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_025":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_025_1");
                    firstLineCellCodes.add("cell_025_2");
                    firstLineCellCodes.add("cell_025_3");
                    firstLineCellCodes.add("cell_025_4");
                    firstLineCellCodes.add("cell_025_5");
                    firstLineCellCodes.add("cell_025_6");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_026":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_026_1");
                    firstLineCellCodes.add("cell_026_2");
                    firstLineCellCodes.add("cell_026_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_027":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_027_1");
                    firstLineCellCodes.add("cell_027_2");
                    firstLineCellCodes.add("cell_027_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_028":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_028_1");
                    firstLineCellCodes.add("cell_028_2");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_029":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    firstLineCellCodes.add("cell_029_1");
                    firstLineCellCodes.add("cell_029_2");
                    firstLineCellCodes.add("cell_029_3");
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_030":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                case "layout_032":
                    firstLineCellCodes = new ArrayList<>(Constant.BUFFER_SIZE_4);
                    mFirstLineModules.put(ids, firstLineCellCodes);
                    break;
                default:
                    break;
            }
        }

        if (mRightEdgeCells == null) {
            mRightEdgeCells = new HashMap<>(Constant.BUFFER_SIZE_32);
        }
        for (String string : rightEdgeIds) {
            mRightEdgeCells.put(string, string);
        }

        for (int i = 0; i < layoutIds.length; ++i) {
            if (isAdaptationVesion) {
                registerModuleById(layoutIds[i], layoutResIds_V2[i], Integer.parseInt(layoutIds[i]
                        .substring(layoutIds[i].indexOf("_") + 1)), subViewSizes[i]);
            }else {
                registerModuleById(layoutIds[i], layoutResIds[i], Integer.parseInt(layoutIds[i]
                        .substring(layoutIds[i].indexOf("_") + 1)), subViewSizes[i]);
            }
        }
    }

    public static ModuleLayoutManager getInstance() {
        if (mInstance == null) {
            synchronized (ModuleLayoutManager.class) {
                if (mInstance == null) {
                    mInstance = new ModuleLayoutManager();
                }
            }
        }
        return mInstance;
    }

    public void filterLayoutDatas(List<Page> pages) {
        Iterator<Page> iterator = pages.iterator();
        while (iterator.hasNext()) {
            Page page = iterator.next();
            if (!ModuleLayoutManager.getInstance().supportLayout(page.getLayoutCode())) {
                iterator.remove();
            }
        }
    }

    /**
     * 是否支持该布局
     *
     * @param layoutCode 布局代码
     * @return
     */
    private boolean supportLayout(String layoutCode) {
        if (TextUtils.equals(layoutCode, "layout_032") && !Constant.canUseAlternate) {
            return false;
        }
        if("search".equals(layoutCode)) return true;
        return Arrays.asList(layoutIds).contains(layoutCode);
    }

    /**
     * 建立组件id<----->布局文件之间的映射关系
     */
    public void registerModuleById(String layoutId, int layoutResId, int viewType, int
            subWidgetSize) {
        mViewTypeAndLayoutResFileMap.put(viewType, layoutResId);
        mWidgetCounter.put(layoutId, subWidgetSize);
    }

    /**
     * 根据viewType获取对应的资源文件
     *
     * @param viewType
     * @return
     */
    public int getLayoutResFileByViewType(int viewType) {
        if (mViewTypeAndLayoutResFileMap != null) {
            Integer result = mViewTypeAndLayoutResFileMap.get(viewType);
            if (result == null) {
                return -1;
            } else {
                return mViewTypeAndLayoutResFileMap.get(viewType);
            }
        }
        return -1;
    }

    /**
     * 根据组件id,返回该组件的推荐位个数
     *
     * @param layoutId
     * @return
     */
    public int getSubWidgetSizeById(String layoutId) {
        return mWidgetCounter.get(layoutId);
    }

    public List<String> getWidgetLayoutList(String layoutId) {
        List<String> layoutList = new ArrayList<>();
        int count = mWidgetCounter.get(layoutId);
        int pos = layoutId.indexOf("_");
        if (pos >= 0) {
            String code = layoutId.substring(pos + 1);
            for (int index = 1; index <= count; index++) {
                layoutList.add(String.format(Locale.getDefault(), "cell_%s_%d", code, index));
            }
        }
        return layoutList;
    }

    public boolean isNeedInterceptKeyEvent(String layoutId, String cellCode) {
        if (mFirstLineModules == null) {
            return false;
        }

        List<String> candidates = mFirstLineModules.get(layoutId);
        if (candidates == null || candidates.size() <= 0) {
            return false;
        }

        for (String id : candidates) {
            if (TextUtils.equals(id, cellCode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNeedInterceptRightKeyEvent(String cellCode) {
        if (TextUtils.isEmpty(cellCode)) {
            return false;
        }

        if (mRightEdgeCells == null || mRightEdgeCells.size() == 0) {
            return false;
        }

        return (mRightEdgeCells.get(cellCode) != null);
    }

    public void unit() {
        if (mFirstLineModules != null) {
            mFirstLineModules.clear();
            mFirstLineModules = null;
        }

        if (mModuleDataBase != null) {
            mModuleDataBase.clear();
            mModuleDataBase = null;
        }

        if (mRightEdgeCells != null) {
            mRightEdgeCells.clear();
            mRightEdgeCells = null;
        }

        if (mViewTypeAndLayoutResFileMap != null) {
            mViewTypeAndLayoutResFileMap.clear();
            mViewTypeAndLayoutResFileMap = null;
        }

        if (mWidgetCounter != null) {
            mWidgetCounter.clear();
            mWidgetCounter = null;
        }

        mInstance = null;
    }
}
