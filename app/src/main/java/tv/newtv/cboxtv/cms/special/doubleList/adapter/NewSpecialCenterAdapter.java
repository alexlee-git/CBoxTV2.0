package tv.newtv.cboxtv.cms.special.doubleList.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.ScoreInfoResult;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;
import tv.newtv.cboxtv.cms.special.doubleList.bean.SpecialBean;

public class NewSpecialCenterAdapter extends RecyclerView.Adapter<NewSpecialCenterAdapter.CenterHolder> {
    private static final String TAG = NewSpecialCenterAdapter.class.getSimpleName();
    protected final Context context;
    private CenterHolder mCenterHolder;
    private List<SpecialBean.DataBean.ProgramsBean> mSpecialData, oldSpecialData;
    private Map<Integer, List<SpecialBean.DataBean.ProgramsBean>> mOldData = new HashMap<Integer, List<SpecialBean.DataBean.ProgramsBean>>();
    private List<Integer> mSelectIdList = new ArrayList<>();
    private List<Integer> mLeftSelectList = new ArrayList<>();
    private boolean isFirstInit = true, isFirstClick = false;
    private int mSelectedId = -1;
    private int mLeftId = -1;
    private OnFocusedVideoChangeListener mOnFocusedVideoChangeListener;

    public NewSpecialCenterAdapter(Context context, List<SpecialBean.DataBean.ProgramsBean> objectList) {
        this.mSpecialData = objectList;
        this.context = context;
    }

    @Override
    public CenterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CenterHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_center_special, parent, false));
    }

    @Override
    public void onBindViewHolder(final CenterHolder holder, final int position) {
        mCenterHolder = holder;
        reFreshSecleted(position);
        holder.topicContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "you just touched me : " + mSpecialcData.get(position), Toast.LENGTH_LONG).show();
                Log.d(TAG, "you just touched me : " + mSpecialData.get(position));
                mSelectedId = position;
                reSetSelect(position);
                if (mOnFocusedVideoChangeListener != null) {
                    mOnFocusedVideoChangeListener.onFocusedVideoChangeListener(mSpecialData.get(position).getTitle(), position);
                }
            }
        });
        holder.topicItem.setText(mSpecialData.get(position).getTitle());
        holder.topicContainer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.topicItem.setBackgroundResource(R.drawable.special_list_focus);
                } else {
                    holder.topicItem.setBackgroundColor(Color.parseColor("#00000000"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSpecialData.size();
    }

    public void reFreshSecleted(int selectedId) {
        Log.d(TAG, "ywy  id : " + selectedId + "  isContains : " + mSelectIdList.contains(selectedId));
        if (isFirstInit) {
            oldSpecialData = mSpecialData;
            mSelectIdList.add(selectedId);
            mSpecialData.get(selectedId).setPlay(true);
            isFirstInit = false;
            //isFirstClick = true;
        }

        if (mSelectIdList.contains(selectedId) && mSpecialData.get(selectedId).isPlay()) {
            mCenterHolder.mPlayerIcon.setVisibility(View.VISIBLE);
            mCenterHolder.topicContainer.setBackgroundResource(R.drawable.xuanhong);
        } else {
            mCenterHolder.mPlayerIcon.setVisibility(View.GONE);
            mCenterHolder.topicContainer.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    public void setSelected(int selectid) {
        reSetSelect(selectid);
        reFreshSecleted(selectid);
        notifyDataSetChanged();
    }

    public void reSetSelect(int position) {
        if (mSpecialData != null && mSpecialData.size() > 0) {
            mSpecialData.get(0).setPlay(false);
        }
        mSpecialData.get(position).setPlay(true);
        if (mSelectIdList.size() > 0) {
            mSelectIdList.clear();
        }
        mSelectIdList.add(position);
    }

    public void refreshData(int leftId, List<SpecialBean.DataBean.ProgramsBean> mData) {
        //mData.clear();
        //if (leftId > 0) {
        //mSpecialData.get(0).setPlay(false);
            /*oldSpecialData = mOldData.get(mLeftId);
            mOldData.remove(mLeftId);
            oldSpecialData.get(0).setPlay(false);
            mOldData.put(mLeftId, oldSpecialData);*/
        /*if (mOldData.size() > 0 && mLeftSelectList.size() > 0) {
            Log.d(TAG, "NewSpecialFragment selectlist id : " + mLeftSelectList.get(0) + " mLeftId: " + mLeftId);
            oldSpecialData = mOldData.get(mLeftSelectList.get(0));
            mOldData.remove(mLeftId);
            oldSpecialData.get(mLeftId).setPlay(false);
            mOldData.put(mLeftId, oldSpecialData);
        }*/
        //isFirstClick = false;
        //}
        /*if (mLeftSelectList.size() > 0) {
            mLeftSelectList.clear();
        }*/
        if (!mOldData.containsKey(leftId)) {
            //mLeftSelectList.add(leftId);
            mOldData.clear();
            mOldData.put(leftId, mData);
            mSpecialData = mData;
        } else {
            mSpecialData = mOldData.get(leftId);
        }
        mLeftId = leftId;
        notifyDataSetChanged();
    }

    public void clearList() {
        isFirstInit = true;
        mSelectIdList.clear();
        mOldData.clear();
    }

    private void getVideoData() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
    }

    class CenterHolder extends RecyclerView.ViewHolder {
        public TextView topicItem;
        public View topicContainer;
        public ImageView mPlayerIcon;

        public CenterHolder(View itemView) {
            super(itemView);
            topicContainer = itemView.findViewById(R.id.topic_center_container);
            topicItem = itemView.findViewById(R.id.topic_center_item);
            mPlayerIcon = itemView.findViewById(R.id.topic_center_item_player);
        }
    }

    public void setOnFocusedVideoChangeListener(OnFocusedVideoChangeListener onFocusedVideoChangeListener) {
        mOnFocusedVideoChangeListener = onFocusedVideoChangeListener;
    }

    public interface OnFocusedVideoChangeListener {
        void onFocusedVideoChangeListener(String title, int position);
    }
}
