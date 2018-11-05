
package tv.newtv.cboxtv.cms.search.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.newtv.libs.util.DisplayUtils;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.search.listener.OnReturnInputString;


/**
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/7 0007 09:36
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class SearchViewKeyboard extends RelativeLayout implements OnClickListener, OnFocusChangeListener {

    private static final String TAG = "SearchViewKeyboard";
    private Context mContext;
    private Button mBtn01;
    private Button mBtn02;
    private Button mBtn03;
    private Button mBtn04;
    private Button mBtn05;
    private Button mBtn06;
    private Button mBtn07;
    private Button mBtn08;
    private Button mBtn09;
    private Button mBtn10;
    private Button mBtn11;
    private String[] btnNames = {"0|1\n ", "2\nABC", "3\nDEF", "4\nGHI",
            "5\nJKL", "6\nMNO", "7\nPQRS", "8\nTUV", "9\nWXYZ", "清空", "退格"};
    private Button mSubBtn01;
    private Button mSubBtn02;
    private Button mSubBtn03;
    private Button mSubBtn04;
    private Button mSubBtn05;
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private TextView mInputTextView;

    //	private HostInterface mHostInterface;
//    private Typeface mTypefaceltzh;

    private StringBuilder mInputStr = new StringBuilder(1024);

    private int mBtnTag;
    private int mLastBtnTag;
    private FrameLayout mFrameLayoutBtn10;
    private FrameLayout mFrameLayoutBtn11;
    private FrameLayout mFrameLayoutBtn1;
    private FrameLayout mFrameLayoutBtn2;
    private FrameLayout mFrameLayoutBtn3;
    private FrameLayout mFrameLayoutBtn4;
    private FrameLayout mFrameLayoutBtn5;
    private FrameLayout mFrameLayoutBtn6;
    private FrameLayout mFrameLayoutBtn7;
    private FrameLayout mFrameLayoutBtn8;
    private FrameLayout mFrameLayoutBtn9;
    private boolean isSpace = false;//判断空格键是否显示
    private View mLastFocusView;
    private TextView mSubInputTextView;

    public TextView getTextView() {
        return mInputTextView;
    }

    public SearchViewKeyboard(Context context) {
        super(context);
        initView(context);
    }

    public SearchViewKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SearchViewKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;

//		mHostInterface = (HostInterface) InterfaceManager.getInstance().obtainInterface(Common.HOST_INTERFACE_NAME);
//		if (mHostInterface != null) {
//		    mTypefaceltzh = mHostInterface.getTypefaceByKey(Common.KEY_TYPEFACE_LTZH);
//		}

        LayoutInflater.from(context).inflate(R.layout.newtv_search_keyboard, this);
        mPopupView = LayoutInflater.from(context).inflate(R.layout.layout_search_sub_btn, null);
        initUI();
    }

    private void initUI() {
        mBtn01 = (Button) findViewById(R.id.btn_1);
        mBtn02 = (Button) findViewById(R.id.btn_2);
        mBtn03 = (Button) findViewById(R.id.btn_3);
        mBtn04 = (Button) findViewById(R.id.btn_4);
        mBtn05 = (Button) findViewById(R.id.btn_5);
        mBtn06 = (Button) findViewById(R.id.btn_6);
        mBtn07 = (Button) findViewById(R.id.btn_7);
        mBtn08 = (Button) findViewById(R.id.btn_8);
        mBtn09 = (Button) findViewById(R.id.btn_9);
        mBtn10 = (Button) findViewById(R.id.btn_10);
        mBtn11 = (Button) findViewById(R.id.btn_11);
        mFrameLayoutBtn1 = (FrameLayout) findViewById(R.id.frameLayout_btn_1);
        mFrameLayoutBtn2 = (FrameLayout) findViewById(R.id.frameLayout_btn_2);
        mFrameLayoutBtn3 = (FrameLayout) findViewById(R.id.frameLayout_btn_3);
        mFrameLayoutBtn4 = (FrameLayout) findViewById(R.id.frameLayout_btn_4);
        mFrameLayoutBtn5 = (FrameLayout) findViewById(R.id.frameLayout_btn_5);
        mFrameLayoutBtn6 = (FrameLayout) findViewById(R.id.frameLayout_btn_6);
        mFrameLayoutBtn7 = (FrameLayout) findViewById(R.id.frameLayout_btn_7);
        mFrameLayoutBtn8 = (FrameLayout) findViewById(R.id.frameLayout_btn_8);
        mFrameLayoutBtn9 = (FrameLayout) findViewById(R.id.frameLayout_btn_9);
        mFrameLayoutBtn10 = (FrameLayout) findViewById(R.id.frameLayout_btn_10);
        mFrameLayoutBtn11 = (FrameLayout) findViewById(R.id.frameLayout_btn_11);
        mBtn01.setText("0|1\n ");
        mBtn02.setText("2\nABC");
        mBtn03.setText("3\nDEF");
        mBtn04.setText("4\nGHI");
        mBtn05.setText("5\nJKL");
        mBtn06.setText("6\nMNO");
        mBtn07.setText("7\nPQRS");
        mBtn08.setText("8\nTUV");
        mBtn09.setText("9\nWXYZ");
        mBtn10.setText("清空");
        mBtn11.setText("退格");
        mInputTextView = (TextView) findViewById(R.id.txt_input);
        mInputTextView.setHintTextColor(Color.WHITE);
        if (mFrameLayoutBtn5 != null) {
            mFrameLayoutBtn5.requestFocus();
            onItemGetFocus(mFrameLayoutBtn5);
        }

        mFrameLayoutBtn1.setTag(1);
        mFrameLayoutBtn2.setTag(2);
        mFrameLayoutBtn3.setTag(3);
        mFrameLayoutBtn4.setTag(4);
        if (mFrameLayoutBtn5 != null) {
            mFrameLayoutBtn5.setTag(5);
        }
        mFrameLayoutBtn6.setTag(6);
        mFrameLayoutBtn7.setTag(7);
        mFrameLayoutBtn8.setTag(8);
        mFrameLayoutBtn9.setTag(9);
        mFrameLayoutBtn10.setTag(10);
        mFrameLayoutBtn11.setTag(11);

        initButtonValue(mFrameLayoutBtn1);
        initButtonValue(mFrameLayoutBtn2);
        initButtonValue(mFrameLayoutBtn3);
        initButtonValue(mFrameLayoutBtn4);
        initButtonValue(mFrameLayoutBtn5);
        initButtonValue(mFrameLayoutBtn6);
        initButtonValue(mFrameLayoutBtn7);
        initButtonValue(mFrameLayoutBtn8);
        initButtonValue(mFrameLayoutBtn9);
        initButtonValue(mFrameLayoutBtn10);
        initButtonValue(mFrameLayoutBtn11);

        mSubBtn01 = (Button) mPopupView.findViewById(R.id.sub_btn_1);
        mSubBtn01.setOnClickListener(this);
        mSubBtn01.setOnFocusChangeListener(this);

        mSubBtn02 = (Button) mPopupView.findViewById(R.id.sub_btn_2);
        mSubBtn02.setOnClickListener(this);
        mSubBtn02.setOnFocusChangeListener(this);

        mSubBtn03 = (Button) mPopupView.findViewById(R.id.sub_btn_3);
        mSubBtn03.setOnClickListener(this);
        mSubBtn03.setOnFocusChangeListener(this);

        mSubBtn04 = (Button) mPopupView.findViewById(R.id.sub_btn_4);
        mSubBtn04.setOnClickListener(this);
        mSubBtn04.setOnFocusChangeListener(this);

        mSubBtn05 = (Button) mPopupView.findViewById(R.id.sub_btn_5);
        mSubBtn05.setOnClickListener(this);
        mSubBtn05.setOnFocusChangeListener(this);
        mSubInputTextView = (TextView) mPopupView.findViewById(R.id.sub_txt_input);
        mSubInputTextView.setHintTextColor(Color.WHITE);
        mPopupWindow = new PopupWindow(mPopupView, DisplayUtils.translate(654, 0), LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 响应返回键必须的语句。
        mPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                switch (mBtnTag) {
                    case 1:
                        mFrameLayoutBtn1.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                        break;
                    case 2:
                        mFrameLayoutBtn2.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                        break;
                    case 3:
                        mFrameLayoutBtn3.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                        break;
                    case 4:
                        mFrameLayoutBtn4.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                        break;
                    case 5:
                        mFrameLayoutBtn5.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                        break;
                    case 6:
                        mFrameLayoutBtn6.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                        break;
                    case 7:
                        mFrameLayoutBtn7.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                        break;
                    case 8:
                        mFrameLayoutBtn8.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                        break;
                    case 9:
                        mFrameLayoutBtn9.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                        break;
                    default:
                        break;
                }
            }
        });

        mFrameLayoutBtn10.setNextFocusUpId(R.id.frameLayout_btn_10);
        mFrameLayoutBtn11.setNextFocusUpId(R.id.frameLayout_btn_11);
        mFrameLayoutBtn7.setNextFocusDownId(R.id.frameLayout_btn_7);
        mFrameLayoutBtn8.setNextFocusDownId(R.id.frameLayout_btn_8);
        mFrameLayoutBtn9.setNextFocusDownId(R.id.frameLayout_btn_9);

    }

    public View getLastFocusView() {
        return mLastFocusView;
    }

    private void initButtonValue(FrameLayout btn) {
        setButtonOnClick(btn);
        setButtonOnFocus(btn);
//        int tag = (Integer) btn.getTag();
//        btn.setText(btnNames[tag - 1]);
//        btn.setTypeface(mTypefaceltzh);
    }

    private void setButtonOnFocus(final FrameLayout btn) {
        btn.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    onItemGetFocus(view);
                    mLastBtnTag = (Integer) btn.getTag();
                    if ((Integer) btn.getTag() == 10) {
                        mFrameLayoutBtn10.setBackgroundResource(R.drawable.search_keyboard_focus_del);
                    } else if ((Integer) btn.getTag() == 11) {
                        mFrameLayoutBtn11.setBackgroundResource(R.drawable.search_keyboard_focus_del);
                    } else {
                        btn.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                    }
                } else {
                    onItemLoseFocus(view);
                    if ((Integer) btn.getTag() == 10) {
                        mFrameLayoutBtn10.setBackgroundResource(R.drawable.search_keyboard_del);
                    } else if ((Integer) btn.getTag() == 11) {
                        mLastFocusView = mFrameLayoutBtn11;
                        mFrameLayoutBtn11.setBackgroundResource(R.drawable.search_keyboard_del);
                    } else {
                        if ((Integer) btn.getTag() == 3) {
                            mLastFocusView = mFrameLayoutBtn3;
                        } else if ((Integer) btn.getTag() == 6) {
                            mLastFocusView = mFrameLayoutBtn6;
                        } else if ((Integer) btn.getTag() == 9) {
                            mLastFocusView = mFrameLayoutBtn9;
                        }
                        btn.setBackgroundResource(R.drawable.search_keyboard_btn);
                    }
                }
            }
        });
    }

    private void setButtonOnClick(final FrameLayout btn) {
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mBtnTag = (Integer) v.getTag();

                isSpace = false;
                mSubBtn04.setBackgroundResource(R.drawable.search_keyboard_btn);
                if (mBtnTag > 1 && mBtnTag < 10) {
                    mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                    btn.setBackgroundResource(R.drawable.search_keyboard_btn);
                    mPopupView.requestFocus();
                    String val = btnNames[mBtnTag - 1];
                    if (val.length() <= 1) {
                        return;
                    }
                    int btnCount = val.length();
                    for (int i = 0; i < btnCount; i++) {
                        if (btnCount == 5) {

                            mSubBtn01.setVisibility(View.VISIBLE);
                            mSubBtn02.setVisibility(View.VISIBLE);
                            mSubBtn03.setVisibility(View.VISIBLE);
                            mSubBtn04.setVisibility(View.VISIBLE);
                            mSubBtn05.setVisibility(View.INVISIBLE);

                            mSubBtn01.setText(val.charAt(2) + "");
                            mSubBtn02.setText(val.charAt(3) + "");
                            mSubBtn03.setText(val.charAt(0) + "");
                            mSubBtn04.setText(val.charAt(4) + "");
                        } else if (val.length() == 6) {
                            mSubBtn03.setVisibility(View.VISIBLE);
                            mSubBtn01.setVisibility(View.VISIBLE);
                            mSubBtn02.setVisibility(View.VISIBLE);
                            mSubBtn04.setVisibility(View.VISIBLE);
                            mSubBtn05.setVisibility(View.VISIBLE);

                            mSubBtn01.setText(val.charAt(2) + "");
                            mSubBtn02.setText(val.charAt(3) + "");
                            mSubBtn03.setText(val.charAt(0) + "");
                            mSubBtn04.setText(val.charAt(4) + "");
                            mSubBtn05.setText(val.charAt(5) + "");
                        }
                    }
                } else if (mBtnTag == 1) {
                    mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
//                    btn.setBackgroundResource(R.drawable.search_keyboard_btn);
                    mPopupView.requestFocus();
                    isSpace = true;
                    String val = btnNames[mBtnTag - 1];
                    if (val.length() <= 1) {
                        return;
                    }
                    mSubBtn01.setVisibility(View.INVISIBLE);
                    mSubBtn05.setVisibility(View.INVISIBLE);
                    mSubBtn02.setVisibility(View.VISIBLE);
                    mSubBtn04.setVisibility(View.VISIBLE);
                    mSubBtn03.setVisibility(View.VISIBLE);
                    mSubBtn02.setText(val.charAt(2) + "");
                    mSubBtn03.setText(val.charAt(0) + "");
                    mSubBtn04.setText(val.charAt(4) + "");
                    mSubBtn04.setBackgroundResource(R.drawable.search_space);

                } else if (mBtnTag == 10) {
                    mBtn10.setText("清空");
                    int inputStrLength = mInputTextView.getText().length();
                    if (inputStrLength == 0) {
                        return;
                    }
                    mInputStr.delete(0, mInputStr.length());
                    mInputTextView.setText(mInputStr.toString());
                    mSubInputTextView.setText(mInputStr.toString());

                    mInputTextView.setTextColor(Color.parseColor("#66ffffff"));
                    mSubInputTextView.setTextColor(Color.parseColor("#66ffffff"));

                    onReturnInputString.onReturnInputString(mInputStr.toString());
                } else if (mBtnTag == 11) {
                    mBtn11.setText("退格");
                    int inputStrLength = mInputTextView.getText().length();
                    if (inputStrLength == 0)
                        return;

                    mInputStr.deleteCharAt(inputStrLength - 1);
                    mInputTextView.setText(mInputStr.toString());
                    mSubInputTextView.setText(mInputStr.toString());

                    if (inputStrLength == 1) {
                        Log.e("legth", inputStrLength + "");
                        mInputTextView.setTextColor(Color.parseColor("#66ffffff"));
                        mSubInputTextView.setTextColor(Color.parseColor("#66ffffff"));
                    }
                    onReturnInputString.onReturnInputString(mInputStr.toString());

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sub_btn_1:
                setBtnClickStyle(mSubBtn01);
                break;
            case R.id.sub_btn_2:
                setBtnClickStyle(mSubBtn02);
                break;
            case R.id.sub_btn_3:
                setBtnClickStyle(mSubBtn03);
                break;
            case R.id.sub_btn_4:
                setBtnClickStyle(mSubBtn04);
                break;
            case R.id.sub_btn_5:
                setBtnClickStyle(mSubBtn05);
                break;
            default:
                break;
        }
        mInputTextView.setText(mInputStr.toString());
        mSubInputTextView.setText(mInputStr.toString());

        mInputTextView.setTextColor(Color.parseColor("#ffffff"));
        mSubInputTextView.setTextColor(Color.parseColor("#ffffff"));
        //上报日志
//        logSDK.getInstance().logUpload(2, mInputStr.toString());
    }

    private void setBtnClickStyle(Button subBtn) {
        String subInputStr = subBtn.getText().toString();
        if (subInputStr != null) {
            if (mInputStr.length() < 10) {
                mInputStr.append(subInputStr);
                onReturnInputString.onReturnInputString(mInputStr.toString());
                mSubBtn01.setVisibility(View.INVISIBLE);
                mSubBtn02.setVisibility(View.INVISIBLE);
                mSubBtn03.setVisibility(View.INVISIBLE);
                mSubBtn04.setVisibility(View.INVISIBLE);
                mSubBtn05.setVisibility(View.INVISIBLE);
                if (mPopupWindow.isShowing()){
                    mPopupWindow.dismiss();
            }
            } else {
                searchResultAlet();
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            switch (v.getId()) {
                case R.id.sub_btn_1:
                    mSubBtn01.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                    onItemGetFocus(mSubBtn01);
                    break;
                case R.id.sub_btn_2:
                    mSubBtn02.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                    onItemGetFocus(mSubBtn02);
                    break;
                case R.id.sub_btn_3:
                    mSubBtn03.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                    onItemGetFocus(mSubBtn03);
                    break;
                case R.id.sub_btn_4:
                    if (isSpace) {
                        mSubBtn04.setBackgroundResource(R.drawable.search_focus_space);
                    } else {
                        mSubBtn04.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                    }
                    onItemGetFocus(mSubBtn04);
                    break;
                case R.id.sub_btn_5:
                    mSubBtn05.setBackgroundResource(R.drawable.search_keyboard_focus_btn);
                    onItemGetFocus(mSubBtn05);
                    break;

                default:
                    break;
            }
        } else {
            switch (v.getId()) {
                case R.id.sub_btn_1:
                    mSubBtn01.setBackgroundResource(R.drawable.search_keyboard_btn);
                    onItemLoseFocus(mSubBtn01);
                    break;
                case R.id.sub_btn_2:
                    mSubBtn02.setBackgroundResource(R.drawable.search_keyboard_btn);
                    onItemLoseFocus(mSubBtn02);
                    break;
                case R.id.sub_btn_3:
                    mSubBtn03.setBackgroundResource(R.drawable.search_keyboard_btn);
                    onItemLoseFocus(mSubBtn03);
                    break;
                case R.id.sub_btn_4:
                    if (isSpace) {
                        mSubBtn04.setBackgroundResource(R.drawable.search_space);
                    } else {
                        mSubBtn04.setBackgroundResource(R.drawable.search_keyboard_btn);
                    }
                    onItemLoseFocus(mSubBtn04);
                    break;
                case R.id.sub_btn_5:
                    mSubBtn05.setBackgroundResource(R.drawable.search_keyboard_btn);
                    onItemLoseFocus(mSubBtn05);
                    break;

                default:
                    break;
            }
        }

    }


    private OnReturnInputString onReturnInputString;

    public void setOnReturnInputString(OnReturnInputString onReturnInputString) {
        this.onReturnInputString = onReturnInputString;
    }

    public FrameLayout getDelBtn() {
        return mFrameLayoutBtn11;
    }

    public FrameLayout getThreeBtn() {
        return mFrameLayoutBtn3;
    }

    public FrameLayout getSixBtn() {
        return mFrameLayoutBtn6;
    }

    public FrameLayout getFiveBtn() {
        return mFrameLayoutBtn5;
    }

    public FrameLayout getNineButton() {
        return mFrameLayoutBtn9;
    }

    private void searchResultAlet() {
        final SearchAletDialog searchAletDialog = new SearchAletDialog(mContext);
        TextView context = searchAletDialog.getContextTextView();
        context.setText("最多只能输入10个字符");
        searchAletDialog.setOnPositiveListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                searchAletDialog.dismiss();
            }
        });
        searchAletDialog.show();
        return;
    }

    public int getBtnTag() {
        return mLastBtnTag;
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    private void onItemGetFocus(View view) {
        //直接放大view
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }

    private void onItemLoseFocus(View view) {
        // 直接缩小view
        ScaleAnimation sa = new ScaleAnimation(1.1f, 1.0f, 1.1f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setFillAfter(true);
        sa.setDuration(150);
        view.startAnimation(sa);
    }
}
