package cn.openwatch.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import cn.openwatch.communication.DataMap;
import cn.openwatch.communication.OpenWatchBothWay;
import cn.openwatch.communication.OpenWatchRegister;
import cn.openwatch.communication.SpecialData;
import cn.openwatch.communication.listener.ConnectListener;
import cn.openwatch.communication.listener.DataListener;
import cn.openwatch.communication.listener.MessageListener;
import cn.openwatch.communication.listener.SpecialTypeListener;

public class MainActivity extends Activity
        implements ConnectListener, DataListener, MessageListener, SpecialTypeListener {

    private WearListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview_layout);

        WearListView listView = (WearListView) findViewById(R.id.wear_listview);
        adapter = new WearListAdapter(this);
        listView.setBindAdapter(adapter);

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        // 根据业务需求
        // 添加设备连接的监听
        OpenWatchRegister.addConnectListener(this);
        // 添加接收数据的监听
        OpenWatchRegister.addDataListener(this);
        OpenWatchRegister.addMessageListener(this);
        // 添加接收图片、文件、数据流等特殊类型数据的监听
        OpenWatchRegister.addSpecialTypeListener(this);

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();

        // 清除这个界面注册的所有监听
        OpenWatchRegister.removeDataListener(this);
        OpenWatchRegister.removeConnectListener(this);
        OpenWatchRegister.removeMessageListener(this);
        OpenWatchRegister.removeSpecialDataListener(this);

    }

    @Override
    public void onMessageReceived(String path, byte[] rawData) {
        // TODO Auto-generated method stub
        if (path.equals("/send_bothway")) {
            // 接收到手机端请求数据并响应 必须传入接收到的path
            OpenWatchBothWay.response(this, path, "response bothway");
        } else {
            Toast.makeText(this, getClass().getSimpleName() + ":手机发来临时性数据:" + new String(rawData), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onDataReceived(String path, byte[] rawData) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手机发来数据:" + new String(rawData), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataDeleted(String path) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手机删除了一条数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataMapReceived(String path, DataMap dataMap) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手机发来键值对", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBitmapReceived(String path, Bitmap bitmap) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手机端发来图片", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFileReceived(SpecialData data) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手机端发来文件：" + new String(data.getData()), Toast.LENGTH_SHORT)
                .show();

        //保存成文件
        data.receiveFile(getExternalCacheDir() + File.separator + "file.txt");
    }

    @Override
    public void onStreamReceived(SpecialData data) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手机端发来数据流：" + new String(data.getData()), Toast.LENGTH_SHORT).show();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getExternalCacheDir(), "file.txt"));
            //写入到输出流
            data.receiveStream(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onInputClosed(String path) {
        //在调用SpecialData的receiveStream或receiveFile后回调
        Toast.makeText(this, getClass().getSimpleName() + "：保存手机端发来的数据完成", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPeerConnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：和手机连接上了  设备名：" + displayName + " 设备id：" + nodeId,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPeerDisconnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：和手机断开了连接  设备名：" + displayName + " 设备id：" + nodeId,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceConnectionSuspended(int cause) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：和连接服务意外断开了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceConnected() {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：已连接上连接服务", Toast.LENGTH_SHORT).show();
    }
}
