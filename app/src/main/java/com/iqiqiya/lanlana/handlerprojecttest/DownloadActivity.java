package com.iqiqiya.lanlana.handlerprojecttest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import androidx.annotation.Nullable;

/**
 * Author: iqiqiya
 * Date: 2019/9/22
 * Time: 13:14
 * Blog: blog.77sec.cn
 * Github: github.com/iqiqiya
 */
public class DownloadActivity extends Activity {

    private Handler mhandler;
    public static final int DOWNLOAD_MESSAGE_CODE = 100001;
    public static final int DOWNLOAD_MESSAGE_FAIL_CODE = 100002;
    private ProgressBar progressBar;
    private String APP_URL = "http://api.77sec.cn/iqiqiya.apk";

    private String TAG = "HandlerTest";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        progressBar = findViewById(R.id.progressBar);

        /**
         * 主线程 -->
         * 点击按钮 |
         * 发起下载 |
         * 开启子线程做下载
         * 下载过程中通知主线程 | -- > 主线程更新进度条
         */
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download(APP_URL);
                        Log.d(TAG,"开始下载");
                    }
                }).start();
            }
        });

        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case DOWNLOAD_MESSAGE_CODE:
                        Log.d(TAG,"进度条开始变化");
                        progressBar.setProgress((Integer) msg.obj);
                        break;
                    case DOWNLOAD_MESSAGE_FAIL_CODE:
                        Log.d(TAG,"出错了");
                        break;
                }
            }
        };
    }

    private void download(String appurl) {
        try {
            URL url = new URL(appurl);
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();

            /**
             * 获取文件的总长度
             */

            int contentLength = urlConnection.getContentLength();

            String downloadFolderName = Environment.getExternalStorageDirectory()
                    + File.separator + "iqiqiya" + File.separator;

            File file = new File(downloadFolderName);
            if (!file.exists()){
                Log.d(TAG,"开始创建文件夹");
                file.mkdir();
            }

            String filename = downloadFolderName + "iqiqiya.apk";


            File apkFile = new File(filename);

            if (apkFile.exists()){
                Log.d(TAG,"已经存在此文件，开始删除");
                apkFile.delete();
            }

            int downloadSize = 0;
            byte[] bytes = new byte[1024];

            int length = 0;
            OutputStream outputStream = new FileOutputStream(filename);
            while ((length = inputStream.read(bytes)) != -1){
                outputStream.write(bytes,0, length);
                downloadSize += length;
                /**
                 * update UI
                 */

                Message message = Message.obtain();
                message.obj = downloadSize * 100 / contentLength;
                message.what = DOWNLOAD_MESSAGE_CODE;
                mhandler.sendMessage(message);

            }
            inputStream.close();
            outputStream.close();
        } catch (MalformedURLException e) {
            notifyDownloadFaild();
            e.printStackTrace();
        } catch (IOException e) {
            notifyDownloadFaild();
            e.printStackTrace();
        }
    }

    private void notifyDownloadFaild() {
        Message message = Message.obtain();
        message.what = DOWNLOAD_MESSAGE_FAIL_CODE;
        mhandler.sendMessage(message);
    }
}
