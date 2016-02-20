package cn.openwatch.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.openwatch.communication.BothWayCallback;
import cn.openwatch.communication.DataMap;
import cn.openwatch.communication.ErrorStatus;
import cn.openwatch.communication.OpenWatchBothWay;
import cn.openwatch.communication.OpenWatchRegister;
import cn.openwatch.communication.OpenWatchSender;
import cn.openwatch.communication.SpecialData;
import cn.openwatch.communication.listener.ConnectListener;
import cn.openwatch.communication.listener.DataListener;
import cn.openwatch.communication.listener.MessageListener;
import cn.openwatch.communication.listener.SendListener;
import cn.openwatch.communication.listener.SpecialTypeListener;

// 手表端代码逻辑基本亦同
public class MainActivity extends Activity
        implements OnClickListener, ConnectListener, DataListener, MessageListener, SpecialTypeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.send_data_btn).setOnClickListener(this);
        findViewById(R.id.send_bitmap_data_btn).setOnClickListener(this);
        findViewById(R.id.send_datamap_data_btn).setOnClickListener(this);
        findViewById(R.id.send_stream_data_btn).setOnClickListener(this);

        findViewById(R.id.send_msg_btn).setOnClickListener(this);
        findViewById(R.id.send_bitmap_msg_btn).setOnClickListener(this);
        findViewById(R.id.send_datamap_msg_btn).setOnClickListener(this);
        findViewById(R.id.send_stream_msg_btn).setOnClickListener(this);

        findViewById(R.id.send_bothway).setOnClickListener(this);
        
        findViewById(R.id.check_wear_app_update).setOnClickListener(this);

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

    private void sendData() {
        // 当配对设备未连接 数据并不会被丢失 会在下次连接上配对设备时接收到数据
        // 发送基础数据
        OpenWatchSender.sendData(this, "/send_data", "你好 openwatch", new MySendListener("data"));
    }

    private void sendBitmapData() {
        // 发送图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        OpenWatchSender.sendData(this, "/send_bitmap", bitmap, new MySendListener("图片"));
    }

    private void sendDatamapData() {
        // 发送键值对
        DataMap datamap = new DataMap();
        datamap.putString("key", "value");
        OpenWatchSender.sendData(this, "/send_datamap", datamap, new MySendListener("键值对"));
    }

    private void sendFileOrStreamData() {
        try {
            InputStream is = getAssets().open("test.txt");
            OpenWatchSender.sendData(this, "/send_stream", is, new MySendListener("数据流"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // // 发送文件
        // OpenWatchSender.sendData(this, "/send_file", new
        // File(filePath), new MySendListener("文件"));
    }

    private void sendMsg() {
        // 当配对设备未连接 数据会被丢失 用于发送临时性或时效性数据
        // 发送基础数据
        OpenWatchSender.sendMsg(this, "/send_msg", "你好 openwatch", new MySendListener("msg"));
    }

    private void sendBitmapMsg() {
        // 发送图片
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        OpenWatchSender.sendMsg(this, "/send_bitmap", bitmap2, new MySendListener("图片"));
    }

    private void sendDatamapMsg() {
        // 发送键值对
        DataMap datamap2 = new DataMap();
        datamap2.putString("key", "value");
        OpenWatchSender.sendMsg(this, "/send_datamap", datamap2, new MySendListener("键值对"));
    }

    private void sendFileOrStreamMsg() {
        try {
            InputStream is = getAssets().open("test.txt");
            OpenWatchSender.sendMsg(this, "/send_stream", is, new MySendListener("数据流"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // // 发送文件
        // OpenWatchSender.sendMsg(this, "/send_file", new
        // File(filePath), new MySendListener("文件"));
    }

    private void sendBothWay() {
        OpenWatchBothWay.request(this, "/send_bothway", "你好 openwatch", new BothWayCallback() {

            @Override
            public void onResponsed(byte[] rawData) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "手表端响应数据:" + new String(rawData), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ErrorStatus error) {
                // TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "数据请求错误" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.send_data_btn:
                sendData();
                break;
            case R.id.send_bitmap_data_btn:
                sendBitmapData();
                break;
            case R.id.send_datamap_data_btn:
                sendDatamapData();
                break;
            case R.id.send_stream_data_btn:
                sendFileOrStreamData();
                break;
            case R.id.send_msg_btn:
                sendMsg();
                break;
            case R.id.send_bitmap_msg_btn:
                sendBitmapMsg();
                break;
            case R.id.send_datamap_msg_btn:
                sendDatamapMsg();
                break;
            case R.id.send_stream_msg_btn:
                sendFileOrStreamMsg();
                break;
            case R.id.send_bothway:
                sendBothWay();
                break;

            default:
                break;
        }
    }

    private class MySendListener implements SendListener {

        private String tag;

        private MySendListener(String tag) {
            this.tag = tag;
        }

        @Override
        public void onSuccess() {
            // TODO Auto-generated method stub
            Toast.makeText(MainActivity.this, "发送" + tag + "成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(ErrorStatus error) {
            // TODO Auto-generated method stub
            Toast.makeText(MainActivity.this, "发送" + tag + "失败 原因是:" + error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMessageReceived(String path, byte[] rawData) {
        // TODO Auto-generated method stub
        if (path.equals("/send_bothway")) {
            // 接收到手表端请求数据并响应 必须传入接收到的path
            OpenWatchBothWay.response(this, path, "response bothway");
        } else {
            Toast.makeText(this, getClass().getSimpleName() + ":手表发来临时性数据:" + new String(rawData), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onDataReceived(String path, byte[] rawData) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手表发来数据:" + new String(rawData), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataDeleted(String path) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手表删除了一条数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataMapReceived(String path, DataMap dataMap) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手表发来键值对", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBitmapReceived(String path, Bitmap bitmap) {
        // TODO Auto-generated method stub
        ImageView imageView = (ImageView) findViewById(R.id.received_bitmap);
        imageView.setImageBitmap(bitmap);
        Toast.makeText(this, getClass().getSimpleName() + "：手表端发来图片", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFileReceived(SpecialData data) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手表端发来文件：" + new String(data.getData()), Toast.LENGTH_SHORT)
                .show();

        //保存成文件
        data.receiveFile(getExternalCacheDir() + File.separator + "file.txt");
    }

    @Override
    public void onStreamReceived(SpecialData data) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：手表端发来数据流：" + new String(data.getData()), Toast.LENGTH_SHORT).show();

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
        Toast.makeText(this, getClass().getSimpleName() + "：保存手表端发来的数据完成", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPeerConnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：和手表连接上了  设备名：" + displayName + " 设备id：" + nodeId,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPeerDisconnected(String displayName, String nodeId) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + "：和手表断开了连接  设备名：" + displayName + " 设备id：" + nodeId,
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
