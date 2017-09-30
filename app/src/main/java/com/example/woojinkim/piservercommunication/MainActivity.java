package com.example.woojinkim.piservercommunication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    Wlan1Info wlan1Info = new Wlan1Info();

    @BindView(R.id.response) TextView textResponse;

    @BindView(R.id.ssid)EditText Etssid;
    @BindView(R.id.pwd) EditText Etpwd;

    @BindView(R.id.address)EditText editTextAddress;
    @BindView(R.id.port) EditText editTextPort;

    @BindView(R.id.connect)Button buttonConnect;
    @OnClick(R.id.connect) void connectyes() {
        wlan1Info.ssid = Etssid.getText().toString();
        wlan1Info.pwd = Etpwd.getText().toString();
        String json = "{\"ssid\":\""+wlan1Info.ssid+"\",\"pwd\":\""+wlan1Info.pwd+"\"}";

        MyClientTask myClientTask = new MyClientTask(
                "10.0.0.1",
                8081,json);
        myClientTask.execute();
    }

    @BindView(R.id.clear) Button buttonClear;
    @OnClick(R.id.clear) void SetTvClear() {
        textResponse.setText("");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        String jsonString;

        MyClientTask(String addr, int port,String json){
            dstAddress = addr;
            dstPort = port;
            jsonString=json;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                outputStream.write(jsonString.getBytes());
    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            super.onPostExecute(result);
        }

    }

}