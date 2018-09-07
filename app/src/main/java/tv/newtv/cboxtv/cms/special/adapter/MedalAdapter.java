package tv.newtv.cboxtv.cms.special.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tv.newtv.cboxtv.R;
import tv.newtv.cboxtv.cms.mainPage.model.ExtendAttr;
import tv.newtv.cboxtv.cms.mainPage.model.ProgramInfo;

public class MedalAdapter extends RecyclerView.Adapter<MedalAdapter.MyHolder>{

    private List<ProgramInfo> mData;
    private Context mContext;

    public MedalAdapter(List<ProgramInfo> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_medal, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        ProgramInfo programInfo = mData.get(position);
        holder.restoreView();
        List<ExtendAttr> attrList = programInfo.getExtendAttr();
        if(attrList != null && attrList.size() > 0){
            ExtendAttr attr = attrList.get(0);
            holder.ranking.setText(attr.getRanking());
            holder.goldMedal.setText(attr.getGold());
            holder.silverMedal.setText(attr.getSiver());
            holder.bronzeMedal.setText(attr.getBronze());
            holder.total.setText(attr.getTotal());
        }
        holder.tvCountry.setText(programInfo.getTitle());
        try {
			Picasso.get().load(programInfo.getImg()).into(holder.ivCountry);
		}catch (Exception e){e.printStackTrace();}

        setBackground(holder.itemView,position);

//        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    holder.itemView.setBackgroundResource(R.color.color_272A3F);
//                }else {
//                	setBackground(holder.itemView,position);
//                }
//            }
//        });
    }

    private void setBackground(View view,int position){
    	if(position < 3){
    		view.setBackgroundResource(R.drawable.medal_bg1);
		}else {
    		view.setBackgroundResource(R.drawable.medal_bg2);
		}
	}

    @Override
    public int getItemCount() {
        return mData.size();
    }


    static class MyHolder extends RecyclerView.ViewHolder{
        private TextView ranking;
        private TextView tvCountry;
        private ImageView ivCountry;
        private TextView goldMedal;
        private TextView silverMedal;
        private TextView bronzeMedal;
        private TextView total;
        MyHolder(View itemView) {
            super(itemView);
            ranking = itemView.findViewById(R.id.text_ranking);
            tvCountry = itemView.findViewById(R.id.tv_country);
            ivCountry = itemView.findViewById(R.id.iv_country);
            goldMedal = itemView.findViewById(R.id.gold_medal);
            silverMedal = itemView.findViewById(R.id.silver_medal);
            bronzeMedal = itemView.findViewById(R.id.bronze_medal);
            total = itemView.findViewById(R.id.total);
        }

        void restoreView(){
            ranking.setText("");
            tvCountry.setText("");
            goldMedal.setText("");
            silverMedal.setText("");
            bronzeMedal.setText("");
            total.setText("");
            ivCountry.setBackgroundResource(0);
        }
    }
}
