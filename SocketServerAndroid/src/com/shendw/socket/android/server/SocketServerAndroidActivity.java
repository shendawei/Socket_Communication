package com.shendw.socket.android.server;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SocketServerAndroidActivity extends Activity
{
    SocketServer server  = null;
    Button       control = null;
    boolean      state   = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        openStrictModeByVersion();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        control = (Button) findViewById(R.id.control_server);
        control.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (state)
                {
                    control.setText("Close server");
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Log.e("recvMsg","Open server....");
                            server = new SocketServer();
                        }
                    }).start();
                }
                else
                {
                    control.setText("Open server");
                    
                    Log.e("recvMsg","Close server....");
                    if (null != server)
                    {
                        server = null;
                    }
                }
                state = !state;
            }
        });
    }

    private void openStrictModeByVersion()
    {
        // String strVer=GetVersion.GetSystemVersion();
        // strVer=strVer.substring(0,3).trim();
        // float fv=Float.valueOf(strVer);
         float fv = 2.3f;
//        float fv = 3.0f;

        if (fv > 2.3)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                    .detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
                    .penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                    .penaltyLog() // 打印logcat
                    .penaltyDeath().build());
        }
    }
}