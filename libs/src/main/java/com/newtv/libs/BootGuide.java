package com.newtv.libs;

import android.content.Context;
import android.text.TextUtils;

import com.newtv.libs.util.LogUtils;
import com.newtv.libs.util.SPrefUtils;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.libs
 * 创建事件:         11:20
 * 创建人:           weihaichao
 * 创建日期:          2018/11/23
 */
@SuppressWarnings({"WeakerAccess", "unused", "SpellCheckingInspection"})
public final class BootGuide {

    private static final boolean ENABLE_DEBUG_URL = true;

    public static final String SERVER_TIME = "SERVER_TIME";
    public static final String VERSION_UP = "VERSION_UP";
    public static final String AD = "AD";
    public static final String LOG = "LOG";
    public static final String ACTIVATE = "ACTIVATE";
    public static final String CDN = "CDN";
    public static final String PERMISSTION_CHECK = "PERMISSTION_CHECK";
    public static final String SEARCH = "SEARCH";
    public static final String DYNAMIC_KEY = "DYNAMIC_KEY";
    public static final String ACTIVATE2 = "ACTIVATE2";
    public static final String IS_ORIENTED = "IS_ORIENTED";
    public static final String CMS = "CMS";
    public static final String PAGE_MEMBER = "PAGE_MEMBER";
    public static final String PAGE_COLLECTION = "PAGE_COLLECTION";
    public static final String PAGE_SUBSCRIPTION = "PAGE_SUBSCRIPTION";
    public static final String PAGE_USERCENTER = "PAGE_USERCENTER";
    public static final String HTML_PATH_ABOUT_US = "HTML_PATH_ABOUT_US";
    public static final String HTML_PATH_HELPER = "HTML_PATH_HELPER";
    public static final String HTML_PATH_MEMBER_PROTOCOL = "HTML_PATH_MEMBER_PROTOCOL";
    public static final String MEMBER_CENTER_PARAMS = "MEMBER_CENTER_PARAMS";
    public static final String PAY = "PAY";
    public static final String USER = "USER";
    public static final String USER_BEHAVIOR = "USER_BEHAVIOR";
    public static final String PRODUCT = "PRODUCT";
//    public static final String NEW_CMS = "NEW_CMS";
//    public static final String NEW_SEARCH = "NEW_SEARCH";
    public static final String BOOT_GUIDE = "BOOT_GUIDE";
    public static final String HOTSEARCH_CONTENTID = "HOTSEARCH_CONTENTID";
    public static final String EXIT_CONTENTID = "EXIT_CONTENTID";
    public static final String HTML_PATH_USER_PROTOCOL = "HTML_PATH_USER_PROTOCOL";
    public static final String MARK_IS4K = "MARK_IS4K";
    public static final String MARK_VIPPRODUCTID = "MARK_VIPPRODUCTID";
    public static final String MARK_NEW_REALEXCLUSIVE = "MARK_NEW_REALEXCLUSIVE";
    public static final String CNTV_USER_LOGIN_HOST = "CNTV_USER_LOGIN_HOST"; // 央视网用户中心手机登录方式所对应的域名, http://reg.cctv.com/mobile/mobileRegAndLogin.action
    public static final String CNTV_USER_LOGIN_HOST_S = "CNTV_USER_LOGIN_HOST_S"; // 央视网用户中心手机登录方式所对应的域名, http://reg.cctv.com/mobile/mobileRegAndLogin.action

    private static HashMap<String, String> mServerAddressMap;

    public static void init(Context context){
        parseAssetServerAddress(context);
    }

    public static String getBaseUrl(String key) {
        if (mServerAddressMap == null) {
            parseAssetServerAddress(Libs.get().getContext());
        }
        return mServerAddressMap.get(key);
    }

    private static void parseAssetServerAddress(Context context) {
        try {
            InputStream inputStream = context.getAssets().open("bootguide.xml");
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String localData = sb.toString();
            LogUtils.d("BootGuide", "assets localData=" + localData);
            parse(localData);
            parseSharedPreferenceAddress(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parseSharedPreferenceAddress(Context context) {
        String localData = (String) SPrefUtils.getValue(context,
                SPrefUtils.KEY_SERVER_ADDRESS, "");
        LogUtils.d("BootGuide", "SharedPreference localData=" + localData);
        parse(localData);
    }

    public static void parse(@Nullable String serverInfo) {
        if (TextUtils.isEmpty(serverInfo)) return;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new ByteArrayInputStream(serverInfo
                    .getBytes())));
            NodeList list = document.getElementsByTagName("address");
            for (int i = 0; i < list.getLength(); ++i) {
                NamedNodeMap namedNodeMap = list.item(i).getAttributes();
                Node urlNode = namedNodeMap.getNamedItem("url");
                Node nameNode = namedNodeMap.getNamedItem("name");
                Node debugNode = namedNodeMap.getNamedItem("debug");
                String value = urlNode.getNodeValue();
                if (Libs.get().isDebug() && ENABLE_DEBUG_URL) {
                    if (debugNode != null) {
                        value = debugNode.getNodeValue();
                    }
                }
                if(mServerAddressMap == null){
                    mServerAddressMap = new HashMap<>();
                }
                mServerAddressMap.put(nameNode.getNodeValue(), value);
            }
        } catch (ParserConfigurationException e) {
            LogUtils.e("parse server address ParserConfigurationException" + e);
        } catch (SAXException e) {
            LogUtils.e("parse server address SAXException" + e);
        } catch (IOException e) {
            LogUtils.e("parse server address IOException" + e);
        }
    }
}
