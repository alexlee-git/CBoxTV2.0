package tv.newtv.cboxtv.uc.v2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.newtv.libs.Libs;
import com.newtv.libs.util.DeviceUtil;
import com.newtv.libs.util.SharePreferenceUtils;
import com.newtv.libs.util.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import tv.newtv.cboxtv.LauncherApplication;
import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.net.NetClient;

public class CodeExChangeActivity extends AppCompatActivity {

    private static final String TAG = CodeExChangeActivity.class.getSimpleName();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_ex_change);
        initView();
    }

    private void initView() {
        mCodeExChangeTitle = findViewById(R.id.user_info_title);
        mCodeExChangeTitle.setText(getResources().getString(R.string.code_exchange_title));
        mCodeExChangeEdit = findViewById(R.id.code_exchange_input_edit);
        mCodeExChangeStatus = findViewById(R.id.code_exchange_status);

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

    private void errorToast(String error) {
        Toast.makeText(LauncherApplication.AppContext, error, Toast.LENGTH_SHORT).show();
    }
}
