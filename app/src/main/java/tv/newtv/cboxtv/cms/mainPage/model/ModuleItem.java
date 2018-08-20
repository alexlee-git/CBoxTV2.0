package tv.newtv.cboxtv.cms.mainPage.model;

import java.util.List;

/**
 * Created by lixin on 2018/2/1.
 */

public class ModuleItem {
    private String blockId;
    private String blockTitle;
    private String blockImg;
    private String haveBlockTitle;
    private String haveContentSubTitle;
    private String contentTitlePosition;
    private String haveContentTitle;
    private String rowNum;
    private String colNum;
    private String BlockType;
    private String layoutCode;
    private List<ProgramInfo> programs;



    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getBlockTitle() {
        return blockTitle;
    }

    public void setBlockTitle(String blockTitle) {
        this.blockTitle = blockTitle;
    }

    public String getBlockImg() {
        return blockImg;
    }

    public void setBlockImg(String blockImg) {
        this.blockImg = blockImg;
    }

    public String getHaveBlockTitle() {
        return haveBlockTitle;
    }

    public void setHaveBlockTitle(String haveBlockTitle) {
        this.haveBlockTitle = haveBlockTitle;
    }

    public String getHaveContentSubTitle() {
        return haveContentSubTitle;
    }

    public void setHaveContentSubTitle(String haveContentSubTitle) {
        this.haveContentSubTitle = haveContentSubTitle;
    }

    public String getContentTitlePosition() {
        return contentTitlePosition;
    }

    public void setContentTitlePosition(String contentTitlePosition) {
        this.contentTitlePosition = contentTitlePosition;
    }

    public String getHaveContentTitle() {
        return haveContentTitle;
    }

    public void setHaveContentTitle(String haveContentTitle) {
        this.haveContentTitle = haveContentTitle;
    }

    public String getRowNum() {
        return rowNum;
    }

    public void setRowNum(String rowNum) {
        this.rowNum = rowNum;
    }

    public String getColNum() {
        return colNum;
    }

    public void setColNum(String colNum) {
        this.colNum = colNum;
    }

    public String getBlockType() {
        return BlockType;
    }

    public void setBlockType(String blockType) {
        BlockType = blockType;
    }

    public String getLayoutCode() {
        return layoutCode;
    }

    public void setLayoutCode(String layoutCode) {
        this.layoutCode = layoutCode;
    }

    public List<ProgramInfo> getDatas() {
        return programs;
    }

    public void setDatas(List<ProgramInfo> datas) {
        programs = datas;
    }

    @Override
    public String toString() {
        return "ModuleItem{" +
                "blockId='" + blockId + '\'' +
                ", blockTitle='" + blockTitle + '\'' +
                ", blockImg='" + blockImg + '\'' +
                ", haveBlockTitle='" + haveBlockTitle + '\'' +
                ", haveContentSubTitle='" + haveContentSubTitle + '\'' +
                ", contentTitlePosition='" + contentTitlePosition + '\'' +
                ", haveContentTitle='" + haveContentTitle + '\'' +
                ", rowNum='" + rowNum + '\'' +
                ", colNum='" + colNum + '\'' +
                ", BlockType='" + BlockType + '\'' +
                ", layoutCode='" + layoutCode + '\'' +
                ", programs=" + programs +
                '}';
    }
}
