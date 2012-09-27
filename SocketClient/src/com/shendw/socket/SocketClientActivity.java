package com.shendw.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SocketClientActivity extends Activity implements Runnable
{
    private static final int CHAT_MSG = 0;
    private static final int CONNECT_EXCEPTION_MSG = 1;
    
    
    private TextView            tv_msg   = null;
    private EditText            ed_msg   = null;
    private Button              btn_send = null;
    // private Button btn_login = null;
    // 自定义host
//    private static final String HOST     = "192.168.1.223";
    // PC host;
    private static final String HOST     = "192.168.5.110";
//    private static final String HOST     = "192.168.0.127";
    // Wlan route host.
//    private static final String HOST     = "192.168.7.4";
    // PC host
//    private static final String HOST     = "127.0.0.1";
    private static final int    PORT     = 8010;
    private Socket              socket   = null;
    private BufferedReader      in       = null;
    private PrintWriter         out      = null;
    private String              content  = "";
    private Button btn_connect;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        openStrictModeByVersion();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tv_msg = (TextView) findViewById(R.id.TextView);
//        tv_msg.append("Chat Msg : ");
        ed_msg = (EditText) findViewById(R.id.EditText01);
        btn_connect = (Button) findViewById(R.id.Button01);
        btn_send = (Button) findViewById(R.id.Button02);

        btn_connect.setOnClickListener(new OnClickListener()
        {
            
            @Override
            public void onClick(View v)
            {
                try
                {
                    socket = new Socket(HOST, PORT);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    mHandler.obtainMessage(CONNECT_EXCEPTION_MSG, ex).sendToTarget();
                }
            }
        });
        btn_send.setOnClickListener(new Button.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                String msg = ed_msg.getText().toString();
                if (null != socket && socket.isConnected())
                {
                    if (!socket.isOutputShutdown())
                    {
                        out.println(msg);
                    }
                }
                new Thread(SocketClientActivity.this).start();
            }
        });
    }

    public void ShowDialog(String msg)
    {
        new AlertDialog.Builder(this).setTitle("notification").setMessage(msg)
                .setPositiveButton("ok", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // TODO Auto-generated method stub

                    }
                }).show();
    }

    public void run()
    {
        try
        {
            while (true)
            {
                if (socket.isConnected())
                {
                    if (!socket.isInputShutdown())
                    {
                        if ((content = in.readLine()) != null)
                        {
                            content += "\n";
//                            mHandler.sendMessage(mHandler.obtainMessage());
                            mHandler.obtainMessage(CHAT_MSG).sendToTarget();
                        }
                        else
                        {

                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            
            switch (msg.what)
            {
            case CHAT_MSG:
            {
//            tv_msg.setText(content);
                
                // TextView append()方法，可以保持显示用户在聊天时的消息对话记录。
                if (null != tv_msg)
                {
                    tv_msg.append(content);
                }
            }   
            break;
                
            case CONNECT_EXCEPTION_MSG:
            {
                IOException ex = (IOException) msg.obj;
                ShowDialog("login exception" + ex.getMessage());
            }
            break;

            default:
                break;
            }
        }
    };
    
    private void openStrictModeByVersion()
    {
        // String strVer=GetVersion.GetSystemVersion();
        // strVer=strVer.substring(0,3).trim();
        // float fv=Float.valueOf(strVer);
//        float fv = 2.3f;
        float fv = 3.0f;

        if (fv > 2.3)
        {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
                    .penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                    .penaltyLog() // 打印logcat
                    .penaltyDeath().build());
        }
    }
}