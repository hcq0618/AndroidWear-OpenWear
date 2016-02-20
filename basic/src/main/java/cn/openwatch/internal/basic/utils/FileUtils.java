package cn.openwatch.internal.basic.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by hcq0618 on 2015/11/6.
 */
public final class FileUtils {

    private FileUtils() {
    }


    public static String readFile(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            StringBuilder sb = new StringBuilder();
            String tempString;
            String enterFlag = "\r\n";

            while ((tempString = reader.readLine()) != null) {
                sb.append(tempString).append(enterFlag);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return "";
    }

    public static boolean writeFile(String path, String fileName, byte[] raw) {
        FileOutputStream fos = null;
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(path, fileName);
            if (file.exists())
                file.delete();

            LogUtils.d(FileUtils.class, "writeFile " + file.getAbsolutePath());
            fos = new FileOutputStream(file.getAbsolutePath());
            fos.write(raw);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }

        return false;
    }

    public static String getFileMD5(File file) {
        if (file == null || !file.isFile() || !file.exists()) {
            return null;
        }

        MessageDigest digest;
        FileInputStream in = null;

        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }

            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    public static byte[] fileToBytes(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        try {
            FileInputStream stream = new FileInputStream(file);
            return IOUtils.streamToBytes(stream);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static File bytesToFile(String filePath, byte[] data) {
        File file = new File(filePath);

        if (!file.exists()) {
            return file;
        }

        FileOutputStream fstream = null;
        try {

            fstream = new FileOutputStream(file);
            IOUtils.bytesToStream(fstream, data);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fstream != null) {
                try {
                    fstream.flush();
                    fstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }
}
