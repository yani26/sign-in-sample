package com.example.signin_sample;

//import go.client.Client;
import client.*;

import android.app.ActivityManager;

import android.content.Context;
import android.content.SharedPreferences;

import android.util.Log;
import de.greenrobot.event.EventBus;

import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class DeviceManager implements Runnable, client.DeviceClientEvent  {
    private final String TAG = "DeviceManager";

    private static DeviceManager instance;
    private client.DeviceClient deviceClient = client.Client.newDeviceClient();
    public UDPClientEvent udpClientEvent;

    private Context conext;
    public ConnectivityManager netStateManager;

    private boolean connected = false;
    private SharedPreferences pref;
    private boolean isGDServiceStarted = false;

    //device info
    private String mac = "";
    private String nickname = "";
    private String qrcode = "";

    public String server;
    private boolean selfStop = false;
    public List<User> users = new ArrayList<User>();



    public DeviceManager(String server) {

        this.server = server;

        // network
        if (instance != null) {
            instance.stop();
        }
        instance = this;

        Client.setGUDPClientEvent(udpClientEvent);
        Client.setGDeviceClientEvent(this);

        connect();

    }

    public static DeviceManager g() {
        return instance;
    }

    public void setGDServiceStarted(boolean started){
        isGDServiceStarted = started;
    }

    public boolean getGDSerivceStatus(){
        return isGDServiceStarted;
    }

    public void connect() {

        Log.i(TAG,".......... Start connect command to server "+ server+"  in DeviceManager");

        deviceClient.connectAsync(server, false); // false means don't ssl verified

    }


    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isConnected() {
        return connected;
    }


    ////////////////////////////////////////////////////////////// useless for now

    @Override
    public void callResult(boolean succ, long err, String receiver, String ip, long port) {
        Log.i(TAG, "CallResult: " + succ + " port:" + port);
    }

    @Override
    public void imageControl(String phone, String mac, int action) {
        Log.i(TAG, "receive ImageControl: " + action + " phone:" + phone);
    }

    @Override
    public void musicControl(final String from, String to, final int action) {

        Log.i(TAG, "receive music control: " + action);
    }

    @Override
    public void remoteControl(final String from, String to, final int action, String info) {
        Log.i(TAG, "receive remote control: " + action + "  " + info);
        // action 0:device 1:sensor 2:appliance 3:ipcam 3:
    }

    @Override
    public void reqControl485(String from, String mac, int address, int action) {
        Log.i(TAG, String.format("ReqControl485: %s %d %d ", mac, address, action));

    }

    @Override
    public void reqModeStatus(String from, String mac) {
        ///// |node1:1|node2:2|node3:3|
        ///// |node1:1@1200ON@1316OFF@2125OFF|node2:2|node3:3|
        /////|mode1:voice content@node11200on@node21300off@node31400on|node2:n2|
    }

    @Override
    public void reqMusicStatus(String from, String mac) {

    }

    @Override
    public void reqNodeStatus(String from, String mac) {
        ///// |node1:1|node2:2|node3:3|
        ///// |node1:1@1200ON@1316OFF@2125OFF|node2:2SCALE|node3:3|
        ///// we send node name nd nodeID
    }

    @Override
    public void reqUserPushInfoResult( String phonenumber, String pushuserid,
                                       String pushchannelid, String modelname, boolean succ){

        Log.i(TAG, "ReqUserPushInfoResult: " + succ );
    }

    @Override
    public void stopCall(String to) {
        Log.i(TAG, "Stop call from  " + to);
    }

    @Override
    public void timerControl(final String from, String to, final int action, String info) {
        Log.i(TAG, "receive timer control: ");
    }

    @Override
    public void volumeControl(String phone, String mac, int volume) {
        Log.i(TAG, "receive VolumeControl: " + volume + " phone:" + phone);
    }

    @Override
    public void userListResult(long all, long index, String phone, String nickname,
                               String pushuserid, String pushchannelid, String modelname) {

        Log.i(TAG, "UserListResult, all: " + all);
        //This is a PATCH ONLY. Need to remove in production
        // when it reports UserListResult, it means network is OK
        connected = true;
        // KC PATCH

        if (all == 0) {
            users.clear();
            return;
        }

        if (index == 0) {
            users.clear();
        }

        users.add(new User(phone, nickname, pushuserid, pushchannelid, modelname));

        Log.i(TAG, "phone: "+phone+" nickname: "+nickname);
        Log.i(TAG, "----------channelID:"+ pushchannelid+" model:"+ modelname);
        Log.i(TAG, "----------pushuserid:"+pushuserid);

        if (all == (index + 1)) {
            Log.i(TAG, "......post DeviceUserListResultEvent ");
            EventBus.getDefault().post(new DeviceUserListResultEvent());
        }
    }

    /////////////////////////////////////////// DeviceClientEvent//////////////////////////
    @Override
    public void connect(boolean success, String err) {
        Log.i(TAG, String.format("Connect Request Result: %b |  %s", success, err));
    }


    @Override
    public void deviceInitResult(boolean success) {
        Log.i(TAG, "DeviceInitResult: " + new Boolean(success).toString());

        EventBus.getDefault().post(new DeviceInitResultEvent(success));

        if (success) {
            sendGetQRCodeInfo();
        }
    }


    @Override
    public void disconnect() {
        Log.i(TAG, "Disconnect");
        connected = false;
    }

    @Override
    public void error(String err) {
        Log.i(TAG, "Error: " + err);
    }


    public void reqKeepAlive(){
        deviceClient.sendReqKeepAliveAsync(mac);
        Log.i(TAG, "Send ReqKeepAlive ....................................");
    }

    @Override
    public void reqKeepAliveResult(boolean success){
        Log.i(TAG, "receive ReqKeepAliveResult: "+ success);

    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    @Override
    public void qrCodeResult(String result) {
        Log.i(TAG, "QRCodeResult: " + result);

    }

    class KeepAliveTimerTask extends TimerTask {
        @Override
        public void run() {
            if (isNetworkOK()) { // network is not ok. Then, it will be handled separately
                reqKeepAlive();  // This is to fix while network is ok, but the low level connection is broken.
            }
        }
    }


    public boolean isNetworkOK() {
        if (netStateManager == null) {
            Log.i(TAG, "..... netStateManager is null ");
            return false;
        }

        boolean network = netStateManager.getActiveNetworkInfo() != null && netStateManager.getActiveNetworkInfo().isConnectedOrConnecting();
        return network;
    }


    public void reconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    while (true) {
                        Thread.sleep(3 * 1000);
                        if (connected) {
                            break;
                        }

                        if (DeviceManager.instance.isNetworkOK()) {
                            connect();
                            Log.i(TAG, " Device connecting in reconnect ");
                            Thread.sleep(5 * 1000);
                        }

                        if (connected || !DeviceManager.instance.isNetworkOK()) {
                            break;
                        }
                        //// test
                        //   break;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void stop() {
        this.selfStop = true;
    }


    public boolean isDeviceRegistered() {
        if (pref == null) {
            return false;
        }
        return pref.getBoolean(Const.Device_Init_Key, false);
    }


    //////////////// keepAlive Timer
    private Timer keepAliveTimer;
    private KeepAliveTimerTask keepAliveTimerTask;

    ///This initWithContent is activated from GDService
    public void initWithContext(Context context) {
        this.conext = context;

        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMan.getConnectionInfo();

        this.mac = info.getMacAddress();
        this.pref = context.getSharedPreferences(Const.Device_Pref_Key, context.MODE_PRIVATE);
        this.nickname = pref.getString(Const.Device_Nickname_Key, "");


        netStateManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


        // Create a keepAlive timer
        keepAliveTimer = new Timer(true);
        if (keepAliveTimerTask == null) {
            keepAliveTimerTask = new KeepAliveTimerTask();
            keepAliveTimer.schedule(keepAliveTimerTask, Const.Timer_Keep_Alive, Const.Timer_Keep_Alive); // repeat the task
            Log.i(Const.TAG, "start keepAlive timer");
        }

        EventBus.getDefault().post(new DeviceServiceReadyEvent());
    }


    public String getMac() {
        return mac;
    }

    public String getDeviceNickname() {
        if (nickname == null) {
            nickname = pref.getString(Const.Device_Nickname_Key, "");
        }

        if (nickname == null) {
            nickname = "";
        }

        return nickname;
    }


    public SharedPreferences getPref() {
        return pref;
    }

    public void setPref(SharedPreferences pref) {
        this.pref = pref;
    }

    public void sendDeviceInit() {
        deviceClient.sendDeviceInitAsync(getMac(), getDeviceNickname());
    }

    public void sendGetQRCodeInfo() {
        deviceClient.sendReqQRCodeInfoAsync(mac);
    }

    public void sendGetUserlist() {
        deviceClient.sendReqUserListAsync(mac);
    }

    public void sendHrvData() {
        //deviceClient.sendNodeStatusAsync();
        deviceClient.errorEvent("send data successfully");
        deviceClient.sendRemovePhone("phone","mac");
    }

    @Override
    public void run() {
        int musicStatus = -1;

        Log.i(TAG,"--------- DeviceManager starts run process");

        // stop the thread now
        selfStop = false; /////

        long _before = Calendar.getInstance().getTimeInMillis();
        while (!selfStop) {
            try {
                Thread.sleep(1500);


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        Log.i(TAG, "device manager end");
    }


}
