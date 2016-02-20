package cn.openwatch.communication;

import java.io.OutputStream;

import cn.openwatch.communication.listener.SpecialTypeListener;
import cn.openwatch.internal.basic.ThreadsManager;
import cn.openwatch.internal.basic.utils.FileUtils;
import cn.openwatch.internal.basic.utils.IOUtils;

/**
 * Created by hcq0618 on 2015/11/5.
 */
public final class SpecialData {

    private String path;
    private byte[] data;
    private SpecialTypeListener listener;

    public byte[] getData() {
        return data;
    }

    public String getPath() {
        return path;
    }

    public SpecialData(String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    public void setListener(SpecialTypeListener listener) {
        this.listener = listener;
    }

    /**
     * 保存数据到指定文件路径 保存完成后回调{@link SpecialTypeListener#onInputClosed(String)}
     *
     * @param saveFilePath 指定文件路径
     * @see SpecialTypeListener#onInputClosed(String)
     */
    public void receiveFile(final String saveFilePath) {
        ThreadsManager.execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.bytesToFile(saveFilePath, data);
                if (listener != null)
                    listener.onInputClosed(path);
            }
        });
    }

    /**
     * 写入数据到指定输出流 写入完成后回调{@link SpecialTypeListener#onInputClosed(String)}
     *
     * @param outputStream 指定输出流
     * @see SpecialTypeListener#onInputClosed(String)
     */
    public void receiveStream(final OutputStream outputStream) {
        ThreadsManager.execute(new Runnable() {
            @Override
            public void run() {
                IOUtils.bytesToStream(outputStream, data);
                if (listener != null)
                    listener.onInputClosed(path);
            }
        });
    }
}
