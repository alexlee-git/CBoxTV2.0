package tv.newtv.cboxtv.cms.util;

import java.util.List;

/**
 * 分页工具
 * <p>
 * Created by gaoleichao on 2018/4/3.
 */

public class PageHelper<T> {
    private List<T> allData; // 所有数据
    private int perPage = 8; // 每页条目
    private int currentPage = 1;// 当前页
    private int pageNum = 1; // 总页码
    private List<T> childData;// 子数据
    private int allNum;// 总共条目

    public PageHelper(List<T> datas, int perPage) {
        this.allData = datas;
        if (perPage > 0)
            this.perPage = perPage;
        // 如果数据大于10条
        allNum = allData.size();
        if (allData.size() > perPage) {
            childData = allData.subList(0, perPage - 1);
        } else {
            this.perPage = allNum;
        }
        // 如果总数能除断perPage，页数就是余数，否则+1
        pageNum = allNum % perPage == 0 ? (allNum / perPage) : (allNum / perPage + 1);
    }

    public int getCount() {
        return this.allNum;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public int getPerPage() {
        return this.perPage;
    }

    public void gotoPage(int n) { // 页面跳转
        currentPage = n > pageNum ? pageNum : (n < 1 ? 1 : n);
    }

    public boolean hasNextPage() {// 是否有下一页
        return currentPage < pageNum;
    }

    public boolean hasPrePage() {// 是否有前一页
        return currentPage > 1;
    }

    public void headPage() {// 第一页
        currentPage = 1;
    }

    public void lastPage() {// 最后一页
        currentPage = pageNum;
    }

    public void nextPage() {// 下一页
        currentPage = hasNextPage() ? currentPage + 1 : pageNum;
    }

    public void prePage() {// 前一页
        currentPage = hasPrePage() ? currentPage - 1 : 1;
    }

    public void setPerPage(int perPage) {// 设置上一页面
        this.perPage = perPage;
    }

    /**
     * 获得当前数据
     *
     * @return
     */
    public List<T> currentList() {
        if (currentPage == 1) {
            if (perPage >=allNum){
                childData = allData.subList(0, allNum);
            }else {
                childData = allData.subList(0, perPage);
            }
        } else if (currentPage == pageNum) {
            childData = allData.subList(perPage * (pageNum - 1), allNum);
        } else {
            childData = allData.subList(perPage * (currentPage - 1), (perPage * currentPage));
        }
        return childData;
    }


    public List<T> allList() {
        return allData;
    }

    public String getPageText(int mPage) {
        String text;
        if (mPage == 1) {
            text = "1-" + perPage;
        } else if (mPage == pageNum) {
            text = perPage * (pageNum - 1) + 1 + "-" + allNum;
        } else {
            text = (perPage * (mPage - 1) + 1 + "-" + perPage * mPage);
        }

        return text;
    }

    public int getCurrentPosition(int position) {
        int size = currentPage - 1;
        return size * perPage + position;
    }

    public int getCurrentPage(int position) {
        int size = position + 1;
        return size % perPage == 0 ? (size / perPage) : (size / perPage + 1);
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
