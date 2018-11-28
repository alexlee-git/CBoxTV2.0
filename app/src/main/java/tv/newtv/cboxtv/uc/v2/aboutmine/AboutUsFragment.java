package tv.newtv.cboxtv.uc.v2.aboutmine;

import android.net.http.SslError;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.newtv.libs.BootGuide;
import com.newtv.libs.Constant;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.uc.v2.BaseDetailSubFragment;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            tv.newtv.cboxtv.uc.v2.sub
 * 创建事件:         09:20
 * 创建人:           weihaichao
 * 创建日期:          2018/8/28
 */

/**
 * 项目名称： CBoxTV2.0
 * 包名： tv.newtv.cboxtv.uc.v2.about
 * 类描述：关于我们界面
 * 创建人：weihaichao
 * 创建时间：09:20
 * 创建日期：2018/8/28
 * 修改人：wqs
 * 修改时间：9:57
 * 修改日期：2018/9/5
 * 修改备注：
 */
public class AboutUsFragment extends BaseDetailSubFragment {
    private final String TAG = "AboutUsFragment";
    private WebView mWebView;
    private WebSettings webSettings = null;
    private boolean isSuccess = false;
    private boolean isError = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_usercenter_about_us_v2;
    }

    @Override
    protected void updateUiWidgets(View view) {
        //以下为webview加载html页面方案
        String aboutUs = BootGuide.getBaseUrl(BootGuide.HTML_PATH_ABOUT_US);
//        Constant.HTML_PATH_ABOUT_US = Constant.getBaseUrl(AppHeadersInterceptor.HTML_PATH_ABOUT_US);
        mWebView = (WebView) view.findViewById(R.id.id_webView);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.setDrawingCacheEnabled(true);
        webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(false);
        // 解决对某些标签的不支持出现白屏
        webSettings.setDomStorageEnabled(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webSettings.setDomStorageEnabled(true);//加上这一句就好了
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setDatabaseEnabled(true);
        //  WebSettings.LOAD_DEFAULT 如果本地缓存可用且没有过期则使用本地缓存，否加载网络数据 默认值
        //  WebSettings.LOAD_CACHE_ELSE_NETWORK 优先加载本地缓存数据，无论缓存是否过期
        //  WebSettings.LOAD_NO_CACHE  只加载网络数据，不加载本地缓存
        //  WebSettings.LOAD_CACHE_ONLY 只加载缓存数据，不加载网络数据
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "wqs:onPageFinished");
                if (!isError) {
                    isSuccess = true;
                    //回调成功后的相关操作
                }
                isError = false;
                if (mWebView != null) {
                    if (isSuccess) {
                        Log.d(TAG, "wqs:loadUrl Success");
                        mWebView.setVisibility(View.VISIBLE);
                    } else {
                        Log.d(TAG, "wqs:loadUrl Error");
                        mWebView.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "wqs:onReceivedError");
                isError = true;
                isSuccess = false;
                //回调失败的相关操作
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Constant.HTML_PATH_HELPER.startsWith("http://") || Constant.HTML_PATH_HELPER.startsWith("https://")) {
                    view.loadUrl(Constant.HTML_PATH_HELPER);
                    mWebView.stopLoading();
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 接受所有网站的证书，忽略SSL错误，执行访问网页
                handler.proceed();
            }
        });
        if (!TextUtils.isEmpty(aboutUs)) {
            mWebView.loadUrl(aboutUs);
        } else {
            Log.d(TAG, "wqs:html:path==null");
            mWebView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "wqs:onDestroy");
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
    }
}
