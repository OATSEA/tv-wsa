package services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import common.utils.FileUtils;
import utils.Utils;

public class HotSpotReceiver extends BroadcastReceiver {
    private static final String TAG = HotSpotReceiver.class.getSimpleName();
    public HotSpotReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {

            // get Wi-Fi Hotspot state here
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

            if (WifiManager.WIFI_STATE_ENABLED == state % 10) {

            }

            String ipAddress = Utils.getIPAddress(true);
            if(ipAddress.trim().isEmpty()){
               FileUtils.writeIpAddress("http://" + "localhost" + ":8080");
            }else{
                FileUtils.writeIpAddress("http://"+ Utils.getIPAddress(true)+":8080");
            }

        }
    }





}
