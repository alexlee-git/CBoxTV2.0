package tv.newtv.cboxtv.exit.presenter;



import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by 冯凯 on 2018/1/16.
 */

public abstract class RecommendPresenter<T> {
    private Reference<T> reference;


    public void attachView(T t) {
        reference = new WeakReference<T>(t);

    }

    public void detachView() {
        if (reference != null) {
            reference.clear();
            reference = null;
        }

    }
    public boolean isLive(){
        return reference!=null && reference.get()!=null;
    }

    public T getIView() {
        if (reference != null) {
            return reference.get();
        }
        return null;
    }
    abstract void bind();
}
