package tv.newtv.cboxtv.menu.model;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TCP on 2018/4/29.
 */

public class DBProgram {
    private static final String TAG = "DBProgram";

    //栏目树历史播放节点必须数据
    public String _contentuuid;
    public String _content_id;
    public String _contenttype;
    //栏目树历史播放节点必须数据
    public String _title_name;
    public String _imageurl;
    public String _user_id;
    public String _actiontype;
    @SerializedName("_play_index")
    public String playIndex;
    @SerializedName("_play_position")
    public String playPosition;
    //栏目树历史播放节点必须数据
    @SerializedName("_play_id")
    public String playId;

    public static List<Program> convertProgram(List<DBProgram> list) {
        List<Program> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                DBProgram program = list.get(i);


                if(TextUtils.isEmpty(program._contentuuid) || TextUtils.isEmpty(program._title_name) || TextUtils.isEmpty(program._content_id)){
                    Log.i(TAG, "过滤掉不符合的数据："+program.toString());
                    continue;
                }
                if (TextUtils.isEmpty(program.playId)){
                    program.playId = program._contentuuid;
//                    program._contentuuid = "";

                }
                result.add(convertProgram(list.get(i)));
            }
        }
        return result;
    }

    public static Program convertProgram(DBProgram dbProgram) {
        Program program = new Program();
        program.setContentUUID(dbProgram.playId);
        program.setContentType(dbProgram._contenttype);
        program.setTitle(dbProgram._title_name);
        program.setActionType(dbProgram._actiontype);
        program.setvImage(dbProgram._imageurl);
        program.setSeriesSubUUID(dbProgram._contentuuid);
        program.setContentID(dbProgram._content_id);
        return program;
    }

    @Override
    public String toString() {
        return "_contentuuid:"+_contentuuid+",_contenttype:"+_contenttype+",_title_name:"+_title_name+",_imageurl:"+_imageurl
                +",_user_id:"+_user_id+",_actiontype:"+_actiontype+",playIndex:"+playIndex+",playPosition:"+playPosition+",playId:"+playId;


    }
}
