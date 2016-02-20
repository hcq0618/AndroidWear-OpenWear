package cn.openwatch.internal.communication.event;

import android.content.Context;

import java.util.concurrent.atomic.AtomicBoolean;

import cn.openwatch.internal.communication.AbsApiClient;
import cn.openwatch.internal.basic.utils.LogUtils;

public abstract class AbsEventObserver<ApiClient> {
    protected Context cx;
    protected AbsApiClient<ApiClient> client;
    private AtomicBoolean isRegisted = new AtomicBoolean(false);

    public AbsEventObserver(Context cx, AbsApiClient<ApiClient> client) {
        this.cx = cx.getApplicationContext();
        this.client = client;
    }


    public void registIfNeed() {
        if (!isRegisted.getAndSet(true)) {
            LogUtils.d(this, "registIfNeed");
            registNodeApi();
            registDataApi();
            registMessageApi();
        }
    }

    public void unRegistIfNeed() {
        if (isRegisted.getAndSet(false)) {
            LogUtils.d(this, "unRegistIfNeed");
            unRegistNodeApi();
            unRegistDataApi();
            unRegistMessageApi();
        }
    }

    public boolean isRegisted() {
        return isRegisted.get();
    }

    protected abstract void unRegistDataApi();

    protected abstract void unRegistMessageApi();

    protected abstract void unRegistNodeApi();

    protected abstract void registDataApi();

    protected abstract void registMessageApi();

    protected abstract void registNodeApi();

}
