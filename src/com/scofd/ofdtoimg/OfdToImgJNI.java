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

    static Integer totalPageNum = new Integer(0);

    static String templateDir;

    static byte[] templateOFDData;

    public native int initConverter(String licence);

    public native void finalizeTask(String taskId);

    public native void finalizeConverter();

    public native int openFile(String taskId, byte[] OFDData, int verIndex, int docIndex, Integer totalPageNum);

    public native int openFileByPath(String taskId, String srcFilePath, int verIndex, int docIndex, Integer totalPageNum);

    public native byte[] convert(String taskId, int pageIndex, String format, int width, int dpi);

    public native byte[] convertByEleType(String taskId, int pageIndex, String format, int width, int dpi, int eleType);

    public native byte[] getText(String taskId, int pageIndex);

    public native String findFontFilePath(String fontName);

    public native boolean convertOFDToPDFByPath(String srcOFDFilePath, String outputPDFFilePath, int docIndex, int verIndex, int beginPageIndex, int endPageIndex);

    public native boolean convertOFDToPDFByData(byte[] srcOFDFileData, String outputPDFFilePath, int docIndex, int verIndex, int beginPageIndex, int endPageIndex);

    public native byte[] stitchPictures(List<byte[]> srcImageDataList, String destImageFormat, int dpi);

    public native byte[] stitchPicturesWithScale(List<byte[]> srcImageDataList, String destImageFormat, int dpi, float scale);

    public native PageSize getPageSize(String taskId, int pageIndex);

    public native String getMetaData(String taskId);

    public native String getMachineCode();

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: <目标OFD文件路径>");
            return;
        }
        System.out.println(System.getProperty("java.library.path"));

        //System.loadLibrary("libexpat");
        System.loadLibrary("zlib");
        System.loadLibrary("libc++");
        System.loadLibrary("absl");
        System.loadLibrary("icuuc");
        System.loadLibrary("partition_alloc");
        System.loadLibrary("pdfium");
        System.loadLibrary("yh_ofdview");
        OfdToImgJNI convertJni = new OfdToImgJNI();

        String s = convertJni.getMachineCode();

        // 授权信息，需要根据自己的机器去生成，找闰土生成.
//        while (true) {
//            System.out.println("init begin");
//            int ret1 = convertJni.initConverter("LDOudv2kXJd8u2NDwUpKhMZEXUVzADIboH+ZgRcQPaPwTkjOmjXUAvIYCwM0adS+3DH5vciGSETXulEnoe5UKyQn7Jj/qh348ER2D6KyvDmMEFLhZ0gMrso2Qu20fmrhQqfTTEIusGmIrAq4i8o+PXwd6FMVzexq+XW1O/6/JDw=");
//            if (ret1 != 0) {
//                System.err.println("init error");
//                break;
//            }
//            System.out.println("init end");
//            convertJni.finalizeConverter();
//            System.out.println("finalize end");
//        }
        int ret = convertJni.initConverter("11111111");
        if (ret != 0) {
            System.err.println("init error");
            return;
        }

//        List<byte[]> srcImageDataList = new ArrayList<>();

//        byte[] srcImageData1 = getFileByteArray(new File("C:\\Users\\gaoyang\\Pictures\\1.jpg"));
//        byte[] srcImageData2 = getFileByteArray(new File("C:\\Users\\gaoyang\\Pictures\\2.jpg"));
//        byte[] srcImageData3 = getFileByteArray(new File("C:\\Users\\gaoyang\\Pictures\\3.jpg"));
//        srcImageDataList.add(srcImageData1);
//        srcImageDataList.add(srcImageData2);
//        srcImageDataList.add(srcImageData3);

//        for (File imgFile : new File("C:\\Users\\gaoyang\\Pictures\\10-ofd").listFiles()) {
//            byte[] srcImageData = getFileByteArray(imgFile);
//            srcImageDataList.add(srcImageData);
//        }
//
//        long beginTime = System.currentTimeMillis();
//        //byte[] stitchImageData = convertJni.stitchPicturesWithScale(srcImageDataList, "jpg", 96, 2.0f);
//        byte[] stitchImageData = convertJni.stitchPictures(srcImageDataList, "jpg", 96);
//        long endTime = System.currentTimeMillis();
//        long coustTime = endTime - beginTime;
//        System.out.println("stitchPictures time: " + coustTime);
//        if (stitchImageData != null) {
//            OutputStream resultOutStream = new FileOutputStream("stitch.jpg");
//            resultOutStream.write(stitchImageData);
//            resultOutStream.close();
//        }

        templateDir = args[0];

//        boolean result = convertJni.convertOFDToPDFByPath(templateDir, "./output.pdf", -1, -1, -1, -1);
//        System.out.println("result: " + ret);

//        String fontFilePath = convertJni.findFontFilePath("123");
//        if (fontFilePath != null) {
//            System.out.println("fontFilePath:" + fontFilePath);
//        }

        //templateOFDData = getFileByteArray(new File(templateDir));

        String taskId = "001";
        ret = convertJni.openFileByPath(taskId, templateDir, 0, 0, totalPageNum);
        if (ret != 0) {
            System.err.println("open file error");
            return;
        }

//        PageSize pageSize = convertJni.getPageSize(taskId, 0);
//        String metaDataStr = convertJni.getMetaData(taskId);

//        byte[] text = convertJni.getText("001", 0);
//        System.out.println(new String(text));

        List<Callable<Integer>> list = null;
        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(8);
        long cc = System.currentTimeMillis();

//        Long beginTime = System.currentTimeMillis();
//        convert(convertJni, 0);
//        long endTime = System.currentTimeMillis();
//        long coustTime = endTime - beginTime;
//        System.out.println("**************************************************耗时:" + coustTime);
//        convertJni.finalizeTask("001");
//        return;

        try {
            list = getCallableList(convertJni);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Long beginTime = System.currentTimeMillis();
            cachedThreadPool.invokeAll(list);
            long endTime = System.currentTimeMillis();
            long coustTime = endTime - beginTime;
            System.out.println("**************************************************耗时:" + coustTime);
            System.out.println("总耗时：" + (endTime - cc) + "毫秒");
            System.out.println("平均每秒转换数：" + convertCount / ((endTime - cc) / 1000.0) + "");
            System.out.println("平均：" + (endTime - cc) / convertCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("**************************************************111");

//        for (int i = 0; i < convertCount; i++) {
//            convert(convertJni, i);
//        }
    }

    private static List<Callable<Integer>> getCallableList(OfdToImgJNI convertJni) throws Exception {
        List<Callable<Integer>> resultList = new ArrayList<>();
        for (int j = 0; j < 1; j++) {
            final int i = j;
//            for (int k = 0; k < totalPageNum; k++) {
//                final int m = k;
//                Callable<Integer> callable = () ->
//                        convert(convertJni, m);
//                resultList.add(callable);
//            }
            Callable<Integer> callable = () ->
                    convert(convertJni, i);
            resultList.add(callable);
        }
        return resultList;
    }

    private static int convert(OfdToImgJNI convertJni, int i) throws IOException {
        System.out.println("i:" + i);
//        int ret = convertJni.initConverter("LDOudv2kXJd8u2NDwUpKhMZEXUVzADIboH+ZgRcQPaPwTkjOmjXUAvIYCwM0adS+3DH5vciGSETXulEnoe5UKyQn7Jj/qh348ER2D6KyvDmMEFLhZ0gMrso2Qu20fmrhQqfTTEIusGmIrAq4i8o+PXwd6FMVzexq+XW1O/6/JDw=");
//        if (ret != 0) {
//            System.err.println("init error");
//            return -1;
//        }

//        byte[] templateOFDData = getFileByteArray(new File(templateDir));

//        String taskId = "" + i;
//        Integer totalPageNum = new Integer(0);
//        int ret = convertJni.openFileByPath(taskId, templateDir, 0, 0, totalPageNum);
//        if (ret != 0) {
//            System.err.println("open file error");
//            return -1;
//        }

        String taskId = "001";

        //System.out.println("111111111");
        //for (int j = 0; j < 1; ++j)
        {
//            byte[] resultData = convertJni.convert(taskId, i, "svg", 0, 96);
            byte[] resultData = convertJni.convertByEleType(taskId, i, "svg", 0, 96, 0x1);
            if (resultData != null) {
                //System.out.println("successfully " + i + ".png  " + resultData.length);
                OutputStream resultOutStream = new FileOutputStream("./" + i + ".svg");
                resultOutStream.write(resultData);
                resultOutStream.close();
                System.out.println("write result ofd successfully " + i + ".jpg");
            }
        }

//        if (resultData != null && 1 == 0) {
//            OutputStream resultOutStream = new FileOutputStream("./" + i + ".png");
//            resultOutStream.write(resultData);
//            resultOutStream.close();
//            System.out.println("write result ofd successfully " + i + ".png");
//        }

        //convertJni.finalizeTask(taskId);
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
