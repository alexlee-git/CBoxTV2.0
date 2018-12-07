package tv.newtv.cboxtv.uc.v2;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.libs.Constant;
import com.newtv.libs.Libs;
import com.newtv.libs.uc.UserStatus;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.QrcodeUtil;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;
import com.newtv.libs.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import tv.newtv.cboxtv.BaseActivity;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;

public class CodeExChangeActivity extends BaseActivity {

    private static final String TAG = CodeExChangeActivity.class.getSimpleName();
    private static final String EXCHANGE_CARD_STATE = "exchangeCard";
    private static final int CODE_60800 = 60800;
    private static final int CODE_60801 = 60801;
    private static final int CODE_60802 = 60802;
    private static final int CODE_60803 = 60803;
    private static final int CODE_60804 = 60804;
    private static final int CODE_60805 = 60805;
    private static final int CODE_60806 = 60806;
    private TextView mCodeExChangeTitle;
    private EditText mCodeExChangeEdit;
    private TextView mCodeExChangeStatus;

    private ImageView mQRImage;
    private QrcodeUtil mQrcodeUtil;
    private String mQRcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_ex_change);
        mQrcodeUtil = new QrcodeUtil();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCodeExChangeQrcode(Utils.getAuthorization(CodeExChangeActivity.this), Constant.RESPONSE_TYPE, Constant.CLIENT_ID);
    }

    private void initView() {
        mCodeExChangeTitle = findViewById(R.id.user_info_title);
        mCodeExChangeTitle.setText(getResources().getString(R.string.code_exchange_title));
        mCodeExChangeEdit = findViewById(R.id.code_exchange_input_edit);
        mCodeExChangeStatus = findViewById(R.id.code_exchange_status);
        mQRImage = findViewById(R.id.code_exchange_qrcode);
        mCodeExChangeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "before : s : " + s.toString() + " start : " + start + "  count : " + count + " after : " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "changed : s : " + s.toString() + " start : " + start + " after : " + before + "  count : " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "after : s : " + s.toString());
            }
        });

        mCodeExChangeEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String token = "Bearer " + SharePreferenceUtils.getToken(getApplicationContext());
                    Log.d(TAG, "token : " + token);

                    if (mCodeExChangeEdit.getText().length() == 16) {
                        RequestBody requestBody = formatJson(mCodeExChangeEdit.getText().toString());
                        checkCode(token, requestBody);
                    } else {
                        errorToast("");
                    }
                    return true;
                }
                return false;
            }
        });
        showSoftInputFromWindow(mCodeExChangeEdit);
    }

    private RequestBody formatJson(String code) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("redeemCode", code);

            JSONObject terminalDTOObject = new JSONObject();
            terminalDTOObject.put("versionNo", DeviceUtil.getAppVersion(CodeExChangeActivity.this));
            terminalDTOObject.put("mac", SystemUtils.getMac(CodeExChangeActivity.this));
            terminalDTOObject.put("ip", "");
            terminalDTOObject.put("sourceFrom", "TV");

            jsonObject.put("terminalDTO", terminalDTOObject);
            jsonObject.put("appKey", Libs.get().getAppKey());
            jsonObject.put("channelCode", Libs.get().getChannelId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
        return requestBody;
    }

    //手动填写兑换码确认
    private void checkCode(String token, RequestBody requestBody) {
        try {
            NetClient.INSTANCE.getUserCenterLoginApi().
                    getCodeExChange(token, requestBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            //{"errorCode" : 60800,"errorMessage" : "您输入的兑换码无效，请重新输入。"}
                            try {
                                String data = responseBody.string();
                                Log.d(TAG, "data : " + data);
                                if (!TextUtils.isEmpty(data)) {
                                    JSONObject js = new JSONObject(data);
                                    if (js.has("errorCode")) {
                                        Log.d(TAG, "code : " + js.getString("errorCode") + "  message : " + js.getString("errorMessage"));
                                        setErrorMessage(Integer.parseInt(js.getString("errorCode")), js.getString("errorMessage"));
                                    } else {
                                        setSuccessMessage();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "GetToken  onError" + e);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //兑换码二维码
    private void getCodeExChangeQrcode(String Authorization, String response_type, String client_id) {
        Log.d(TAG, "OK getCodeExChangeQrcode  Authorization: " + Authorization + "  response_type :" + response_type
                + "  client_id : " + client_id);
        try {
            NetClient.INSTANCE.getUserCenterLoginApi()
                    .getCodeExChangeQRCode(Authorization, response_type, client_id, Libs.get().getChannelId(), EXCHANGE_CARD_STATE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String data = responseBody.string();
                                Log.i(TAG, "Login Qrcode :" + data.toString());
                                JSONObject mJsonObject = new JSONObject(data);
                                mQRcode = mJsonObject.optString("veriﬁcation_uri_complete");
                                mQrcodeUtil.createQRImage(mQRcode, getResources().getDimensionPixelOffset(R.dimen.width_448px),
                                        getResources().getDimensionPixelOffset(R.dimen.height_448px), null, mQRImage);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(TAG, "GetToken  onError" + e);
                            String error = getResources().getString(R.string.send_phone_err);
                            if (e instanceof HttpException) {
                                HttpException httpException = (HttpException) e;
                                try {
                                    String responseString = httpException.response().errorBody().string();
                                    JSONObject jsonObject = new JSONObject(responseString);
                                    error = jsonObject.getString("msg");
                                    Log.i(TAG, "error: " + responseString);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSuccessMessage() {
        mCodeExChangeStatus.setVisibility(View.VISIBLE);
        mCodeExChangeStatus.setText(getResources().getString(R.string.code_exchange_success));
    }

    private void setErrorMessage(int status, String errorMessage) {
        /*{
            "timestamp" : 1543227530654,
                "status" : 500,
                "error" : "Internal Server Error",
                "exception" : "com.newtv.account.service.controller.exception.ErrorRtnException",
                "message" : "您输入的兑换码无效，请重新输入。",
                "path" : "/service/exchangeCards/exchange"
        }*/
        Log.d(TAG, "setErrorMessage status : " + status + "  message : " + errorMessage);
        String error = null;
        switch (status) {
            case CODE_60800://500
                error = getResources().getString(R.string.code_exchange_fail_0);
                break;
            case CODE_60801:
                error = getResources().getString(R.string.code_exchange_fail_1);
                break;
            case CODE_60802:
                error = getResources().getString(R.string.code_exchange_fail_2);
                break;
            case CODE_60803:
                error = getResources().getString(R.string.code_exchange_fail_3);
                break;
            case CODE_60804:
                error = getResources().getString(R.string.code_exchange_fail_4);
                break;
            case CODE_60805:
                error = getResources().getString(R.string.code_exchange_fail_5);
                break;
            case CODE_60806:
                error = getResources().getString(R.string.code_exchange_fail_6);
                break;
            default:
                error = getResources().getString(R.string.code_exchange_fail);
                break;

        }
        mCodeExChangeStatus.setVisibility(View.VISIBLE);
        mCodeExChangeStatus.setText(errorMessage);
    }

    public void showSoftInputFromWindow(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    private void errorToast(String error) {
        Toast.makeText(LauncherApplication.AppContext, error, Toast.LENGTH_SHORT).show();
    }
}
