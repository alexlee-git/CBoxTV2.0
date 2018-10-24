package tv.newtv.cboxtv.menu.model;

public class LastNode extends Node {
    public String contentID;

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public String getId() {
        return contentID;
    }
}
