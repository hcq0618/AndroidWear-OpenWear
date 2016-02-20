package cn.openwatch.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.openwatch.communication.BothWayCallback;
import cn.openwatch.communication.DataMap;
import cn.openwatch.communication.ErrorStatus;
import cn.openwatch.communication.HttpCallback;
import cn.openwatch.communication.OpenWatchBothWay;
import cn.openwatch.communication.OpenWatchHttp;
import cn.openwatch.communication.OpenWatchSender;
import cn.openwatch.communication.listener.SendListener;

public class WearListAdapter extends WearListView.AbsWearListBindAdapter<Feature> {

    private Context context;
    private String testPatchTargetField = "未加载";

    public WearListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void onBindView(TextView textView, ImageView iconView, int position) {
        // TODO Auto-generated method stub
        Feature item = getItem(position);
        textView.setText(item.name);

        iconView.setVisibility(View.GONE);
    }

    private void sendData() {
        // 当手机未连接 数据并不会被丢失 会在下次连接上手机时接收到数据
        // 发送基础数据
        OpenWatchSender.sendData(context, "/send_data", "你好 openwatch", new MySendListener("data"));
    }

    private void sendBitmapData() {
        // 发送图片
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        OpenWatchSender.sendData(context, "/send_bitmap", bitmap, new MySendListener("图片"));
    }

    private void sendDatamapData() {
        // 发送键值对
        DataMap datamap = new DataMap();
        datamap.putString("key", "value");
        OpenWatchSender.sendData(context, "/send_datamap", datamap, new MySendListener("键值对"));
    }

    private void sendStreamData() {
        try {
            InputStream is = context.getAssets().open("test.txt");
            OpenWatchSender.sendData(context, "/send_stream", is, new MySendListener("数据流"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // // 发送文件
        // OpenWatchSender.sendData(context, "/send_file", new
        // File(filePath), new MySendListener("文件"));
    }

    private void sendMsg() {
        // 当手机未连接 数据会被丢失 用于发送临时性或时效性数据
        // 发送基础数据
        OpenWatchSender.sendMsg(context, "/send_msg", "你好 openwatch", new MySendListener("msg"));
    }

    private void sendBitmapMsg() {
        // 发送图片
        Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        OpenWatchSender.sendMsg(context, "/send_bitmap", bitmap2, new MySendListener("图片"));
    }

    private void sendDatamapMsg() {
        // 发送键值对
        DataMap datamap2 = new DataMap();
        datamap2.putString("key", "value");
        OpenWatchSender.sendMsg(context, "/send_datamap", datamap2, new MySendListener("键值对"));
    }

    private void sendStreamMsg() {
        try {
            InputStream is = context.getAssets().open("test.txt");
            OpenWatchSender.sendMsg(context, "/send_stream", is, new MySendListener("数据流"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // // 发送文件
        // OpenWatchSender.sendMsg(context, "/send_file", new
        // File(filePath), new MySendListener("文件"));
    }

    private void sendBothway() {
        OpenWatchBothWay.request(context, "/send_bothway", "你好 openwatch", new BothWayCallback() {

            @Override
            public void onResponsed(byte[] rawData) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "手机端响应数据:" + new String(rawData), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ErrorStatus error) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "数据请求错误" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void http() {
        OpenWatchHttp http = new OpenWatchHttp(context);
        http.get("http://www.baidu.com", new HttpCallback() {

            @Override
            public void onResponse(byte[] data, int statusCode, Map<String, String> headers) {
                // TODO Auto-generated method stub
                Log.d(WearListAdapter.class.getName(), new String(data));
                Toast.makeText(context, "网络请求响应成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(byte[] data, int statusCode, Map<String, String> headers) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "网络请求响应失败:" + statusCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(Feature item, int position) {
        // TODO Auto-generated method stub
        switch (item) {
            case SEND_DATA:
                sendData();
                break;
            case SEND_BITMAP_DATA:
                sendBitmapData();
                break;
            case SEND_DATAMAP_DATA:
                sendDatamapData();
                break;
            case SEND_STREAM_DATA:
                sendStreamData();
                break;
            case SEND_MSG:
                sendMsg();
                break;
            case SEND_BITMAP_MSG:
                sendBitmapMsg();
                break;
            case SEND_DATAMAP_MSG:
                sendDatamapMsg();
                break;
            case SEND_STREAM_MSG:
                sendStreamMsg();
                break;
            case SEND_BOTHWAY:
                sendBothway();
                break;
            case HTTP:
                http();
                break;
            default:
                break;
        }
    }

    @Override
    public List<Feature> onBindData() {
        // TODO Auto-generated method stub
        ArrayList<Feature> featureList = new ArrayList<Feature>();

        String[] names = {"发送基础数据给手机", "发送图片给手机", "发送键值对给手机", "发送数据流/文件给手机", "发送临时性基础数据给手机", "发送临时性图片给手机",
                "发送临时性键值对给手机", "发送临时性数据流/文件给手机", "发送数据给手机并等待响应", "网络请求"};
        Feature[] features = new Feature[]{Feature.SEND_DATA, Feature.SEND_BITMAP_DATA, Feature.SEND_DATAMAP_DATA,
                Feature.SEND_STREAM_DATA, Feature.SEND_MSG, Feature.SEND_BITMAP_MSG, Feature.SEND_DATAMAP_MSG,
                Feature.SEND_STREAM_MSG, Feature.SEND_BOTHWAY,Feature.HTTP};

        for (int i = 0; i < features.length; i++) {
            Feature feature = features[i];

            feature.name = names[i];

            featureList.add(feature);
        }

        return featureList;
    }

    private class MySendListener implements SendListener {

        private String tag;

        private MySendListener(String tag) {
            this.tag = tag;
        }

        @Override
        public void onSuccess() {
            // TODO Auto-generated method stub
            Toast.makeText(context, "发送" + tag + "成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(ErrorStatus error) {
            // TODO Auto-generated method stub
            Toast.makeText(context, "发送" + tag + "失败 原因是:" + error, Toast.LENGTH_SHORT).show();
        }
    }

}