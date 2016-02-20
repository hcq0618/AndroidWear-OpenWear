package cn.openwatch.demo;

import android.app.Application;

import cn.openwatch.communication.OpenWatchCommunication;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        // 手表端初始化
        OpenWatchCommunication.init(this);
    }
}
