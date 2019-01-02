package com.newtv.libs.ad;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ADConfig {

    private String columnId; //一级栏目
    private String secondColumnId; //二级栏目
    private String categoryIds;
    private String seriesID;//内容id
    private String seriesUUID;//节目集uuid
    private String programId;
    private String duration;
    private String videoType;//一级分类
    private String videoClass;//二级分类
    private String vipFlag;
    private String carousel;


    public String getCarousel() {
        return carousel;
    }

    public void setCarousel(String carousel) {
        this.carousel = carousel;
    }

    private List<ColumnListener> listenerList = new ArrayList<>();

    private ADConfig(){}

    public static ADConfig getInstance(){
        return Holder.instance;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getSeriesID() {
        return seriesID;
    }

    public void setSeriesID(String seriesID) {
        setSeriesID(seriesID,true);

    }

    public void setSeriesID(String seriesID,boolean reset){
        this.seriesID = seriesID;
        if(reset){
            columnId = "";
            secondColumnId = "";
            programId = "";
            duration = "";
            videoType = "";
            videoClass = "";
            seriesUUID = "";
            vipFlag = "";
            carousel = "";
        }
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
        parseCategoryIdsCmsV31(categoryIds);
//        parseCategoryIdsCmsV30(categoryIds);
    }

    public String getColumnId() {
        return columnId;
    }

    public int getIntSecondDuration(){
        int result = 0;
        if(!TextUtils.isEmpty(duration)){
            try {
                result = Integer.parseInt(duration);
            }catch (Exception e){}
        }
        return result;
    }

    public int getIntMillisDuration(){
        int result = 0;
        if(!TextUtils.isEmpty(duration)){
            try {
                int i = Integer.parseInt(duration);
                result = i * 1000;
            }catch (Exception e){}
        }
        return result;
    }

    public String getSeriesUUID() {
        return seriesUUID;
    }

    public void setSeriesUUID(String seriesUUID) {
        this.seriesUUID = seriesUUID;
    }

    public String getVipFlag() {
        return vipFlag;
    }

    public void setVipFlag(String vipFlag) {
        this.vipFlag = vipFlag;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getSecondColumnId() {
        return secondColumnId;
    }

    public void setSecondColumnId(String secondColumnId) {
        this.secondColumnId = secondColumnId;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoClass() {
        return videoClass;
    }

    public void setVideoClass(String videoClass) {
        this.videoClass = videoClass;
    }

    private void parseCategoryIdsCmsV31(String categoryIds) {
        if(TextUtils.isEmpty(categoryIds)){
            return ;
        }
        StringBuilder secondColumn = new StringBuilder();

        String[] categoryArr = categoryIds.split("\\|");
        for(String categoryId : categoryArr){
            secondColumn.append(categoryId);
            secondColumn.append(",");
        }
        if(secondColumn.length() > 0){
            secondColumn.delete(secondColumn.length()-1,secondColumn.length());
        }
        setSecondColumnId(secondColumn.toString());
        int size = listenerList.size();
        for(int i=0;i<size;i++){
            listenerList.get(i).receive();
        }
        listenerList.clear();
    }

    private void parseCategoryIdsCmsV30(String categoryIds){
        if(TextUtils.isEmpty(categoryIds)){
            return ;
        }

        StringBuilder column = new StringBuilder();
        StringBuilder secondColumn = new StringBuilder();
        String[] categoryArr = categoryIds.split(",");
        for(String categoryId : categoryArr){
            String[] split = categoryId.split("/");
            if(split.length >= 1){
                column.append(split[0]);
                column.append(",");
            }
            if(split.length >= 2){
                secondColumn.append(split[1]);
                secondColumn.append(",");
            }
        }
        column.delete(column.length()-1,column.length());
        if(secondColumn.length() > 0){
            secondColumn.delete(secondColumn.length()-1,secondColumn.length());
        }
        setColumnId(column.toString());
        setSecondColumnId(secondColumn.toString());

        int size = listenerList.size();
        for(int i=0;i<size;i++){
            listenerList.get(i).receive();
        }
        listenerList.clear();
    }

    public void registerListener(ColumnListener listener) {
        if(listener != null && !listenerList.contains(listener)){
            listenerList.add(listener);
        }
    }

    public void removeListener(ColumnListener listener){
        listenerList.remove(listener);
    }

    public interface ColumnListener{
        void receive();
    }

    private static class Holder{
        private final static ADConfig instance = new ADConfig();
    }

    @Override
    public String toString() {
        return "ADConfig{" +
                "columnId='" + columnId + '\'' +
                ", secondColumnId='" + secondColumnId + '\'' +
                ", categoryIds='" + categoryIds + '\'' +
                ", seriesID='" + seriesID + '\'' +
                ", programId='" + programId + '\'' +
                ", duration='" + duration + '\'' +
                ", listenerList=" + listenerList +
                '}';
    }

    public void reset(){
        columnId = "";
        secondColumnId = "";
        categoryIds = "";
        seriesID = "";
        programId = "";
        duration = "";
        videoType = "";
        videoClass = "";
        seriesUUID = "";
        vipFlag = "";
        carousel = "";
    }
}
