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

import utils.Utils;

public class HotSpotReceiver extends BroadcastReceiver {
    private static final String TAG = HotSpotReceiver.class.getSimpleName();
    private static String htdocs = Environment.getExternalStorageDirectory() + File.separator + "htdocs";

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

            Log.e(TAG,"Ip Written for hotspot");
            checkHtdocsOK();
            String ipAddress = Utils.getIPAddress(true);
            if(ipAddress.trim().isEmpty()){
                writeIpAddress("http://"+"localhost"+":8080");
            }else{
                writeIpAddress("http://"+ Utils.getIPAddress(true)+":8080");
            }

        }
    }


    public boolean checkHtdocsOK() {
        File file = new File(htdocs);
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();

    } // END checkHtdocsOK
    private void writeIpAddress(String ipAddress){

        File ipFile = new File(htdocs,"IP.txt");
        try {
            FileOutputStream f = new FileOutputStream(ipFile);
            PrintWriter pw = new PrintWriter(f);
            pw.print(ipAddress);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
