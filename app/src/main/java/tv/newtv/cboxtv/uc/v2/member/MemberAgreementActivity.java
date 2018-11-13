package tv.newtv.cboxtv.uc.v2.member;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.newtv.libs.Constant;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.HeadersInterceptor;
import tv.newtv.cboxtv.uc.v2.aboutmine.ScanScrollView;

/**
 * 项目名称： CBoxTV2.0
 * 包名： tv.newtv.cboxtv.uc.v2.member
 * 类描述：会员协议界面
 * 创建人：wqs
 * 创建时间：14:02
 * 创建日期：2018/9/13
 * 修改人：
 * 修改时间：
 * 修改日期：
 * 修改备注：
 */
public class MemberAgreementActivity extends Activity implements ScanScrollView.IScanScrollChangedListener {
    private final String TAG = "MemberAgreementActivity";
    private ScanScrollView mScrollView;
    private ImageView mBottomArrow;
    private TextView mMessageTextView;
    private WebView mWebView;
    private WebSettings webSettings = null;
    private boolean isSuccess = false;
    private boolean isError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter_member_agreement_v2);
//        mScrollView = (ScanScrollView) findViewById(R.id.id_member_agreement_scroll);
//        mScrollView.setScanScrollChangedListener(this);
//        mBottomArrow = (ImageView) findViewById(R.id.id_member_agreement_bottom_arrow);
//        mMessageTextView = findViewById(R.id.id_member_agreement_message);
//        mMessageTextView.setText("欢迎您使用未来电视New TV平台服务！\n" +
//                "为维护您自身权益，建议您仔细阅读各条款具体表述。本协议是用户（下称“您”，含个人或者单位）与未来电视有限公司（下称“未来电视”）之间的协议（下称“本协议”）。本协议条款构成您使用未来电视New TV平台所提供的服务及其衍生服务（下称“本服务”）之先决必要条件，通过访问或使用未来电视提供的网站、客户端及其他服务，视为您知晓、接受并认可本协议的全部条款。\n" +
//                "1.服务说明\n" +
//                "1.1本协议中的New TV平台服务包括但不限于以下内容：\n" +
//                "（1）由未来电视单独和/或其合作伙伴共同所有、直接或间接运营的网站平台（下称“New TV网站”）；\n" +
//                "（2）未来电视直接所有或运营的互联网电视客户端或应用APP，包括但不限于PC、平板、手机、电视、机顶盒等全部终端客户端中内置的服务平台（下称“New TV客户端”）；\n" +
//                "（3）未来电视单独和/或其合作伙伴共同所有其他技术或服务（下称“New TV其他技术和服务”）。\n" +
//                "1.2本协议1.1条所涉服务统称为“New TV服务”，上述服务所在或所依托的系统平台统称为“New TV平台”。\n" +
//                "1.3未来电视所提供全部服务的服务范围均仅限于New TV平台，任何在该平台之外使用未来电视提供的内容及服务的行为所引起的法律后果由使用者单独承担，此种情况下，未来电视有权立即停止提供服务并追究使用者的法律责任。\n" +
//                "2、New TV用户注册服务简介\n" +
//                "2.1本服务条款所称的“New TV用户注册”是指用户通过未来电视指定渠道及方式注册合法、有效帐号的行为。\n" +
//                "2.2用户注册帐号时必须使用申请人本人真实有效的手机号、本人名下第三方授权账号或邮箱地址。\n" +
//                " 3、服务条款的修改\n" +
//                "3.1未来电视有权修改、变更和新增本服务条款以及各单项服务的相关条款，并通过未来电视网站、终端产品服务页面等官方途径向用户进行告知，告知后视为用户对相关条款全部知晓、理解并接受。\n" +
//                "3.2您在享受New TV服务时，应当及时查阅了解修改、变更和新增的内容，并自觉遵守本服务条款以及相关单项服务的相关条款。在不同意上述内容的情况下，您有权停止使用修改、变更和新增内涉及的服务，否则您的使用行为即视为对上述内容的同意。\n" +
//                " 4、服务的变更或中止\n" +
//                "4.1未来电视保留随时变更或中止本服务的权利，您同意未来电视有权行使上述权利且不需对您或第三方承担任何责任。\n" +
//                "5、用户隐私制度\n" +
//                "5.1您知悉并同意，为便于向用户提供更好的服务，未来电视将在用户自愿选择服务或者提供信息的情况下收集用户的个人信息，并将这些信息进行整合，用户个人信息范围包括但不限于用户名、密码、操作记录、浏览记录、评论、URL、IP地址、浏览器类型、使用语言、访问日期和时间等。\n" +
//                "5.2为方便用户登录或使用New TV服务，未来电视在有需要时将使用相关技术手段将收集到的信息发送到对应的服务器。用户可以选择接受或者拒绝上述技术手段的使用，如用户选择拒绝，则有可能无法登陆或使用依赖于该技术手段的服务或者功能，未来电视对此不承担责任。\n" +
//                "5.3未来电视收集的用户信息将成为未来电视常规商业档案的一部分，且有可能因为转让、合并、收购、重组等原因而被转移到未来电视的继任公司或者指定的一方。未来电视承诺谨慎、妥善保管并使用收集的信息，且将积极采取各项措施保证信息安全。 \n" +
//                "5.4尊重用户个人隐私是未来电视秉承的理念，未来电视不会出于商业目的恶意使用用户信息资料或存储在各项服务中获取的各类信息，除非未来电视在诚信的基础上认为透露这些信息是必要的，包括但不限于以下情形：\n" +
//                "（1）依据有关法律法规、国家政策、上级单位要求进行告知及提供相关数据；\n" +
//                "（2）保持维护未来电视的知识产权和其他重要合法权利；\n" +
//                "（3）在紧急情况下竭力维护用户个人和社会大众的隐私安全；\n" +
//                "（4）根据本条款相关规定或者未来电视认为必要的其他情况下。\n" +
//                "6、用户的帐号、密码和安全性\n" +
//                "6.1用户注册成功将得到唯一帐号和初始密码。用户应妥善保管，如因用户原因导致其自己帐号和、密码泄露而对用户、未来电视或第三方造成的损害，用户应承担全部责任。\n" +
//                "6.2用户要对其帐户中发生的所有行为和事件承担全部经济和法律责任。\n" +
//                "6.3用户使用账号、初始密码成功登陆后可自行更改密码，同一用户可申请注册多个账号。\n" +
//                "6.4若发现任何非法使用帐号情况，用户有义务立即通知未来电视，否则应承担全部责任。\n" +
//                " 7、拒绝提供担保和免责声明\n" +
//                "7.1使用本服务的风险由用户承担。\n" +
//                "7.2本服务以即时状态提供给用户，未来电视对本服务的呈现形式、服务方式不提供任何类型的担保或保证。\n" +
//                "7.3未来电视不担保服务满足每个用户的要求。用户理解并接受下载或通过未来电视相关产品服务取得的任何信息资料取决于用户自己，并由其承担系统受损、资料丢失以及其它任何风险。\n" +
//                "7.4未来电视对在服务网上得到的任何商品购物服务、交易进程、招聘信息，都不作担保。\n" +
//                "8、免责条款\n" +
//                "8.1未来电视对用户不正当使用行为、违法违规等行为所产生的后果不承担责任，上述行为包括但不限于在非官方渠道购买产品及服务、在第三方网站进行交易、非法使用本服务或擅自向第三人或社会公众发布信息等非归因于未来电视的行为，相关行为后果由行为人自行承担，给未来电视造成损失的，应当予以赔偿。\n" +
//                "8.2未来电视对本服务涉及的境内外基础电信运营商的移动通信网络的故障、技术缺陷、覆盖范围限制、不可抗力、计算机病毒、黑客攻击、用户所在位置、用户关机或其他非未来电视技术能力范围内的事因等造成的服务中断、用户发送的短信息的内容丢失、出现乱码、错误接收、无法接收、迟延接收不承担责任。\n" +
//                "9、禁止服务的商业化\n" +
//                "9.1用户承诺，非经未来电视同意，不得利用本服务进行销售或其他商业用途。\n" +
//                "9.2如用户有需要将本服务用于商业用途，应提前书面通知未来电视并获得未来电视的明确授权，否则视为侵权。\n" +
//                " 10、用户管理\n" +
//                "10.1用户独立承担其发布内容的责任。用户对服务的使用必须遵守所有适用于服务的地方法律、国家法律和国际法律。用户承诺：\n" +
//                "（1）用户在未来电视的网页、平台上发布信息或者利用本服务时必须符合国家法律法规、公序良俗，不得利用本服务制作、复制、发布、传播以下信息：\n" +
//                "a违反宪法确定的基本原则的；\n" +
//                "b危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；\n" +
//                "c损害国家荣誉和利益的；\n" +
//                "d煽动民族仇恨、民族歧视，破坏民族团结的；\n" +
//                "e破坏国家宗教政策，宣扬邪教和封建迷信的；\n" +
//                "f散布谣言，扰乱社会秩序，破坏社会稳定的；\n" +
//                "g散布淫秽、色情、赌博、暴力、恐怖或者教唆犯罪的；\n" +
//                "h侮辱或者诽谤他人，侵害他人合法权益的；\n" +
//                "i煽动非法集会、结社、游行、示威、聚众扰乱社会秩序的；\n" +
//                "j以非法民间组织名义活动的；\n" +
//                "k含有法律、行政法规禁止的其他内容的。\n" +
//                "（2）用户在未来电视的网页、平台上发布信息或者利用本服务时必须符合其他有关国家和地区的法律规定以及国际法的有关规定。\n" +
//                "（3）用户不得利用本服务从事以下活动：\n" +
//                "a进入计算机信息网络或者使用计算机信息网络资源的；\n" +
//                "b对计算机信息网络功能进行删除、修改或者增加的；\n" +
//                "c对进入计算机信息网络中存储、处理或者传输的数据和应用程序进行删除、修改或者增加的；\n" +
//                "d故意制作、传播计算机病毒等破坏性程序的；\n" +
//                "e其他危害计算机信息网络安全的行为。\n" +
//                "（4）用户不得以任何方式干扰New TV平台及服务。\n" +
//                "（5）用户不得滥用New TV服务，包括但不限于：利用New TV服务进行侵害他人知识产权或者其他合法利益的行为。\n" +
//                "（6）用户不能上传或填写违法信息或国家禁止或不健康的内容，包含但不限于用户名、昵称、头像、简介等。\n" +
//                "（7）用户应遵守未来电视的所有其他规定和程序。\n" +
//                "10.2用户存在本协议第十条情形的，未来电视有权视情况单方决定采取删除相关信息、暂停或终止该用户服务、禁止用户账号使用、向国家行政司法机关报告检举等措施进行制止，且不予退还该用户已缴费用，相关法律责任由用户承担。\n" +
//                "10.3用户须对自己在使用未来电视产品服务过程中的行为承担法律责任。\n" +
//                "10.4用户使用未来电视电子公告服务，包括电子布告牌、电子白板、电子论坛、网络聊天室和留言板等以交互形式为上网用户提供信息发布条件的行为，也须遵守本协议以及未来电视电子公告服务规则，相关法律责任同样适用于电子公告服务的用户。\n" +
//                "10.5用户行为属于本协议第10条禁止行为的，未来电视有权立即取消用户帐号、删除全部用户信息并将该用户拉入黑名单。\n" +
//                "11、费用承担\n" +
//                "11.1用户同意保障和维护未来电视全体成员的利益，负责支付因用户违约、侵权而使未来电视承担的一切费用。\n" +
//                " 12、服务终止\n" +
//                "12.1未来电视及用户均有权单方暂停、终止本服务，无需通知对方，未来电视已经收取或用户已经支付的费用不予退还。\n" +
//                "12.2本服务终止时，用户基于本服务享有的权利终止，未来电视亦不再对用户承担任何义务。\n" +
//                "12.3用户知晓并同意，服务变更、中止与结束属未来电视商业决策，用户不得因服务的变更、中止或终止要求未来电视继续提供服务或者承担任何形式的赔偿责任。\n" +
//                " 13、通知\n" +
//                "13.1未来电视可通过电子邮件、常规的信件或在网站显著位置公告等方式进行通知发布。\n" +
//                "13.2信息一经发布，视为未来电视完成对用户的告知义务及用户接受该通知事项。\n" +
//                "14、内容的所有权\n" +
//                "14.1内容的定义包括：本服务提供或涉及的全部信息，包括但不限于：\n" +
//                "（1）文字、软件、声音、图片、视频、图表等；\n" +
//                "（2）广告；\n" +
//                "（3）电子邮件系统的全部内容；\n" +
//                "（4）虚拟社区服务为用户提供的商业信息。\n" +
//                "14.2用户只能在未来电视和/或合作伙伴、广告发布者事先合法、完整授权下才能使用上述内容，。\n" +
//                "15、法律的适用和管辖\n" +
//                "15.1本协议的生效、履行、解释及争议的解决均适用中华人民共和国法律，本协议部分条款无效，不影响其他部分的效力。\n" +
//                "15.2如就本协议内容或其执行发生任何争议，应友好协商解决；协商不成时，则争议各方一致同意将争议提交天津市滨海新区人民法院以诉讼方式处理。\n" +
//                "16、信息储存及相关知识产权\n" +
//                "16.1未来电视将尽力维护本服务的安全性及方便性，但对本服务所涉的信息（包括但不限于用户发布的信息）删除或储存失败不承担任何责任。\n" +
//                "16.2未来电视保留判定用户的行为是否符合本服务条款的要求的权利，保留对本协议的完全解释权。\n" +
//                "16.3本服务项下，用户对内容的上传行为即表明用户授权未来电视对该内容享有免费的、不可撤销的、永久的使用权和收益权，并为其上传行为所引起的法律后果独立承担全部责任，如因此给未来电视或第三人造成损失的，应当予以赔偿。\n" +
//                "17、权利声明\n" +
//                "17.1未来电视不行使、未能及时行使或者未充分行使本条款或者按照法律规定所享有的权利，不应被视为放弃该权利，也不影响未来电视在将来行使该权利。\n" +
//                "17.2在法律允许的最大范围内，未来电视保留对本服务条款的最终解释权。各条款标题仅为帮助您理解该条款表达的主旨之用，不影响或限制本协议条款的含义或解释。\n" +
//                "特别提示：如用户对本条款内容有任何疑问，可拨打未来电视官方客服电话（400 046 3366）。");
        //以上为会员协议文本写死方案
        //以下为webview加载html页面方案
        Constant.HTML_PATH_MEMBER_PROTOCOL = Constant.getBaseUrl(HeadersInterceptor.HTML_PATH_MEMBER_PROTOCOL);
        mWebView = (WebView) findViewById(R.id.id_webView);
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
                Log.e(TAG, "wqs:onPageFinished");
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
                Log.e(TAG, "wqs:onReceivedError");
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
        if (!TextUtils.isEmpty(Constant.HTML_PATH_MEMBER_PROTOCOL)) {
            mWebView.loadUrl(Constant.HTML_PATH_MEMBER_PROTOCOL);
        } else {
            Log.e(TAG, "wqs:html:path==null");
            mWebView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "wqs:onDestroy");
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
    }

    @Override
    public void onScrolledToBottom() {
        Log.e(TAG, "wqs:onScrolledToBottom");
//        if (mBottomArrow != null) {
//            mBottomArrow.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    public void onScrolledChange() {
        Log.e(TAG, "wqs:onScrolledChange");
//        if (mBottomArrow != null) {
//            mBottomArrow.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onScrolledToTop() {


    }
}
