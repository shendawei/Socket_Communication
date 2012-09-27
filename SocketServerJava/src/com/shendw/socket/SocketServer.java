package com.shendw.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer
{
    private static final int PORT             = 8010;
    private List<Socket>     mList            = new ArrayList<Socket>();
    private ServerSocket     server           = null;
    private ExecutorService  mExecutorService = null;                   // thread
                                                                         // pool

    public static void main(String[] args)
    {
        new SocketServer();
    }

    public SocketServer()
    {
        try
        {
            server = new ServerSocket(PORT);
            mExecutorService = Executors.newCachedThreadPool(); // create a
                                                                // thread pool
            System.out.print("server start ...\n");
            Socket client = null;
            while (true)
            {
                client = server.accept();
                mList.add(client);
                mExecutorService.execute(new Service(client)); // start a new
                                                               // thread to
                                                               // handle the
                                                               // connection
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    class Service implements Runnable
    {
        private Socket         socket;
        private BufferedReader in  = null;
        private String         msg = "";

        public Service(Socket socket)
        {
            this.socket = socket;
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                InetAddress inetSocketAddress  = this.socket.getInetAddress();
                SocketAddress socketAddress = this.socket.getLocalSocketAddress();
                int localPort = this.socket.getLocalPort();
                SocketAddress remoteSocketAddress = this.socket.getRemoteSocketAddress();
                String address = "inetSocketAddress = " + inetSocketAddress + "\n"
                                 + "socketAddress = " + socketAddress + "\n"
                                 + "localPort = " + localPort + "\n" 
                                 + "remoteSocketAddress = " + remoteSocketAddress;
                msg = "user\n" + /*this.socket.getInetAddress()*/ address + "\ncome toal:" + mList.size() + "\n";
                this.sendmsg();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }

        @Override
        public void run()
        {
            // TODO Auto-generated method stub
            try
            {
                while (true)
                {
                    if ((msg = in.readLine()) != null)
                    {
                        if (msg.equals("exit"))
                        {
                            System.out.println("ssssssss");
                            mList.remove(socket);
                            in.close();
                            msg = "user:" + socket.getInetAddress() + "exit total:" + mList.size();
                            socket.close();
                            this.sendmsg();
                            break;
                        }
                        else
                        {
                            msg = socket.getInetAddress() + ":" + msg;
                            this.sendmsg();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void sendmsg()
        {
            System.out.println(msg);
            int num = mList.size();
            for (int index = 0; index < num; index++)
            {
                Socket mSocket = mList.get(index);
                PrintWriter pout = null;
                try
                {
                    BufferedWriter br = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                    br.write(msg);
                    br.flush();
                    pout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
                    pout.println(msg);
                    pout.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
