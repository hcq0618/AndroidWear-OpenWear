package cn.openwatch.internal.communication;

import android.content.Context;

import cn.openwatch.communication.service.OpenWatchListenerService;
import cn.openwatch.internal.basic.utils.LogUtils;
import cn.openwatch.internal.communication.event.AbsEventHandler;
import cn.openwatch.internal.communication.event.EventRegister;
import cn.openwatch.internal.communication.event.ServiceEventHandler;

/**
 * Created by hcq0618 on 2015/11/3.
 */
public final class Config {

    private static Boolean isDeclared;

    private Config() {

    }

    public static void init(Context context, AbsEventHandler otherEventHandler) {

        if (isDeclared == null) {
            synchronized (Config.class) {
                if (isDeclared == null)
                    isDeclared = OpenWatchListenerService.detectDeclaredService(context);
            }
        }

        LogUtils.d(Config.class, "isDeclared " + isDeclared);
        if (isDeclared) {
            // 注册了后台监听服务 则直接通过后台监听服务监听内部事件
            if (otherEventHandler != null)
                ServiceEventHandler.putExtraEventHandler(otherEventHandler.getClass());
        } else {
            // 没注册后台监听服务 需要注册内部事件监听
            EventRegister register = EventRegister.getInstance();
            register.initInternalEventHandlerIfNeed(context);
            register.putExtraEventHandler(context, otherEventHandler);
        }


    }
}
