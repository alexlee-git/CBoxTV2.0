package tv.newtv.cboxtv.cms.ad;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ADConfig {

    private String columnId; //一级栏目
    private String secondColumnId; //二级栏目
    private String categoryIds;
    private String seriesID;
    private String programId;
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
        this.seriesID = seriesID;
        columnId = "";
        secondColumnId = "";
    }

    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
        parseCategoryIds(categoryIds);
    }

    public String getColumnId() {
        return columnId;
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

    private void parseCategoryIds(String categoryIds){
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

    public void setListener(ColumnListener listener) {
        listenerList.add(listener);
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
                ", listenerList=" + listenerList +
                '}';
    }
}
