
package com.internet.demo.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SocketAndroidClientActivity extends Activity implements Runnable
{
    private TextView            tv_msg    = null;

    private EditText            ed_msg    = null;

    private Button              btn_send  = null;

//    private Button              btn_login = null;

    // The host name.
//    private static final String HOST      = "192.168.0.132";
    // 这个是本机的局域网分配的ip，可以实现通信
//    private static final String HOST      = "192.168.1.101";
    // 这个是拨号上网的本机ip，可以实现通信
//    private static final String HOST      = "10.198.1.7";
    // 
    private static final String HOST      = "127.0.0.1";

    // The port.
    private static final int    PORT      = 9999;

    private Socket              socket    = null;

    private BufferedReader      in        = null;

    private PrintWriter         out       = null;

    private String              content   = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tv_msg = (TextView) this.findViewById(R.id.TextView);
        ed_msg = (EditText) this.findViewById(R.id.EditText01);
//        btn_login = (Button) this.findViewById(R.id.Button01);
        btn_send = (Button) this.findViewById(R.id.Button02);
        try
        {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())), true);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            ShowDialog("登陆异常:" + ex.getMessage());
        }
        btn_send.setOnClickListener(new Button.OnClickListener()
        {

            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                String msg = ed_msg.getText().toString();
                if (socket.isConnected())
                {
                    if (!socket.isOutputShutdown())
                    {
                        out.println(msg);
                    }
                }
            }

        });
        new Thread(this).start();
    }

    public void ShowDialog(String msg)
    {
        new AlertDialog.Builder(this).setTitle("提示").setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {

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
                            Log.i("TAG", "++ " + content);
                            content += "\n";
                            mHandler.sendMessage(mHandler.obtainMessage());
                        }
                        else
                        {

                        }
                    }
                }

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public Handler mHandler = new Handler()
                            {
                                public void handleMessage(Message msg)
                                {
                                    super.handleMessage(msg);
                                    Log.i("TAG", "-- " + msg);
                                    tv_msg.setText(tv_msg.getText().toString()
                                            + content);
                                }
                            };
}
