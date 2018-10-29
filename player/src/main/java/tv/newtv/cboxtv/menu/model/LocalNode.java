package tv.newtv.cboxtv.menu.model;

public class LocalNode extends Node{

    @Override
    public boolean isLeaf() {
        if(child != null && child.size() > 0)
            return false;
        return true;
    }
}
