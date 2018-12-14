package com.newtv.cms;

import com.newtv.libs.util.LogUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

/**
 * 项目名称:         CBoxTV2.0
 * 包名:            com.newtv.cms
 * 创建事件:         15:53
 * 创建人:           weihaichao
 * 创建日期:          2018/10/15
 */
public class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {

    private final int maxRetryCount;
    private final int retryDelay;
    private int retryCount;
    private TimeUnit timeUnit;

    public RetryWithDelay(final int maxRetryCount, final int retryDelay, final TimeUnit timeUnit) {
        this.maxRetryCount = maxRetryCount;
        this.retryDelay = retryDelay;
        this.timeUnit = timeUnit;
        this.retryCount = 0;
    }

    @Override
    public Observable<?> apply(final Observable<? extends Throwable> attempts) {
        return attempts.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                if (++retryCount < maxRetryCount & canRetry(throwable)) {
                    LogUtils.e("do request retry currentTime=" + retryCount);
                    return Observable.timer(retryDelay, timeUnit);
                }

                return Observable.error(throwable);
            }
        });
    }

    private boolean canRetry(Throwable throwable) {
        return throwable instanceof SocketTimeoutException
                || throwable instanceof TimeoutException
                || throwable instanceof IOException;
    }
}
