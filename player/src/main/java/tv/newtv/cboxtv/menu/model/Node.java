package tv.newtv.cboxtv.menu.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TCP on 2018/4/18.
 */

public class Node {
    private String id;
    private String title;
    private String pid;
    private String img;
    /**
     * 类似于这样OPEN_TV OPEN_PS
     */
    private String actionType;
    private String actionUri;
    /**
     * 类似于这样TV PS
     */
    private String contentType;


    private Node parent;
    private List<Node> child = new ArrayList<>();
    private List<Program> programs = new ArrayList<>();
    private LastMenuBean lastMenuBean;
    private boolean request = false;

    public boolean isLeaf(){
//        if(child != null && child.size() > 0)
            return false;
//        return true;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isRequest() {
        return request;
    }

    public void setRequest(boolean request) {
        this.request = request;
    }

    public LastMenuBean getLastMenuBean() {
        return lastMenuBean;
    }

    public void setLastMenuBean(LastMenuBean lastMenuBean) {
        this.lastMenuBean = lastMenuBean;
    }

    public int getLevel(){
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChild() {
        if(child == null){
            child = new ArrayList<>();
        }
        return child;
    }

    public void setChild(List<Node> child) {
        this.child = child;
    }

    public void addChild(List<LastNode> child){
        for(LastNode lastNode : child){
            lastNode.setParent(this);
            this.child.add(lastNode);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionUri() {
        return actionUri;
    }

    public void setActionUri(String actionUri) {
        this.actionUri = actionUri;
    }

    public List<Node> getNodes(){
        List<Node> result = new ArrayList<>();
        result.add(this);
        for(Node node : child){
            result.addAll(node.getNodes());
        }
        return result;
    }

    public Node searchNode(String id){
        Node result = null;
        if(TextUtils.equals(getId(),id)){
            return this;
        }
        for(Node node : child){
            result = node.searchNode(id);
            if(result != null){
                return result;
            }
        }
        return null;
    }

    public void initParent(){
        for(Node node : child){
            node.initParent();
            node.setParent(this);
            node.setPid(getId());
        }
    }
}
