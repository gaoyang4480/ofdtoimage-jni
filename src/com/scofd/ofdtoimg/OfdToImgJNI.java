package com.scofd.ofdtoimg;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OfdToImgJNI {

    static final double convertCount = 1;

    public native int initConverter();

    public native void finalizeConverter(String taskId);

    public native int openFile(String taskId, byte[] OFDData, int verIndex, int docIndex);

    public native byte[] convert(String taskId, int pageIndex, String format, int width, int dpi);

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: <目标OFD文件路径>");
            return;
        }
        System.out.println(System.getProperty("java.library.path"));

        System.loadLibrary("ofdview");
        OfdToImgJNI convertJni = new OfdToImgJNI();
        int ret = convertJni.initConverter();
        String templateDir = args[0];
        byte[] templateOFDData = getFileByteArray(new File(templateDir));
        ret = convertJni.openFile("001", templateOFDData, 0, 0);
        System.out.println("JAVA: open ok");
        List<Callable<Integer>> list = null;
        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(1);
        long cc = System.currentTimeMillis();
        convert(convertJni, 0);
        //convertJni.finalizeConverter("001");
        return;
//    try {
//      list = getCallableList(convertJni);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//    try {
//      Long beginTime = System.currentTimeMillis();
//      cachedThreadPool.invokeAll(list);
//      long endTime = System.currentTimeMillis();
//      long coustTime = endTime - beginTime;
//      System.out.println("**************************************************耗时:" + coustTime);
//      System.out.println("总耗时：" + (endTime - cc) + "毫秒");
//      System.out.println("平均每秒转换数：" + convertCount / ((endTime - cc) / 1000.0) + "");
//      System.out.println("平均：" + (endTime - cc) / convertCount);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    System.out.println("**************************************************111");
    }

    private static List<Callable<Integer>> getCallableList(OfdToImgJNI convertJni) throws Exception {
        List<Callable<Integer>> resultList = new ArrayList<>();
        for (int j = 0; j < convertCount; j++) {
            final int i = j;
            Callable<Integer> callable = () ->
                    convert(convertJni, i);
            resultList.add(callable);
        }
        return resultList;
    }

    private static int convert(OfdToImgJNI convertJni, int i) throws IOException {
        byte[] resultData = convertJni.convert("001", i, "jpg", 0, 0);

        if (resultData != null) {
            System.out.println("successfully " + i + ".jpg  " + resultData.length);
        }

        if (resultData != null) {
            OutputStream resultOutStream = new FileOutputStream("./" + i + ".jpg");
            resultOutStream.write(resultData);
            resultOutStream.close();
            System.out.println("write result ofd successfully " + i + ".jpg");
        }
        return 0;
    }

    private static String byteToString(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return "";
        }
        String strContent = "";
        try {
            strContent = new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strContent;
    }

    public static byte[] getFileByteArray(File file) {
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        byte[] buffer = null;
        try (FileInputStream fi = new FileInputStream(file)) {
            buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

}
