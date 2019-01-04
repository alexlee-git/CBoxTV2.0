/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv
 * 创建事件:         10:27
 * 创建人:           weihaichao
 * 创建日期:          2019/1/3
 */
package com.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称:         DanceTv_Android
 * 包名:            com.android
 * 创建事件:         13:55
 * 创建人:           weihaichao
 * 创建日期:          2018/3/20
 */

public class DimenTool {

    private static final String path = "./app/src/main/res/values-%sx%s/dimens.xml";
    private static final int defaultWidth = 1920;
    private static final int defaultHeight = 1080;
    private static List<ScreenSize> SizeList;//屏幕尺寸列表
    private static List<Integer> negativeValueList;//负数值
    private static List<Integer> speicalValueList;//特殊值  超出 1920|1080 的值

    static {
        SizeList = new ArrayList<>();
        negativeValueList = new ArrayList<>();
        speicalValueList = new ArrayList<>();

        negativeValueList.add(5);
        negativeValueList.add(6);
        negativeValueList.add(7);
        negativeValueList.add(8);
        negativeValueList.add(11);
        negativeValueList.add(12);
        negativeValueList.add(13);
        negativeValueList.add(15);
        negativeValueList.add(16);
        negativeValueList.add(18);
        negativeValueList.add(19);
        negativeValueList.add(36);

        speicalValueList.add(2576);

        SizeList.add(new ScreenSize(854, 480));
        SizeList.add(new ScreenSize(1280, 720));
        SizeList.add(new ScreenSize(1366, 768));
        SizeList.add(new ScreenSize(1920, 1024));
        SizeList.add(new ScreenSize(1920, 1080));
        SizeList.add(new ScreenSize(3840, 2160));
        SizeList.add(new ScreenSize(4096, 2160));
    }

    public static void main(String[] args) {
        for (ScreenSize screenSize : SizeList) {
            String make_path = String.format(path, (int) screenSize.Width, (int) screenSize.Height);
            File file = new File(make_path);
            Make(file);
            writeFile(file, makeResources(screenSize.Width, screenSize.Height));
        }
    }

    public static String makeResources(float width, float height) {
        StringBuilder stringBuilder = new StringBuilder("<resources>");
        float scaleW = width / defaultWidth;
        System.out.println("scale width=" + scaleW);
        for (int index = 0; index <= defaultWidth; index++) {
            insertDimenStr(index,scaleW,true,false,stringBuilder);
            if (negativeValueList != null && negativeValueList.contains(index)) {
                insertDimenStr(index,scaleW,true,true,stringBuilder);
            }
        }
        float scaleH = Math.abs(height / defaultHeight);
        System.out.println("scale height=" + scaleH);
        for (int index = 0; index <= defaultHeight; index++) {
            insertDimenStr(index,scaleH,false,false,stringBuilder);
            if (negativeValueList != null && negativeValueList.contains(index)) {
                insertDimenStr(index,scaleH,false,true,stringBuilder);
            }
        }

        if (speicalValueList != null && speicalValueList.size() > 0) {
            for (int index : speicalValueList) {
                insertDimenStr(index,scaleW,true,false,stringBuilder);
                insertDimenStr(index,scaleW,true,true,stringBuilder);
                insertDimenStr(index,scaleH,false,false,stringBuilder);
                insertDimenStr(index,scaleH,false,true,stringBuilder);
            }
        }


        stringBuilder.append("\n\t");
        stringBuilder.append("</resources>");
        return stringBuilder.toString();
    }

    private static void insertDimenStr(int index, float scale, boolean width, boolean negative,
                                       StringBuilder stringBuilder) {
        stringBuilder.append("\n\t");
        stringBuilder.append("<dimen ");
        stringBuilder.append("name=\"");
        if(negative){
            stringBuilder.append("_");
        }
        if(width) {
            stringBuilder.append("width_");
        }else{
            stringBuilder.append("height_");
        }
        stringBuilder.append(index);
        stringBuilder.append("px");
        stringBuilder.append("\">");
        if(negative){
            stringBuilder.append("-");
        }
        stringBuilder.append(index * scale);
        stringBuilder.append("px</dimen>");
    }

    /**
     * 写入方法
     */

    public static void writeFile(File file, String text) {
        PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }

    }

    //自定义检测生成指定文件夹下的指定文件
    public static void Make(File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ScreenSize {
        float Width;
        float Height;

        ScreenSize(float width, float height) {
            Width = width;
            Height = height;
        }
    }


}

