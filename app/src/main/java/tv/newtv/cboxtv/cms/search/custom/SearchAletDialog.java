package tv.newtv.cboxtv.cms.search.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import tv.newtv.cboxtv.R;

/**
 * 类描述：
 * 创建人：wqs
 * 创建时间： 2018/3/7 0007 10:25
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class SearchAletDialog extends Dialog {
	
	private TextView mTitle,mContextText;
	private Button mOkBtn;
	private Context mContext;

	public SearchAletDialog(Context context) {
		super(context, R.style.search_dialog);
		this.mContext = context;
		setCustomDialog();
	}
	
	private void setCustomDialog() {
		View mView = LayoutInflater.from(mContext).inflate(R.layout.newtv_search_dialog, null);
		mTitle = (TextView) mView.findViewById(R.id.alter_title);
		mContextText = (TextView) mView.findViewById(R.id.alter_content);
		mOkBtn = (Button) mView.findViewById(R.id.alter_btn);
		super.setContentView(mView);
	}
	
	public TextView getContextTextView(){
		return mContextText;
	}
	
	/** 
     * 确定键监听器 
     * @param listener 
     */  
    public void setOnPositiveListener(View.OnClickListener listener){
    	mOkBtn.setOnClickListener(listener);  
    } 
}  
