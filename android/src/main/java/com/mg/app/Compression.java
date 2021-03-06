package com.mg.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import android.util.Base64;
import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import id.zelory.compressor.Compressor;


class Compression {

    public File compressImage(final Activity activity, final ReadableMap options, final String originalImagePath) throws IOException {
        Integer maxWidth = options.hasKey("width") ? options.getInt("width") : null;
        Integer maxHeight = options.hasKey("height") ? options.getInt("height") : null;
        Integer quality = options.hasKey("compressQuality") ? options.getInt("compressQuality") : null;
        Integer minCompressSize = options.hasKey("minCompressSize") ? options.getInt("minCompressSize") : null;

        if (maxWidth == null || maxWidth<=0) {
            // maxWidth = 300;
        }
        if (maxHeight == null || maxHeight<=0 ) {
            // maxHeight = 300;
        }
        if (quality == null || quality >= 100 || quality <= 0) {
            return new File(originalImagePath);
        }
        //小于最小压缩大小，就不压缩
        if(minCompressSize != null && minCompressSize >0 && getFileSize(new File(originalImagePath)) < minCompressSize * 1024){
            return new File(originalImagePath);
        }
        String path = RxGalleryFinalApi.getImgSaveRxDirByStr();
        Compressor compressor = new Compressor(activity)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(path);

        compressor.setQuality(quality);

        if (maxWidth != null) {
            compressor.setMaxWidth(maxWidth);
        }

        if (maxHeight != null) {
            compressor.setMaxHeight(maxHeight);
        }

        File image = new File(originalImagePath);

        String[] paths = image.getName().split("\\.(?=[^\\.]+$)");
        String compressedFileName = paths[0] + "-compressed";

        if(paths.length > 1)
            compressedFileName += "." + paths[1];

        return compressor
                .compressToFile(image, compressedFileName);
    }
    public String createImageThumbnailBase64(final Activity activity,final String originalImagePath) throws IOException {
        File actualImageFile = new File(originalImagePath);
        Bitmap compressedImageBitmap = new Compressor(activity).compressToBitmap(actualImageFile);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
    synchronized void compressVideo(final Activity activity, final ReadableMap options,
                                    final String originalVideo, final String compressedVideo, final Promise promise) {
        // todo: video compression
        // failed attempt 1: ffmpeg => slow and licensing issues
        promise.resolve(originalVideo);
    }

    /**
     * 获取指定文件大小
     * @param file
     * @return
     * @throws Exception 　　
     */
    public static long getFileSize(File file)  {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis  = null;
            try {
                fis  = new FileInputStream(file);
                size = fis.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return size;
    }
}
