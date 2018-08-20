package tv.newtv.cboxtv.cms.listPage.adapter;

import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class UniversalViewHolder {
    
	private SparseArray<View> mViewMap;
	private int mPosition;
	private LayoutInflater mLayoutInflater;
	private View mConvertView;
	private DisplayImageOptions mOptions;
	private Bitmap mDefltBitmap;
	
	private static final String TAG = "BitmapUtil";
	
	public UniversalViewHolder(LayoutInflater layoutInflater, int position, int layoutResId, DisplayImageOptions options) {
		mViewMap = new SparseArray<View>();
		mPosition = position;
		mLayoutInflater = layoutInflater;
		mConvertView = mLayoutInflater.inflate(layoutResId, null);
		mOptions = options;
		mConvertView.setTag(this);
	}
	
	public UniversalViewHolder(View itemView, DisplayImageOptions options) {
		mViewMap = new SparseArray<View>();
		mOptions = options;
		mConvertView = itemView;
		mConvertView.setTag(this);
	}
	       
	public static UniversalViewHolder get(View convertView, int position, int layoutResId, LayoutInflater layoutInflater, DisplayImageOptions options) {
	 	if (convertView == null) {
	 		return new UniversalViewHolder(layoutInflater, position, layoutResId, null);
	 	} else {
	 		UniversalViewHolder holder = (UniversalViewHolder) convertView.getTag();
	 		holder.mPosition = position;
	 		return holder;
	 	}
	}
	
	public View getConvertView() {
		return mConvertView;
	}
	
	public View getViewByKey(int key) {
		View view = mViewMap.get(key);
		if (view == null) {
			view = mConvertView.findViewById(key);
			mViewMap.put(key, view);
		}
		return view;
	}
	
	public int getPosition() {
		return mPosition;
	}
	
	public UniversalViewHolder setImage(int viewId, String url) {
		final ImageView imageView = (ImageView) getViewByKey(viewId);
		ImageLoader.getInstance().displayImage(url, imageView, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			    (imageView).setImageBitmap(mDefltBitmap);	
			}
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {}
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {}
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {}
		});
		return this;
	}
	
	public UniversalViewHolder setText(int viewId, String content) {
		((TextView) getViewByKey(viewId)).setText(content);
		return this;
	}
	
	public void setDefaultBitmap(Bitmap defaultBitmap) {
		mDefltBitmap = defaultBitmap;
	}
	
	public Bitmap getDefaultBitmap() {
		return mDefltBitmap;
	}
	
//	public UniversalViewHolder setImageWithReflection(final int viewId, String url) {
//		ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
//			@Override
//			public void onLoadingStarted(String arg0, View arg1) {}
//			
//			@Override
//			public void onLoadingFailed(String arg0, View arg1, FailReason reason) {
//				Log.e(TAG, "推荐位海报加载失败, reason : " + reason.toString());
//			} 
//			
//			@Override
//			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
//				ImageView imageView = (ImageView) getViewByKey(viewId);
//				imageView.setImageBitmap(BitmapUtil.getInstance().getReflectBitmap(bitmap));
//			}
//			
//			@Override
//			public void onLoadingCancelled(String arg0, View arg1) {
//			}
//		});
//		return this;
//	}
}
