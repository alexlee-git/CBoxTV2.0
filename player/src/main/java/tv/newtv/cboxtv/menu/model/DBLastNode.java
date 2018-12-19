package tv.newtv.cboxtv.menu.model;

import java.util.ArrayList;
import java.util.List;

public class DBLastNode {
    public String id;
    public String _contentuuid;
    public String _content_id;
    public String _title_name;
    public String _contenttype;
    public String is_finish;
    public String real_exclusive;
    public String issue_date;
    public String last_publish_date;
    public String sub_title;
    public String _update_time;
    public String _user_id;
    public String v_image;
    public String h_image;
    public String vip_flag;
    public String alternate_number;


    public static List<LastNode> converLastNode(List<DBLastNode> list){
        List<LastNode> lastNodeList = new ArrayList<>();
        if(list == null || list.size() == 0){
            return lastNodeList;
        }

        for(DBLastNode lastNode : list){
            lastNodeList.add(converLastNode(lastNode));
        }

        return lastNodeList;
    }

    public static LastNode converLastNode(DBLastNode dbLastNode){
        LastNode lastNode = new LastNode();
        lastNode.contentUUID = dbLastNode._contentuuid;
        lastNode.contentId = dbLastNode._content_id;
        lastNode.setTitle(dbLastNode._title_name);
        lastNode.setContentType(dbLastNode._contenttype);
        lastNode.isFinish = dbLastNode.is_finish;
        lastNode.realExclusive = dbLastNode.real_exclusive;
        lastNode.issuedate = dbLastNode.issue_date;
        lastNode.lastPublishDate = dbLastNode.last_publish_date;
        lastNode.subTitle = dbLastNode.sub_title;
        lastNode.vImage = dbLastNode.v_image;
        lastNode.hImage = dbLastNode.h_image;
        lastNode.vipFlag = dbLastNode.vip_flag;
        lastNode.alternateNumber = dbLastNode.alternate_number;
        return lastNode;
    }
}
