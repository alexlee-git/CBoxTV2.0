package tv.newtv.cboxtv.cms;

import android.content.Context;

import com.google.gson.Gson;
import com.newtv.libs.Constant;
import com.newtv.libs.util.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * 项目名称:         CBoxTV
 * 包名:            tv.newtv.cboxtv.cms
 * 创建事件:         12:16
 * 创建人:           weihaichao
 * 创建日期:          2018/4/12
 */
public class BaseRequestModel {
    private Context mContext;
    private Gson mGson;

    public BaseRequestModel(Context context){
        mContext = context;
        mGson = new Gson();
    }

    public void destroy(){
        mGson = null;
        mContext = null;
    }

    protected Gson getGson(){
        return mGson;
    }

    protected Context getContext(){
        return mContext;
    }


    /**
     * 将json数据存到本地json文件中
     *
     * @param context  context
     * @param data     需要保存的json数据
     * @param fileName 文件名
     */
    protected boolean saveDataToJsonFile(Context context, String data, String fileName) {
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            /**
             * "data"为文件名,MODE_PRIVATE表示如果存在同名文件则覆盖，
             * 还有一个MODE_APPEND表示如果存在同名文件则会往里面追加内容
             */
            fileOutputStream = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(data);
            return true;
        } catch (IOException e) {
            LogUtils.e(e);
            return false;
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
    }

    /**
     * 从本地json文件中读取json数据
     *
     * @param context  context
     * @param fileName 文件名
     * @return 从文件中读取的数据
     */
    protected String loadDataFromJsonFile(Context context, String fileName) {
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder(Constant.BUFFER_SIZE_1K);
        try {
            /**
             * 注意这里的fileName不要用绝对路径，只需要文件名就可以了，系统会自动到data目录下去加载这个文件
             */
            fileInputStream = context.openFileInput(fileName);
            bufferedReader = new BufferedReader(
                    new InputStreamReader(fileInputStream));
            String result = "";
            while ((result = bufferedReader.readLine()) != null) {
                stringBuilder.append(result);
            }
        } catch (IOException e) {
            LogUtils.e(e.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LogUtils.e(e);
                }
            }
        }
        return stringBuilder.toString();
    }
}
