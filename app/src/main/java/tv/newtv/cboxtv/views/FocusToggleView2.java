package tv.newtv.cboxtv.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import tv.newtv.cboxtv.R;

public class FocusToggleView2 extends android.support.v7.widget.AppCompatImageView implements View.OnFocusChangeListener,FocusToggleSelect {

    /**
     * id顺序 无焦点未选中  无焦点选中  有焦点未选中  有焦点已选中
     */
    private static final int[][] resId = {{R.drawable.collect_normal_nofocus,R.drawable.collect_select_nofocus,
             R.drawable.collect_normal_hasfocus,R.drawable.collect_select_hasfocus},
            {R.drawable.full_screen_nofocus,0, R.drawable.full_screen_hasfocus,0},
            {R.drawable.attention_normal_nofocus,R.drawable.attention_select_nofocus,R.drawable.attention_normal_hasfocus,R.drawable.attention_select_hasfocus},
            {R.drawable.pay_normal_nofocus,0,R.drawable.pay_normal_hasfocus,0},
            {R.drawable.like_normal_nofocus,0,R.drawable.like_normal_hasfocus,0},
            {R.drawable.send_flowers_normal_nofocus,0,R.drawable.send_flowers_normal_hasfocus,0}};
    private int type;
    private boolean isSelect = false;
    private int noFoucsNormalId;
    private int noFocusSelectId;
    private int hasFocusNormalId;
    private int hasFocusSelectId;

    public FocusToggleView2(Context context) {
        this(context,null);
    }

    public FocusToggleView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FocusToggleView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FocusToggleView2);
        if(typedArray != null){
            type = typedArray.getInt(R.styleable.FocusToggleView2_type,-1);

            if(type != -1 && type < resId.length){
                noFoucsNormalId = resId[type][0];
                noFocusSelectId = resId[type][1];
                hasFocusNormalId = resId[type][2];
                hasFocusSelectId = resId[type][3];
            }
        }

        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnFocusChangeListener(this);
        changeImageResource(hasFocus());
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select){
        this.isSelect = select;
        changeImageResource(hasFocus());
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        changeImageResource(hasFocus);
    }

    private void changeImageResource(boolean hasFocus){
        if(hasFocus){
            if(isSelect() && checkIdCanUse(hasFocusSelectId)){
                setImageResource(hasFocusSelectId);
            }else if(checkIdCanUse(hasFocusNormalId)){
                setImageResource(hasFocusNormalId);
            }
        } else {
            if(isSelect() && checkIdCanUse(noFocusSelectId)){
                setImageResource(noFocusSelectId);
            }else if(checkIdCanUse(noFoucsNormalId)){
                setImageResource(noFoucsNormalId);
            }
        }
    }

    private boolean checkIdCanUse(int id){
        if(id != 0){
            return true;
        }
        return false;
    }
}
