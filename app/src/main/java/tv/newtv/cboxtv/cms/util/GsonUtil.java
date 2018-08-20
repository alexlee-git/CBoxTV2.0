package tv.newtv.cboxtv.cms.util;

import com.google.gson.Gson;

public class GsonUtil {

	public static <T> T fromjson(String json, Class<T> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(json, clazz);
	}

}
