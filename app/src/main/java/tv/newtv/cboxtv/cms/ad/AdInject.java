package tv.newtv.cboxtv.cms.ad;

import android.app.Activity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import tv.newtv.cboxtv.annotation.BuyGoodsAD;
import tv.newtv.cboxtv.annotation.BuyGoodsInject;

public class AdInject {

    public static void inject(Activity activity){
        injectBuyGoods(activity);
    }

    private static void injectBuyGoods(Activity activity){
        if(hasAnnotation(activity,BuyGoodsAD.class)){
            Class<?> clazz = activity.getClass().getSuperclass();
            Field[] mField = clazz.getDeclaredFields();
            for(Field field : mField){
                BuyGoodsInject annotation = field.getAnnotation(BuyGoodsInject.class);
                if(annotation != null){
                    field.setAccessible(true);
                    BuyGoodsBusiness buyGoodsBusiness = new BuyGoodsBusiness(activity,activity.findViewById(android.R.id.content));
                    try {
                        field.set(activity,buyGoodsBusiness);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static boolean hasAnnotation(Object obj,Class ann){
        Class<? extends Object> clazz = obj.getClass();
        Annotation annotation = clazz.getAnnotation(ann);
        if(annotation != null){
            return true;
        }
        return false;
    }

}
