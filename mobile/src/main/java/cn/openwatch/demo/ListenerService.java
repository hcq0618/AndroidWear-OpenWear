package cn.openwatch.demo;

import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import cn.openwatch.communication.DataMap;
import cn.openwatch.communication.SpecialData;
import cn.openwatch.communication.service.OpenWatchListenerService;

//数据通信和设备连接的监听服务 用于接收配对设备发送过来的数据 以及监听与配对设备的连接状态
//不需要自己管理WearableListenerService的生命周期，当有数据发送过来时会自动bind service 当不需要再工作时
//会自动unbind service

//如果你不想使用service 也可以在某个界面中使用OpenWatchEvent 来在界面中监听 配对设备发送过来的数据
public class ListenerService extends OpenWatchListenerService {

    @Override
    public void onMessageReceived(String path, byte[] rawData) {
        // TODO Auto-generated method stub
        Toast.makeText(this, getClass().getSimpleName() + ":手表发来临时性数据:" + new String(rawData), Toast.LENGTH_SHORT)
                .show();
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

}
