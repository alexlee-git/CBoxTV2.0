package tv.newtv.cboxtv.cms.mainPage.model;

/**
 * 项目名称： NewTVLauncher
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/2/2 0002 10:47
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class SearchConditions {
    private String searchType;
    private String category;
    private String type;
    private String year;
    private String area;
    private String classType;

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    @Override
    public String toString() {
        return "SearchConditions{" +
                "searchType='" + searchType + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", year='" + year + '\'' +
                ", area='" + area + '\'' +
                ", classType='" + classType + '\'' +
                '}';
    }
}
