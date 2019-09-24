package com.iqiqiya.lanlana.handlerprojecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // handler简单操作

        final TextView textView = findViewById(R.id.textview);

        // 创建Handler
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 处理消息
                Log.d(TAG,"handleMessage：" + msg.what);

                if (msg.what == 1001) {
                    textView.setText("iqiqiya");
                }
            }
        };

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 子线程
                 */
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        /**
                         * 通知UI更新
                         */
                        handler.sendEmptyMessage(1001);

                        Message message = new Message();
                    }
                });

            }
        });

        handler.sendEmptyMessage(1001);
    }
}
