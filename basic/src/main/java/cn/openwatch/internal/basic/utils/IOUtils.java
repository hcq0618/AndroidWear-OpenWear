package cn.openwatch.internal.basic.utils;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public final class IOUtils {
    private IOUtils() {
    }

    public static byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);

            // 此方法大图片 可能导致OutOfMemory
            return byteStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (byteStream != null) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }
            }
        }

        return null;
    }

    public static byte[] streamToBytes(InputStream stream) {
        if (stream == null)
            return null;

        try {

            byte[] buffer = new byte[stream.available()];
            stream.read(buffer);

            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static OutputStream bytesToStream(OutputStream outputStream, byte[] data) {
        BufferedOutputStream stream = null;
        try {

            stream = new BufferedOutputStream(outputStream);
            stream.write(data);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (stream != null) {
                try {
                    stream.flush();
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }

        return stream;
    }

    // http://my.oschina.net/chape/blog/201725#OSC_h2_2
    // http://zhangyuefeng1983.blog.163.com/blog/static/1083372520126693524870/
    public static String bytesToString(byte[] raw) {
        try {
            if (raw != null)
                return new String(raw, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static byte[] stringToBytes(String origin) {
        try {
            return origin.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "".getBytes();
    }
}
