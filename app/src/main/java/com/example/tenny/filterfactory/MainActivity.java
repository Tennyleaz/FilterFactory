package com.example.tenny.filterfactory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView Pname, Iname, Input, message;
    private ImageView imageStatus;
    private static ProgressDialog pd;
    static final String SERVERIP = "140.113.167.14";
    static final int SERVERPORT = 9000; //8000= echo server, 9000=real server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Pname = (TextView) findViewById(R.id.tv2);
        Iname = (TextView) findViewById(R.id.tv4);
        Input = (TextView) findViewById(R.id.tv6);
        message = (TextView) findViewById(R.id.textView);
        imageStatus = (ImageView) findViewById(R.id.imageView);
        imageStatus.setVisibility(View.INVISIBLE);

        if(!isNetworkConnected()){  //close when not connected
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("警告");
            dialog.setMessage("無網路連線,\n程式即將關閉");
            dialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialoginterface, int i) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });
            dialog.show();
            Log.e("Mylog", "no network");
            //android.os.Process.killProcess(android.os.Process.myPid());
            //System.exit(1);
        }
        else {
            pd = ProgressDialog.show(MainActivity.this, "連線中", "Please wait...");
        /* 開啟一個新線程，在新線程裡執行耗時的方法 */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    InitServer();// 耗時的方法
                    handler.sendEmptyMessage(0);// 執行耗時的方法之後發送消給handler
                }

            }).start();
        }
    }

    private void InitServer() {
        SocketHandler.closeSocket();
        SocketHandler.initSocket(SERVERIP, SERVERPORT);
        String init = "CONNECT FF<END>";
        SocketHandler.writeToSocket(init);

        //receive result
        String str2;
        str2 = SocketHandler.getOutput();
        Log.d("Mylog", str2);
        /*if (str2.equals("CONNECT_OK<END>"))
            connected = 1;
        else if (str2.equals("CONNECT_WRONG<END>"))
            connected = 2;
        else if (str2.equals("CONNECT_EXIST<END>"))
            connected = 3;
        else if (str2.equals("CONNECT_REPEAT<END>"))
            connected = 4;
        else
            connected = 0;
        Log.d("Mylog", "connected=" + connected);*/
        message.setText(str2);
    }

    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息後就會執行此方法
            pd.dismiss();// 關閉ProgressDialog
        }
    };
}
